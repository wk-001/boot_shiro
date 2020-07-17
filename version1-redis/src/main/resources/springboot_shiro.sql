/*
 Navicat Premium Data Transfer

 Source Server         : 3306
 Source Server Type    : MySQL
 Source Server Version : 50723
 Source Host           : localhost:3306
 Source Schema         : springboot_shiro

 Target Server Type    : MySQL
 Target Server Version : 50723
 File Encoding         : 65001

 Date: 17/07/2020 17:29:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for resource
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称',
  `permission` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限标识符',
  `parent_id` int(11) DEFAULT NULL COMMENT '上级ID',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '0：菜单；1：按钮',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resource
-- ----------------------------
INSERT INTO `resource` VALUES (1, '系统管理', 'system', 0, '0');
INSERT INTO `resource` VALUES (2, '用户管理', 'user:view', 1, '0');
INSERT INTO `resource` VALUES (3, '角色管理', 'role:view', 1, '0');
INSERT INTO `resource` VALUES (4, '资源管理', 'resource:view', 1, '0');
INSERT INTO `resource` VALUES (5, '用户添加', 'user:add', 2, '1');
INSERT INTO `resource` VALUES (6, '用户删除', 'user:delete', 2, '1');
INSERT INTO `resource` VALUES (7, '用户修改', 'user:update', 2, '1');
INSERT INTO `resource` VALUES (8, '角色添加', 'role:add', 3, '1');
INSERT INTO `resource` VALUES (9, '角色删除', 'role:delete', 3, '1');
INSERT INTO `resource` VALUES (10, '角色修改', 'role:update', 3, '1');
INSERT INTO `resource` VALUES (11, '资源修改', 'resource:update', 4, '1');
INSERT INTO `resource` VALUES (12, '资源增加', 'resource:add', 4, '1');
INSERT INTO `resource` VALUES (13, '资源删除', 'resource:delete', 4, '1');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '角色名',
  `role_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '角色代码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '管理员', 'admin');
INSERT INTO `role` VALUES (2, '开发', 'dev');

-- ----------------------------
-- Table structure for role_resource
-- ----------------------------
DROP TABLE IF EXISTS `role_resource`;
CREATE TABLE `role_resource`  (
  `role_id` int(11) NOT NULL,
  `resource_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`, `resource_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_resource
-- ----------------------------
INSERT INTO `role_resource` VALUES (1, 1);
INSERT INTO `role_resource` VALUES (1, 2);
INSERT INTO `role_resource` VALUES (1, 3);
INSERT INTO `role_resource` VALUES (1, 4);
INSERT INTO `role_resource` VALUES (1, 5);
INSERT INTO `role_resource` VALUES (1, 6);
INSERT INTO `role_resource` VALUES (1, 7);
INSERT INTO `role_resource` VALUES (1, 8);
INSERT INTO `role_resource` VALUES (1, 9);
INSERT INTO `role_resource` VALUES (1, 10);
INSERT INTO `role_resource` VALUES (1, 11);
INSERT INTO `role_resource` VALUES (1, 12);
INSERT INTO `role_resource` VALUES (1, 13);
INSERT INTO `role_resource` VALUES (2, 1);
INSERT INTO `role_resource` VALUES (2, 2);
INSERT INTO `role_resource` VALUES (2, 3);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(33) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `password` varchar(33) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `salt` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '加密盐',
  `enable` int(10) DEFAULT 1 COMMENT '是否启用 0：不可用；1：可用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '1', 'f0a039e0b9501c7f79c72aa07c6fa03b', 'mQH/4pdRBsVOcqleEIY/1A==', 1);
INSERT INTO `user` VALUES (2, '2', 'f0a039e0b9501c7f79c72aa07c6fa03b', 'mQH/4pdRBsVOcqleEIY/1A==', 1);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1);
INSERT INTO `user_role` VALUES (2, 2);
INSERT INTO `user_role` VALUES (3, 3);

SET FOREIGN_KEY_CHECKS = 1;
