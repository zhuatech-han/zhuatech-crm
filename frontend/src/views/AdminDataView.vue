<!-- 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 -->
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showSuccessToast } from 'vant'
import { api } from '../api/crm'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const file = ref(null)
const input = ref(null)
const busy = ref(false)
if (auth.user?.role !== 'ADMIN') router.replace('/profile')

function download(blob, name) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url; link.download = name; link.click()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}
async function getFile(kind) {
  try {
    const blob = kind === 'template' ? await api.customerTemplate() : await api.exportCustomers()
    download(blob, kind === 'template' ? 'crm-customer-template.csv' : 'crm-customers.csv')
  } catch { /* 请求层已提示。 */ }
}
async function importFile() {
  if (!file.value) return
  try { await showConfirmDialog({ title: '导入客户', message: `确认导入 ${file.value.name}？客户名称重复或任何一行有误时，整批不会保存。` }) } catch { return }
  busy.value = true
  try {
    const count = await api.importCustomers(file.value)
    file.value = null
    if (input.value) input.value.value = ''
    showSuccessToast(`已导入 ${count} 条客户`)
  } catch { /* 请求层已提示。 */ } finally { busy.value = false }
}
</script>

<template>
  <div class="page safe-top">
    <van-nav-bar title="客户数据" left-arrow @click-left="router.back()" />
    <section class="card data-card">
      <h2>导出客户</h2>
      <p>下载全部客户档案。文件包含联系方式，请妥善保管。</p>
      <van-button block color="#2856a8" @click="getFile('export')">下载客户 CSV</van-button>
    </section>
    <section class="card data-card">
      <h2>导入客户</h2>
      <p>仅新增客户；每次最多 500 条。先下载模板，填写启用成员的负责人账号。</p>
      <van-button block plain color="#2856a8" @click="getFile('template')">下载空模板</van-button>
      <label class="file-label">选择 UTF-8 CSV 文件<input ref="input" type="file" accept=".csv,text/csv" @change="file=$event.target.files?.[0]||null" /></label>
      <p v-if="file">{{ file.name }}</p>
      <van-button block color="#2856a8" :disabled="!file" :loading="busy" @click="importFile">导入客户</van-button>
    </section>
  </div>
</template>

<style scoped>
.van-nav-bar{background:transparent;margin:-8px -16px 14px}.data-card{padding:18px;margin-bottom:16px}.data-card h2{margin:0 0 8px;font-size:17px}.data-card p{font-size:12px;line-height:1.6;color:#71829c}.data-card .van-button{margin-top:12px}.file-label{display:block;margin-top:22px;font-size:14px}.file-label input{display:block;max-width:100%;margin-top:8px;font-size:12px}
</style>
