package com.leemon.wushiwan.service;

import com.leemon.wushiwan.entity.CoreImg;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.enums.CoreImgTypeMission;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ICoreImgService extends IService<CoreImg> {

	/**
	 * 将保存到七牛云的图片key存储到core_img表中
	 *
	 * @param userId
	 * @param dataId
	 * @param type
	 * @param imgKeyList
	 */
	void saveImgListToDB(Integer userId, Integer dataId, CoreImgTypeMission type, List<String> imgKeyList);

	/**
	 * 查询某个用户的某个dataId的所有图片
	 *
	 * @param dataId
	 * @param userId
	 */
	List<CoreImg> queryUserAllImg(Integer dataId, Integer userId, Integer type);
}
