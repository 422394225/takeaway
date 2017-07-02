/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : takeaway

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-07-03 00:58:58
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for schema_version
-- ----------------------------
DROP TABLE IF EXISTS `schema_version`;
CREATE TABLE `schema_version` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of schema_version
-- ----------------------------

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

-- ----------------------------
-- Records of t_admin
-- ----------------------------
INSERT INTO `t_admin` VALUES ('1', 'admin', '5689F26101A027CB328CE6C981E10936', '422394225@qq.com', '', '管理员', 'http://onzsmr037.bkt.clouddn.com/FvKDwTMf0agN_IkEXSuf-YBzYIfz?imageMogr2/crop/!557.00x557.00a0.00a77.75', '15659838505', '', '2017-03-27 16:00:26');

-- ----------------------------
-- Table structure for t_audit
-- ----------------------------
DROP TABLE IF EXISTS `t_audit`;
CREATE TABLE `t_audit` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `SHOP_ID` int(11) NOT NULL,
  `REASON` text,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `OPERATOR_ID` int(11) NOT NULL COMMENT '操作人id',
  `OPERATOR_NAME` varchar(31) DEFAULT NULL COMMENT '操作人名',
  `STATE` varchar(255) NOT NULL COMMENT '状态（1通过，2不通过）',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺审核表';

-- ----------------------------
-- Records of t_audit
-- ----------------------------

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
-- Records of t_customer
-- ----------------------------

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户分析缓存数据';

-- ----------------------------
-- Records of t_data
-- ----------------------------

-- ----------------------------
-- Table structure for t_delivery
-- ----------------------------
DROP TABLE IF EXISTS `t_delivery`;
CREATE TABLE `t_delivery` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(15) DEFAULT NULL,
  `USERNAME` varchar(31) DEFAULT NULL,
  `PASSWORD` varchar(32) DEFAULT NULL,
  `TEL` varchar(15) DEFAULT NULL,
  `PCCODE` int(11) DEFAULT NULL,
  `PCODE` int(11) DEFAULT NULL,
  `NOW_LONGITUDE` double DEFAULT NULL,
  `NOW_LATIDUTE` double DEFAULT NULL,
  `ORDER_NUM` int(11) DEFAULT '0' COMMENT '订单数量',
  `DELIVERY_TIME` int(11) DEFAULT '0' COMMENT '平均送达时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送员表';

-- ----------------------------
-- Records of t_delivery
-- ----------------------------

