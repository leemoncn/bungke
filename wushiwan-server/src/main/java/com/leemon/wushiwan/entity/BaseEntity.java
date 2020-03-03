package com.leemon.wushiwan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leemon.wushiwan.enums.base.Logic;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: leemon
 * @create: 2019-03-18 11:42
 **/
@Data
@Accessors(chain = true)
public class BaseEntity implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@JSONField(serialize = false, deserialize = false)
	@TableField(value = "creator")
	private String creator;

	@JSONField(deserialize = false, format = "yyyy-MM-dd HH:mm:ss")
	@TableField(value = "create_time")
	private LocalDateTime createTime;

	@JSONField(serialize = false, deserialize = false)
	@TableField(value = "updater")
	private String updater;

	@JSONField(serialize = false, deserialize = false)
	@TableField(value = "update_time")
	private LocalDateTime updateTime;

	@JSONField(serialize = false, deserialize = false)
	@TableField(value = "deleted")
	@TableLogic(value = "0", delval = "1")
	private Logic deleted;

	@TableField(exist = false)
	@JSONField(serialize = false)
	private Integer pageNo = 1;
	@TableField(exist = false)
	@JSONField(serialize = false)
	private Integer pageSize = 10;
	@TableField(exist = false)
	@JSONField(serialize = false)
	private String column = null;
	@TableField(exist = false)
	@JSONField(serialize = false)
	private String order = null;
}
