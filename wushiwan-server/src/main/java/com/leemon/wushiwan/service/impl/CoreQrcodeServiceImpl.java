package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.entity.CoreQrcode;
import com.leemon.wushiwan.mapper.CoreQrcodeMapper;
import com.leemon.wushiwan.service.ICoreQrcodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Service
public class CoreQrcodeServiceImpl extends ServiceImpl<CoreQrcodeMapper, CoreQrcode> implements ICoreQrcodeService {

	private final Config config;
	private final CoreQrcodeMapper qrcodeMapper;

	@Autowired
	public CoreQrcodeServiceImpl(Config config, CoreQrcodeMapper qrcodeMapper) {
		this.config = config;
		this.qrcodeMapper = qrcodeMapper;
	}

	@Override
	public CoreQrcode addNewCoreQrcode(Integer userId) {
		CoreQrcode newCq = new CoreQrcode();
		LocalDateTime ldt = LocalDateTime.now();
		ldt = ldt.plusDays(config.getQrcodeTimeoutDay());
		newCq.setExpireTime(ldt);
		newCq.setUserId(userId);
		newCq.setUuid(UUID.randomUUID().toString().replace("-", ""));
		this.save(newCq);
		return newCq;
	}

	/**
	 * 通过分享二维码分享出去的uuid获取父级用户
	 *
	 * @param uuid
	 * @return
	 */
	@Override
	public SysUser getSysUserByUUID(String uuid) {
		if (Strings.isNullOrEmpty(uuid)) {
			return null;
		}
		return qrcodeMapper.selectUserByUUID(uuid);
	}
}
