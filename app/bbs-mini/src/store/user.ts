/**
 * 简易用户 Store (Zustand)
 * - 仅存登录态, token 持久化到 storage
 */
import { create } from 'zustand';
import Taro from '@tarojs/taro';
import type { UserInfo } from '@/types/model';

const TOKEN_KEY = 'bbs_token';
const USER_KEY = 'bbs_user';

interface UserState {
  token: string;
  user: UserInfo | null;
  setLogin: (token: string, user: UserInfo) => void;
  setUser: (user: UserInfo) => void;
  logout: () => void;
  loadFromStorage: () => void;
}

export const useUserStore = create<UserState>((set) => ({
  token: '',
  user: null,
  setLogin: (token, user) => {
    Taro.setStorageSync(TOKEN_KEY, token);
    Taro.setStorageSync(USER_KEY, user);
    set({ token, user });
  },
  setUser: (user) => {
    Taro.setStorageSync(USER_KEY, user);
    set({ user });
  },
  logout: () => {
    Taro.removeStorageSync(TOKEN_KEY);
    Taro.removeStorageSync(USER_KEY);
    set({ token: '', user: null });
  },
  loadFromStorage: () => {
    try {
      const token = Taro.getStorageSync<string>(TOKEN_KEY) || '';
      const user = Taro.getStorageSync<UserInfo | null>(USER_KEY) || null;
      set({ token, user });
    } catch (e) {
      console.error('[UserStore] 加载本地登录态失败', e);
    }
  },
}));
