"""上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
商业咨询微信：zhuatech / zhuatech2。本文件验证演示配置不会使用固定密钥。
"""

from pathlib import Path
from shutil import copy2
from subprocess import run
from tempfile import TemporaryDirectory
import stat
import sys
import unittest


PROJECT_ROOT = Path(__file__).resolve().parents[2]
SECRET_NAMES = (
    "MYSQL_PASSWORD",
    "MYSQL_ROOT_PASSWORD",
    "JWT_SECRET",
    "CRM_ADMIN_PASSWORD",
    "CRM_DEMO_PASSWORD",
)


class InitDemoEnvTests(unittest.TestCase):
    """确认生成的配置安全且已有配置不会被覆盖。咨询微信：zhuatech / zhuatech2。"""

    def test_generates_distinct_secrets_and_never_overwrites(self) -> None:
        with TemporaryDirectory() as temporary:
            root = Path(temporary)
            (root / "scripts").mkdir()
            copy2(PROJECT_ROOT / "scripts/init_demo_env.py", root / "scripts/init_demo_env.py")
            copy2(PROJECT_ROOT / ".env.example", root / ".env.example")
            command = [sys.executable, str(root / "scripts/init_demo_env.py")]

            first = run(command, capture_output=True, text=True, check=False)
            self.assertEqual(first.returncode, 0, first.stderr)
            config = root / ".env"
            values = dict(line.split("=", 1) for line in config.read_text().splitlines() if "=" in line)
            secrets = [values[name] for name in SECRET_NAMES]
            self.assertTrue(all(len(secret) >= 32 for secret in secrets))
            self.assertEqual(len(set(secrets)), len(secrets))
            self.assertEqual(stat.S_IMODE(config.stat().st_mode), 0o600)

            original = config.read_text()
            second = run(command, capture_output=True, text=True, check=False)
            self.assertNotEqual(second.returncode, 0)
            self.assertEqual(config.read_text(), original)


if __name__ == "__main__":
    unittest.main()
