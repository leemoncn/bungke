package com.leemon.wushiwan.system.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.leemon.wushiwan.system.entity.SysMenu;
import com.leemon.wushiwan.system.entity.SysRoleMenu;
import com.leemon.wushiwan.system.service.ISysMenuService;
import com.leemon.wushiwan.system.service.ISysRoleMenuService;
import com.leemon.wushiwan.system.vo.SysMenuTree;
import com.leemon.wushiwan.system.vo.TreeModel;
import com.leemon.wushiwan.util.ConvertUtils;
import com.leemon.wushiwan.util.UserUtil;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.leemon.wushiwan.controller.BaseController;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@Slf4j
@RestController
@RequestMapping("/sys-menu")
public class SysMenuController extends BaseController {

	private final ISysMenuService sysMenuService;

	private final ISysRoleMenuService sysRoleMenuService;

	@Autowired
	public SysMenuController(ISysMenuService sysMenuService, ISysRoleMenuService sysRoleMenuService) {
		this.sysMenuService = sysMenuService;
		this.sysRoleMenuService = sysRoleMenuService;
	}


	/**
	 * 加载数据节点
	 *
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/list")
	public List<SysMenuTree> list() {
		LambdaQueryWrapper<SysMenu> query = new LambdaQueryWrapper<>();
		query.orderByAsc(SysMenu::getSort);
		List<SysMenu> list = sysMenuService.list(query);
		return getSysMenuTreeList(list);
	}


	/**
	 * 查询用户的权限
	 *
	 * @return
	 */
	@PreAuthorize("allPermitted()")
	@RequestMapping(value = "/queryByUser")
	public Object queryByUser() {
		List<SysMenu> list = sysMenuService.queryByUserId(UserUtil.getSysUser().getId());
		List<String> buttonPermissionList = new ArrayList<>();
		List<String> roleList = UserUtil.getRoleNames();
		for (String s : roleList) {//拥有多种角色
			if ("admin".equals(s)) {//如果是管理员，只需要添加管理员一个就可以
				buttonPermissionList.clear();
				buttonPermissionList.add("admin");
				break;
			}
			List<SysRoleMenu> l = sysRoleMenuService.querySysRoleMenuByRoleName(s);
			for (SysRoleMenu srm : l) {//每种角色拥有的权限，最大的权限
				if (!Strings.isNullOrEmpty(srm.getAddPermission())) {
					buttonPermissionList.add(srm.getAddPermission());
				}
				if (!Strings.isNullOrEmpty(srm.getEditPermission())) {
					buttonPermissionList.add(srm.getEditPermission());
				}
				if (!Strings.isNullOrEmpty(srm.getDeletePermission())) {
					buttonPermissionList.add(srm.getDeletePermission());
				}
			}
		}
		Map<String, Object> result = new HashMap<>(2);
		result.put("menus", getSysMenuTreeList(list));
		result.put("buttonPermissionList", buttonPermissionList);
		return result;
	}


	@RequestMapping(value = "/add")
	@PreAuthorize("hasAuthority('admin')")
	public Object add(@RequestBody @Valid SysMenu menu) {
		sysMenuService.addSysMenu(menu);
		return null;
	}

	@RequestMapping(value = "/edit")
	@PreAuthorize("hasAuthority('admin')")
	public Object edit(@RequestBody @Valid SysMenu menu) {
		sysMenuService.editSysMenu(menu);
		return null;
	}

