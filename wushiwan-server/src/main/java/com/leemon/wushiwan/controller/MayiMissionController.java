package com.leemon.wushiwan.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Strings;
import com.leemon.wushiwan.dto.ExplainData;
import com.leemon.wushiwan.dto.UploadToken;
import com.leemon.wushiwan.entity.*;
import com.leemon.wushiwan.enums.DeviceType;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.jwt.JwtTokenUtil;
import com.leemon.wushiwan.service.ICoreMissionRuleService;
import com.leemon.wushiwan.service.ISysPropertyService;
import com.leemon.wushiwan.service.QiniuKodoService;
import com.leemon.wushiwan.vo.CreateMissionRequest;
import com.leemon.wushiwan.vo.UploadTokenResponse;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 获取蚂蚁帮扶任务
 * @author: limeng
 * @create: 2019-11-18 21:56
 **/
@RestController
@RequestMapping("/mayi/mission")
@Slf4j
public class MayiMissionController {

	private final ICoreMissionRuleService ruleService;
	private final ISysPropertyService propertyService;
	private final QiniuKodoService kodoService;
	private final JwtTokenUtil jwtTokenUtil;

	public MayiMissionController(ICoreMissionRuleService ruleService, ISysPropertyService propertyService, QiniuKodoService kodoService, JwtTokenUtil jwtTokenUtil) {
		this.ruleService = ruleService;
		this.propertyService = propertyService;
		this.kodoService = kodoService;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	private static class MayiListRequest extends BaseEntity {
		@NotBlank
		private String sessionId;
	}

	@PostMapping("/list")
	public Object list(@RequestBody @Valid MayiListRequest req) {
		return getMayiMissionList(req.getPageSize().toString(), req.getPageNo().toString(), req.getSessionId(), null);
	}

	private Document getMissionDetailDocument(CreateMayiMissionRequest missionRequest) {
		Mono<String> resp = WebClient.create().get()
				.uri("http://www.aliyunqifu.com/web/antAssist_pc/taskInformation?task_id={missionNo}&from_type=view", missionRequest.getMissionNo())
				.cookie("JSESSIONID", missionRequest.getSessionId())
				.retrieve().bodyToMono(String.class);
		String respStr = resp.block();
		if (respStr.contains("长时间没有操作，系统保护性退出")) {
			throw new LogicException("请重新输入cookie");
		}
		return Jsoup.parse(respStr);
	}

	private MayiBaseModel<MayiMission> getMayiMissionList(String pageSize, String pageNo, String sessionId, String missionNo) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("pCount", pageSize);
		formData.add("pageNo", pageNo);
		formData.add("user_id", "1753908");
		formData.add("query_type", "1");
		if (!Strings.isNullOrEmpty(missionNo)) {
			formData.add("search_type", "1");
			formData.add("task_id", missionNo);
		}
		Mono<String> resp = WebClient.create().post()
				.uri("http://www.aliyunqifu.com/weixin/antAssist/ajaxTaskList")
				.cookie("JSESSIONID", sessionId)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(formData))
				.retrieve().bodyToMono(new ParameterizedTypeReference<String>() {
				});
		String respStr = resp.block();
		if (respStr.contains("长时间没有操作，系统保护性退出")) {
			throw new LogicException("请重新输入cookie");
		}
		return JSONObject.parseObject(respStr, new TypeReference<MayiBaseModel<MayiMission>>() {
		});
	}

	@Data
	public static class CreateMayiMissionRequest {
		@NotBlank
		private String missionNo;
		@NotBlank
		private String sessionId;
		@NotNull
		private Integer toUserId;//任务发布到哪个用户身上
	}

	@Data
	@Builder
	public static class MayiMissionDetailResponse {
		private String title;
		private String missionNo;
		private String price;
		private String category;
		private String textVerify;
		private String note;
		private String topEndTime;
		private String endTime;
		private String url;
	}

	@PostMapping("/detail")
	public Object missionDetail(@RequestBody @Valid CreateMayiMissionRequest missionRequest) {
		MayiBaseModel<MayiMission> model = getMayiMissionList("2", "1", missionRequest.getSessionId(), missionRequest.getMissionNo());
		if (model.getList().size() == 1) {
			return model.getList().get(0);
		}
		return null;
	}

	@PostMapping("/create")
	public Object createMission(@RequestBody @Valid CreateMayiMissionRequest missionRequest) {
		Document document = getMissionDetailDocument(missionRequest);
		Element missionEle = document.selectFirst(".infor_main_wrap .content_wrap .rw_intro_wrap");
		String title = missionEle.selectFirst(".each_intro:nth-child(1) span").text();
		String missionNo = missionEle.selectFirst(".each_intro:nth-child(2) div:nth-child(2) span").text();
		String price = missionEle.selectFirst(".each_intro:nth-child(3) div:nth-child(2) span").text().replace("元", "");
		String category = missionEle.selectFirst(".each_intro:nth-child(3) div:nth-child(1) span").text();
		String textVerify = null, note = null, topEndTime = null, endTime = null, url = null;
		Elements infoEles = missionEle.select(".beizhu_intro tr");
		for (Element infoEle : infoEles) {
			String titleText = infoEle.selectFirst(".beizhu_intro_title").text();
			if (titleText.contains("链") && titleText.contains("接")) {
				url = infoEle.selectFirst("a").text().trim();
			}
			if ("文字验证：".equals(titleText)) {
				textVerify = infoEle.selectFirst("span").text().trim();
			}
			if (titleText.contains("注") && titleText.contains("备")) {
				note = infoEle.selectFirst("span").text().trim();
			}
			if ("置顶到期：".equals(titleText)) {
				topEndTime = infoEle.selectFirst("span").text().trim();
			}
			if ("截止时间：".equals(titleText)) {
				endTime = infoEle.selectFirst("span").text().trim();
			}
		}
		List<String> verifyImgList = new ArrayList<>();
		Elements verifyImgEle = document.select(".infor_main_wrap .content_wrap .step_wrap li");
		for (Element element : verifyImgEle) {
			verifyImgList.add("http://www.aliyunqifu.com" + element.selectFirst("img").attr("src"));
		}
		List<ExplainData> stepImgList = new ArrayList<>();
		Elements missionStepEle = document.select(".infor_main_wrap .operation_clearfix li");
		for (Element element : missionStepEle) {
			ExplainData step = new ExplainData();
			Element textElement = element.selectFirst(".rw_intro_wrap .step_each_intro .yzt_intro");
			if (textElement != null) {
				step.setText(textElement.text());
			}
			Element imgElement = element.selectFirst("img");
			if (imgElement != null) {
				step.setImg("http://www.aliyunqifu.com" + imgElement.attr("src"));
			}
			stepImgList.add(step);
		}

		CreateMissionRequest req = new CreateMissionRequest();
		req.setTitle(title);
		req.setPrice((int) (Double.parseDouble(price) * 100));
		req.setDeadlineTime(LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		req.setTextVerify(textVerify);
		req.setRemark(note);
		req.setUrl(url);
		req.setSampleImgList(verifyImgList);
		req.setExplainList(stepImgList);
		List<CoreMissionRule> ruleList = ruleService.list();
		CoreMissionRule currentRule = null;
		for (CoreMissionRule coreMissionRule : ruleList) {
			SysProperty property = propertyService.getById(coreMissionRule.getTypePropertyId());
			if (property.getName().equals(category)) {
				currentRule = coreMissionRule;
				break;
			}
		}
		if (currentRule == null) {
			log.info("任务类型[{}]不存在，使用其他代替", category);
			for (CoreMissionRule coreMissionRule : ruleList) {
				SysProperty property = propertyService.getById(coreMissionRule.getTypePropertyId());
				if ("其他".equals(property.getName())) {
					currentRule = coreMissionRule;
					break;
				}
			}
		}
		req.setTypePropertyId(currentRule.getTypePropertyId());
		req.setCount(currentRule.getMinCount());
		req.setMobilePropertyId(DeviceType.ALL.getValue());
		this.requestCreateMission(req, missionRequest.getToUserId());
		return null;
	}

	private void requestCreateMission(CreateMissionRequest req, Integer userId) {
		List<String> sampleList = req.getSampleImgList();
		UploadTokenResponse sampleRes = kodoService.uploadFileFromServer(sampleList, userId);
		sampleList.clear();
		for (UploadToken uploadToken : sampleRes.getList()) {
			sampleList.add(uploadToken.getFileName());
		}

		List<String> explainList = new ArrayList<>();
		for (ExplainData explainData : req.getExplainList()) {
			if (!Strings.isNullOrEmpty(explainData.getImg())) {
				explainList.add(explainData.getImg());
			}
		}
		if (explainList.size() > 0) {
			UploadTokenResponse explainRes = kodoService.uploadFileFromServer(explainList, userId);
			int index = 0;
			for (int i = 0; i < req.getExplainList().size(); i++) {
				ExplainData data = req.getExplainList().get(i);
				if (!Strings.isNullOrEmpty(data.getImg())) {
					data.setImg(explainRes.getList().get(index).getFileName());
					index++;
				}
			}
		}
		String token = jwtTokenUtil.generateNeverExpireToken(userId.toString());
		if (!jwtTokenUtil.isTokenInWhiteList(token)) {
			jwtTokenUtil.addTokenToWhiteList(token);
		}
		String jsonStr = JSONObject.toJSONString(req);
		Mono<BaseModel> resp = WebClient.create().post()
				.uri("http://localhost:9091/core-mission/create")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.syncBody(jsonStr)
				.retrieve().bodyToMono(BaseModel.class);
		BaseModel result = resp.block();
		if (!"SUCCESS".equals(result.getCode())) {
			throw new LogicException(result.getMsg());
		}
	}
}