-- ----------------------------
-- Table structure for t_dict_code
-- ----------------------------
DROP TABLE IF EXISTS `t_dict_code`;
CREATE TABLE `t_dict_code` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `FIELD` varchar(63) DEFAULT NULL,
  `CODE` tinyint(4) DEFAULT NULL,
  `NAME` varchar(31) DEFAULT NULL,
  `CREATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COMMENT='字典表';

-- ----------------------------
-- Records of t_dict_code
-- ----------------------------
INSERT INTO `t_dict_code` VALUES ('1', 't_order.PAY_STATE', '0', '未支付', '2017-07-02 22:52:37');
INSERT INTO `t_dict_code` VALUES ('2', 't_order.PAY_STATE', '1', '用户已支付', '2017-07-02 22:52:37');
INSERT INTO `t_dict_code` VALUES ('3', 't_order.PAY_STATE', '2', '商家已收款', '2017-07-02 22:52:37');
INSERT INTO `t_dict_code` VALUES ('4', 't_order.PAY_STATE', '-1', '未退款', '2017-07-02 22:52:37');
INSERT INTO `t_dict_code` VALUES ('5', 't_order.PAY_STATE', '-2', '已退款', '2017-07-02 23:03:05');
INSERT INTO `t_dict_code` VALUES ('6', 't_order.ORDER_STATE', '1', '未付款', '2017-07-02 23:03:06');
INSERT INTO `t_dict_code` VALUES ('7', 't_order.ORDER_STATE', '2', '已付款，未接单', '2017-07-02 23:03:06');
INSERT INTO `t_dict_code` VALUES ('8', 't_order.ORDER_STATE', '3', '正在准备商品', '2017-07-02 23:03:06');
INSERT INTO `t_dict_code` VALUES ('9', 't_order.ORDER_STATE', '4', '正在配送', '2017-07-02 23:03:06');
INSERT INTO `t_dict_code` VALUES ('10', 't_order.ORDER_STATE', '5', '买家已收货，未评价', '2017-07-02 23:03:06');
INSERT INTO `t_dict_code` VALUES ('11', 't_order.ORDER_STATE', '6', '已评价', '2017-07-02 23:03:06');
INSERT INTO `t_dict_code` VALUES ('12', 't_order.CANCEL_STATE', '0', '发起退款', '2017-07-02 23:03:06');
INSERT INTO `t_dict_code` VALUES ('13', 't_order.CANCEL_STATE', '1', '退款失败', '2017-07-02 23:03:07');
INSERT INTO `t_dict_code` VALUES ('14', 't_order.CANCEL_STATE', '2', '已退款', '2017-07-02 23:23:26');
INSERT INTO `t_dict_code` VALUES ('15', 't_order.CANCEL_STATE', '3', '已取消', '2017-07-02 23:24:34');
INSERT INTO `t_dict_code` VALUES ('16', 't_discount.TYPE', '1', '配送费满减', '2017-07-02 23:42:50');
INSERT INTO `t_dict_code` VALUES ('17', 't_discount.TYPE', '2', '总价满减', '2017-07-02 23:42:50');
INSERT INTO `t_dict_code` VALUES ('18', 't_rate.TYPE', '0', '用户评论', '2017-07-03 00:07:57');
INSERT INTO `t_dict_code` VALUES ('19', 't_rate.TYPE', '1', '商家回复', '2017-07-03 00:08:39');
INSERT INTO `t_dict_code` VALUES ('20', 't_shop.AUDIT_STATE', '0', '未审核', '2017-07-03 00:19:42');
INSERT INTO `t_dict_code` VALUES ('21', 't_shop.AUDIT_STATE', '1', '通过审核', '2017-07-03 00:19:48');
INSERT INTO `t_dict_code` VALUES ('22', 't_shop.AUDIT_STATE', '2', '未通过审核', '2017-07-03 00:20:05');
INSERT INTO `t_dict_code` VALUES ('23', 't_shop.STATE', '-2', '删除', '2017-07-03 00:20:37');
INSERT INTO `t_dict_code` VALUES ('24', 't_shop.STATE', '0', '休息', '2017-07-03 00:20:46');
INSERT INTO `t_dict_code` VALUES ('25', 't_shop.STATE', '2', '营业中', '2017-07-03 00:20:52');
INSERT INTO `t_dict_code` VALUES ('26', 't_audit.STATE', '1', '通过审核', '2017-07-03 00:30:13');
INSERT INTO `t_dict_code` VALUES ('27', 't_audit.STATE', '2', '未通过审核', '2017-07-03 00:30:21');
INSERT INTO `t_dict_code` VALUES ('28', 't_shop.STATE', '-1', '未开业', '2017-07-03 00:47:21');

-- ----------------------------
-- Table structure for t_discount
-- ----------------------------
DROP TABLE IF EXISTS `t_discount`;
CREATE TABLE `t_discount` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `TYPE` varchar(255) DEFAULT NULL,
  `REDUCE_THRESHOLD` double DEFAULT NULL,
  `REDUCE` double DEFAULT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠信息表';

-- ----------------------------
-- Records of t_discount
-- ----------------------------

-- ----------------------------
-- Table structure for t_feedback
-- ----------------------------
DROP TABLE IF EXISTS `t_feedback`;
CREATE TABLE `t_feedback` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `QUESTION` varchar(255) DEFAULT NULL COMMENT '问题类型',
  `DESCRIBE` varchar(511) DEFAULT NULL COMMENT '问题描述',
  `OPENID` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈表';

-- ----------------------------
-- Records of t_feedback
-- ----------------------------

-- ----------------------------
-- Table structure for t_food
-- ----------------------------
DROP TABLE IF EXISTS `t_food`;
CREATE TABLE `t_food` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(255) DEFAULT NULL,
  `PRICE` double DEFAULT NULL COMMENT '现价',
  `ORIGN_PRICE` double DEFAULT NULL COMMENT '原价',
  `STATE` varchar(255) DEFAULT '0' COMMENT '状态(-1删除0下架1上架）',
  `TYPE_ID` int(11) DEFAULT NULL COMMENT '商品分类',
  `UNIT` varchar(7) DEFAULT NULL COMMENT '单位',
  `IMG` text,
  `DESCRIPTION` varchar(511) DEFAULT NULL COMMENT '描述',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `USE_STOCK` tinyint(4) DEFAULT NULL COMMENT '是否使用库存',
  `STOCK` int(11) DEFAULT NULL COMMENT '库存',
  `ORDER_NUM` int(11) DEFAULT '0',
  `SALE_NUM` int(11) DEFAULT '0',
  `SALE_LIMIT` tinyint(4) DEFAULT '0',
  `SHOP_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食品表';

