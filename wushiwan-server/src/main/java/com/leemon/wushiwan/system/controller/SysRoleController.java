package com.leemon.wushiwan.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.leemon.wushiwan.enums.base.ErrorCode;
import com.leemon.wushiwan.exception.LogicException;
import com.leemon.wushiwan.system.entity.SysMenu;
import com.leemon.wushiwan.system.entity.SysRole;
import com.leemon.wushiwan.system.entity.SysRoleMenu;
import com.leemon.wushiwan.system.mapper.SysRoleMenuMapper;
import com.leemon.wushiwan.system.service.ISysMenuService;
import com.leemon.wushiwan.system.service.ISysRoleMenuService;
import com.leemon.wushiwan.system.service.ISysRoleService;
import com.leemon.wushiwan.vo.IdRequest;
import com.leemon.wushiwan.vo.IdsRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.leemon.wushiwan.controller.BaseController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author leemon
 * @since 2019-04-02
 */
@RestController
@RequestMapping("/sys-role")
@Slf4j
public class SysRoleController extends BaseController<SysRole> {

	private final ISysRoleService sysRoleService;
	private final ISysRoleMenuService sysRoleMenuService;
	private final ISysMenuService sysMenuService;
	private final SysRoleMenuMapper sysRoleMenuMapper;

	@Autowired
	public SysRoleController(ISysRoleService sysRoleService, ISysRoleMenuService sysRoleMenuService, ISysMenuService sysMenuService, SysRoleMenuMapper sysRoleMenuMapper) {
		this.sysRoleService = sysRoleService;
		this.sysRoleMenuService = sysRoleMenuService;
		this.sysMenuService = sysMenuService;
		this.sysRoleMenuMapper = sysRoleMenuMapper;
	}

