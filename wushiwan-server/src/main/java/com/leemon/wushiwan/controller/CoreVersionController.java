package com.leemon.wushiwan.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.entity.BaseModel;
import com.leemon.wushiwan.entity.CoreVersion;
import com.leemon.wushiwan.enums.SystemConfig;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.service.ICoreVersionService;
import com.leemon.wushiwan.service.ISysPropertyService;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 前端版本记录 更新流程： 1、提交代码到svn，等待jenkins自动编译完成（生成dist文件夹）
 * 2、admin端添加一个新版本
 * @author: leemon
 * @date: 2019-08-18
 */
@RestController
@RequestMapping("/core-version")
@Slf4j
public class CoreVersionController extends NoAuthBaseController<CoreVersion> {

	private final ICoreVersionService coreVersionService;
	private final Config config;
	private final ISysPropertyService propertyService;

	@Autowired
	public CoreVersionController(ICoreVersionService coreVersionService, Config config,
								 ISysPropertyService propertyService) {
		this.coreVersionService = coreVersionService;
		this.config = config;
		this.propertyService = propertyService;
	}

	@Data
	private static class UpdateRequest {
		@NotBlank
		private String appVersion;
		@NotBlank
		private String h5Version;
		private String channel;
	}

	@RequestMapping("/no-auth/update-version")
	public Object updateVersion(@RequestBody @Valid UpdateRequest req) {
		if (!Strings.isNullOrEmpty(req.getChannel())) {
			String json = SystemConfig.IN_REVIEW_APP.getPropertyValue();
			JSONObject obj = (JSONObject) JSONObject.parse(json);
			JSONObject channelObj = (JSONObject) obj.get(req.getChannel());
			if (channelObj != null) {
				String version = channelObj.getString("version");
				Boolean inReview = channelObj.getBoolean("review");
				if (inReview && version.equals(req.getAppVersion())) {
					return null;
				}
			}
		}
		QueryWrapper<CoreVersion> qw = new QueryWrapper<>();
		qw.lambda().orderByDesc(CoreVersion::getId);
		CoreVersion lastVersion = this.coreVersionService.getOne(qw, false);
		if (lastVersion == null) {
			return null;
		}
		if (lastVersion.getVersionName().equals(req.getH5Version())) {
			return null;
		}
		return lastVersion;
	}

	private String createErrorMsg(List<CoreVersion> list) {
		StringBuilder msg = new StringBuilder("版本号不得低于之前的版本号\n");
		for (CoreVersion coreVersion : list) {
			msg.append(String.format("id: %d,version_name: %s,version_code :%d\n", coreVersion.getId(),
					coreVersion.getVersionName(), coreVersion.getVersionCode()));
		}
		return msg.toString();
	}

	private synchronized String createWgtFile(CoreVersion version) {
		log.info("开始打包wgt文件");
		String manifestJson = SystemConfig.MANIFEST.getPropertyValue();
		log.info("manifestJson = {}", manifestJson);
		JSONObject jsonObject = JSONObject.parseObject(manifestJson);
		JSONObject versionObj = jsonObject.getJSONObject("version");
		versionObj.put("name", version.getVersionName());
		versionObj.put("code", version.getVersionCode());
		String jsonString = jsonObject.toJSONString();
		String fileName = jsonObject.get("id").toString() + "_" + version.getVersionName() + "_"
				+ version.getVersionCode() + ".wgt";
		try {
			// 拷贝manifest.json
			File manifestFile = new File("/tmp/manifest.json");
			FileUtils.writeStringToFile(manifestFile, jsonString, "UTF-8");
			// 删除dist文件夹并拷贝新的dist文件夹
			File distFile = new File("/tmp/dist");
			FileUtils.deleteDirectory(distFile);
			//这里是把正式环境的前端代码拷贝过去
			FileUtils.copyDirectory(new File(config.getClientDistPath()), distFile);
			// 生成压缩后的wgt文件
			ZipFile zipFile = new ZipFile("/tmp/" + fileName);
			zipFile.addFolder(distFile);
			zipFile.addFile(manifestFile);
			FileUtils.copyFile(new File("/tmp/" + fileName),
					new File(config.getWgtFilePath() + File.separator + fileName));
		} catch (IOException e) {
			throw new LogicException(ErrorCode.SYS_ERROR);
		}
		return fileName;
	}

	@Data
	@Builder
	private static class VersionListResponse {
		private Object list;
		private CoreVersion lastVersion;
	}

	/**
	 * 分页列表查询
	 *
	 * @param coreVersion
	 * @return
	 */
	@PreAuthorize("hasPermission('core_version')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CoreVersion coreVersion) {
		Page<CoreVersion> page = new Page<>(coreVersion.getPageNo(), coreVersion.getPageSize());
		QueryWrapper<CoreVersion> qw = new QueryWrapper<>();
		qw.lambda().orderByDesc(CoreVersion::getId);
		CoreVersion lastVersion = this.coreVersionService.getOne(qw, false);
		return VersionListResponse.builder().list(coreVersionService.page(page, getLikeQueryWrapper(coreVersion)))
				.lastVersion(lastVersion).build();
	}

