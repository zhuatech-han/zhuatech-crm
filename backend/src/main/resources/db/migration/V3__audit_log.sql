-- 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2
CREATE TABLE crm_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    actor_id BIGINT NOT NULL,
    actor_username VARCHAR(32) NOT NULL,
    action VARCHAR(40) NOT NULL,
    target_type VARCHAR(30) NOT NULL,
    target_id BIGINT,
    detail VARCHAR(500),
    created_at DATETIME(6) NOT NULL,
    INDEX idx_audit_created (created_at),
    INDEX idx_audit_target (target_type, target_id),
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_id) REFERENCES crm_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
