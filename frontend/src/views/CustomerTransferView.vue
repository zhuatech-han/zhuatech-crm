<!-- 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 -->
<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showConfirmDialog, showSuccessToast } from 'vant'
import { api } from '../api/crm'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const id = Number(route.params.id)
const customer = ref(null)
const users = ref([])
const form = reactive({ ownerId: '', reason: '' })
const saving = ref(false)

onMounted(async () => {
  if (!['ADMIN', 'SALES_MANAGER'].includes(auth.user?.role)) return router.replace('/customers')
  try {
    const [loadedCustomer, loadedUsers] = await Promise.all([api.customer(id), api.assignableUsers()])
    customer.value = loadedCustomer
    users.value = loadedUsers
  } catch { router.replace('/customers') }
})

async function save() {
  const target = users.value.find(user => user.id === Number(form.ownerId))
  if (!target) return
  try {
    await showConfirmDialog({ title: '调整客户负责人', message: `客户、关联商机和任务将转给 ${target.fullName}。` })
  } catch { return }
  saving.value = true
  try {
    await api.transferCustomer(id, { ownerId: target.id, reason: form.reason.trim() })
    showSuccessToast('负责人已调整')
    router.replace(`/customers/${id}`)
  } catch { /* 请求层已提示。 */ } finally { saving.value = false }
}
</script>

<template>
  <div class="page safe-top">
    <van-nav-bar title="调整负责人" left-arrow @click-left="router.back()" />
    <section v-if="customer" class="card transfer-card">
      <h2>{{ customer.name }}</h2>
      <p>当前负责人：{{ customer.ownerName }}</p>
      <label for="new-owner">新负责人</label>
      <select id="new-owner" v-model="form.ownerId">
        <option value="" disabled>选择启用中的成员</option>
        <option v-for="user in users.filter(item => item.id !== customer.ownerId)" :key="user.id" :value="user.id">{{ user.fullName }}（{{ user.username }}）</option>
      </select>
      <van-field v-model="form.reason" label="转移原因" type="textarea" maxlength="200" rows="3" show-word-limit placeholder="填写原因，避免敏感信息" />
      <van-button block color="#2856a8" :disabled="!form.ownerId || !form.reason.trim()" :loading="saving" @click="save">确认转移</van-button>
    </section>
  </div>
</template>

<style scoped>
.van-nav-bar{background:transparent;margin:-8px -16px 14px}.transfer-card{padding:18px}.transfer-card h2{margin:0 0 8px;font-size:18px}.transfer-card p{font-size:13px;color:#71829c;margin:0 0 22px}.transfer-card label{display:block;font-size:14px;margin-bottom:8px}.transfer-card select{width:100%;padding:12px;border:1px solid #d9e1ed;border-radius:10px;background:white}.transfer-card .van-field{padding:16px 0}.transfer-card .van-button{margin-top:12px}
</style>