	@RequestMapping(value = "/delete")
	@PreAuthorize("hasAuthority('admin')")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		sysMenuService.deletePermissionLogical(idRequest.getId());
		return null;
	}


	@RequestMapping(value = "/deleteBatch")
	@PreAuthorize("hasAuthority('admin')")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		String[] arr = idsRequest.getIds().split(",");
		for (String id : arr) {
			if (ConvertUtils.isNotEmpty(id)) {
				sysMenuService.deletePermissionLogical(Integer.parseInt(id));
			}
		}
		return null;
	}

	/**
	 * 获取全部的权限树
	 *
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/queryTreeList")
	public Object queryTreeList() {
		//全部权限ids
		List<Integer> ids = new ArrayList<>();
		LambdaQueryWrapper<SysMenu> query = new LambdaQueryWrapper<>();
		query.orderByAsc(SysMenu::getSort);
		List<SysMenu> list = sysMenuService.list(query);
		for (SysMenu sysPer : list) {
			ids.add(sysPer.getId());
		}
		List<TreeModel> treeList = new ArrayList<>();
		getTreeModelList(treeList, list, null);

		Map<String, Object> resMap = new HashMap<>(2);
		resMap.put("treeList", treeList); //全部树节点数据
		resMap.put("ids", ids);//全部树ids
		return resMap;
	}

	/**
	 * 查询角色授权
	 *
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/queryRolePermission")
	public Object queryRolePermission(@RequestParam(name = "roleId") Integer roleId) {
		List<SysRoleMenu> list = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().lambda().eq(SysRoleMenu::getRoleId, roleId));
		return list.stream().map(sysRoleMenu -> String.valueOf(sysRoleMenu.getMenuId())).collect(Collectors.toList());
	}

	/**
	 * 保存角色授权
	 *
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/saveRolePermission")
	public Object saveRolePermission(@RequestBody JSONObject json) {
		Integer roleId = json.getInteger("roleId");
		String permissionIds = json.getString("permissionIds");
		sysRoleMenuService.saveRoleMenu(roleId, permissionIds);
		return null;
	}

	private List<SysMenuTree> getSysMenuTreeList(List<SysMenu> list) {
		List<SysMenuTree> treeList = new ArrayList<>();
		for (SysMenu menu : list) {//先添加所有的跟路径节点
			Integer parentId = menu.getParentId();
			if (parentId == 0) {//根路径节点
				SysMenuTree parentTree = new SysMenuTree(menu);
				treeList.add(parentTree);
				getTreeList(list, parentTree);
			}
		}
		return treeList;
	}

	private void getTreeList(List<SysMenu> menuList, SysMenuTree parentTree) {
		for (SysMenu sysMenu : menuList) {
			if (sysMenu.getParentId().equals(parentTree.getId())) {
				SysMenuTree smt = new SysMenuTree(sysMenu);
				parentTree.getChildren().add(smt);
				getTreeList(menuList, smt);
			}
		}

//		for (SysMenu menu : menuList) {
//			Integer parentId = menu.getParentId();
//			if (parentId == 0) {//根路径节点
//				treeList.add(new SysMenuTree(menu));
//			}else{
//
//			}
//		}
//		for (SysMenu menu : menuList) {
//			Integer parentId = menu.getParentId();
//			SysMenuTree tree = new SysMenuTree(menu);
//			if (temp == null && parentId == null) {
//				treeList.add(tree);
//				if (!tree.getSysMenu().getIsLeaf()) {
//					getTreeList(treeList, menuList, tree);
//				}
//			} else if (temp != null && parentId != null && parentId.equals(temp.getSysMenu().getId())) {
//				temp.getChildren().add(tree);
//				if (!tree.getSysMenu().getIsLeaf()) {
//					getTreeList(treeList, menuList, tree);
//				}
//			}
//		}
	}

	private void getTreeModelList(List<TreeModel> treeList, List<SysMenu> metaList, TreeModel temp) {
		for (SysMenu permission : metaList) {
			Integer tempPid = permission.getParentId();
			TreeModel tree = new TreeModel(permission);
//			if (temp == null && ConvertUtils.isEmpty(tempPid)) {
//				treeList.add(tree);
//				if (!permission.getIsLeaf()) {
//					getTreeModelList(treeList, metaList, tree);
//				}
//			} else if (temp != null && tempPid != null && tempPid.equals(temp.getKey())) {
//				temp.getChildren().add(tree);
//				if (!permission.getIsLeaf()) {
//					getTreeModelList(treeList, metaList, tree);
//				}
//			}
		}
	}
//
//	/**
//	 * 获取菜单JSON数组
//	 *
//	 * @param jsonArray
//	 * @param menuList
//	 * @param parentJson
//	 */
//	private void getPermissionJsonArray(JSONArray jsonArray, List<SysMenu> menuList, JSONObject parentJson) {
//		for (SysMenu menu : menuList) {
//			if (menu.getMenuType() == null) {
//				continue;
//			}
//			Integer parentId = menu.getParentId();
//			JSONObject json = getPermissionJsonObject(menu);
//			if (parentJson == null && parentId == null) {//根菜单路径
//				jsonArray.add(json);
////				if (!menu.getIsLeaf()) {
//				//不是叶子节点的，要递归找叶子节点，是叶子节点的，要递归找子button
//				getPermissionJsonArray(jsonArray, menuList, json);
////				}
//			} else if (parentJson != null && parentId != null && parentId.equals(parentJson.getInteger("id"))) {
//				if (menu.getMenuType() == SysMenuType.BUTTON) {
//					JSONObject metaJson = parentJson.getJSONObject("meta");
//					if (metaJson.containsKey("permissionList")) {
//						metaJson.getJSONArray("permissionList").add(json);
//					} else {
//						JSONArray permissionList = new JSONArray();
//						permissionList.add(json);
//						metaJson.put("permissionList", permissionList);
//					}
//
//				} else if (menu.getMenuType() == SysMenuType.CHILD_MENU) {
//					if (parentJson.containsKey("children")) {
//						parentJson.getJSONArray("children").add(json);
//					} else {
//						JSONArray children = new JSONArray();
//						children.add(json);
//						parentJson.put("children", children);
//					}
//
////					if (!menu.getIsLeaf()) {
//					getPermissionJsonArray(jsonArray, menuList, json);
////					}
//				}
//			}
//
//
//		}
//	}
//
//	private JSONObject getPermissionJsonObject(SysMenu permission) {
//		JSONObject json = new JSONObject();
//		//类型(0：一级菜单 1：子菜单  2：按钮)
//		if (permission.getMenuType() == SysMenuType.BUTTON) {
//			json.put("permission", permission.getPermission());
//			json.put("describe", permission.getName());
//		} else if (permission.getMenuType() == SysMenuType.ROOT_MENU || permission.getMenuType() == SysMenuType.CHILD_MENU) {
//			json.put("id", permission.getId());
//			if (permission.getHref() != null && (permission.getHref().startsWith("http://") || permission.getHref().startsWith("https://"))) {
//				json.put("path", MD5Util.MD5Encode(permission.getHref(), "utf-8"));
//			} else {
//				json.put("path", permission.getHref());
//			}
//
//			//重要规则：路由name (通过URL生成路由name,路由name供前端开发，页面跳转使用)
//			String componentName = permission.getComponentName();
//			json.put("name", Strings.isNullOrEmpty(componentName) ? urlToRouteName(permission.getHref()) : componentName);
//
//			//是否隐藏路由，默认都是显示的
//			if (!permission.getIsShow()) {
//				json.put("hidden", true);
//			}
//			//聚合路由
//			if (permission.getAlwaysShow() != null && permission.getAlwaysShow()) {
//				json.put("alwaysShow", true);
//			}
//			json.put("component", permission.getComponent());
//			JSONObject meta = new JSONObject();
//			meta.put("title", permission.getName());
//			if (ConvertUtils.isEmpty(permission.getParentId())) {
//				//一级菜单跳转地址
//				json.put("redirect", permission.getRedirect());
//				meta.put("icon", ConvertUtils.getString(permission.getIcon(), ""));
//			} else {
//				meta.put("icon", ConvertUtils.getString(permission.getIcon(), ""));
//			}
//			if (permission.getHref() != null && (permission.getHref().startsWith("http://") || permission.getHref().startsWith("https://"))) {
//				meta.put("url", permission.getHref());
//			}
//			json.put("meta", meta);
//		}
//
//		return json;
//	}

	/**
	 * 通过URL生成路由name（去掉URL前缀斜杠，替换内容中的斜杠‘/’为-）
	 * 举例： URL = /isystem/role
	 * RouteName = isystem-role
	 *
	 * @return
	 */
	private String urlToRouteName(String url) {
		if (ConvertUtils.isNotEmpty(url)) {
			if (url.startsWith("/")) {
				url = url.substring(1);
			}
			url = url.replace("/", "-");
			return url;
		} else {
			return null;
		}
	}
}
