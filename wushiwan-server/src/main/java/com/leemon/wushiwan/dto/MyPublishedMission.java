package com.leemon.wushiwan.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 我发布的任务
 * @author: limeng
 * @create: 2019-05-22 23:31
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MyPublishedMission extends MissionDetail {
	//待审核数量
	private Integer waitingReviewCount;
	//已完成
	private Integer finishCount;
	//不合格
	private Integer rejectedCount;
}
