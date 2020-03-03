package com.leemon.wushiwan.vo;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.leemon.wushiwan.enums.LoginRegisterType;
import com.leemon.wushiwan.util.LoginUtil;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description: 注册请求
 * @author: leemon
 * @create: 2019-10-31 14:50
 **/
@Data
public class RegisterRequest {
	@Data
	public static class RegisterData {
		@NotBlank
		private String account;
		private String password;
		@NotNull
		private LoginRegisterType typePropertyId;
		private String data;

		public String getNicknameFromData() {
			if (Strings.isNullOrEmpty(data)) {
				return null;
			}
			if (typePropertyId == LoginRegisterType.WECHAT) {
				return LoginUtil.getNicknameFromDataForWechat(data);
			}
			return null;
		}

		public String getHeadImgUrlFromData() {
			if (Strings.isNullOrEmpty(data)) {
				return null;
			}
			if (typePropertyId == LoginRegisterType.WECHAT) {
				return LoginUtil.getHeadImgUrlFromDataForWechat(data);
			}
			return null;
		}
	}

	private RegisterData mobileRegisterData;
	private RegisterData otherRegisterData;
	private String uuid;
}
