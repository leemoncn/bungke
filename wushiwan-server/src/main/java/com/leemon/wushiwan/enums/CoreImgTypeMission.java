package com.leemon.wushiwan.enums;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description: 创建任务时的图片类型
 * @author: limeng
 * @create: 2019-05-19 10:40
 **/
public enum CoreImgTypeMission {
	SAMPLE,//创建任务时审核图样的图片
	EXPLAIN,//创建任务时操作说明的图片
	VERIFY_IMG,//提交任务时，任务截图,用于发布任务的人进行审核
	ADVICE,//建议
	REVIEW_CHAT,//商家在审核任务的时候，与上传者进行聊天
}
