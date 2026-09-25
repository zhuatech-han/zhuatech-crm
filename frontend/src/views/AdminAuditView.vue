<!-- 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 -->
<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api/crm'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const events = ref([])
const actions = { CUSTOMER_CREATE: '新建客户', CUSTOMER_UPDATE: '修改客户', CUSTOMER_TRANSFER: '转移客户', CUSTOMER_IMPORT: '导入客户', CONTACT_CREATE: '新增联系人', CONTACT_DELETE: '删除联系人', OPPORTUNITY_CREATE: '新建商机', OPPORTUNITY_STAGE: '推进商机', FOLLOW_UP_CREATE: '记录跟进', TASK_CREATE: '新建任务', TASK_STATUS: '更新任务', TASK_DELETE: '删除任务', ACCOUNT_CREATE: '创建账号', ACCOUNT_ENABLE: '启用账号', ACCOUNT_DISABLE: '停用账号', ACCOUNT_PASSWORD_RESET: '重置密码', ACCOUNT_PASSWORD_CHANGE: '修改密码' }
const targets = { CUSTOMER: '客户', CUSTOMER_BATCH: '客户档案', CONTACT: '联系人', OPPORTUNITY: '商机', FOLLOW_UP: '跟进', TASK: '任务', USER: '账号' }
onMounted(async () => {
  if (auth.user?.role !== 'ADMIN') return router.replace('/profile')
  try { events.value = await api.auditEvents() } catch { /* 请求层已提示。 */ }
})
</script>

<template>
  <div class="page safe-top">
    <van-nav-bar title="操作记录" left-arrow @click-left="router.back()" />
    <section v-for="event in events" :key="event.id" class="card event">
      <div class="head"><strong>{{ actions[event.action] || event.action }}</strong><span>{{ event.createdAt?.replace('T', ' ').slice(0, 16) }}</span></div>
      <p>{{ event.actorUsername }} · {{ targets[event.targetType] || '记录' }}{{ event.targetId ? ` ${event.targetId}` : '' }}</p>
      <p v-if="event.detail">{{ event.detail }}</p>
    </section>
    <div v-if="!events.length" class="empty">暂无操作记录</div>
  </div>
</template>

<style scoped>
.van-nav-bar{background:transparent;margin:-8px -16px 14px}.event{margin-bottom:10px}.head{display:flex;justify-content:space-between;gap:8px}.head strong{font-size:14px}.head span,.event p{font-size:12px;color:#71829c}.event p{margin:8px 0 0;line-height:1.5}
</style>
