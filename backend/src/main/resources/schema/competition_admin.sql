-- 创建竞赛管理员关联表（多对多关系）
CREATE TABLE IF NOT EXISTS competition_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    competition_id BIGINT NOT NULL COMMENT '竞赛ID',
    admin_id BIGINT NOT NULL COMMENT '管理员ID（竞赛管理员角色的用户）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    
    -- 索引
    INDEX idx_competition_id (competition_id),
    INDEX idx_admin_id (admin_id),
    
    -- 外键约束
    FOREIGN KEY (competition_id) REFERENCES competition(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES user(id) ON DELETE CASCADE,
    
    -- 唯一约束：一个管理员不能重复管理同一个竞赛
    UNIQUE KEY uk_competition_admin (competition_id, admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛管理员关联表';