package com.leemon.wushiwan.vo;

import com.leemon.wushiwan.entity.CoreMissionAccept;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: limeng
 * @create: 2019-06-16 15:52
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MissionAcceptVO extends CoreMissionAccept {

	/**
	 * 当前任务如果是不合格状态，是否可以申诉
	 */
	private Boolean canComplaint;

	/**
	 * 申诉结果，如果没申诉过，为null
	 */
	private Boolean complaintResult;
}
