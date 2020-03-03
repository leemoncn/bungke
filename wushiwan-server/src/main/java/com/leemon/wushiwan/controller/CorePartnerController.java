package com.leemon.wushiwan.controller;

import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;

import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.entity.CorePartner;
import com.leemon.wushiwan.service.ICorePartnerService;
import com.leemon.wushiwan.service.ICoreTopService;
import com.leemon.wushiwan.system.entity.SysUser;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 合作商配置
 * @author: leemon
 * @date: 2019-05-10
 */
@RestController
@RequestMapping("/core-partner")
@Slf4j
public class CorePartnerController extends BaseController<CorePartner> {

	private final ICorePartnerService corePartnerService;
	private final ICoreTopService topService;

	@Autowired
	public CorePartnerController(ICorePartnerService corePartnerService, ICoreTopService topService) {
		this.corePartnerService = corePartnerService;
		this.topService = topService;
	}

	@RequestMapping(value = "/clist")
	public Object clist() {
		return corePartnerService.list();
	}

	@Data
	private static class PartnerDetailResponse {
		CorePartner partner;
		int freeHourToday;
	}

	@RequestMapping("/partner-detail")
	public Object partnerDetail() {
		SysUser user = UserUtil.getSysUser();
		CorePartner partner = corePartnerService.getById(user.getPartnerId());
		int freeHourToday = topService.getFreeHourToday();
		PartnerDetailResponse res = new PartnerDetailResponse();
		res.setFreeHourToday(freeHourToday);
		res.setPartner(partner);
		return res;
	}

	@Data
	private static class PartnerInfoResponse {
		private String name;
		private List<CorePartner> list;
	}

	/**
	 * 个人页面-合作商
	 *
	 * @return
	 */
	@PostMapping("/info")
	public PartnerInfoResponse info() {
		PartnerInfoResponse res = new PartnerInfoResponse();

		SysUser user = UserUtil.getSysUser();
		CorePartner partner = corePartnerService.getById(user.getPartnerId());
		res.setName(partner.getName());
		res.setList(corePartnerService.list());

		return res;
	}

	/*----------------------------以下是admin的接口----------------------------**/


	/**
	 * 分页列表查询
	 *
	 * @param corePartner
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid CorePartner corePartner) {
		Page<CorePartner> page = new Page<>(corePartner.getPageNo(), corePartner.getPageSize());
		return corePartnerService.page(page, getLikeQueryWrapper(corePartner));
	}

	/**
	 * 保存
	 *
	 * @param corePartner
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_add')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid CorePartner corePartner) {
		if (corePartner.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		corePartnerService.saveOrUpdate(corePartner);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param corePartner
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_edit')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid CorePartner corePartner) {
		if (corePartner.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = corePartnerService.updateById(corePartner);
		if (!ok) {
			log.error("corePartner = {}", corePartner);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CorePartner失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_delete')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		CorePartner corePartner = getExistEntityById(idRequest.getId(), corePartnerService);
		boolean ok = corePartnerService.removeById(corePartner);
		if (!ok) {
			log.error("corePartner = {}", corePartner);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新CorePartner失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner_delete')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		corePartnerService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasPermission('core_partner')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return corePartnerService.getById(idRequest.getId());
	}

}