-- ----------------------------
-- Records of t_food
-- ----------------------------

-- ----------------------------
-- Table structure for t_food_histroy
-- ----------------------------
DROP TABLE IF EXISTS `t_food_histroy`;
CREATE TABLE `t_food_histroy` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(255) DEFAULT NULL,
  `PRICE` double DEFAULT NULL COMMENT '现价',
  `ORIGN_PRICE` double DEFAULT NULL COMMENT '原价',
  `STATE` varchar(255) DEFAULT NULL COMMENT '状态(-1删除0下架1商家）',
  `TYPE_ID` int(11) DEFAULT NULL,
  `UNIT` varchar(7) DEFAULT NULL COMMENT '单位',
  `IMG` text,
  `DESCRIPTION` varchar(511) DEFAULT NULL COMMENT '描述',
  `CREATE_TIME` datetime DEFAULT NULL,
  `USE_STOCK` tinyint(4) DEFAULT NULL COMMENT '是否使用库存',
  `STOCK` int(11) DEFAULT NULL COMMENT '库存',
  `ORDER_NUM` int(11) DEFAULT NULL,
  `SALE_NUM` int(11) DEFAULT NULL,
  `SALE_LIMIT` tinyint(4) DEFAULT NULL,
  `SHOP_ID` int(11) DEFAULT NULL,
  `HISTROY_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食品历史表';

-- ----------------------------
-- Records of t_food_histroy
-- ----------------------------

-- ----------------------------
-- Table structure for t_food_type
-- ----------------------------
DROP TABLE IF EXISTS `t_food_type`;
CREATE TABLE `t_food_type` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(31) DEFAULT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DELETED` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食品类型表';

-- ----------------------------
-- Records of t_food_type
-- ----------------------------

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
-- Records of t_log
-- ----------------------------

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

