package com.leemon.wushiwan.system.vo;

import com.leemon.wushiwan.system.entity.SysMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenuTree extends SysMenu {

	private List<SysMenuTree> children;

	public SysMenuTree(SysMenu sysMenu) {
		this.setChildren(new ArrayList<>());
		BeanUtils.copyProperties(sysMenu, this);
	}
}
