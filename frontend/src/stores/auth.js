/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
import { defineStore } from 'pinia'
import { api } from '../api/crm'
export const useAuthStore = defineStore('auth', { state:()=>({ user: JSON.parse(localStorage.getItem('zhuatech_crm_user') || 'null') }), actions:{ async login(form){ this.setSession(await api.login(form)) }, setSession(result){ localStorage.setItem('zhuatech_crm_token',result.token); this.user=result.user; localStorage.setItem('zhuatech_crm_user',JSON.stringify(result.user)) }, logout(){ localStorage.removeItem('zhuatech_crm_token'); localStorage.removeItem('zhuatech_crm_user'); this.user=null } } })
