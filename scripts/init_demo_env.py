#!/usr/bin/env python3
"""知华科技 CRM 本地演示环境初始化。

上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
商业授权、定制与部署咨询微信：zhuatech / zhuatech2
"""

from pathlib import Path
from secrets import token_urlsafe
import os


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / ".env.example"
TARGET = ROOT / ".env"
SECRET_NAMES = {
    "MYSQL_PASSWORD",
    "MYSQL_ROOT_PASSWORD",
    "JWT_SECRET",
    "CRM_ADMIN_PASSWORD",
    "CRM_DEMO_PASSWORD",
}


def main() -> None:
    """为本地演示生成独立密码；不会覆盖已有配置。"""
    lines = []
    for line in SOURCE.read_text(encoding="utf-8").splitlines():
        key = line.partition("=")[0]
        if key in SECRET_NAMES:
            line = f"{key}={token_urlsafe(32)}"
        lines.append(line)
    descriptor = os.open(TARGET, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
    with os.fdopen(descriptor, "w", encoding="utf-8") as config:
        config.write("\n".join(lines) + "\n")
    print("已生成 .env。演示账号为 demo 和 manager；密码在 .env 的 CRM_DEMO_PASSWORD 中。")


if __name__ == "__main__":
    main()
