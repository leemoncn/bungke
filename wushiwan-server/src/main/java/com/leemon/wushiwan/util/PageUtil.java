package com.leemon.wushiwan.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @description:
 * @author: limeng
 * @create: 2019-05-11 15:54
 **/
public class PageUtil {

	public static Page convertPageRecords(Page oldPage, List newRecords) {
		Page page = new Page(oldPage.getCurrent(), oldPage.getSize(), oldPage.getTotal());
		return page.setRecords(newRecords);
	}
}
