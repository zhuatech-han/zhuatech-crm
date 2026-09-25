<!-- 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 -->
<script setup>
const entries = [
  ['客户管理', 'friends-o', '/customers'],
  ['跟进记录', 'chat-o', '/follow-ups'],
  ['销售任务', 'todo-list-o', '/tasks'],
  ['商机漏斗', 'chart-trending-o', '/opportunities']
]
</script>

<template>
  <div class="page safe-top">
    <van-nav-bar title="CRM 工作台" left-arrow @click-left="$router.back()" />
    <section class="card">
      <div class="section-heading">业务入口</div>
      <div class="grid">
        <router-link v-for="entry in entries" :key="entry[0]" :to="entry[2]" class="app-icon">
          <span class="circle"><van-icon :name="entry[1]" /></span>
          <span>{{ entry[0] }}</span>
        </router-link>
      </div>
    </section>
    <div class="version">ZhuaTech CRM Community v1.0.0</div>
  </div>
</template>

<style scoped>
.van-nav-bar{background:transparent;margin:-8px -16px 14px}
.section-heading{font-weight:700;margin-bottom:18px}
.app-icon{text-decoration:none}
.version{text-align:center;color:#a0adb1;font-size:12px;margin:30px 0}
</style>
