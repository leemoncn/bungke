package com.leemon.wushiwan.service;

import com.leemon.wushiwan.dto.PartnerInfo;
import com.leemon.wushiwan.entity.CorePartnerPurchase;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.enums.PartnerTimeType;
import com.leemon.wushiwan.system.entity.SysUser;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-06-27
 */
public interface ICorePartnerPurchaseService extends IService<CorePartnerPurchase> {

	/**
	 * 给某个用户添加合作商
	 *
	 * @param sysUser
	 * @param partnerId
	 */
	void addNewPartner(SysUser sysUser, Integer partnerId, PartnerTimeType timeType, String operator);

//	/**
//	 * 对已存在的合作商计划进行退款
//	 *
//	 * @param currentPartner 退款金额
//	 * @return
//	 */
//	int refundPartner(PartnerInfo currentPartner);

	/**
	 * 将某个合作商记录变为不可用状态
	 *
	 * @param corePartnerPurchaseId
	 */
	void disablePartner(@NotNull Integer corePartnerPurchaseId);

	List<PartnerInfo> getValidPartnerInfoList();
}
