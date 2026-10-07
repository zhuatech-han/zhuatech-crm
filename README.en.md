[中文](README.md) | [English](README.en.md)

# ZhuaTech CRM — Mobile Customer Relationship Management

[Individual non-commercial learning license](LICENSE) · [Java 21 / Spring Boot 4](backend/pom.xml) · [Vue 3](frontend/package.json)

ZhuaTech CRM is a source-available mobile CRM from **ZhiHua Technology (Shanghai Rujing Zhihua Information Technology Co., Ltd.)**. Website: [https://www.zhuatech.cn/](https://www.zhuatech.cn/). Salespeople manage customers, contacts, opportunities, follow-ups and tasks through a Vue 3 H5 application backed by Java 21, Spring Boot and MySQL.

The screenshots below show the running application with fictional acceptance data. The home page also provides the current statistics; there is no separate analytics dashboard. The application UI is Chinese.

| Sign-in | Home and statistics | Customer business page |
| --- | --- | --- |
| ![CRM sign-in](docs/screenshots/login.png) | ![Customer and pipeline statistics](docs/screenshots/home-statistics.png) | ![Running customer list](docs/screenshots/business.png) |

| Account administration | Operation history | Personal settings |
| --- | --- | --- |
| ![Administrator account management](docs/screenshots/admin-users.png) | ![Actual business operation history](docs/screenshots/audit.png) | ![Profile and password settings](docs/screenshots/account-settings.png) |

Additional running pages: [customer details](docs/images/crm-customer-detail.jpg), [ownership transfer](docs/images/crm-customer-transfer.jpg) and [CSV data management](docs/images/crm-admin-data.jpg). These pages cover customer-related records, reassignment and controlled data migration.

> **Use restriction:** This project is licensed only for individuals' non-commercial learning, research and technical exchange. Company evaluation, internal use, production deployment, SaaS, project delivery, commercial integration and any direct or indirect commercial use require prior written authorization from Shanghai Rujing Zhihua Information Technology Co., Ltd. Read the unchanged [LICENSE](LICENSE).

This is source-available software with non-commercial restrictions, not OSI-approved open-source software. Enterprise digitization, AI customization, private deployment, software outsourcing, implementation, FDE services, OPC support and deeper customization are available by arrangement through [ZhiHua Technology](https://www.zhuatech.cn/).

## Suitable uses and application roles

The project supports personal study of mobile sales workflows, customer ownership, handover, CSV migration and Spring Security / JPA business modeling. With written commercial authorization it can serve as a customization foundation; organizational evaluation and internal use remain subject to the existing license.

The sales interface includes customers, contacts, opportunities, follow-ups, tasks and profile settings. Managers have a team customer view and can transfer customers. Administrators use **My profile** in the same H5 application to access account management, customer CSV data and operation history. There is no separate desktop administration application.

## Implemented features

- Customer records: name, industry, grade, status, source, owner and next follow-up date.
- Contacts: primary contact, job title, phone, email and notes.
- Opportunities: amount, stage, probability, expected closing date and next action.
- Follow-ups: communication method, content, associated opportunity and future action.
- Sales tasks: optional customer association, priority, due date and completion state.
- Workbench: customer count, active opportunities, pipeline amount, follow-ups due and pending tasks.
- Three fixed roles: administrator, sales manager and salesperson, with customer ownership access controls.
- Account management: administrator creation, enable/disable and password reset for other users; personal password change invalidates old tokens.
- Customer transfer: manager or administrator supplies a reason; related opportunities and tasks are reassigned with the customer.
- Customer CSV migration: administrator-only UTF-8 import/export. Invalid rows reject the entire import batch.
- Operation history: administrators view the latest 100 core modification events; avoid sensitive information in handover reasons.
- Mobile H5: customer cards, opportunity updates, phone links and follow-up entry.
- Engineering support: JWT, BCrypt, MySQL migrations, database backup, Docker Compose, Nginx and GitHub Actions CI.

## Architecture

| Layer | Technology |
| --- | --- |
| H5 frontend | Vue 3, Vite, Vant, Pinia, Vue Router, Axios |
| Backend | Java 21, Spring Boot 4.0.7, Spring Security, Spring Data JPA, Flyway |
| Database | MySQL 8.4; backend tests use H2 |
| Deployment | Docker, Docker Compose, Nginx |

Browser → Nginx / Vue H5 → same-origin REST API with JWT → Spring Boot → JPA → MySQL. Flyway applies versioned schema changes before entity validation. Java packages use `cn.zhuatech.crm`.

See the [operating manual](docs/操作手册.md), [architecture](docs/ARCHITECTURE.md) and [API reference](docs/API.md). These supporting documents are currently Chinese.

## Local learning setup

Requirements: Python 3.8+, Docker Desktop or Docker Engine 24+, and Docker Compose v2+. For direct development use JDK 21, Maven 3.9, MySQL 8, Node.js 24.19.0+ and npm 11.

Run from the repository root:

```bash
python3 scripts/init_demo_env.py
docker compose up --build -d
```

Open [http://localhost:8088](http://localhost:8088). The script creates a Git-ignored `.env` readable only by its owner and refuses to overwrite an existing file. Keep generated passwords private.

| Role | Username | Password configuration |
| --- | --- | --- |
| Sales demo | `demo` | `CRM_DEMO_PASSWORD` in `.env` |
| Manager demo | `manager` | `CRM_DEMO_PASSWORD` in `.env` |
| Administrator | `admin` | `CRM_ADMIN_PASSWORD` in `.env` |

The learning configuration binds Web access to `127.0.0.1`. On an empty database, enabled demo initialization creates two fictional customers, an opportunity, contacts, follow-ups and tasks. Set `CRM_DEMO_ENABLED=false` to initialize only the administrator, without demo accounts or business data.

The administrator can manage accounts through **My profile → Account management**. Password resets and account disablement invalidate previously issued tokens. Changing environment variables does not reset passwords for existing accounts. Disabling demo mode does not delete old business records.

Stop services with `docker compose down`. Removing volumes permanently clears the database; use `docker compose down -v` only for an intentional disposable demo reset.

## Direct development

Configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` and `CRM_ADMIN_PASSWORD`. The initial administrator password must have at least 12 characters. Demo accounts additionally require `CRM_DEMO_ENABLED=true` and `CRM_DEMO_PASSWORD`.

```bash
cd backend
mvn spring-boot:run
```

In a separate terminal:

```bash
cd frontend
npm ci
npm run dev
```

The development UI is at [http://localhost:5173](http://localhost:5173); Vite proxies `/api` to `http://localhost:8080`. Configuration names are listed in [.env.example](.env.example).

## Database initialization and configuration

On the first empty database startup, Flyway runs migrations in `backend/src/main/resources/db/migration/`: V1 creates business tables, V2 adds account token versions, and V3 adds operation audit storage. Hibernate then validates entities against the schema. Add new migration versions for upgrades; do not rewrite published migrations. The `mysql_data` volume preserves data across restarts.

| Configuration | Purpose |
| --- | --- |
| `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD` | Compose database and initial credentials; example password values are empty |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Direct backend database connection; Compose constructs and injects these |
| `CRM_ADMIN_PASSWORD`, `CRM_DEMO_ENABLED`, `CRM_DEMO_PASSWORD` | Empty-database administrator and optional fictional demo initialization |
| `JWT_SECRET` | Independent random signing secret; JWT lifetime is 24 hours; password changes or account disablement invalidate old tokens |
| `WEB_PORT`, `WEB_BIND_ADDRESS`, `CORS_ORIGINS` | Web entry and complete browser origins; loopback binding is the local default |
| `ZHUATECH_AI_PROVIDER`, `ZHUATECH_AI_BASE_URL`, `ZHUATECH_AI_MODEL`, `ZHUATECH_AI_API_KEY` | Optional compatible model; `local` needs no key; missing configuration or request failure returns local rule advice |

The existing Compose file **does not forward the AI environment variables to backend**. Adding them to the root `.env` alone will not enable an external model. Export them for direct backend execution, or explicitly configure container environment injection. A real model request sends the opportunity context from its request: authorize and redact that information before configuring it. Learning workflows need no paid model; no external provider connection is claimed as verified.

## Tests and acceptance

```bash
# Repository root: configuration generation and release materials
python3 -m unittest discover -s scripts/tests -p 'test_*.py'
node scripts/verify-release.mjs

# Backend: unit/integration tests and packaging
cd backend
mvn -B verify

# Frontend: locked installation and production build
cd ../frontend
npm ci
npm run build

# Repository root: Compose and images, after creating .env
cd ..
docker compose config --quiet
docker compose build
git diff --check
```

Backend tests use H2 with Flyway disabled, so they do not validate MySQL migrations. Backend container builds run `clean package` with tests enabled. When the Docker test layer is cached, run the current tests separately. No separate frontend test, formatter or lint command is configured; a production build is not a complete browser test.

Use an independent project name, port and new database volume for runtime acceptance: check `/health` for `UP`, sign in as the administrator, create sales and manager accounts, and exercise customer → contact → opportunity → follow-up → task → ownership transfer. Verify denied access, atomic CSV import, account disablement and old-token rejection. Compare data after restart, then restore the backup into another independent volume and verify data and role access.

## Repository layout

```text
zhuatech-crm/
├── backend/        # Java application, tests and versioned migrations
├── frontend/       # Vue mobile H5
├── deploy/         # Deployment and acceptance documentation
├── docs/           # Operating manual, architecture, API and actual screenshots
├── scripts/        # Demo configuration, backup and release checks
├── compose.yaml    # MySQL, backend and frontend
├── LICENSE
├── README.md       # Chinese homepage
└── README.en.md    # English homepage
```

## Independent rule APIs

These capabilities are available as APIs; some have no H5 controls. Consult [API documentation](docs/API.md) and the actual request DTOs/tests before using them.

- Lead scoring, customer health, weighted opportunity forecast, next best sales action and sales coaching. The coach supports optional compatible-model advice and a local rule fallback.
- Lead conversion validation, [opportunity stage gates](docs/ENTERPRISE_OPPORTUNITY_GATE.md), [quotation and margin approval](docs/ENTERPRISE_QUOTATION_APPROVAL.md), [ownership governance](docs/ENTERPRISE_ACCOUNT_OWNERSHIP_TRANSFER.md), and [customer merge validation](docs/ENTERPRISE_CUSTOMER_ACCOUNT_MERGE.md).

These are rule services for further development. They do not demonstrate integration with an existing enterprise CRM, ERP, master-data system or approval workflow. The ownership governance rule API does not change ownership; the actual transfer endpoint is `/api/customers/{id}/owner`.

## Deployment, backup and upgrades

This Compose configuration is a single-host learning example. For a port override, update the full browser origins as well:

```sh
WEB_PORT=18188 CORS_ORIGINS=http://localhost:18188,http://127.0.0.1:18188 docker compose up --build -d
```

The default frontend is `http://127.0.0.1:8088/`; Nginx exposes a health summary at `/health`. Backend health is `http://backend:8080/actuator/health` inside the Compose network; backend and database ports are not published separately. MySQL, backend and frontend wait for health in sequence.

For the current default Compose project, create a backup at a new path:

```sh
mkdir -p backups
sh scripts/backup_db.sh backups/crm-$(date +%Y%m%d-%H%M%S).sql
```

Backups contain customer/contact information. Keep them private and out of source control. Use a separate Compose project with MySQL 8.4 and a new volume to restore and verify the backup before switching traffic. See [deployment instructions](deploy/README.md) and the [delivery checklist](deploy/交付验收清单.md) for restore commands, migration verification and legacy demo-password handling.

Before upgrading, retain source/images, create a verified backup and review new Flyway migrations. Roll back with the prior version and a verified pre-upgrade backup in an independent instance; do not attach old code directly to a newer schema. Production or business use requires written commercial authorization, HTTPS, access restrictions, secret management, reliable backups, restore drills and monitoring.

For failures, check health and redacted logs: verify database configuration and volume state; investigate roles/ownership for 403 responses; override `WEB_PORT` for conflicts; retain local rule mode if a model is unconfigured. Do not resolve permission failures by relaxing access controls.

## Known limitations and roadmap

- Roles are fixed. Editable role/menu matrices, departments, tenants, field-level permissions and general dictionary/settings administration are not implemented.
- H5 opportunity stage/probability changes are available; the independent stage-gate, quotation and merge APIs calculate rule results, without automatic enforcement in opportunity updates, a live approval workflow or database record merging.
- Audit queries return the latest 100 events. CSV import only adds records, with 1–500 rows and a 1 MB file limit; exports are capped at 10,000 customers. Lists have no general server-side pagination.
- Full quotation, contract, order, payment, invoice, notification, enterprise identity and high-availability capabilities are not implemented.
- On 2026-10-07, `npm audit` reported four high-severity dependency findings for the current lockfile, involving Axios, Vue and related dependencies. This is not an exploitability assessment or a passed security gate. Dependency upgrades require separate evaluation and retesting before deployment.
- Paid model calls, enterprise integrations, production load and production security are unverified. Source availability does not imply production readiness.

Roadmap items include lead pools, unassigned customer pools, deduplication, assignment/reclaim workflows, products and commercial documents, sales targets/rankings, richer funnel/forecast reports, desktop administration, configurable fields, long-term audit archiving, enterprise messaging/calling integrations, multi-tenancy, external integration APIs, webhooks and configurable workflows. They are future work, not implemented integrations.

## License, contributions and contact

The unchanged [ZhuaTech CRM Community Source License](LICENSE) permits individuals' non-commercial learning, research, experiments and technical exchange, with the license, copyright, [NOTICE](NOTICE) and source attribution retained. Any commercial or organizational use requires prior written authorization from Shanghai Rujing Zhihua Information Technology Co., Ltd. Publicly visible source grants no trademark permission. Third-party components retain their own licenses. The software is provided as-is; agreed delivery and support responsibilities belong in a written agreement.

Use [contribution guidance](CONTRIBUTING.md) for issues and pull requests. Report security vulnerabilities privately as described in [SECURITY.md](SECURITY.md).

For commercial licensing, deeper CRM customization, private deployment, integration and support, contact **ZhiHua Technology (Shanghai Rujing Zhihua Information Technology Co., Ltd.)**:

- Website: [https://www.zhuatech.cn/](https://www.zhuatech.cn/)
- Email: [han@zhuatech.cn](mailto:han@zhuatech.cn)
- Email: [jack@zhuatech.cn](mailto:jack@zhuatech.cn)
- WhatsApp: [+86 17521234993](https://wa.me/8617521234993)

Copyright © 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd.
