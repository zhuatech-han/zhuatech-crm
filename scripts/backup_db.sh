#!/bin/sh
# 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2
# 从当前 Compose 项目的 MySQL 生成单一事务备份，不覆盖已有文件。
set -eu

if [ "$#" -ne 1 ]; then
  echo "用法：sh scripts/backup_db.sh /安全目录/crm-backup.sql" >&2
  exit 2
fi

target=$1
if [ -e "$target" ]; then
  echo "备份文件已存在，未覆盖：$target" >&2
  exit 2
fi

umask 077
partial="${target}.partial.$$"
trap 'rm -f "$partial"' EXIT HUP INT TERM
docker compose exec -T mysql sh -c 'MYSQL_PWD="$MYSQL_PASSWORD" exec mysqldump --single-transaction --routines --triggers --events --no-tablespaces --hex-blob --default-character-set=utf8mb4 -u"$MYSQL_USER" "$MYSQL_DATABASE"' > "$partial"
mv "$partial" "$target"
trap - EXIT HUP INT TERM
echo "数据库备份已写入：$target"
