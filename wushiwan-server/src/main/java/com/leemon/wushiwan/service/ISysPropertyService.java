package com.leemon.wushiwan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leemon.wushiwan.entity.SysProperty;
import com.leemon.wushiwan.enums.base.PropertyType;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leemon
 * @since 2019-03-20
 */
public interface ISysPropertyService extends IService<SysProperty> {

	PropertyType getPropertyTypeById(Integer id);
}
