package com.leemon.wushiwan.controller;

import com.leemon.wushiwan.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-18 11:47
 **/

/**
 * 继承了BaseController的，默认里面所有都是前端接口，后端接口只需要复写@PreAuthorize即可
 *
 * @param <T>
 */
@Slf4j
public class NoAuthBaseController<T extends BaseEntity> extends CommonController<T> {


}
