# Copyright 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd. · 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2
.PHONY: up down logs test build
up:
	docker compose up --build -d
down:
	docker compose down
logs:
	docker compose logs -f
test:
	docker compose build backend frontend
build:
	docker compose build
