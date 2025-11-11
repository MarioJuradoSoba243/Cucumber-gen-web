import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/tests'
    },
    {
      path: '/tests',
      component: () => import('../views/TestCasesView.vue')
    },
    {
      path: '/attributes',
      component: () => import('../views/AttributesView.vue')
    },
    {
      path: '/templates',
      component: () => import('../views/TemplatesView.vue')
    },
    {
      path: '/generation',
      component: () => import('../views/GenerationView.vue')
    }
  ]
});

export default router;
