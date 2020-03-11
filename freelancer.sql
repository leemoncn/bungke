/*
 Date: 11/03/2020 20:55:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for core_agency
-- ----------------------------
DROP TABLE IF EXISTS `core_agency`;
CREATE TABLE `core_agency` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `recharge_by_app` int(11) NOT NULL DEFAULT '0' COMMENT '充值任务币时平台给出的返现提成，百分比，乘以100',
  `withdraw_by_user` int(11) NOT NULL DEFAULT '0' COMMENT '提现收入时用户从自己的钱拿出来给上级返现的比例',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_banner
-- ----------------------------
DROP TABLE IF EXISTS `core_banner`;
CREATE TABLE `core_banner` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `img` varchar(255) DEFAULT NULL COMMENT 'banner图片地址',
  `html` mediumtext COMMENT '点击后跳转连接，可以是html代码，也可以是网址',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `usable` tinyint(1) DEFAULT NULL COMMENT '是否可用',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_currency_change
-- ----------------------------
DROP TABLE IF EXISTS `core_currency_change`;
CREATE TABLE `core_currency_change` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户自增id',
  `record` int(11) DEFAULT NULL COMMENT '货币变动值，可以正数也可以负数',
  `type_property_id` int(11) DEFAULT NULL COMMENT '货币的类型',
  `reason_property_id` int(11) DEFAULT NULL COMMENT '货币变化的原因',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=948 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Table structure for core_deposit_purchase
-- ----------------------------
DROP TABLE IF EXISTS `core_deposit_purchase`;
CREATE TABLE `core_deposit_purchase` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `deposit` int(11) DEFAULT NULL COMMENT '保证金金额,单位分',
  `refund_time` timestamp NULL DEFAULT NULL COMMENT '申退保证金日期',
  `refund_finish_time` timestamp NULL DEFAULT NULL COMMENT '申退保证金完成',
  `usable` tinyint(1) NOT NULL COMMENT '当前保证金是否可用',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_img
-- ----------------------------
DROP TABLE IF EXISTS `core_img`;
CREATE TABLE `core_img` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` int(11) DEFAULT NULL COMMENT '具体的ID,比如发布任务是core_mission_detail,提交任务是core_mission_accept的id,意见反馈是sys_advice的id',
  `user_id` int(11) NOT NULL COMMENT '任务发布者的ID，或者任务接取者的ID',
  `path` varchar(255) NOT NULL COMMENT '图片的本地路径',
  `type` int(11) DEFAULT NULL COMMENT '此字段主要是为了区分同一个id却有多个需要区分的图片，比如创建任务，需要上传审核图片和操作图片',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `data_id` (`data_id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1315 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_login_auth
-- ----------------------------
DROP TABLE IF EXISTS `core_login_auth`;
CREATE TABLE `core_login_auth` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户表，用户ID',
  `account` varchar(100) DEFAULT NULL COMMENT '登陆账号，如果是三方登陆的话，应该是三方的id。比如微信的是openid',
  `password` varchar(100) DEFAULT NULL COMMENT '登陆的密码，存储的是加密后的密码。如果是三方登陆的话，有可能是token，',
  `type_property_id` int(11) DEFAULT NULL COMMENT '登陆类型，属性表的id',
  `data` varchar(1500) DEFAULT NULL COMMENT '某些三方登录返回的json',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `account` (`account`,`type_property_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=136 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_mission
-- ----------------------------
DROP TABLE IF EXISTS `core_mission`;
CREATE TABLE `core_mission` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mission_detail_id` int(10) unsigned NOT NULL,
  `user_id` int(11) NOT NULL COMMENT '任务发布者的id',
  `publish_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务发布时间',
  `status_property_id` int(11) DEFAULT NULL COMMENT '任务当前状态（被封、数量完成、到期等各种情况）\r\n29被封\r\n32发布\r\n57审核驳回\r\n28待审核\r\n30任务截止\r\n31任务全部完成\r\n',
  `top_end_time` timestamp NULL DEFAULT NULL COMMENT '任务置顶结束时间',
  `reason` varchar(255) DEFAULT NULL COMMENT '任务审核驳回或者被封，保存原因',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `fk_core_mission_core_mission_detail` (`mission_detail_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1000402 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_mission_accept
-- ----------------------------
DROP TABLE IF EXISTS `core_mission_accept`;
CREATE TABLE `core_mission_accept` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mission_id` int(11) NOT NULL COMMENT '接取的任务ID',
  `accept_user_id` int(11) DEFAULT NULL COMMENT '接单者ID',
  `publish_user_id` int(11) DEFAULT NULL COMMENT '发单者ID',
  `accept_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '接受任务时间',
  `finish_time` timestamp NULL DEFAULT NULL COMMENT '完成任务时间，或者任务不合格时，12小时内用户没有再次上传，12小时后就会填上完成时间,或者已超时（接取20分钟后），或者截止和被封',
  `upload_time` timestamp NULL DEFAULT NULL COMMENT '提交任务时间，每次提交都会更新',
  `review_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '商家审核时间，每次审核都会更新',
  `proceed_property_id` int(11) DEFAULT NULL COMMENT '任务执行情况',
  `text_verify` varchar(255) DEFAULT NULL COMMENT '提交任务时的文字验证',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`accept_user_id`) USING BTREE,
  KEY `core_mission_accept_proceed_property_id` (`proceed_property_id`) USING BTREE,
  KEY `core_mission_accept_mission_id` (`mission_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=51192 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_mission_complaint
-- ----------------------------
DROP TABLE IF EXISTS `core_mission_complaint`;
CREATE TABLE `core_mission_complaint` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mission_accept_id` int(11) unsigned NOT NULL,
  `complainter_user_id` int(11) NOT NULL COMMENT '任务申诉者的id',
  `text` varchar(500) NOT NULL COMMENT '申诉内容',
  `result` tinyint(1) DEFAULT NULL COMMENT '投诉是否成功，1代表申诉成功',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_core_mission_complaint_complainter_mission_accept_id` (`mission_accept_id`) USING BTREE,
  KEY `idx_core_mission_complaint_complainter_user_id` (`complainter_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_mission_detail
-- ----------------------------
DROP TABLE IF EXISTS `core_mission_detail`;
CREATE TABLE `core_mission_detail` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type_property_id` int(11) NOT NULL COMMENT '发布任务的类型，在属性表中的id',
  `user_id` int(11) NOT NULL COMMENT '发布人id',
  `mobile_property_id` int(11) NOT NULL COMMENT '属性表里面的支持设备类型',
  `title` varchar(12) NOT NULL COMMENT '标题，12字以内',
  `deadline_time` timestamp NOT NULL COMMENT '截止时间',
  `price` int(11) NOT NULL COMMENT '出价，单位分',
  `count` int(11) NOT NULL COMMENT '剩余未完成的任务数量',
  `publish_count` int(11) NOT NULL COMMENT '任务发布时的数量，发布后不会改变',
  `fee_price` int(11) NOT NULL COMMENT '服务费金额，单位为分',
  `fee_percent` int(11) NOT NULL COMMENT '手续费的百分比，乘以100之后的数值',
  `mission_rule_id` int(11) NOT NULL COMMENT '任务规则的id',
  `url` varchar(500) DEFAULT NULL COMMENT '链接',
  `text_verify` varchar(500) DEFAULT NULL COMMENT '文字验证',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `type_property_id` (`type_property_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=400 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_mission_detail_step
-- ----------------------------
DROP TABLE IF EXISTS `core_mission_detail_step`;
CREATE TABLE `core_mission_detail_step` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mission_detail_id` int(11) DEFAULT NULL,
  `haveImg` tinyint(1) DEFAULT NULL COMMENT '是否包含图片',
  `text` varchar(255) DEFAULT NULL COMMENT '文字',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_core_mission_detail_step_mission_detail_id` (`mission_detail_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=632 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_mission_rule
-- ----------------------------
DROP TABLE IF EXISTS `core_mission_rule`;
CREATE TABLE `core_mission_rule` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type_property_id` int(11) DEFAULT NULL COMMENT '任务类型的属性表ID',
  `min_price` int(11) DEFAULT NULL COMMENT '最低出价，单位为分',
  `min_count` int(11) DEFAULT NULL COMMENT '最低发布的任务数量',
  `verify_img_count` tinyint(4) DEFAULT '0' COMMENT '审核验证图最多上传数量',
  `usable` tinyint(1) DEFAULT NULL COMMENT '是否可用。如果不可用，则任务列表不显示。已接取的还可以完成',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_core_mission_rule_type_property_id` (`type_property_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_notice
-- ----------------------------
DROP TABLE IF EXISTS `core_notice`;
CREATE TABLE `core_notice` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `text` varchar(255) DEFAULT NULL COMMENT '文字内容',
  `html` mediumtext COMMENT '点击后跳转连接，可以是html代码，也可以是网址',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `usable` tinyint(1) DEFAULT NULL COMMENT '是否可用',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_partner
-- ----------------------------
DROP TABLE IF EXISTS `core_partner`;
CREATE TABLE `core_partner` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL COMMENT '合作商的级别名字',
  `level` tinyint(4) DEFAULT NULL COMMENT '合作商级别，0是非合作商，级别越高数越大',
  `fee_percent` int(11) NOT NULL COMMENT '交易手续费的百分比，乘以100之后的数值',
  `mission_payment_percent` int(11) NOT NULL COMMENT '任务币的提现手续费，乘以100之后的数值',
  `min_fee_price` int(11) NOT NULL COMMENT '发布任务最低的服务费金额，单位分',
  `mouth_price` int(11) NOT NULL DEFAULT '0' COMMENT '月卡价格，单位分/月',
  `year_price` int(11) NOT NULL DEFAULT '0' COMMENT '年卡价格，单位分/年',
  `ad_hour` int(11) NOT NULL DEFAULT '0' COMMENT '免费推荐的时间，单位小时/日',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_core_partner_level` (`level`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_partner_purchase
-- ----------------------------
DROP TABLE IF EXISTS `core_partner_purchase`;
CREATE TABLE `core_partner_purchase` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `partner_id` int(11) DEFAULT NULL COMMENT '合作商id',
  `price` int(11) DEFAULT NULL COMMENT '花费的钱,单位分',
  `start_time` date DEFAULT NULL COMMENT '合作商开始日期，购买立即开始算时间',
  `end_time` date DEFAULT NULL COMMENT '合作商结束日期,这个日期的零时过期',
  `time_type` tinyint(4) DEFAULT NULL COMMENT '1为月卡，2位年卡',
  `usable` tinyint(1) NOT NULL COMMENT '当前合作商是否可用',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_qrcode
-- ----------------------------
DROP TABLE IF EXISTS `core_qrcode`;
CREATE TABLE `core_qrcode` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL,
  `uuid` varchar(32) NOT NULL COMMENT '生成唯一二维码用的随机UUID',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '到期时间，暂时没有做什么判断',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_core_qrcode_uuid` (`uuid`) USING BTREE,
  KEY `fk_core_qrcode_sys_user` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_question
-- ----------------------------
DROP TABLE IF EXISTS `core_question`;
CREATE TABLE `core_question` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `msg` varchar(5000) NOT NULL COMMENT '内容',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_top
-- ----------------------------
DROP TABLE IF EXISTS `core_top`;
CREATE TABLE `core_top` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `mission_id` int(11) DEFAULT NULL COMMENT '任务ID',
  `free_hours` int(11) DEFAULT NULL COMMENT '免费小时数',
  `paid_hours` int(11) DEFAULT NULL COMMENT '收费的小时数',
  `price` int(11) DEFAULT NULL COMMENT '收费小时数花费的钱',
  `top_end_time` timestamp NULL DEFAULT NULL COMMENT '置顶结束时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for core_version
-- ----------------------------
DROP TABLE IF EXISTS `core_version`;
CREATE TABLE `core_version` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `version_code` int(11) DEFAULT NULL COMMENT '版本号',
  `version_name` varchar(20) DEFAULT NULL COMMENT '版本名',
  `wgt_filename` varchar(255) DEFAULT NULL COMMENT 'wgt文件名',
  `force_update` tinyint(1) DEFAULT NULL COMMENT '强制更新',
  `deployed` tinyint(1) DEFAULT NULL COMMENT 'wgt和html是否部署完成',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for financial_trade
-- ----------------------------
DROP TABLE IF EXISTS `financial_trade`;
CREATE TABLE `financial_trade` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `trade_no` varchar(64) DEFAULT NULL COMMENT '单号,userId_reason_amount_now_random',
  `amount` int(11) DEFAULT NULL COMMENT '金额，单位分',
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `description` varchar(50) DEFAULT NULL COMMENT '内容',
  `third_trade_no` varchar(64) DEFAULT NULL COMMENT '第三方订单号',
  `finished` tinyint(1) DEFAULT NULL COMMENT '交易状态,0是未完成，1是已完成',
  `finish_time` timestamp NULL DEFAULT NULL COMMENT '支付完成时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `finacial_trade_trade_no` (`trade_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for financial_transfer
-- ----------------------------
DROP TABLE IF EXISTS `financial_transfer`;
CREATE TABLE `financial_transfer` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '申请提现用户的id',
  `withdrawal_id` int(11) NOT NULL COMMENT 'FinancialWithdrawal提现申请表的id',
  `third_transfer_no` varchar(64) NOT NULL COMMENT '第三方支付的转账单号',
  `transfer_no` varchar(64) NOT NULL COMMENT '系统生成的转账单号',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `finacial_transfer_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for financial_withdrawal
-- ----------------------------
DROP TABLE IF EXISTS `financial_withdrawal`;
CREATE TABLE `financial_withdrawal` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL,
  `money` int(11) DEFAULT NULL COMMENT '提现人民币数值',
  `reason_property_id` int(11) DEFAULT NULL COMMENT '货币变化的原因',
  `approve` tinyint(1) DEFAULT NULL COMMENT '提现申请是否批准',
  `approve_time` timestamp NULL DEFAULT NULL COMMENT '提现批准时间',
  `approve_user_id` int(11) DEFAULT NULL COMMENT '批准人',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_payment_mode
-- ----------------------------
DROP TABLE IF EXISTS `pay_payment_mode`;
CREATE TABLE `pay_payment_mode` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `type_property_id` int(11) DEFAULT NULL COMMENT '绑定的提现方式ID，比如支付宝、微信',
  `name` varchar(10) DEFAULT NULL COMMENT '真实姓名',
  `account` varchar(30) DEFAULT NULL COMMENT '账号',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for social_follow
-- ----------------------------
DROP TABLE IF EXISTS `social_follow`;
CREATE TABLE `social_follow` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `from_user_id` int(11) DEFAULT NULL COMMENT '关注者',
  `to_user_id` int(11) DEFAULT NULL COMMENT '被关注者',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `from_user_id` (`from_user_id`) USING BTREE,
  KEY `to_user_id` (`to_user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for social_notice
-- ----------------------------
DROP TABLE IF EXISTS `social_notice`;
CREATE TABLE `social_notice` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `type_property_id` int(11) DEFAULT NULL COMMENT '通知类型',
  `msg1` varchar(255) DEFAULT NULL,
  `msg2` varchar(255) DEFAULT NULL,
  `msg3` varchar(255) DEFAULT NULL,
  `msg4` varchar(255) DEFAULT NULL,
  `msg5` varchar(255) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=919 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for social_review_chat
-- ----------------------------
DROP TABLE IF EXISTS `social_review_chat`;
CREATE TABLE `social_review_chat` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mission_id` int(11) DEFAULT NULL COMMENT '任务ID',
  `mission_accept_id` int(11) DEFAULT NULL COMMENT '接任务的ID',
  `from_user_id` int(11) DEFAULT NULL COMMENT '信息发出者，如果是null，说明是官方',
  `to_user_id` int(11) DEFAULT NULL COMMENT '信息接受者',
  `include_img` tinyint(1) DEFAULT NULL COMMENT '是否包含图片',
  `text` varchar(255) DEFAULT NULL COMMENT '文字',
  `status` varchar(30) DEFAULT NULL COMMENT '状态文字',
  `previous_chat_id` int(11) DEFAULT NULL COMMENT '上一条对话ID，如果是第一条为null',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sys_advice
-- ----------------------------
DROP TABLE IF EXISTS `sys_advice`;
CREATE TABLE `sys_advice` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `message` text COMMENT '意见反馈内容',
  `contact_info` varchar(50) DEFAULT NULL COMMENT '联系方式',
  `have_img` tinyint(1) DEFAULT NULL COMMENT '是否包含图片',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
-- Table structure for sys_property
-- ----------------------------
DROP TABLE IF EXISTS `sys_property`;
CREATE TABLE `sys_property` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id对应所有类型枚举的propertyId值',
  `type` tinyint(4) NOT NULL COMMENT '属性类型名称，如果出现新的type值,需要再代码里面PropertyType里面添加',
  `name` varchar(30) NOT NULL COMMENT '属性名字',
  `value` mediumtext COMMENT '属性的具体的值，可为空',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `type` (`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户自增id',
  `name` varchar(100) NOT NULL COMMENT '角色名称(一般是中文)',
  `role_name` varchar(255) DEFAULT NULL COMMENT '角色英文名称',
  `useable` tinyint(1) DEFAULT NULL COMMENT '是否可用',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_role_role_name` (`role_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户自增id',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  `add_permission` varchar(30) DEFAULT NULL,
  `edit_permission` varchar(30) DEFAULT NULL,
  `delete_permission` varchar(30) DEFAULT NULL,
  `deleted` tinyint(11) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_role_menu_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8 COMMENT='角色-菜单';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户自增id',
  `pay_password` varchar(30) DEFAULT NULL COMMENT 'MD5后的支付密码',
  `superior_id` int(11) unsigned DEFAULT NULL COMMENT '用户的上级id',
  `agency_id` int(11) unsigned DEFAULT NULL COMMENT '我作为代理商的分成级别',
  `partner_id` int(11) DEFAULT NULL COMMENT '代理商的id',
  `nickname` varchar(20) DEFAULT NULL COMMENT '昵称',
  `head_img_url` varchar(255) DEFAULT NULL COMMENT '三方登陆的头像地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '用户绑定的手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `login_ip` varchar(30) DEFAULT NULL COMMENT '最后一次登陆IP',
  `login_time` timestamp NULL DEFAULT NULL COMMENT '最后一次登陆日期',
  `login_status_property_id` tinyint(4) DEFAULT NULL COMMENT '登陆状态，比如允许登陆、禁止登陆、需绑定手机才可以登陆等等',
  `user_type` tinyint(4) DEFAULT NULL COMMENT '用户类型',
  `deposit` int(11) DEFAULT NULL COMMENT '保证金，单位为分',
  `mission_coin` int(11) DEFAULT NULL COMMENT '任务币，单位分',
  `earning` int(11) DEFAULT NULL COMMENT '我当前收入，包括任务收入和分红，单位分',
  `alipay` varchar(30) DEFAULT NULL COMMENT '支付宝账号',
  `real_name` varchar(10) DEFAULT NULL COMMENT '真实姓名',
  `push_id` varchar(100) DEFAULT NULL COMMENT 'unipush的id',
  `device_info` varchar(255) DEFAULT NULL COMMENT '设备信息',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100159 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户自增id',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creator` varchar(20) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='用户-角色';

SET FOREIGN_KEY_CHECKS = 1;
