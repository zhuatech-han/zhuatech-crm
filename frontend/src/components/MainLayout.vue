<!-- 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 -->
<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const active = computed({
  get() {
    if (route.path === '/') return '/'
    if (route.path.startsWith('/customers')) return '/customers'
    if (route.path.startsWith('/opportunities')) return '/opportunities'
    if (route.path.startsWith('/profile')) return '/profile'
    return ''
  },
  set(path) { router.push(path) }
})
</script>

<template>
  <main class="app-shell"><router-view /></main>
  <van-tabbar v-model="active" safe-area-inset-bottom active-color="#2856a8">
    <van-tabbar-item name="/" icon="wap-home-o">首页</van-tabbar-item>
    <van-tabbar-item name="/customers" icon="friends-o">客户</van-tabbar-item>
    <van-tabbar-item name="/opportunities" icon="chart-trending-o">商机</van-tabbar-item>
    <van-tabbar-item name="/profile" icon="user-o">我的</van-tabbar-item>
  </van-tabbar>
</template>
