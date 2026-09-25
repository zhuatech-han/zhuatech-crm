# ZhuaTech CRM REST API

Copyright © 2026 上海如静知华信息科技有限公司。官网：https://www.zhuatech.cn/ · 商业咨询微信：`zhuatech` / `zhuatech2`。

基础路径为 `/api`。除登录外，请在请求头中传入 `Authorization: Bearer <token>`。统一响应结构：

```json
{"success":true,"message":"操作成功","data":{},"timestamp":"2026-07-25T00:00:00Z"}
```

| 方法 | 路径 | 功能 |
| --- | --- | --- |
| POST | `/auth/login` | 登录并获取 JWT |
| GET | `/auth/me` | 获取当前用户 |
| POST | `/auth/change-password` | 本人提供当前密码和新密码，换取新令牌；旧令牌立即失效 |
| GET / POST | `/users` | 管理员列出 / 创建账号，密码不出现在响应中 |
| GET | `/users/assignable` | 经理和管理员列出可接收客户的启用成员 |
| PATCH | `/users/{id}/password` | 管理员重置其他账号密码，旧令牌立即失效 |
| PATCH | `/users/{id}/enabled` | 管理员启停其他账号，旧令牌立即失效 |
| GET | `/dashboard` | 客户、商机、漏斗、跟进和任务统计 |
| GET / POST | `/customers` | 查询 / 新建客户 |
| GET / PUT | `/customers/{id}` | 客户详情 / 更新客户 |
| PATCH | `/customers/{id}/owner` | 经理或管理员提交 `ownerId` 和 `reason`，实际转移客户及关联商机、任务 |
| GET | `/customers/template` | 管理员下载客户 CSV 空模板 |
| GET | `/customers/export` | 管理员导出全部客户 CSV，含负责人账号 |
| POST | `/customers/import` | 管理员上传 `file` 多部分表单，整批校验后导入 1—500 条客户 |
| GET | `/audit-events` | 管理员查看最近 100 条核心业务修改记录 |
| GET / POST | `/contacts?customerId={id}` | 客户联系人列表 / 新建联系人 |
| DELETE | `/contacts/{id}` | 删除联系人 |
| GET / POST | `/opportunities` | 商机列表 / 新建商机 |
| PATCH | `/opportunities/{id}/stage` | 推进商机阶段和成交概率 |
| GET | `/follow-ups?customerId={id}` | 客户跟进历史 |
| GET | `/follow-ups/recent` | 当前销售最近十条跟进 |
| POST | `/follow-ups` | 新建跟进记录并更新下次跟进日期 |
| GET / POST | `/tasks` | 销售任务列表 / 新建任务 |
| PATCH / DELETE | `/tasks/{id}` | 更新任务状态 / 删除本人任务 |

销售人员只能访问自己负责的客户及其关联数据；销售经理和管理员可查看全部客户与商机。时间使用 ISO 8601 格式，例如 `2026-07-25T09:00:00`。

创建账号需提供 `username`、`fullName`、`role`（`ADMIN` / `SALES_MANAGER` / `SALES`）和 `password`。修改本人密码需提供 `currentPassword`、`newPassword`；管理员重置其他账号密码需提供 `password`，启停需提供 `enabled` 布尔值。密码须至少 12 个字符、最多 72 字节，且不能使用公开示例值。管理员不能停用自己的账号；停用用户后，其业务记录不会删除。

客户 CSV 使用 UTF-8（可带 BOM），表头以 `/customers/template` 下载的文件为准。状态使用下方枚举，日期使用 `YYYY-MM-DD`；负责人账号必须为已启用的销售人员或经理。导入仅新增，不覆盖已有客户；同批或库内已有同名客户时拒绝，任一行错误时全部不写入。单次导出最多 10000 条客户，超出需数据库备份或定制分批导出。导出会对可能被电子表格当作公式的字段加安全前缀；文件包含客户联系方式，应限制访问和留存。

## 关键枚举

- 客户状态：`LEAD`、`FOLLOWING`、`CUSTOMER`、`INACTIVE`
- 商机阶段：`LEAD`、`DISCOVERY`、`PROPOSAL`、`NEGOTIATION`、`WON`、`LOST`
- 跟进方式：`PHONE`、`WECHAT`、`VISIT`、`EMAIL`、`OTHER`
- 任务优先级：`LOW`、`MEDIUM`、`HIGH`

## 独立规则 API

以下接口输出评分、预测或流程校验结果，不代表已连接企业主数据、ERP 或外部审批系统。

| 方法 | 完整路径 | 功能 |
| --- | --- | --- |
| POST | `/api/crm/insights/lead-score` | 线索评分与分层 |
| POST | `/api/customer-intelligence/health-score` | 客户健康度与经营动作 |
| POST | `/api/customer-intelligence/opportunity-forecast` | 商机加权预测与风险排序 |
| POST | `/api/crm/insights/next-best-action` | 下一最佳销售动作 |
| POST | `/api/crm/ai/sales-coach` | 销售教练与异议处理建议 |
| POST | `/api/enterprise/crm/lead-conversion` | 线索转客户前置校验 |
| POST | `/api/enterprise/crm/opportunity-stage-gate` | 商机阶段门禁 |
| POST | `/api/enterprise/crm/quotation-approval` | 报价与毛利审批校验 |
| POST | `/api/enterprise/crm/account-ownership-transfer` | 独立的复杂交接规则评估；不修改客户归属，实际转移使用 `/api/customers/{id}/owner` |
| POST | `/api/enterprise/crm/customer-account-merge` | 客户主数据合并校验 |

各请求字段和返回值以对应控制器、DTO 与测试为准；企业使用需接入自己的权限、主数据和审批流程。
