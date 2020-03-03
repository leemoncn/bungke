package com.leemon.wushiwan.dto;

import com.leemon.wushiwan.entity.CoreMissionDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: limeng
 * @create: 2019-05-29 21:07
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MyAcceptMission extends CoreMissionDetail {
	private Integer missionId;
	private LocalDateTime acceptTime;
	private Integer proceedPropertyId;
	private Integer acceptId;
	private LocalDateTime finishTime;
	/**
	 * 当前任务如果是不合格状态，是否可以申诉
	 */
	private Boolean canComplaint;

	/**
	 * 申诉结果，如果没申诉过或者平台还未确定申诉结果，为null
	 */
	private Boolean complaintResult;
}
