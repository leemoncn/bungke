package com.leemon.wushiwan.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.leemon.wushiwan.entity.BaseEntity;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
public class CommonController<T extends BaseEntity> {
	protected T getExistEntityById(Serializable id, IService<T> service) {
		T t = service.getById(id);
		if (t == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "查询 id = %d不存在", id);
		}
		return t;
	}

	/**
	 * 一般是后台使用，根据传入的结构体，生成所有不是null的属性的模糊查询
	 *
	 * @param t
	 * @return
	 */
	protected QueryWrapper<T> getLikeQueryWrapper(T t) {
		QueryWrapper<T> queryWrapper = new QueryWrapper<>();
		initLikeQueryWrapper(t, queryWrapper);
		initOrderQueryWrapper(t, queryWrapper);
		return queryWrapper;
	}

	protected QueryWrapper<T> getEqualsQueryWrapper(T t) {
		QueryWrapper<T> queryWrapper = new QueryWrapper<>(t);
		initOrderQueryWrapper(t, queryWrapper);
		return queryWrapper;
	}

	private String getTableColumnName(List<Field> list, String fieldName) {
		for (Field field : list) {
			if (fieldName.equals(field.getName())) {//找到指定字段
				if (field.isAnnotationPresent(TableField.class)) {//是否使用TableField注解
					for (Annotation anno : field.getDeclaredAnnotations()) {//获得字段上所有的注解
						if (anno.annotationType().equals(TableField.class)) {//找到自己的注解
							return ((TableField) anno).value();
						}
					}
				}
			}
		}
		return null;
	}

	private void initLikeQueryWrapper(T t, QueryWrapper<T> queryWrapper) {
		if (t == null) {
			return;
		}
		Map<String, Object> map = Maps.newHashMap();
		BeanMap beanMap = BeanMap.create(t);
		for (Object key : beanMap.keySet()) {
			map.put(key + "", beanMap.get(key));
		}
		List<Field> list = Arrays.asList(t.getClass().getDeclaredFields());
		//TODO 有时间改成通过注解判断这四个属性
		map.forEach((s, o) -> {
			if (!"pageNo".equals(s) && !"pageSize".equals(s) && !"column".equals(s) && !"order".equals(s)) {
				if (o != null) {
					if (o instanceof String) {
						String str = (String) o;
						if (Strings.isNullOrEmpty(str)) {
							return;
						}
					}
					String tableColumnName = getTableColumnName(list, s);
					if (!Strings.isNullOrEmpty(tableColumnName)) {//注解的值
						if (o instanceof IEnum) {//枚举值只能是相等判断，like没意义
							queryWrapper.eq(tableColumnName, ((IEnum) o).getValue());
						} else {
							queryWrapper.like(tableColumnName, o);
						}
					}
				}
			}
		});
	}

	private void initOrderQueryWrapper(T t, QueryWrapper<T> queryWrapper) {
		String column = t.getColumn();
		String order = null;
		if (!Strings.isNullOrEmpty(t.getOrder())) {
			order = t.getOrder().toUpperCase();
		}
		if (!Strings.isNullOrEmpty(column) && !Strings.isNullOrEmpty(order)) {
			String underScoreColumn = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column);
			if ("ASC".equals(order)) {
				queryWrapper.orderByAsc(underScoreColumn);
			} else {
				queryWrapper.orderByDesc(underScoreColumn);
			}
		}
	}
}
