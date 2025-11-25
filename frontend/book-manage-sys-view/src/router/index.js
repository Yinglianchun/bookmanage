import Vue from "vue";
import VueRouter from "vue-router";
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import { getToken } from "@/utils/storage.js";
import echarts from 'echarts';
Vue.prototype.$echarts = echarts;
Vue.use(ElementUI);
Vue.use(VueRouter);

const routes = [
  { path: "/", component: () => import(`@/views/login/Login.vue`) },
  { path: "/login", component: () => import(`@/views/login/Login.vue`) },
  { path: "/register", component: () => import(`@/views/register/Register.vue`) },
  { path: "/createNotice", component: () => import(`@/views/admin/CreateNotice.vue`), meta: { requireAuth: true } },
  {
    path: "/admin",
    component: () => import(`@/views/admin/Home.vue`),
    meta: { requireAuth: true },
    children: [
      { path: "/adminLayout", name: '数据总览', icon: 'el-icon-data-analysis', component: () => import(`@/views/admin/Main.vue`), meta: { requireAuth: true } },
      { path: "/userManage", name: '用户管理', icon: 'el-icon-user-solid', component: () => import(`@/views/admin/UserManage.vue`), meta: { requireAuth: true } },
      { path: "/noticeManage", name: '公告管理', icon: 'el-icon-edit-outline', component: () => import(`@/views/admin/NoticeManage.vue`), meta: { requireAuth: true } },
      { path: "/CategoryManage", name: '书籍类别管理', icon: 'el-icon-document-copy', component: () => import(`@/views/admin/CategoryManage.vue`), meta: { requireAuth: true } },
      { path: "/BookshelfManage", name: '书架管理', icon: 'el-icon-monitor', component: () => import(`@/views/admin/BookshelfManage.vue`), meta: { requireAuth: true } },
      { path: "/BookManage", name: '书籍管理', icon: 'el-icon-reading', component: () => import(`@/views/admin/BookManage.vue`), meta: { requireAuth: true } },
      { path: "/ReaderProposalManage", name: '留言板管理', icon: 'el-icon-edit-outline', component: () => import(`@/views/admin/ReaderProposalManage.vue`), meta: { requireAuth: true } }
    ]
  },
  {
    path: "/user",
    component: () => import(`@/views/user/Home.vue`),
    meta: { requireAuth: true },
    children: [
      { name: '留言板', path: "/main", icon: 'el-icon-question', component: () => import(`@/views/user/Main.vue`), meta: { requireAuth: true } },
      { name: '借阅管理', path: "/BookOperation", icon: 'el-icon-document-copy', component: () => import(`@/views/user/BookOperation.vue`), meta: { requireAuth: true } },
      { name: '还书', path: "/BookOrderHistory", icon: 'el-icon-document-checked', component: () => import(`@/views/user/BookOrderHistory.vue`), meta: { requireAuth: true } }
    ]
  }
];

const router = new VueRouter({
  routes,
  mode: 'history'
});
router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth) {
    const token = getToken();
    if (token !== null) {
      next();
    } else {
      next("/login");
    }
  }
  else {
    next();
  }
});
// import 'vue-vibe'
export default router;
