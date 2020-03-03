package com.leemon.wushiwan.system.vo;

import com.leemon.wushiwan.system.entity.SysMenu;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class TreeModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer key;
	private String title;
	private String icon;
	private Integer parentId;
	private String label;
	private Integer value;
	private List<TreeModel> children;


	public TreeModel(SysMenu menu) {
		setKey(menu.getId())
				.setIcon(menu.getIcon())
				.setParentId(menu.getParentId())
				.setTitle(menu.getName())
				.setValue(menu.getId());
//		if (!menu.getIsLeaf()) {
			setChildren(new ArrayList<>());
//		}
	}
}
