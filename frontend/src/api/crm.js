/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
import http from './http'
export const api={
 login:data=>http.post('/auth/login',data),me:()=>http.get('/auth/me'),changePassword:data=>http.post('/auth/change-password',data),dashboard:()=>http.get('/dashboard'),
 users:()=>http.get('/users'),assignableUsers:()=>http.get('/users/assignable'),createUser:data=>http.post('/users',data),resetUserPassword:(id,password)=>http.patch(`/users/${id}/password`,{password}),setUserEnabled:(id,enabled)=>http.patch(`/users/${id}/enabled`,{enabled}),
 customers:keyword=>http.get('/customers',{params:{keyword}}),customer:id=>http.get(`/customers/${id}`),createCustomer:data=>http.post('/customers',data),updateCustomer:(id,data)=>http.put(`/customers/${id}`,data),transferCustomer:(id,data)=>http.patch(`/customers/${id}/owner`,data),
 customerTemplate:()=>http.get('/customers/template',{responseType:'blob'}),exportCustomers:()=>http.get('/customers/export',{responseType:'blob'}),importCustomers:file=>{const data=new FormData();data.append('file',file);return http.post('/customers/import',data)},auditEvents:()=>http.get('/audit-events'),
 contacts:customerId=>http.get('/contacts',{params:{customerId}}),createContact:data=>http.post('/contacts',data),deleteContact:id=>http.delete(`/contacts/${id}`),
 opportunities:customerId=>http.get('/opportunities',{params:customerId?{customerId}:{}}),createOpportunity:data=>http.post('/opportunities',data),updateOpportunityStage:(id,data)=>http.patch(`/opportunities/${id}/stage`,data),
 followUps:customerId=>http.get('/follow-ups',{params:{customerId}}),recentFollowUps:()=>http.get('/follow-ups/recent'),createFollowUp:data=>http.post('/follow-ups',data),
 tasks:()=>http.get('/tasks'),createTask:data=>http.post('/tasks',data),setTask:(id,completed)=>http.patch(`/tasks/${id}`,{completed}),deleteTask:id=>http.delete(`/tasks/${id}`),
 opportunityForecast:data=>http.post('/customer-intelligence/opportunity-forecast',data)
}
