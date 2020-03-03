package com.leemon.wushiwan.dto;

import com.leemon.wushiwan.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 我的店铺统计数据
 * @author: leemon
 * @create: 2019-10-22 09:39
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopDetail extends BaseEntity {
	//完成单数
	private Long finishedOrderCount;
	//被申诉数
	private Long beComplaintCount;
	//接受过的任务数量
	private Long acceptMissionCount;
	//申诉数
	private Long complaintCount;
}
