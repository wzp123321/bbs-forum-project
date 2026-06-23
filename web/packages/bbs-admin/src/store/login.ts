// 转发 pinia/modules/login,避免在 store 与 pinia 中重复 defineStore('login') 冲突
export { default } from '@/pinia/modules/login';
