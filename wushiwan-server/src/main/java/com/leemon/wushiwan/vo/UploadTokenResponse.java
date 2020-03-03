package com.leemon.wushiwan.vo;

import com.leemon.wushiwan.dto.UploadToken;
import lombok.Data;

import java.util.List;

/**
 * @description: 请求上传文件，获取七牛云上传凭证
 * @author: leemon
 * @create: 2019-11-11 16:03
 **/
@Data
public class UploadTokenResponse {
	//存在redis里面的key
	private String key;
	private List<UploadToken> list;
}
