package com.leemon.wushiwan.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.entity.CoreMissionDetail;
import com.leemon.wushiwan.enums.MissionPublishType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: limeng
 * @create: 2019-05-13 21:34
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MissionDetail extends CoreMissionDetail {
	private Integer missionId;
	private String reason;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime publishTime;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime topEndTime;
	@TableField("status_property_id")
	private MissionPublishType statusPropertyId;
	/**
	 * 可以接受的任务数量，可接受的任务数量为，剩余任务数量-正在进行中的任务数量-审核中的任务数-不合格的任务但还没有超过12小时的重新上传期的任务书
	 */
	private Integer acceptableMissionCount;
}
