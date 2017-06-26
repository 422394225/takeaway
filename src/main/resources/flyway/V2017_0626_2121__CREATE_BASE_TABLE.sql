/*
Navicat MySQL Data Transfer

Source Server         : Local
Source Server Version : 50717
Source Host           : localhost:8888
Source Database       : takeaway

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-06-26 21:21:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin` (
  `ID` varchar(64) NOT NULL,
  `USERNAME` varchar(255) DEFAULT NULL COMMENT '账号',
  `PASSWORD` varchar(255) DEFAULT NULL COMMENT '密码',
  `EMAIL` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `ROLE_ID` varchar(64) DEFAULT NULL COMMENT '角色ID',
  `NAME` varchar(255) DEFAULT NULL COMMENT '姓名',
  `AVATAR` varchar(255) DEFAULT NULL COMMENT '头像',
  `PHONE` varchar(255) DEFAULT NULL COMMENT '手机号',
  `ENABLE` bit(1) DEFAULT b'1' COMMENT '是否可用',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

INSERT INTO `t_admin` (`ID`, `USERNAME`, `PASSWORD`, `EMAIL`, `ROLE_ID`, `NAME`, `AVATAR`, `PHONE`, `ENABLE`, `CREATE_TIME`) VALUES ('1', 'admin', '5689F26101A027CB328CE6C981E10936', '422394225@qq.com', '59a3f99aa78c4e0695be8c09bbce3893', '管理员', 'http://onzsmr037.bkt.clouddn.com/FvKDwTMf0agN_IkEXSuf-YBzYIfz?imageMogr2/crop/!557.00x557.00a0.00a77.75', '15659838505', b'1', '2017-03-27 16:00:26');

-- ----------------------------
-- Table structure for t_customer
-- ----------------------------
DROP TABLE IF EXISTS `t_customer`;
CREATE TABLE `t_customer` (
  `ID` varchar(64) NOT NULL,
  `DESCRIBE` varchar(255) DEFAULT NULL COMMENT '描述',
  `DUOKEFU` varchar(100) NOT NULL COMMENT '客服账号',
  `PHONE` varchar(25) DEFAULT NULL COMMENT '手机号',
  `WEIXIN` varchar(255) DEFAULT NULL COMMENT '微信',
  `NICKNAME` varchar(255) DEFAULT NULL COMMENT '昵称',
  `AVATAR` varchar(400) DEFAULT NULL COMMENT '偶像',
  `PASSWORD` varchar(50) DEFAULT NULL COMMENT '多客服密码',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`,`DUOKEFU`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客服表';

-- ----------------------------
-- Table structure for t_data
-- ----------------------------
DROP TABLE IF EXISTS `t_data`;
CREATE TABLE `t_data` (
  `ID` varchar(255) NOT NULL COMMENT '获取接口分析分时数据',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `REF_DATE` varchar(255) DEFAULT NULL,
  `REF_HOUR` int(255) DEFAULT NULL,
  `CALLBACK_COUNT` int(255) DEFAULT NULL,
  `WEEK` int(255) DEFAULT NULL COMMENT '周一到周日0-6',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_feedback
-- ----------------------------
DROP TABLE IF EXISTS `t_feedback`;
CREATE TABLE `t_feedback` (
  `ID` varchar(64) NOT NULL,
  `QUESTION` varchar(255) DEFAULT NULL COMMENT '问题类型',
  `DESCRIBE` varchar(255) DEFAULT NULL COMMENT '问题描述',
  `OPENID` varchar(255) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈表';

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log` (
  `ID` varchar(64) NOT NULL,
  `ADMIN_ID` varchar(255) DEFAULT NULL COMMENT '操作者id',
  `OPERATION` varchar(255) DEFAULT NULL COMMENT '操作名称',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日志表';

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu` (
  `ID` varchar(64) NOT NULL,
  `PARENT_ID` varchar(64) DEFAULT NULL COMMENT '父级菜单ID',
  `ATTRI` varchar(255) DEFAULT NULL COMMENT 'html中的id',
  `NAME` varchar(255) DEFAULT NULL COMMENT '菜单名称',
  `ICON` varchar(255) DEFAULT NULL COMMENT '图标',
  `URL` varchar(255) DEFAULT NULL COMMENT '菜单链接',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('0', NULL, '', '全部', NULL, NULL, '2017-04-10 18:03:45');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('01', '0', 'index', '首页', 'fa fa-dashboard', '/', '2017-04-10 18:05:47');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('02', '0', 'administrator', '管理员', 'fa fa-user', NULL, '2017-04-10 18:05:56');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('0201', '02', NULL, '管理员列表', 'fa fa-list-ol', '/admin', '2017-04-10 18:06:01');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('0202', '02', NULL, '角色管理', 'fa fa-vcard', '/role', '2017-04-10 18:06:06');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('0203', '02', NULL, '已停用账户', 'fa fa-address-book', '/admin/unactive', '2017-04-10 18:06:10');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('03', '0', 'user', '用户', 'fa fa-users', NULL, '2017-04-10 18:06:14');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('0301', '03', NULL, '用户列表', 'fa fa-list-ol', '/user', '2017-04-10 18:06:22');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('0302', '03', NULL, '取关用户列表', 'fa fa-address-book', '/user/unsubList', '2017-04-10 18:10:23');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('05', '0', 'customer', '客服', 'fa fa-user-circle-o', '/customer', '2017-04-10 18:12:10');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('07', '0', 'feedback', '问题反馈', 'fa fa-bug', '/feedback', '2017-04-10 18:12:45');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('08', '0', 'settings', '系统管理', 'fa fa-cogs', NULL, '2017-04-10 18:13:07');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('0801', '08', NULL, '日志管理', 'fa fa-calendar', '/log', '2017-04-10 18:13:11');
INSERT INTO `t_menu` (`ID`, `PARENT_ID`, `ATTRI`, `NAME`, `ICON`, `URL`, `CREATE_TIME`) VALUES ('0802', '08', NULL, '关于', 'fa fa-tags', '/about', '2017-04-10 18:13:49');


-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `ID` varchar(64) NOT NULL COMMENT 'openid',
  `NICKNAME` varchar(50) DEFAULT NULL COMMENT '昵称',
  `AVATAR` varchar(400) DEFAULT NULL COMMENT '头像',
  `SEX` tinyint(4) DEFAULT '0' COMMENT '性别(0未知1男2女)',
  `SUBSCRIBE` tinyint(1) DEFAULT '1' COMMENT '关注的状态0未关注1已关注',
  `SUBSCRIBE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '用户关注时间。如果用户曾多次关注，则取最后关注时间',
  `GROUPID` varchar(64) DEFAULT NULL COMMENT '用户所在的分组ID',
  `UNIONID` varchar(64) DEFAULT NULL,
  `NAME` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `AGE` int(11) DEFAULT NULL COMMENT '年龄',
  `PHONE` varchar(20) DEFAULT NULL COMMENT '手机号',
  `SCHOOL` varchar(64) DEFAULT NULL COMMENT '学校',
  `ACADEMY` varchar(255) DEFAULT NULL COMMENT '学院',
  `ROLE` varchar(255) DEFAULT NULL COMMENT '用户角色',
  `MAJOR` varchar(255) DEFAULT NULL COMMENT '专业',
  `ID_NUM` varchar(50) DEFAULT NULL COMMENT '身份证号',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `ID` varchar(64) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `POWER` varchar(255) DEFAULT NULL COMMENT '权限',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';