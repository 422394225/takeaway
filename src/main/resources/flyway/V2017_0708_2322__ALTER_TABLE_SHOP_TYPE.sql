ALTER TABLE `t_shop_type`
ADD COLUMN `ICON`  varchar(255) NULL DEFAULT NULL COMMENT '图标';

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
INSERT INTO `t_menu` VALUES ('04', '0', 'shop', '商家', 'fa fa-home', null, '2017-07-09 00:03:51');
INSERT INTO `t_menu` VALUES ('0401', '04', null, '商家列表', 'fa fa-list-ol', '/shop', '2017-07-09 00:08:06');
INSERT INTO `t_menu` VALUES ('0402', '04', null, '商家分类', 'fa fa-bookmark', '/shopType', '2017-07-09 00:09:11');
INSERT INTO `t_menu` VALUES ('07', '0', 'food', '商品', 'fa fa-cutlery', null, '2017-07-03 23:21:36');
INSERT INTO `t_menu` VALUES ('0701', '04', null, '商品列表', 'fa fa-list-ol', '/food', '2017-07-03 23:21:36');
INSERT INTO `t_menu` VALUES ('08', '0', 'feedback', '问题反馈', 'fa fa-bug', '/feedback', '2017-04-10 18:12:45');
INSERT INTO `t_menu` VALUES ('09', '0', 'settings', '系统管理', 'fa fa-cogs', null, '2017-04-10 18:13:07');
INSERT INTO `t_menu` VALUES ('0901', '09', null, '日志管理', 'fa fa-calendar', '/log', '2017-04-10 18:13:11');
INSERT INTO `t_menu` VALUES ('0902', '09', null, '关于', 'fa fa-tags', '/about', '2017-04-10 18:13:49');
