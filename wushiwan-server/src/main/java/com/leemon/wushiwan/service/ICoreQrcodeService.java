package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.CoreQrcode;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.system.entity.SysUser;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ICoreQrcodeService extends IService<CoreQrcode> {
	CoreQrcode addNewCoreQrcode(Integer userId);

	/**
	 * 通过分享二维码分享出去的uuid获取父级用户
	 *
	 * @param uuid
	 * @return
	 */
	SysUser getSysUserByUUID(String uuid);
}
