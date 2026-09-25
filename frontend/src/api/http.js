/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
import axios from 'axios'
import { showFailToast } from 'vant'
const http = axios.create({ baseURL: import.meta.env.VITE_API_BASE || '/api', timeout: 10000 })
http.interceptors.request.use(config => { const token=localStorage.getItem('zhuatech_crm_token'); if(token) config.headers.Authorization=`Bearer ${token}`; return config })
http.interceptors.response.use(
  response => response.config.responseType === 'blob' ? response.data : response.data.data,
  async error => {
    let message = error.response?.data?.message
    if (!message && error.response?.data instanceof Blob) {
      try { message = JSON.parse(await error.response.data.text()).message } catch { /* 非 JSON 错误。 */ }
    }
    showFailToast(message || '网络连接异常')
    if (error.response?.status === 401 && location.pathname !== '/login') {
      localStorage.removeItem('zhuatech_crm_token')
      localStorage.removeItem('zhuatech_crm_user')
      location.href = '/login'
    }
    return Promise.reject(error)
  }
)
export default http