	/**
	 * 分页列表查询
	 *
	 * @param sysRole
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/list")
	public Object queryPageList(@RequestBody @Valid SysRole sysRole) {
		Page<SysRole> page = new Page<>(sysRole.getPageNo(), sysRole.getPageSize());
		return sysRoleService.page(page, getLikeQueryWrapper(sysRole));
	}

	/**
	 * 保存
	 *
	 * @param sysRole
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/add")
	public Object add(@RequestBody @Valid SysRole sysRole) {
		if (sysRole.getId() != null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "保存时不能设置id");
		}
		sysRoleService.saveOrUpdate(sysRole);
		return null;
	}

	/**
	 * 编辑
	 *
	 * @param sysRole
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/edit")
	public Object edit(@RequestBody @Valid SysRole sysRole) {
		if (sysRole.getId() == null) {
			throw new LogicException(ErrorCode.PARAMS_INVALID, "更新时需要传入id");
		}
		boolean ok = sysRoleService.updateById(sysRole);
		if (!ok) {
			log.error("sysRole = {}", sysRole);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SysRole失败");
		}

		return null;
	}

	/**
	 * 通过id删除
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/delete")
	public Object delete(@RequestBody @Valid IdRequest idRequest) {
		SysRole sysRole = getExistEntityById(idRequest.getId(), sysRoleService);
		boolean ok = sysRoleService.removeById(sysRole);
		if (!ok) {
			log.error("sysRole = {}", sysRole);
			throw new LogicException(ErrorCode.SYS_ERROR, "更新SysRole失败");
		}

		return null;
	}

	/**
	 * 批量删除
	 *
	 * @param idsRequest
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/deleteBatch")
	public Object deleteBatch(@RequestBody @Valid IdsRequest idsRequest) {
		sysRoleService.removeByIds(Arrays.asList(idsRequest.getIds().split(",")));
		return null;
	}

	/**
	 * 通过id查询
	 *
	 * @param idRequest
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/queryById")
	public Object queryById(@RequestBody @Valid IdRequest idRequest) {
		return sysRoleService.getById(idRequest.getId());
	}

	@Data
	private static class RolePermissionRequest {
		@NotNull
		private Integer roleId;
	}

	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/role-permission")
	public Object queryRolePermissions(@RequestBody @Valid RolePermissionRequest req) {
		Integer roleId = req.getRoleId();
		QueryWrapper<SysRoleMenu> qw = new QueryWrapper<>();
		qw.lambda().eq(SysRoleMenu::getRoleId, roleId);
		return sysRoleMenuService.list(qw);
	}


	@Data
	private static class RolePermissionFlag {
		private boolean edit;
		private boolean add;
		private boolean delete;
	}

	@Data
	private static class EditRolePermissionRequest {
		@NotNull
		private Integer roleId;
		@NotNull
		private Map<String, RolePermissionFlag> data;
	}


	/**
	 * 编辑用户的权限，先删除这个用户的所有权限，再重新添加这个用户的权限
	 *
	 * @param req
	 * @return
	 */
	@PreAuthorize("hasAuthority('admin')")
	@RequestMapping(value = "/edit-role-permission")
	public Object editRolePermissions(@RequestBody @Valid EditRolePermissionRequest req) {
		Integer roleId = req.getRoleId();
		List<SysRoleMenu> list = new ArrayList<>();
		for (String key : req.getData().keySet()) {
			if ("roleId".equals(key)) {
				continue;
			}
			RolePermissionFlag r = req.getData().get(key);
			Integer menuId = Integer.parseInt(key);
			SysMenu menu = sysMenuService.getById(menuId);
			if (menu != null) {
				SysRoleMenu sysRoleMenu = new SysRoleMenu();
				sysRoleMenu.setRoleId(roleId);
				sysRoleMenu.setMenuId(Integer.parseInt(key));
				String permission = menu.getPermission();
				if (!Strings.isNullOrEmpty(permission)) {//这个菜单不需要权限
//					throw new LogicException(ErrorCode.SYS_ERROR, "menuId = %d,permission为空", menuId);
					if (r.isAdd()) {
						sysRoleMenu.setAddPermission(permission + "_add");
					}
					if (r.isEdit()) {
						sysRoleMenu.setEditPermission(permission + "_edit");
					}
					if (r.isDelete()) {
						sysRoleMenu.setDeletePermission(permission + "_delete");
					}
				}
				list.add(sysRoleMenu);
			}

		}
		if (list.size() != 0) {
			// 先删除再添加
			sysRoleMenuMapper.deleteAllByRoleId(roleId);
			sysRoleMenuService.saveBatch(list);
		}
		return null;
	}

//	/**
//	 * 分页列表查询
//	 *
//	 * @param role
//	 * @param pageNo
//	 * @param pageSize
//	 * @param req
//	 * @return
//	 */
//	@RequestMapping(value = "/list")
//	public Object queryPageList(SysRole role, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
//								@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
//								HttpServletRequest req) {
//		QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>(role);
//		Page<SysRole> page = new Page<>(pageNo, pageSize);
//		//排序逻辑 处理
//		String column = req.getParameter("column");
//		String order = req.getParameter("order");
//		if (ConvertUtils.isNotEmpty(column) && ConvertUtils.isNotEmpty(order)) {
//			if ("asc".equals(order)) {
//				queryWrapper.orderByAsc(ConvertUtils.camelToUnderline(column));
//			} else {
//				queryWrapper.orderByDesc(ConvertUtils.camelToUnderline(column));
//			}
//		}
//		//TODO 过滤逻辑处理
//		//TODO begin、end逻辑处理
//		//TODO 一个强大的功能，前端传一个字段字符串，后台只返回这些字符串对应的字段
//		//创建时间/创建人的赋值
//		IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
//		log.info("查询当前页：" + pageList.getCurrent());
//		log.info("查询当前页数量：" + pageList.getSize());
//		log.info("查询结果数量：" + pageList.getRecords().size());
//		log.info("数据总数：" + pageList.getTotal());
//		return pageList;
//	}
//
//	/**
//	 * 添加
//	 *
//	 * @param role
//	 * @return
//	 */
//	@RequestMapping(value = "/add")
//	public Object add(@RequestBody SysRole role) {
//		sysRoleService.save(role);
//		return null;
//	}
//
//	/**
//	 * 编辑
//	 *
//	 * @param role
//	 * @return
//	 */
//	@RequestMapping(value = "/edit")
//	@PreAuthorize("hasRole('admin')")
//	public Object edit(@RequestBody SysRole role) {
//		SysRole sysrole = sysRoleService.getById(role.getId());
//		if (sysrole == null) {
//			throw new LogicException("未找到对应实体");
//		} else {
//			boolean ok = sysRoleService.updateById(role);
//			if (!ok) {
//				throw new LogicException(ErrorCode.SYS_ERROR, "未找到对应实体");
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 通过id删除
//	 *
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping(value = "/delete")
//	@PreAuthorize("hasRole('admin')")
//	public Object delete(@RequestParam(name = "id") Integer id) {
//		sysRoleService.removeById(id);
//		return null;
//	}
//
//	/**
//	 * 批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@RequestMapping(value = "/deleteBatch")
//	@PreAuthorize("hasRole('admin')")
//	public Object deleteBatch(@RequestParam(name = "ids") String ids) {
//		if (ids == null || "".equals(ids.trim())) {
//			throw new LogicException(ErrorCode.PARAMS_INVALID, "参数不识别！");
//		}
//		sysRoleService.removeByIds(Arrays.stream(ids.split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList()));
//		return null;
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping(value = "/queryById")
//	public Object queryById(@RequestParam(name = "id") Integer id) {
//		return sysRoleService.getById(id);
//	}
//
//	@RequestMapping(value = "/queryall")
//	public Object queryAll() {
//		List<SysRole> list = sysRoleService.list();
//		if (list == null || list.size() <= 0) {
//			throw new LogicException(ErrorCode.PARAMS_INVALID, "未找到角色信息");
//		}
//		return list;
//	}
//
//	/**
//	 * 校验角色编码唯一
//	 */
//	@RequestMapping(value = "/checkRoleCode")
//	public Object checkUsername(Integer id, String roleCode) {
//		log.info("--验证角色编码是否唯一---id:" + id + "--roleCode:" + roleCode);
//		SysRole role = null;
//		if (ConvertUtils.isNotEmpty(id)) {
//			role = sysRoleService.getById(id);
//		}
//		if (role == null) {
//			throw new LogicException(ErrorCode.PARAMS_INVALID, "角色ID不存在");
//		}
//		SysRole newRole = sysRoleService.getOne(new QueryWrapper<SysRole>().lambda().eq(SysRole::getRoleName, roleCode));
//		if (newRole != null && !id.equals(newRole.getId())) {
//			throw new LogicException(ErrorCode.PARAMS_INVALID, "角色编码已存在");
//		}
//		return null;
//	}
}
