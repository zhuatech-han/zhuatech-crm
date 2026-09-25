# 部署说明

Copyright © 2026 上海如静知华信息科技有限公司。官网：https://www.zhuatech.cn/ · 商业咨询微信：`zhuatech` / `zhuatech2`。

根目录 `compose.yaml` 默认只在本机开放 Web 端，面向个人非商业学习体验。未经上海如静知华信息科技有限公司书面授权，不得用于企业内部、商业项目或生产环境。取得商业授权后，生产环境应使用 HTTPS、受限网络、集中日志、指标监控和定期备份，并将数据库密码、管理员初始密码与 JWT 密钥交由 Secret Manager 或等价服务管理。

Compose 会检查 MySQL、后端 `/actuator/health` 和前端首页的健康状态；健康端点只返回概要，不能代替外部告警、业务可用性检查和备份验证。

## 本地启动与账号

需要 Docker Engine 24+、Docker Compose v2+、Python 3.8+。在空目录首次启动：

```sh
python3 scripts/init_demo_env.py
docker compose up --build -d
```

浏览器打开 `http://127.0.0.1:8088/`。管理员可在“我的 → 账号管理”创建、停用账号并重置其他成员的密码；每个人可在“我的”修改本人密码。密码重置或账号停用会使该账号已签发的令牌失效。正式环境设置 `CRM_DEMO_ENABLED=false`，并在实际部署前确认 `.env`、HTTPS、管理账号及防火墙配置。

如需改用其他 Web 端口，须同时设置 `WEB_PORT` 和对应的 `CORS_ORIGINS`；后者列出实际在浏览器中使用的完整来源，例如 `http://127.0.0.1:8089,http://localhost:8089`。

## 备份、恢复与升级

在项目根目录运行以下命令，目标文件必须尚不存在。SQL 备份包含客户与联系人信息，应放在受控位置，不要上传到仓库或公开链接。

```sh
mkdir -p backups
sh scripts/backup_db.sh backups/crm-$(date +%Y%m%d-%H%M%S).sql
```

恢复时优先建一个**独立 Compose 项目**，用同版 MySQL 8.4 在新数据卷中验证备份；不要在尚未核对前覆盖现用数据卷。以下示例使用同一份仅限本机的 `.env`，并将恢复实例的 Web 端口改为 8089：

```sh
docker compose --project-name crm-restore up -d mysql
docker compose --project-name crm-restore exec -T mysql sh -c 'MYSQL_PWD="$MYSQL_PASSWORD" exec mysql -u"$MYSQL_USER" "$MYSQL_DATABASE"' < backups/已保存的备份.sql
WEB_PORT=8089 CORS_ORIGINS=http://127.0.0.1:8089,http://localhost:8089 docker compose --project-name crm-restore up --build -d backend frontend
```

在 `http://127.0.0.1:8089/` 登录，核对客户、联系人、商机、跟进和任务，再验证管理员、销售经理和销售人员的访问范围。升级前保留旧版本源码与镜像、生成并验证备份；Flyway 会应用新增迁移，不能把“容器启动成功”当成数据验收。回滚时以旧版本和备份启动独立实例，核对数据与权限后再切换访问入口。

从旧版固定演示密码升级时，若数据库中的 `admin` 仍使用公开示例密码，启动时会要求有效的 `CRM_ADMIN_PASSWORD` 并轮换密码；`demo`、`manager` 若仍使用公开示例密码，会按 `CRM_DEMO_ENABLED` 轮换或停用。关闭演示开关不会自动删除旧业务数据，管理员应检查已有账号和演示记录。数据库迁移新增令牌版本字段，旧令牌不再可用。

V3 迁移新增 `crm_audit_log`，不改动既有业务表。升级后须验证账号管理、客户交接、CSV 导入导出和操作记录；如需回退到 V2 程序，应从升级前备份启动独立实例，不能只回退程序镜像。实际交付核对项见[交付验收清单](交付验收清单.md)。

此编排是单机验收样例，不提供高可用、自动异地备份和企业身份平台接入。商业部署的保留期限、备份演练、监控和授权范围须在具体项目中确定。

部署支持与私有化方案请联系 [知华科技](https://www.zhuatech.cn/)（上海如静知华信息科技有限公司）。
