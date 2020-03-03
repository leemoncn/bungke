package com.leemon.wushiwan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leemon.wushiwan.config.Config;
import com.leemon.wushiwan.controller.IndexController;
import com.leemon.wushiwan.entity.BaseModel;
import com.leemon.wushiwan.entity.CoreImg;
import com.leemon.wushiwan.enums.CoreImgTypeMission;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.mapper.CoreImgMapper;
import com.leemon.wushiwan.service.ICoreImgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leemon.wushiwan.util.Base64Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
@Slf4j
@Service
public class CoreImgServiceImpl extends ServiceImpl<CoreImgMapper, CoreImg> implements ICoreImgService {

	private final Config config;

	public CoreImgServiceImpl(Config config) {
		this.config = config;
	}

	/**
	 * 将保存到七牛云的图片key存储到core_img表中
	 *
	 * @param userId
	 * @param dataId
	 * @param type
	 * @param imgKeyList
	 */
	@Override
	public void saveImgListToDB(Integer userId, Integer dataId, CoreImgTypeMission type, List<String> imgKeyList) {
		if (imgKeyList == null || imgKeyList.size() == 0) {
			return;
		}
		List<CoreImg> imgListToDatabase = new ArrayList<>();
		for (int i = 0; i < imgKeyList.size(); i++) {
			String fileName = imgKeyList.get(i);
			CoreImg coreImg = new CoreImg();
			coreImg.setPath(fileName);
			coreImg.setUserId(userId);
			coreImg.setDataId(dataId);
			coreImg.setType(type.ordinal());
			imgListToDatabase.add(coreImg);
		}
		//保存数据库
		saveBatch(imgListToDatabase);
	}

	@Override
	public List<CoreImg> queryUserAllImg(Integer dataId, Integer userId, Integer type) {
		QueryWrapper<CoreImg> qw = new QueryWrapper<>();
		qw.lambda().eq(CoreImg::getDataId, dataId);
		qw.lambda().eq(CoreImg::getUserId, userId);
		qw.lambda().eq(CoreImg::getType, type);
		return this.list(qw);
	}
}
