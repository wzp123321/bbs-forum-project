import { ref } from 'vue';
import { SESSION_KEY_USER_NAME } from '@/config/session-key';
import { getSessionData } from '@bbs/utils';

export const useUserInfo = () => {
  // 用户名
  const userName = ref('');

  const initUserByStorage = () => {
    if (getSessionData(SESSION_KEY_USER_NAME)) {
      userName.value = getSessionData(SESSION_KEY_USER_NAME) ?? '';
    }
  };

  return {
    userName,
    initUserByStorage,
  };
};
