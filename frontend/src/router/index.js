/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
import {createRouter,createWebHistory} from 'vue-router'
import LoginView from '../views/LoginView.vue'
import MainLayout from '../components/MainLayout.vue'
const routes=[
 {path:'/login',component:LoginView,meta:{public:true,title:'登录'}},
 {path:'/',component:MainLayout,children:[
  {path:'',component:()=>import('../views/HomeView.vue'),meta:{title:'销售首页'}},
  {path:'customers',component:()=>import('../views/CustomersView.vue'),meta:{title:'客户'}},
  {path:'customers/:id',component:()=>import('../views/CustomerDetailView.vue'),meta:{title:'客户详情'}},
  {path:'customers/:id/transfer',component:()=>import('../views/CustomerTransferView.vue'),meta:{title:'调整负责人'}},
  {path:'opportunities',component:()=>import('../views/OpportunitiesView.vue'),meta:{title:'商机'}},
  {path:'follow-ups',component:()=>import('../views/FollowUpsView.vue'),meta:{title:'跟进'}},
  {path:'tasks',component:()=>import('../views/TasksView.vue'),meta:{title:'销售任务'}},
  {path:'workbench',component:()=>import('../views/WorkbenchView.vue'),meta:{title:'工作台'}},
  {path:'admin/users',component:()=>import('../views/AdminUsersView.vue'),meta:{title:'账号管理'}},
  {path:'admin/data',component:()=>import('../views/AdminDataView.vue'),meta:{title:'客户数据'}},
  {path:'admin/audit',component:()=>import('../views/AdminAuditView.vue'),meta:{title:'操作记录'}},
  {path:'profile',component:()=>import('../views/ProfileView.vue'),meta:{title:'我的'}}
 ]}
]
const router=createRouter({history:createWebHistory(),routes,scrollBehavior:()=>({top:0})})
router.beforeEach(to=>{document.title=`${to.meta.title||'客户管理'}｜知华科技 CRM`;if(!to.meta.public&&!localStorage.getItem('zhuatech_crm_token'))return'/login';if(to.path==='/login'&&localStorage.getItem('zhuatech_crm_token'))return'/'})
export default router
