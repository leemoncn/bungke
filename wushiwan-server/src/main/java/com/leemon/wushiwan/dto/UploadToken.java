package com.leemon.wushiwan.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: 七牛云上传凭证
 * @author: leemon
 * @create: 2019-11-11 16:05
 **/
@Data
public class UploadToken implements Serializable {
	//上传凭证
	private String token;
	//文件名
	private String fileName;
	//是否收到了七牛云上传完成的回调
	@JSONField(serialize = false)
	private Boolean isReceiveCallback;
	//这个凭证的上传过期时间，过了这个时间，这个凭证就不能用了
	@JSONField(serialize = false)
	private LocalDateTime expirationTime;
}
