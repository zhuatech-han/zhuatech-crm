<!-- 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 -->
<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast } from 'vant'
import { useAuthStore } from '../stores/auth'

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const router = useRouter()
const auth = useAuthStore()

async function submit() {
  loading.value = true
  try {
    await auth.login(form)
    showSuccessToast('登录成功')
    router.replace('/')
  } catch {
    // 请求层已展示失败原因，保持在登录页供用户重试。
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login safe-top">
    <img class="logo" src="/zhuatech-logo.jpg" alt="知华科技 Logo" />
    <h1>知华 CRM</h1>
    <div class="login-card">
      <van-field v-model="form.username" label="账号" placeholder="请输入账号" left-icon="contact-o" />
      <van-field v-model="form.password" label="密码" type="password" placeholder="请输入密码" left-icon="closed-eye" />
      <van-button block round color="#2856a8" :disabled="!form.username || !form.password" :loading="loading" @click="submit">登录</van-button>
    </div>
    <a href="https://www.zhuatech.cn/" target="_blank" rel="noopener">知华科技官网</a>
    <small>商业授权与定制部署：微信 zhuatech / zhuatech2</small>
  </div>
</template>

<style scoped>
.login { min-height: 100vh; padding: 14vh 24px 30px; background: #f7f9fd; text-align: center; }
.logo { width: 66px; height: 66px; border-radius: 16px; object-fit: cover; }
.login h1 { margin: 18px 0 28px; color: #18345e; }
.login-card { width: 100%; max-width: 440px; margin: auto; background: #fff; border-radius: 24px; padding: 26px 18px; box-shadow: 0 20px 50px #244b5214; text-align: left; }
.van-button { margin-top: 24px; }
.login > a { display: block; color: #2856a8; text-decoration: none; font-size: 13px; margin-top: 36px; }
.login > small { display: block; color: #71829c; font-size: 11px; margin-top: 8px; }
</style>
