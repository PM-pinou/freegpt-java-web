/*
Navicat MySQL Data Transfer

Source Server         : 本机
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : bluecat

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2023-09-16 14:54:54
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `channel_config`
-- ----------------------------
DROP TABLE IF EXISTS `channel_config`;
CREATE TABLE `channel_config` (
  `id` bigint(11) NOT NULL,
  `name` varchar(64) NOT NULL COMMENT '通道名称',
  `model_type` varchar(64) NOT NULL DEFAULT '0' COMMENT '通道对应模型类型',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1:有效 0：无效',
  `create_user` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_user` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of channel_config
-- ----------------------------
INSERT INTO `channel_config` VALUES ('1', '默认通道', 'gpt-3.5-turbo', '1', null, null, null, null);
INSERT INTO `channel_config` VALUES ('1692143162576973826', 'gpt-4-32k模型通道', 'gpt-4-32k', '1', '18230675983', '2023-08-17 19:55:43', null, null);
INSERT INTO `channel_config` VALUES ('1695078211769180162', 'Midjourney 通道', 'Midjourney', '1', '15889198403', '2023-08-25 22:18:33', null, null);
INSERT INTO `channel_config` VALUES ('1698642588447404033', 'GPT-4-32k通道', 'gpt-4-32k', '1', '15565139513', '2023-09-04 18:22:07', null, null);
INSERT INTO `channel_config` VALUES ('1699096473553096705', 'vip（gpt-4）', 'gpt-4', '1', '18230675983', '2023-09-06 00:25:42', '15565139513', '2023-09-11 00:27:07');
INSERT INTO `channel_config` VALUES ('1699096575860559874', '3.5专属通道', 'gpt-3.5-turbo', '1', '18230675983', '2023-09-06 00:26:06', null, null);

-- ----------------------------
-- Table structure for `channel_model_config`
-- ----------------------------
DROP TABLE IF EXISTS `channel_model_config`;
CREATE TABLE `channel_model_config` (
  `id` bigint(11) NOT NULL,
  `channel_config_id` bigint(11) NOT NULL COMMENT '通道id',
  `model_config_id` bigint(11) NOT NULL COMMENT '模型表id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of channel_model_config
-- ----------------------------
INSERT INTO `channel_model_config` VALUES ('1690008194375606274', '1', '1690008194371411970');
INSERT INTO `channel_model_config` VALUES ('1698345150704644097', '1', '1284717249812278294');
INSERT INTO `channel_model_config` VALUES ('1698345150704644098', '1695078211769180162', '1284717249812278294');
INSERT INTO `channel_model_config` VALUES ('1698345427486765058', '1', '1692143625418420226');
INSERT INTO `channel_model_config` VALUES ('1698345427486765059', '1692143162576973826', '1692143625418420226');
INSERT INTO `channel_model_config` VALUES ('1698642697138597890', '1698642588447404033', '1698642697126014977');
INSERT INTO `channel_model_config` VALUES ('1698858941800378370', '1698657953978118145', '1698658080323137537');
INSERT INTO `channel_model_config` VALUES ('1699359089185828866', '1', '1');
INSERT INTO `channel_model_config` VALUES ('1699359089185828867', '1699096575860559874', '1');
INSERT INTO `channel_model_config` VALUES ('1699359410775699457', '1699096575860559874', '1699359410758922241');
INSERT INTO `channel_model_config` VALUES ('1699359410775699458', '1', '1699359410758922241');
INSERT INTO `channel_model_config` VALUES ('1700911408394272770', '1699096473553096705', '1697615930919612417');
INSERT INTO `channel_model_config` VALUES ('1700911408394272771', '1', '1697615930919612417');
INSERT INTO `channel_model_config` VALUES ('1700911425901297665', '1699096473553096705', '1697615806172622849');
INSERT INTO `channel_model_config` VALUES ('1700911425901297666', '1', '1697615806172622849');
INSERT INTO `channel_model_config` VALUES ('1701187654315511810', '2', '1696480917335412737');
INSERT INTO `channel_model_config` VALUES ('1701187654319706114', '1', '1696480917335412737');
INSERT INTO `channel_model_config` VALUES ('1701195100404682753', '1697273685938814978', '1697607272315875329');
INSERT INTO `channel_model_config` VALUES ('1701544620325867522', '1697273685938814978', '1700897493056749570');
INSERT INTO `channel_model_config` VALUES ('1701544649174290433', '1697273685938814978', '1700897703799554050');
INSERT INTO `channel_model_config` VALUES ('1701971501898534914', '1701450383479541761', '1701777284546957314');
INSERT INTO `channel_model_config` VALUES ('1702281403229540353', '1694921144529862658', '1702262244944908289');
INSERT INTO `channel_model_config` VALUES ('1702288086609694721', '1699096575860559874', '1702288086605500417');
INSERT INTO `channel_model_config` VALUES ('1702333653419298817', '1699096473553096705', '1700895404754731010');
INSERT INTO `channel_model_config` VALUES ('1702620795584385026', '1699096473553096705', '1702333122877591554');
INSERT INTO `channel_model_config` VALUES ('1702620819034738690', '1699096473553096705', '1702333024940593154');
INSERT INTO `channel_model_config` VALUES ('1702620847522451458', '1699096473553096705', '1702332893386248193');
INSERT INTO `channel_model_config` VALUES ('1702621555395137538', '1699096473553096705', '1696370195490246657');

-- ----------------------------
-- Table structure for `consumption_records`
-- ----------------------------
DROP TABLE IF EXISTS `consumption_records`;
CREATE TABLE `consumption_records` (
  `id` bigint(11) NOT NULL,
  `cost_before` varchar(64) DEFAULT NULL COMMENT '消费之前的值',
  `cost_after` varchar(64) DEFAULT NULL COMMENT '消费之前',
  `system_token` varchar(64) DEFAULT NULL COMMENT '系统渠道的token',
  `user_token` varchar(64) DEFAULT NULL COMMENT 'api_config的token',
  `cost` varchar(64) DEFAULT NULL COMMENT '本次消费的值',
  `op` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `type` int(4) NOT NULL COMMENT '消费记录类型\n1: 会话\n2:兑换码生成',
  `biz_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_token_index` (`user_token`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户的消费日志';

-- ----------------------------
-- Records of consumption_records
-- ----------------------------
INSERT INTO `consumption_records` VALUES ('1702938904699883521', '2000000', '1999055', 'sk-71ee175d9b43406cafb8b0588aaf9019', 'sk-69d64df654ff4a5db4e00052eb0d21c9', '945', '消费gpt-3.5-turbo', '2023-09-16 14:54:09', '2', null);

-- ----------------------------
-- Table structure for `coupon`
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id` bigint(11) NOT NULL,
  `coupon_no` varchar(50) NOT NULL COMMENT '兑换卷号码',
  `account` varchar(50) NOT NULL COMMENT '兑换卷生成人',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1:未使用 0：已使用 -1:失效',
  `coupon_amount` bigint(11) NOT NULL COMMENT '兑换金额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `use_end_time` datetime DEFAULT NULL COMMENT '兑换卷失效时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `use_account` varchar(50) DEFAULT NULL COMMENT '兑换卷使用人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of coupon
-- ----------------------------

-- ----------------------------
-- Table structure for `coupon_order_snapshoot`
-- ----------------------------
DROP TABLE IF EXISTS `coupon_order_snapshoot`;
CREATE TABLE `coupon_order_snapshoot` (
  `id` bigint(11) NOT NULL,
  `coupon_no` varchar(50) NOT NULL COMMENT '兑换卷号码',
  `user_id` bigint(11) DEFAULT NULL COMMENT '兑换卷生成人',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `order_id` bigint(11) NOT NULL COMMENT '订单id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of coupon_order_snapshoot
-- ----------------------------

-- ----------------------------
-- Table structure for `gpt_api_token_config`
-- ----------------------------
DROP TABLE IF EXISTS `gpt_api_token_config`;
CREATE TABLE `gpt_api_token_config` (
  `id` bigint(11) NOT NULL,
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户id',
  `name` varchar(64) NOT NULL DEFAULT '小可爱的token' COMMENT 'token名称',
  `token` varchar(128) NOT NULL,
  `visit_number` bigint(11) NOT NULL DEFAULT '20' COMMENT '访问次数',
  `balance` bigint(11) DEFAULT '2000000' COMMENT '余额',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `create_user` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_user` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1:有效 0：无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of gpt_api_token_config
-- ----------------------------
INSERT INTO `gpt_api_token_config` VALUES ('1702938881757040641', '1702938881627017217', '小可爱的token', 'sk-69d64df654ff4a5db4e00052eb0d21c9', '20', '2000000', null, 'system', '2023-09-16 14:54:03', null, null, '1');

-- ----------------------------
-- Table structure for `gpt_model_config`
-- ----------------------------
DROP TABLE IF EXISTS `gpt_model_config`;
CREATE TABLE `gpt_model_config` (
  `id` bigint(22) NOT NULL COMMENT '访问的地址',
  `base_url` varchar(128) NOT NULL,
  `token` varchar(128) NOT NULL COMMENT '访问地址',
  `model` varchar(128) NOT NULL COMMENT '模型标识',
  `name` varchar(64) DEFAULT NULL COMMENT '来源名称',
  `weight` tinyint(4) DEFAULT '1' COMMENT '权重 ',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 1：开启 0 ：禁用 ',
  `create_time` datetime DEFAULT NULL COMMENT '新增时间',
  `create_user` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(64) DEFAULT NULL COMMENT '更新人 ',
  PRIMARY KEY (`id`),
  UNIQUE KEY `token_index` (`base_url`,`token`,`model`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of gpt_model_config
-- ----------------------------
INSERT INTO `gpt_model_config` VALUES ('1', 'http://www.chosen1.xyz/', 'sk-71ee175d9b43406cafb8b0588aaf9019', 'gpt-3.5-turbo', '默认模型（不能删除）', '2', '1', '2023-08-11 22:32:07', '18230675983', '2023-09-06 17:49:14', '18230675983');
INSERT INTO `gpt_model_config` VALUES ('1284717249812278294', 'https://mj-api.starxmate.com', 'L9Y5date9o8R4SL', 'Midjourney', '新的mj渠道', '5', '1', '2023-09-03 14:25:59', '5889198403', '2023-09-03 22:40:12', '18230675983');

-- ----------------------------
-- Table structure for `intercept_record`
-- ----------------------------
DROP TABLE IF EXISTS `intercept_record`;
CREATE TABLE `intercept_record` (
  `id` bigint(22) NOT NULL,
  `ip` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
  `user_id` bigint(22) DEFAULT NULL,
  `reason` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '理由',
  `create_time` datetime DEFAULT NULL,
  `source` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of intercept_record
-- ----------------------------

-- ----------------------------
-- Table structure for `login_user_info`
-- ----------------------------
DROP TABLE IF EXISTS `login_user_info`;
CREATE TABLE `login_user_info` (
  `id` bigint(22) NOT NULL,
  `session_id` varchar(64) NOT NULL COMMENT 'session_key',
  `user_info` varchar(5072) NOT NULL COMMENT '用户登录信息的json串',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录的id';

-- ----------------------------
-- Records of login_user_info
-- ----------------------------

-- ----------------------------
-- Table structure for `mj_task_info`
-- ----------------------------
DROP TABLE IF EXISTS `mj_task_info`;
CREATE TABLE `mj_task_info` (
  `id` bigint(11) NOT NULL,
  `task_id` varchar(128) NOT NULL COMMENT '任务id',
  `use_token` varchar(128) NOT NULL COMMENT '使用 的token',
  `user_id` bigint(11) NOT NULL COMMENT '用户id',
  `status` int(4) NOT NULL COMMENT '任务状态 0 -SUBMITTED 已提交,1-IN_PROGRESS 执行中,2-SUCCESS 完成,-1 -失败',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `prompt` varchar(2048) DEFAULT NULL,
  `model_id` bigint(22) NOT NULL DEFAULT '0',
  `task_url` varchar(255) DEFAULT '' COMMENT '生成的url',
  `finish_time` datetime DEFAULT NULL COMMENT '任务完成时间',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '0 - 普通 1 变换',
  `parent_id` varchar(128) DEFAULT '' COMMENT '父亲id',
  `parent_photo` varchar(256) DEFAULT '',
  `parent_index` int(4) DEFAULT NULL,
  `state` varchar(2048) DEFAULT '',
  `action` varchar(128) DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of mj_task_info
-- ----------------------------

-- ----------------------------
-- Table structure for `popup_info`
-- ----------------------------
DROP TABLE IF EXISTS `popup_info`;
CREATE TABLE `popup_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `popupLocation` varchar(255) DEFAULT NULL,
  `isShow` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of popup_info
-- ----------------------------
INSERT INTO `popup_info` VALUES ('2', '蓝猫AI第1版公告', '蓝猫AI目前已开源，<a href=\"https://gitee.com/lixinjiuhao/chatgpt-web-java\" style=\"color: #58a6ff\">源码地址</a> ，<br>目前gpt-4也免费放开，登录即可使用。<br><a href=\"http://chosen1.xyz/me.jpg\" style=\"color: #58a6ff\">点击联系我们</a>', '2023-08-13 21:09:00', 'index', '0');
INSERT INTO `popup_info` VALUES ('6', '蓝猫AI', '蓝猫AI助手 GPT4全新上线，点击顶部模型选择GPT4,即可免费体验，目前由于GPT4开销太大，只能限时开放 <a href=\"https://mp.weixin.qq.com/s/WxwXPfThFEHjAE7hHyvwoA\" style=\"color: #58a6ff\">点击联系我们</a>', '2023-06-02 08:57:59', 'login', '0');

-- ----------------------------
-- Table structure for `prompt_model`
-- ----------------------------
DROP TABLE IF EXISTS `prompt_model`;
CREATE TABLE `prompt_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(100) NOT NULL COMMENT '分类',
  `title` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '标题',
  `introduce` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '简介',
  `demo` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '示例',
  `content` varchar(1000) CHARACTER SET utf8mb4 NOT NULL COMMENT '提示内容',
  `state` tinyint(4) NOT NULL COMMENT '0，无效；1，有效',
  `sort` tinyint(4) NOT NULL DEFAULT '0' COMMENT '排序值',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of prompt_model
-- ----------------------------
INSERT INTO `prompt_model` VALUES ('1', 'Java', 'Java代码优化', '只需要将你想要的优化的代码复制到输入框，AI将会自动帮你优化它', '请输入/粘贴你想要优化的代码', '假如你是高级Java开发工程师，有着丰富的代码编写能力，那么请使用JDK8新语法将下面的代码进行优化并且将优化后的代码能够被markdowm渲染的代码出来：', '1', '0', '2023-05-21 22:42:46', '2023-05-21 22:56:34');
INSERT INTO `prompt_model` VALUES ('7', 'Java', '使用JDK8新特性优化代码', '只需要将你想要的优化的代码复制到输入框，AI将会自动使用JDK8新语法帮你优化它', '请输入/粘贴你想要优化的代码', ' 假如你是高级Java开发工程师，非常擅长使用JDK8新语法，请你首先判断提交的代码是否是Java代码，如果不是则告知：请输入正确格式的java代码 ，如果是：输出被jdk8优化并带有注释的代码 ，下面是用户提交的代码：', '1', '0', '2023-05-21 22:47:14', '2023-08-01 18:09:33');
INSERT INTO `prompt_model` VALUES ('8', 'Java', 'Java面试打分工具', '只需要将你的问题和回答复制到输入框，AI将会自动根据你的问题和回答进行分析和打分', '问题：在synchronized代码块中调用wait方法进入等待的线程和因为拿不到锁而等待线程是否同一种状态？blocking？waiting？\r\n回答：1. 因为拿不到锁会处于 blocking状态；\r\n2. 调用wait方法，会处于 waiting状态，不会释放锁。\r\n', '假如你是Java面试官，有着非常丰富的面试经验，那么请基于下面的问题和回答进行系统的分析，给出相应的分数，并且以markdown的格式分点输出回答中不足的地方并且加以补充：', '1', '0', '2023-05-21 22:58:02', '2023-05-21 23:15:45');
INSERT INTO `prompt_model` VALUES ('9', '精简文字', '精简文章', '只需要将你想要的文章贴进去，安装你想要的要求进行精简', '请输入你想要精简的文章', '假如你是文字提炼家，帮我将下面的内容精简，要求精简后的内容不能超出50个字：', '1', '0', '2023-08-13 22:34:56', '2023-08-13 22:35:04');
INSERT INTO `prompt_model` VALUES ('10', '翻译官', '中文转化为英文', '只需要将你想要的中文输入进去，将会自动翻译成英文', '请你输入/粘贴你想要的中文', '你是一名英语翻译官，请你将下面的内容翻译成英文：', '1', '0', '2023-08-25 20:33:22', '2023-09-03 21:18:46');
INSERT INTO `prompt_model` VALUES ('11', 'mj提示词', 'mj提示词', '只需要将你想要优化的内容贴进去，gpt将会自动给你优化：', '请输入/粘贴你想要的描述的内容：', '  我正在使用一个叫做Midjourney的AI图像生成工具。我想让你充当关键词生成器。我将在我想生成的主题之前添加\"/\"你会生成各种关键词。例如，如果我输入\"/跑车图像\"，你将生成关键词，如\"Realistic true details photography of Sports car,laction shots, speed motion blur, racing tracks, urban environments, scenic roads, dramatic skies\"。/', '1', '0', '2023-09-03 23:17:57', '2023-09-03 23:17:57');

-- ----------------------------
-- Table structure for `prompt_record`
-- ----------------------------
DROP TABLE IF EXISTS `prompt_record`;
CREATE TABLE `prompt_record` (
  `id` bigint(22) NOT NULL,
  `conversation_id` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '会话ID',
  `service_type` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模型标识',
  `token` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
  `prompt_token` int(8) DEFAULT '0',
  `rely_token` int(8) DEFAULT '0',
  `cost` int(8) DEFAULT NULL,
  `source_token` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '渠道key',
  `rely_text` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL,
  `prompt` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `source` varchar(22) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '来源',
  PRIMARY KEY (`id`),
  KEY `create_index` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of prompt_record
-- ----------------------------
INSERT INTO `prompt_record` VALUES ('1702938904699883522', '4af72540-df79-4148-b8a4-200bc81ba261', 'gpt-3.5-turbo', 'sk-69d64df654ff4a5db4e00052eb0d21c9', '39', '18', '945', 'sk-71ee175d9b43406cafb8b0588aaf9019', '你好！有什么我可以帮助你的吗？', '你好', '2023-09-16 14:54:09', 'chat');

-- ----------------------------
-- Table structure for `token_channel_config`
-- ----------------------------
DROP TABLE IF EXISTS `token_channel_config`;
CREATE TABLE `token_channel_config` (
  `id` bigint(11) NOT NULL,
  `token` varchar(128) NOT NULL COMMENT '用户的token',
  `channel_config_id` bigint(11) NOT NULL COMMENT 'channel用户表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of token_channel_config
-- ----------------------------
INSERT INTO `token_channel_config` VALUES ('1702938881790595073', 'sk-69d64df654ff4a5db4e00052eb0d21c9', '1');

-- ----------------------------
-- Table structure for `user_access_rule`
-- ----------------------------
DROP TABLE IF EXISTS `user_access_rule`;
CREATE TABLE `user_access_rule` (
  `id` bigint(22) NOT NULL COMMENT '用户id',
  `user_id` bigint(22) NOT NULL,
  `service_type` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '模型标识',
  `use_number` tinyint(4) NOT NULL DEFAULT '10' COMMENT '使用次数, 如果 = -2 代表不限制次数',
  `start_effective_time` datetime NOT NULL COMMENT '开始生效时间',
  `end_effective_time` datetime NOT NULL COMMENT '有效结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_index` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户访问规则';

-- ----------------------------
-- Records of user_access_rule
-- ----------------------------

-- ----------------------------
-- Table structure for `user_info`
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint(22) NOT NULL,
  `bot_id` varchar(64) DEFAULT 'blueCat' COMMENT '机器人表示',
  `username` varchar(64) DEFAULT '小可爱！' COMMENT '用户昵称',
  `open_id` varchar(128) DEFAULT NULL COMMENT '微信授权的openId',
  `avatar` varchar(128) DEFAULT '/src/assets/avatar.jpg' COMMENT '用户头像',
  `phone` varchar(11) DEFAULT NULL COMMENT '电话信息',
  `account` varchar(32) NOT NULL COMMENT '用户账号',
  `user_level` varchar(16) DEFAULT 'common_user' COMMENT '用户等级',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '账号状态，0正常，1禁用，2审核中',
  `password` varchar(64) NOT NULL COMMENT '登录密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_index` (`account`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('1702938881627017217', 'blueCat', '小可爱！', null, '/src/assets/avatar.jpg', null, '18230670000', 'common_user', '0', 'e8f8c481e9c07e971fad8c5fad5a5fc3', '2023-09-16 14:54:03', null);

-- ----------------------------
-- Table structure for `user_log`
-- ----------------------------
DROP TABLE IF EXISTS `user_log`;
CREATE TABLE `user_log` (
  `id` bigint(22) NOT NULL,
  `app_name` varchar(32) COLLATE utf8mb4_bin DEFAULT 'blueCat',
  `browser_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '浏览器',
  `ip` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
  `biz` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务id',
  `op` tinyint(4) DEFAULT NULL COMMENT '操作 1 登录、2注册 、3 异常',
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of user_log
-- ----------------------------
INSERT INTO `user_log` VALUES ('1702938881723486209', 'blueCat', 'Chrome 11-116.0.0.0', '127.0.0.1', '1702938881627017217', '2', '2023-09-16 14:54:03', '1702938881627017217');
