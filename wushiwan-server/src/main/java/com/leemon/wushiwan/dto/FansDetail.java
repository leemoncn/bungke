package com.leemon.wushiwan.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: limeng
 * @create: 2019-10-26 21:31
 **/
@Data
public class FansDetail {
	//昵称
	private String name;
	//关注或者被关注的时间
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime time;
	private String headImgUrl;
	private Integer id;
}
