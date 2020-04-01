/*
 Navicat Premium Data Transfer

 Source Server         : 帮客正式
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : 111.231.134.217:3306
 Source Schema         : freelancer

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 01/04/2020 21:47:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户自增id',
  `parent_id` int(10) unsigned NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '菜单标题',
  `sort` decimal(10,0) NOT NULL COMMENT '排序',
  `href` varchar(255) DEFAULT NULL COMMENT '链接',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `is_show` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在菜单中显示',
  `component` varchar(255) DEFAULT NULL COMMENT '组件的路径',
  `component_name` varchar(255) DEFAULT NULL COMMENT '前端组件的名字',
  `permission` varchar(200) DEFAULT NULL COMMENT '权限标识',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_sys_menu_permission` (`permission`) USING BTREE,
  KEY `idx_sys_menu_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8 COMMENT='菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` VALUES (23, 0, '系统管理', 50, '/manager', NULL, 1, '', '', 'sys_manager', 0, NULL, '2019-04-28 20:55:12', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (24, 23, '菜单管理', 20, '/menu', NULL, 1, 'system/menu/index', 'SysMenuManagement', 'sys_menu', 0, NULL, '2019-04-28 20:56:11', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (28, 0, '首页集合', 50, '/homes', NULL, 1, NULL, NULL, NULL, 1, NULL, '2019-04-28 21:19:38', NULL, '2019-06-27 09:05:58');
INSERT INTO `sys_menu` VALUES (30, 23, '用户管理', 30, '/user', NULL, 1, 'system/user/index', 'SysUserManagement', 'sys_user', 0, NULL, '2019-04-28 21:36:54', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (31, 0, '首页1', 70, '/home1', NULL, 1, 'Home1', 'Home1', 'sys_home1', 1, NULL, '2019-04-29 16:37:14', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (32, 23, '角色管理', 40, '/role', NULL, 1, 'system/role/index', 'SysRoleManagement', 'sys_role', 0, NULL, '2019-04-29 21:42:52', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (34, 28, '首页2', 60, '/home2', NULL, 1, 'Home2', 'Home2', '', 1, NULL, '2019-05-05 21:41:55', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (35, 23, '属性管理', 50, '/property', NULL, 1, 'SysProperty/index', 'SysPropertyManagement', 'sys_property', 0, NULL, '2019-05-06 10:44:20', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (36, 0, '任务管理', 60, '/mission', NULL, 1, '', '', 'core_mission', 0, NULL, '2019-05-09 20:32:10', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (37, 36, '任务规则', 10, '/rule', '', 1, 'CoreMissionRule/index', 'CoreMissionRuleManagement', 'core_mission_rule', 0, NULL, '2019-05-09 20:34:15', NULL, '2019-11-27 21:16:48');
INSERT INTO `sys_menu` VALUES (38, 36, '任务完成', 20, '/proceed', '', 1, 'CoreMissionAccept/index', 'CoreMissionAcceptManagement', 'core_mission_accept', 0, NULL, '2019-05-19 22:39:41', NULL, '2019-11-27 21:16:43');
INSERT INTO `sys_menu` VALUES (39, 36, '任务审核', 30, '/publish', '', 1, 'CoreMission/index', 'CoreMissionManagement', 'core_mission_publish', 0, NULL, '2019-05-19 22:44:57', NULL, '2019-11-27 21:16:34');
INSERT INTO `sys_menu` VALUES (40, 36, '任务详情', 40, '/detail', NULL, 1, 'CoreMissionDetail/index', 'CoreMissionDetailManagement', 'core_mission_detail', 0, NULL, '2019-05-19 22:50:33', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (43, 36, '任务申诉', 50, '/complaint', NULL, 1, 'CoreMissionComplaint/index', 'CoreMissionComplaintManagement', 'core_mission_complaint', 0, NULL, '2019-06-16 17:21:19', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (44, 0, '财务管理', 70, '/finance', NULL, 1, '', '', 'core_finance', 0, NULL, '2019-06-16 21:47:13', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (45, 44, '资金变动', 10, '/currency', NULL, 1, 'CoreCurrencyChange/index', 'CoreCurrencyChangeManagement', 'core_currency_change', 0, NULL, '2019-06-16 21:52:15', NULL, '2019-06-16 21:52:15');
INSERT INTO `sys_menu` VALUES (46, 0, '合作商管理', 80, '/partner', NULL, 1, '', '', 'partner', 0, NULL, '2019-06-27 20:55:40', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (49, 46, '合作商配置', 10, '/config', NULL, 1, 'CorePartner/index', 'CorePartnerManagement', 'core_partner', 0, NULL, '2019-06-27 20:59:07', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (50, 46, '合作商记录', 20, '/record', NULL, 1, 'CorePartnerPurchase/index', 'CorePartnerPurchaseManagement', 'core_partner_purchase', 0, NULL, '2019-06-27 21:11:25', NULL, '2019-06-27 21:11:25');
INSERT INTO `sys_menu` VALUES (51, 44, '提现审核', 20, '/withdrawal', NULL, 1, 'FinancialWithdrawal/index', 'FinancialWithdrawalManagement', 'financial_withdrawal', 0, NULL, '2019-06-29 17:57:47', NULL, '2019-06-29 17:57:47');
INSERT INTO `sys_menu` VALUES (52, 0, '抽成配置', 90, '/agency', NULL, 1, 'CoreAgency/index', 'CoreAgencyManagement', 'core_agency', 0, NULL, '2019-06-29 23:04:19', NULL, '2019-08-18 15:19:06');
INSERT INTO `sys_menu` VALUES (53, 23, '版本管理', 60, '/version', NULL, 1, 'CoreVersion/index', 'CoreVersionManagement', 'core_version', 1, NULL, '2019-08-18 15:17:32', NULL, '2019-12-15 11:32:48');
INSERT INTO `sys_menu` VALUES (55, 0, '常见问题', 100, '/question', NULL, 1, 'CoreQuestion/index', 'CoreQuestionManagement', 'core_question', 0, NULL, '2019-10-27 15:04:13', NULL, '2019-10-27 15:04:13');
INSERT INTO `sys_menu` VALUES (56, 0, '资讯管理', 110, '/information', NULL, 1, NULL, NULL, 'information', 0, NULL, '2019-11-14 16:27:33', NULL, '2019-11-14 16:27:33');
INSERT INTO `sys_menu` VALUES (57, 56, 'banner管理', 10, '/banner', NULL, 1, 'CoreBanner/index', 'CoreBannerManagement', 'core_banner', 0, NULL, '2019-11-14 16:29:20', NULL, '2019-11-14 16:29:20');
INSERT INTO `sys_menu` VALUES (58, 56, '公告管理', 20, '/notice', NULL, 1, 'CoreNotice/index', 'CoreNoticeManagement', 'core_notice', 0, NULL, '2019-11-14 16:30:56', NULL, '2019-11-14 16:30:56');
INSERT INTO `sys_menu` VALUES (60, 0, '消息管理', 120, '/notice', NULL, 1, NULL, NULL, 'notice', 0, NULL, '2019-11-27 08:35:09', NULL, '2019-11-27 08:35:09');
INSERT INTO `sys_menu` VALUES (61, 60, '意见建议', 10, '/advice', '', 1, 'SysAdvice/index', 'SysAdviceManagement', 'sys_advice', 0, NULL, '2019-11-27 08:36:15', NULL, '2019-11-27 08:39:33');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