	/**
	 * 保存
	 *
	 * @param coreVersion
	 * @return
	 */
	@PreAuthorize("hasPermission('core_version_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CoreVersion coreVersion) {
		if (coreVersion.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		if (coreVersion.getVersionCode() == null || Strings.isNullOrEmpty(coreVersion.getVersionName())
				|| coreVersion.getForceUpdate() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "需要传入version_name和version_code和force_update");
		}
		List<CoreVersion> list = coreVersionService.getVersionListGreaterThanVersion(coreVersion.getVersionName(),
				coreVersion.getVersionCode());
		if (list.size() != 0) {
			throw new LogicException(createErrorMsg(list));
		}
		coreVersion.setDeployed(false);
		coreVersionService.saveOrUpdate(coreVersion);
		boolean result = coreVersionService.startJenkinsJob("dist-wushiwan-client");
		if (!result) {
			throw new LogicException("打包失败，请重试或联系技术支持");
		}
		coreVersion.setWgtFilename(this.createWgtFile(coreVersion));
		coreVersion.setDeployed(true);
		String cmd = String.format("", config.getWgtFilePath(), coreVersion.getWgtFilename(), coreVersion.getWgtFilename());
		log.info("开始向远程服务器拷贝，命令 = {}", cmd);
		exeCmd(cmd);
		log.info("拷贝命令执行完成");

		IndexController.AddVersionRequest req = new IndexController.AddVersionRequest();
		req.setVersionCode(coreVersion.getVersionCode());
		req.setVersionName(coreVersion.getVersionName());
		req.setForceUpdate(coreVersion.getForceUpdate());
		req.setWgtFilename(coreVersion.getWgtFilename());

		WebClient webClient = WebClient.create();
		//向正式服务器上添加新版本
		Mono<BaseModel> mono = webClient.post().uri("http://server.bungke.com:9091/index/no-auth/add-version").syncBody(req).retrieve().bodyToMono(BaseModel.class);
		BaseModel model = mono.block();
		log.info("请求远程bungke服务器更新model = {},{}", model, model.getCode());
		if (model == null) {
			throw new LogicException("打包失败，请重试或联系技术支持");
		}
		if (model.getCode().equals(ErrorCode.SUCCESS.name())) {
			log.info("请求远程服务器添加新版本号完成");
		} else {
			log.error("请求远程服务器添加版本号错误 = {}", model);
		}
		coreVersionService.saveOrUpdate(coreVersion);
		log.info("更新部署状态完成");
		return null;
	}

//	public static void main(String[] args) {
//		IndexController.AddVersionRequest req = new IndexController.AddVersionRequest();
//
//		WebClient webClient = WebClient.create();
//		//向正式服务器上添加新版本
//		log.info("time1 = {}", LocalDateTime.now().toString());
//		Mono<BaseModel> mono = webClient.post().uri("http://www.bungke.com:9091/index/no-auth/add-versio").syncBody(req).retrieve().bodyToMono(BaseModel.class);
//		log.info("time2 = {}", LocalDateTime.now().toString());
//		BaseModel model = mono.block(Duration.ofSeconds(2));
//		log.info("time3 = {}", LocalDateTime.now().toString());
////		log.info("请求model = {},{}", model, model.getCode());
////		if (model == null) {
////			throw new LogicException("打包失败，请重试或联系技术支持");
////		}
////		if (model.getCode().equals(ErrorCode.SUCCESS.name())) {
////			log.info("请求远程服务器添加新版本号完成");
////		} else {
////			log.error("请求远程服务器添加版本号错误 = {}", model);
////		}
//	}

	private static void exeCmd(String commandStr) {
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec(commandStr);
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			System.out.println(sb.toString());
		} catch (Exception e) {
			throw new LogicException("向远程服务器部署失败，请联系技术支持");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 编辑
	 *
	 * @param coreVersion
	 * @return
	 */
	@PreAuthorize("hasPermission('core_version_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CoreVersion coreVersion) {
		if (coreVersion.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		if (coreVersion.getVersionCode() == null || Strings.isNullOrEmpty(coreVersion.getVersionName())) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "需要传入version_name和version_code");
		}
		QueryWrapper<CoreVersion> qw = new QueryWrapper<>();
		qw.lambda().orderByDesc(CoreVersion::getId).last("limit 1");
		CoreVersion lastVersion = this.coreVersionService.getOne(qw, false);
		if (lastVersion != null) {
			if (!lastVersion.getId().equals(coreVersion.getId())) {
				throw new LogicException("只可以编辑最后一条记录");
			}
		}
		boolean ok = coreVersionService.updateById(coreVersion);
		if (!ok) {
			log.error("coreVersion = {}", coreVersion);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreVersion失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_version_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CoreVersion coreVersion = getExistEntityById(idRequest.getId(), coreVersionService);
		boolean ok = coreVersionService.removeById(coreVersion);
		if (!ok) {
			log.error("coreVersion = {}", coreVersion);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CoreVersion失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_version_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		coreVersionService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_version')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return coreVersionService.getById(idRequest.getId());
	}

}