-- ----------------------------
-- Records of t_menu
-- ----------------------------
INSERT INTO `t_menu` VALUES ('0', null, '', '全部', null, null, '2017-04-10 18:03:45');
INSERT INTO `t_menu` VALUES ('01', '0', 'index', '首页', 'fa fa-dashboard', '/', '2017-04-10 18:05:47');
INSERT INTO `t_menu` VALUES ('02', '0', 'administrator', '管理员', 'fa fa-user', null, '2017-04-10 18:05:56');
INSERT INTO `t_menu` VALUES ('0201', '02', null, '管理员列表', 'fa fa-list-ol', '/admin', '2017-04-10 18:06:01');
INSERT INTO `t_menu` VALUES ('0202', '02', null, '角色管理', 'fa fa-vcard', '/role', '2017-04-10 18:06:06');
INSERT INTO `t_menu` VALUES ('0203', '02', null, '已停用账户', 'fa fa-address-book', '/admin/unactive', '2017-04-10 18:06:10');
INSERT INTO `t_menu` VALUES ('03', '0', 'user', '用户', 'fa fa-users', null, '2017-04-10 18:06:14');
INSERT INTO `t_menu` VALUES ('0301', '03', null, '用户列表', 'fa fa-list-ol', '/user', '2017-04-10 18:06:22');
INSERT INTO `t_menu` VALUES ('0302', '03', null, '取关用户列表', 'fa fa-address-book', '/user/unsubList', '2017-04-10 18:10:23');
INSERT INTO `t_menu` VALUES ('04', '0', 'customer', '客服', 'fa fa-user-circle-o', '/customer', '2017-04-10 18:12:10');
INSERT INTO `t_menu` VALUES ('05', '0', 'feedback', '问题反馈', 'fa fa-bug', '/feedback', '2017-04-10 18:12:45');
INSERT INTO `t_menu` VALUES ('06', '0', 'settings', '系统管理', 'fa fa-cogs', null, '2017-04-10 18:13:07');
INSERT INTO `t_menu` VALUES ('0601', '06', null, '日志管理', 'fa fa-calendar', '/log', '2017-04-10 18:13:11');
INSERT INTO `t_menu` VALUES ('0602', '06', null, '关于', 'fa fa-tags', '/about', '2017-04-10 18:13:49');

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `SHOP_ID` int(11) DEFAULT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  `DELEVERY_ID` int(11) DEFAULT NULL,
  `FOODS` varchar(255) DEFAULT NULL COMMENT '{ID,AMOUNT}',
  `ORDER_STATE` tinyint(4) DEFAULT '1' COMMENT '1:未付款,2:已付款，未接单,3:正在准备商品,4:正在配送,5:买家已收货，未评价,6:已评价',
  `PAY_STATE` tinyint(4) DEFAULT '0' COMMENT '0:未支付,1:用户已支付,2:商家已收款,-1:未退款,-2:已退款',
  `TOTAL_PRICE` double DEFAULT NULL COMMENT '总价',
  `DISCOUNT` varchar(255) DEFAULT NULL COMMENT '折扣json{ID,ID,ID}',
  `PAY_PRICE` double DEFAULT NULL COMMENT '支付价格',
  `CANCEL_STATE` tinyint(4) DEFAULT NULL COMMENT '0:发起退款,1:退款失败,2:已退款,3:已取消',
  `CANCEL_USER_REASON` varchar(255) DEFAULT NULL,
  `CANCEL_USER_REASON_IMG` text,
  `CANCEL_SHOP_REASON` varchar(255) DEFAULT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单开始时间',
  `PAY_TIME` datetime DEFAULT NULL COMMENT '付款时间',
  `TAKING_TIME` datetime DEFAULT NULL COMMENT '接单时间',
  `DELEVERY_TIME` datetime DEFAULT NULL,
  `RECIEVE_TIME` datetime DEFAULT NULL COMMENT '买家收到货物的时间',
  `RATE_TIME` datetime DEFAULT NULL COMMENT '评价时间',
  `START_CANCEL_TIME` datetime DEFAULT NULL COMMENT '发起退款时间',
  `FINISH_CANCEL_TIME` datetime DEFAULT NULL COMMENT '退款失败',
  `USER_ADDRESS` varchar(255) DEFAULT NULL COMMENT '送货地址',
  `USER_TEL` varchar(255) DEFAULT NULL,
  `USER_NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Records of t_order
-- ----------------------------

-- ----------------------------
-- Table structure for t_rate
-- ----------------------------
DROP TABLE IF EXISTS `t_rate`;
CREATE TABLE `t_rate` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `TYPE` tinyint(4) DEFAULT NULL COMMENT '谁给谁评',
  `USER_ID` varchar(32) DEFAULT NULL,
  `SHOP_ID` int(11) DEFAULT NULL,
  `ORDER_ID` int(11) DEFAULT NULL,
  `RATE` tinyint(4) DEFAULT NULL,
  `COMMENT` varchar(511) DEFAULT NULL,
  `IMG` text,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评分表';

-- ----------------------------
-- Records of t_rate
-- ----------------------------

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

-- ----------------------------
-- Records of t_role
-- ----------------------------

-- ----------------------------
-- Table structure for t_shop
-- ----------------------------
DROP TABLE IF EXISTS `t_shop`;
CREATE TABLE `t_shop` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(63) DEFAULT NULL COMMENT '店铺名',
  `UERNAME` varchar(31) DEFAULT NULL COMMENT '用户名',
  `PASSWORD` varchar(32) DEFAULT NULL COMMENT '密码',
  `OPENID` varchar(32) DEFAULT NULL,
  `DESCRIPTION` text COMMENT '自述',
  `ADDRESS` varchar(255) DEFAULT NULL COMMENT '地址',
  `PCODE` int(6) DEFAULT NULL COMMENT '省级区域代码',
  `PCCODE` int(6) DEFAULT NULL COMMENT '县级区域代码',
  `IMG` varchar(511) DEFAULT NULL COMMENT '头像地址',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `OPEN_TIME` time DEFAULT NULL COMMENT '开店时间',
  `CLOSE_TIME` time DEFAULT NULL COMMENT '打烊时间',
  `AUTO_OPEN` tinyint(1) DEFAULT '0' COMMENT '自动判断是否开店',
  `SALE_NUM` int(11) DEFAULT '0' COMMENT '销量',
  `DELIVERY_PRICE` double DEFAULT NULL COMMENT '起送价',
  `DELIVERY_OFF_THRESHOLD` double DEFAULT '0' COMMENT '减配送费阈值',
  `DELIVERY_OFF` double DEFAULT '0' COMMENT '减配送费金额',
  `COOK_TIME` int(11) DEFAULT NULL COMMENT '做饭时间',
  `DELIVERYED_TIME` int(11) DEFAULT NULL COMMENT '平均送达时间',
  `REDUCTION` double DEFAULT '0' COMMENT '满减金额',
  `REDUCTION_THRESHOLD` double DEFAULT '0' COMMENT '满减阈值',
  `LONGITUDE` double DEFAULT NULL COMMENT '经度',
  `LATIDUTE` double DEFAULT NULL COMMENT '纬度',
  `QQ` varchar(31) DEFAULT NULL,
  `TEL` varchar(15) DEFAULT NULL,
  `GIFT` varchar(511) DEFAULT NULL COMMENT '赠品',
  `GIFT_THRESHOLD` double(15,0) DEFAULT '0' COMMENT '赠品阈值',
  `RATE_COUNT` int(11) DEFAULT '0' COMMENT '评论数',
  `RATE_1` int(11) DEFAULT '0',
  `RATE_2` int(11) DEFAULT '0',
  `RATE_3` int(11) DEFAULT '0',
  `RATE_4` int(11) DEFAULT '0',
  `RATE_5` int(11) DEFAULT '0',
  `RATE_AVG` float DEFAULT '0' COMMENT '平均评分',
  `AUDIT_STATE` tinyint(4) DEFAULT '0' COMMENT '审核状态（0未审核，1通过，2未通过）',
  `STATE` tinyint(4) DEFAULT '0' COMMENT '（-2删除,-1未开业，0休息，2营业中）',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='店铺表';

-- ----------------------------
-- Records of t_shop
-- ----------------------------

-- ----------------------------
-- Table structure for t_shop_type
-- ----------------------------
DROP TABLE IF EXISTS `t_shop_type`;
CREATE TABLE `t_shop_type` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(31) DEFAULT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DELETED` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺类型表';

