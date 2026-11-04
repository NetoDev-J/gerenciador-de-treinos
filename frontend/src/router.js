import { createRouter, createWebHistory } from 'vue-router'
import ExerciciosView from './views/ExerciciosView.vue'
import DivisoesView from './views/DivisoesView.vue'
import TreinarView from './views/TreinarView.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/treinar' },
    { path: '/exercicios', component: ExerciciosView },
    { path: '/divisoes', component: DivisoesView },
    { path: '/treinar', component: TreinarView },
  ],
})