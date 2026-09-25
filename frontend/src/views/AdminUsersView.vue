<!-- 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 -->
<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showSuccessToast } from 'vant'
import { api } from '../api/crm'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const users = ref([])
const form = reactive({ username: '', fullName: '', role: 'SALES', password: '' })
const resetId = ref(null)
const resetPassword = ref('')
const busy = ref(false)
const roles = { ADMIN: '管理员', SALES_MANAGER: '销售经理', SALES: '销售人员' }

async function load() {
  try { users.value = await api.users() } catch { /* 请求层已提示。 */ }
}
onMounted(() => { if (auth.user?.role !== 'ADMIN') router.replace('/profile'); else load() })

async function create() {
  busy.value = true
  try {
    await api.createUser(form)
    form.username = ''; form.fullName = ''; form.password = ''
    showSuccessToast('账号已创建')
    await load()
  } catch { /* 请求层已提示。 */ } finally { busy.value = false }
}

async function toggle(user) {
  try {
    await showConfirmDialog({ title: user.enabled ? '停用账号' : '启用账号', message: `${user.fullName}（${user.username}）` })
    await api.setUserEnabled(user.id, !user.enabled)
    showSuccessToast(user.enabled ? '账号已停用' : '账号已启用')
    await load()
  } catch { /* 取消操作或请求失败。 */ }
}

async function saveReset() {
  busy.value = true
  try {
    await api.resetUserPassword(resetId.value, resetPassword.value)
    resetId.value = null; resetPassword.value = ''
    showSuccessToast('密码已重置，旧登录已失效')
  } catch { /* 请求层已提示。 */ } finally { busy.value = false }
}
</script>

<template>
  <div class="page safe-top account-page">
    <van-nav-bar title="账号管理" left-text="返回" left-arrow @click-left="router.back()" />
    <section class="card account-card">
      <h3>创建账号</h3>
      <van-field v-model="form.username" label="登录账号" placeholder="3—32 位英文、数字或 ._-" autocomplete="off" />
      <van-field v-model="form.fullName" label="姓名" placeholder="输入成员姓名" />
      <van-field v-model="form.password" label="初始密码" type="password" autocomplete="new-password" placeholder="至少 12 位，私下交给本人" />
      <label class="role-row">角色
        <select v-model="form.role" aria-label="新账号角色">
          <option value="SALES">销售人员</option>
          <option value="SALES_MANAGER">销售经理</option>
          <option value="ADMIN">管理员</option>
        </select>
      </label>
      <van-button block color="#2856a8" :loading="busy" :disabled="!form.username || !form.fullName || form.password.length < 12" @click="create">创建账号</van-button>
    </section>
    <section class="card account-card">
      <h3>成员账号</h3>
      <div v-for="user in users" :key="user.id" class="member">
        <div><strong>{{ user.fullName }}</strong><span>{{ user.username }} · {{ roles[user.role] }} · {{ user.enabled ? '启用' : '停用' }}</span></div>
        <div v-if="user.id !== auth.user?.id" class="member-actions">
          <button type="button" @click="resetId = user.id; resetPassword = ''">重置密码</button>
          <button type="button" @click="toggle(user)">{{ user.enabled ? '停用' : '启用' }}</button>
        </div>
      </div>
    </section>
    <section v-if="resetId !== null" class="card account-card">
      <h3>重置密码</h3>
      <p class="hint">新密码请私下交给成员；保存后，该账号原有登录立即失效。</p>
      <van-field v-model="resetPassword" type="password" autocomplete="new-password" label="新密码" placeholder="至少 12 位" />
      <van-button block color="#2856a8" :loading="busy" :disabled="resetPassword.length < 12" @click="saveReset">保存</van-button>
      <van-button block plain @click="resetId = null; resetPassword = ''">取消</van-button>
    </section>
  </div>
</template>

<style scoped>
.account-card{padding:16px;margin-top:16px}.account-card h3{margin:0 0 12px;font-size:16px}
.account-card .van-button{margin-top:12px}.role-row{display:flex;justify-content:space-between;align-items:center;padding:13px 16px;color:#323233;font-size:14px}
.role-row select{border:1px solid #d9e1ed;border-radius:8px;background:white;padding:7px;color:#253650}
.member{padding:13px 0;border-top:1px solid #edf0f4}.member:first-of-type{border-top:0}.member strong{display:block;font-size:14px}.member span{display:block;color:#71829c;font-size:12px;margin-top:4px}
.member-actions{display:flex;gap:16px;margin-top:10px}.member-actions button{border:0;background:none;color:#2856a8;padding:0;font-size:13px}.hint{font-size:12px;color:#71829c;line-height:1.6}
</style>
