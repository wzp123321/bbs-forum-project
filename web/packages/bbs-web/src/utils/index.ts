import {
  SESSION_KEY_TOKEN,
  SESSION_KEY_USER_ID,
  SESSION_KEY_USER_NAME,
} from '@/config/session-key';
import { getSessionData, setSessionData, batchRemoveSessionData } from '@bbs/utils';

export const tokenStore = {
  get: () => getSessionData(SESSION_KEY_TOKEN) ?? '',
  set: (t: string) => setSessionData(SESSION_KEY_TOKEN, t),
  clear: () => {
    batchRemoveSessionData([SESSION_KEY_TOKEN, SESSION_KEY_USER_ID, SESSION_KEY_USER_NAME]);
  },
};

export const userStore = {
  getUserId: () => getSessionData(SESSION_KEY_USER_ID) ?? '',
  getUserName: () => getSessionData(SESSION_KEY_USER_NAME) ?? '',
  setUser: (userId: string, userName: string) => {
    setSessionData(SESSION_KEY_USER_ID, userId);
    setSessionData(SESSION_KEY_USER_NAME, userName);
  },
  clear: () => {
    batchRemoveSessionData([SESSION_KEY_USER_ID, SESSION_KEY_USER_NAME]);
  },
};

/** 是否已登录 */
export const isLoggedIn = () => !!tokenStore.get();
