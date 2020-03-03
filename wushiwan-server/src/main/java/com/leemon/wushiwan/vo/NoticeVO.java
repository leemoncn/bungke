package com.leemon.wushiwan.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.leemon.wushiwan.enums.NoticeType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: limeng
 * @create: 2019-06-30 22:52
 **/
@Data
public class NoticeVO implements Serializable {
	@TableField("type_property_id")
	private NoticeType typePropertyId;

	@TableField("msg1")
	private String msg1;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@TableField(value = "create_time")
	private LocalDateTime createTime;
}
