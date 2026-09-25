<!-- Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 -->
<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast } from 'vant'
import { useAuthStore } from '../stores/auth'
import { api } from '../api/crm'

const auth = useAuthStore()
const router = useRouter()
const roles = { ADMIN: '系统管理员', SALES_MANAGER: '销售经理', SALES: '销售人员' }
const password = reactive({ currentPassword: '', newPassword: '' })
const saving = ref(false)

async function savePassword() {
  saving.value = true
  try {
    auth.setSession(await api.changePassword(password))
    password.currentPassword = ''
    password.newPassword = ''
    showSuccessToast('密码已修改，其他设备需重新登录')
  } catch {
    // 请求层已提示错误，保留输入供修改。
  } finally {
    saving.value = false
  }
}
function logout() { auth.logout(); router.replace('/login') }
</script>

<template>
  <div class="page safe-top">
    <section class="profile brand-gradient">
      <div class="avatar">{{ auth.user?.fullName?.slice(0, 1) }}</div>
      <div><h2>{{ auth.user?.fullName }}</h2><p>{{ roles[auth.user?.role] }} · {{ auth.user?.position }}</p></div>
    </section>
    <section class="card details">
      <van-cell title="登录账号" :value="auth.user?.username" icon="contact-o" />
      <van-cell title="手机号码" :value="auth.user?.phone || '未设置'" icon="phone-o" />
      <van-cell title="电子邮箱" :value="auth.user?.email || '未设置'" icon="envelop-o" />
      <van-cell title="销售任务" value="查看待办" icon="todo-list-o" is-link to="/tasks" />
      <van-cell v-if="auth.user?.role === 'ADMIN'" title="账号管理" icon="manager-o" is-link to="/admin/users" />
      <van-cell v-if="auth.user?.role === 'ADMIN'" title="客户数据" icon="records-o" is-link to="/admin/data" />
      <van-cell v-if="auth.user?.role === 'ADMIN'" title="操作记录" icon="notes-o" is-link to="/admin/audit" />
      <van-cell title="项目主页" value="zhuatech.cn" icon="link-o" is-link url="https://www.zhuatech.cn/" />
    </section>
    <section class="card password-card">
      <h3>修改密码</h3>
      <van-field v-model="password.currentPassword" label="当前密码" type="password" autocomplete="current-password" />
      <van-field v-model="password.newPassword" label="新密码" type="password" autocomplete="new-password" placeholder="至少 12 位" />
      <van-button block color="#2856a8" :disabled="!password.currentPassword || password.newPassword.length < 12" :loading="saving" @click="savePassword">保存新密码</van-button>
    </section>
    <button class="logout" @click="logout">退出登录</button>
    <p class="copyright">© 2026 上海如静知华信息科技有限公司<br />仅限个人非商业学习交流 · 商用须书面授权</p>
  </div>
</template>

<style scoped>
.profile{padding:26px 22px;border-radius:22px;display:flex;align-items:center;gap:16px}
.avatar{width:58px;height:58px;border-radius:19px;background:#ffffff2e;display:grid;place-items:center;font-size:25px;font-weight:700}
.profile h2{margin:0 0 6px}.profile p{margin:0;opacity:.76;font-size:13px}
.details{padding:7px;margin-top:16px}.password-card{padding:16px;margin-top:16px}.password-card h3{margin:0 0 12px;font-size:16px}.password-card .van-button{margin-top:14px}
.logout{width:100%;height:48px;border:0;background:white;color:#d45151;border-radius:15px;font-size:15px;margin-top:16px}
.copyright{text-align:center;color:#9aa7ab;font-size:11px;line-height:1.7;margin-top:28px}
</style>