-- ----------------------------
-- Records of t_shop_type
-- ----------------------------

-- ----------------------------
-- Table structure for t_shop_type_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_shop_type_relation`;
CREATE TABLE `t_shop_type_relation` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `SHOPID` int(11) DEFAULT NULL,
  `TYPEID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺类型关系表';

-- ----------------------------
-- Records of t_shop_type_relation
-- ----------------------------

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `ID` varchar(32) NOT NULL COMMENT 'openid',
  `NICKNAME` varchar(50) DEFAULT NULL COMMENT '昵称',
  `AVATAR` varchar(400) DEFAULT NULL COMMENT '头像',
  `SEX` tinyint(4) DEFAULT '0' COMMENT '性别(0未知1男2女)',
  `SUBSCRIBE` tinyint(1) DEFAULT '1' COMMENT '关注的状态0未关注1已关注',
  `SUBSCRIBE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '用户关注时间。如果用户曾多次关注，则取最后关注时间',
  `GROUPID` varchar(64) DEFAULT NULL COMMENT '用户所在的分组ID',
  `UNIONID` varchar(64) DEFAULT NULL,
  `NAME` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `TEL` varchar(20) DEFAULT NULL COMMENT '手机号',
  `ADDRESS` varchar(255) DEFAULT NULL,
  `COLLECTION` varchar(255) DEFAULT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of t_user
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_address
-- ----------------------------
DROP TABLE IF EXISTS `t_user_address`;
CREATE TABLE `t_user_address` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `USERID` varchar(32) DEFAULT NULL,
  `PCODE` int(255) DEFAULT NULL,
  `PCCODE` int(255) DEFAULT NULL,
  `ADDRESS` varchar(255) DEFAULT NULL COMMENT '地址',
  `LONGITUDE` double(255,0) DEFAULT NULL COMMENT '经度',
  `LATIDUTE` double DEFAULT NULL COMMENT '纬度',
  `NAME` varchar(15) DEFAULT NULL,
  `TEL` varchar(15) DEFAULT NULL,
  `DELETED` tinyint(4) NOT NULL DEFAULT '0',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DELETE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='买家的收货地址';

-- ----------------------------
-- Records of t_user_address
-- ----------------------------
