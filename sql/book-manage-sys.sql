/*
 Navicat Premium Data Transfer

 Source Server         : local-db-mysql
 Source Server Type    : MySQL
 Source Server Version : 50741
 Source Host           : localhost:3306
 Source Schema         : questionnaire-sys

 Target Server Type    : MySQL
 Target Server Version : 50741
 File Encoding         : 65001

 Date: 22/08/2024 17:24:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '内容',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '网站即将进行系统维护，服务暂停通知', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">尊敬的用户，为了给您带来更加流畅、安全的浏览体验，本网站将于今晚23:00至次日凌晨5:00进行系统升级与维护。</span></p><p><img src=\"/api/online-test/v1.0/file/getFile?fileName=a83d99110.jpg\" alt=\"/api/online-test/v1.0/file/getFile?fileName=a83d99110.jpg\" data-href=\"/api/online-test/v1.0/file/getFile?fileName=a83d99110.jpg\" style=\"width: 417.00px;height: 311.12px;\"/></p><p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">在此期间，网站将暂停所有服务，包括登录、浏览、下单等功能。</span></p><p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">请提前安排好您的使用计划，对给您带来的不便深表歉意，并感谢您的理解与支持！</span></p>', '2024-08-03 12:23:30');
INSERT INTO `notice` VALUES (5, '加强账户安全，请立即更新密码', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">近期，我们发现有不法分子试图通过非法手段获取用户信息。为了保障您的账户安全，我们强烈建议您立即登录网站，更新您的登录密码，并开启双重验证功能。同时，请确保不要使用过于简单或与个人信息相关的密码，以免遭受损失。</span></p>', '2024-08-03 12:24:16');
INSERT INTO `notice` VALUES (6, '夏日狂欢节，限时优惠等你来抢！', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">炎炎夏日，我们为您准备了丰富多彩的夏日狂欢节活动！从即日起至本月底，全场商品低至五折起，更有满额赠礼、积分翻倍等多重好礼等你来拿！快来抢购心仪的商品，享受清凉一夏吧！</span></p>', '2024-08-03 12:24:31');
INSERT INTO `notice` VALUES (7, '关于隐私政策的重要更新，请仔细阅读', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">为了更好地保护您的个人隐私，我们已对隐私政策进行了全面更新。新政策详细说明了我们如何收集、使用、存储和保护您的个人信息。请您务必花时间仔细阅读，并在下次登录时确认同意。如有任何疑问，欢迎随时联系我们的客服团队。</span></p>', '2024-08-03 12:24:46');
INSERT INTO `notice` VALUES (8, '全新功能“智能推荐”正式上线，让购物更便捷！', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">经过长时间的研发与测试，我们的“智能推荐”功能终于与大家见面了！该功能将根据您的浏览历史和购买记录，为您精准推荐感兴趣的商品和优惠信息。让您的购物之旅更加轻松愉快，快来体验吧！</span></p>', '2024-08-03 12:25:01');
INSERT INTO `notice` VALUES (9, '快递服务调整公告，请注意查收时间变化', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">由于近期物流行业调整，我们的快递服务配送时间将有所变动。请广大用户在下单时留意预计送达时间，并提前做好收货准备。对于因此给您带来的不便，我们深表歉意，并感谢您的理解与支持。</span></p>', '2024-08-03 12:25:14');
INSERT INTO `notice` VALUES (10, '会员日尊享特惠，独家福利不容错过！', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">每月的会员日即将来临，我们为尊贵的会员朋友们准备了专属的优惠活动和丰富礼品。会员日当天，所有会员均可享受额外折扣、积分加倍、专属商品等多重福利。快来加入我们的会员行列，享受更多惊喜吧！</span></p>', '2024-08-03 12:25:27');
INSERT INTO `notice` VALUES (11, '警惕网络诈骗，保护个人财产安全', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\"> 近期，我们发现有不法分子冒充本网站客服进行诈骗活动。请广大用户提高警惕，切勿轻信任何要求提供账户密码、验证码或转账的信息。如遇可疑情况，请立即联系我们的官方客服进行核实。我们也将加大打击力度，维护用户的合法权益。</span></p>', '2024-08-03 12:25:39');
INSERT INTO `notice` VALUES (12, '全新面貌，更优体验 —— 网站改版上线', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">经过精心设计与优化，我们的网站以全新的面貌与大家见面了！新版网站在界面设计、功能布局、用户体验等方面均进行了大幅提升。希望您能在新版网站中找到更多的乐趣与便利。如有任何建议或反馈，欢迎随时与我们联系。</span></p>', '2024-08-03 12:25:56');
INSERT INTO `notice` VALUES (13, ' 关于网站内容版权的严正声明', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">本网站所有内容（包括但不限于文字、图片、视频等）均受版权法保护。未经本网站明确授权，任何单位或个人不得擅自转载、复制、链接或以其他方式使用本网站内容。我们保留对侵权行为追究法律责任的权利。请广大用户尊重知识产权，共同维护良好的网络环境。</span></p>', '2024-08-03 12:26:09');
INSERT INTO `notice` VALUES (14, '诚邀合作伙伴，共创辉煌未来！', '<p><span style=\"color: rgb(5, 7, 59); background-color: rgb(253, 253, 254); font-size: 15px;\">随着业务的不断拓展，我们现面向全社会招募志同道合的合作伙伴。无论您是拥有丰富资源的行业精英，还是具有创新精神的初创企业，只要您认同我们的理念，愿意与我们携手共进，我们都将热烈欢迎。期待与您共创美好未来！</span></p>', '2024-08-03 12:26:23');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `user_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_pwd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `user_email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `user_role` int(11) NULL DEFAULT NULL COMMENT '用户角色',
  `is_login` tinyint(1) NULL DEFAULT NULL COMMENT '可登录状态(0：可用，1：不可用)',
  `is_word` tinyint(1) NULL DEFAULT NULL COMMENT '禁言状态(0：可用，1：不可用)',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '用户注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '程序员辰星', '14e1b600b1fd579f47433b88e8d85291', '/api/questionnaire-sys-api/v1.0/file/getFile?fileName=a08d9c5pic_3.jpg', '1343243@qq.com', 1, 0, 0, '2024-08-09 12:53:05');
INSERT INTO `user` VALUES (2, 'zhouzhiruo', '周芷若', '14e1b600b1fd579f47433b88e8d85291', '/api/questionnaire-sys-api/v1.0/file/getFile?fileName=55cee0bpic_4.jpg', '12311323@qq.com', 2, 0, 0, '2024-08-16 16:12:12');

SET FOREIGN_KEY_CHECKS = 1;
