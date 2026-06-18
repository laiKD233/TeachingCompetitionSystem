-- 教师信息表
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户ID（参考sys_user的id）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `teacher_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工号',
  `education` int NULL DEFAULT NULL COMMENT '学历（1-小学, 2-初中, 3-高中, 4-专科, 5-本科, 6-硕士, 7-博士）',
  `degree` int NULL DEFAULT NULL COMMENT '学位（1-学士, 2-硕士, 3-博士）',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人简介',
  `major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专业',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '照片路径',
  `is_external` tinyint(1) NULL DEFAULT 0 COMMENT '是否校外教师（0-否, 1-是）',
  `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学院名称',
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校名称',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE,
  INDEX `idx_college_name` (`college_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '教师信息表';