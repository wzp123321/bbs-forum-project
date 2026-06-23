/** token 存储 key (调用方传入,内部不再拼前缀,避免与老 token.ts 的 ems-* 等裸 key 冲突) */
export const TOKEN_KEY = 'bbs-token';
export const USER_ID_KEY = 'bbs-userId';
export const USER_NAME_KEY = 'bbs-userName';

export const session = {
  get: (key: string) => sessionStorage.getItem(key) ?? '',
  set: (key: string, value?: string) => sessionStorage.setItem(key, value || ''),
  remove: (key: string) => sessionStorage.removeItem(key),
};

/** token 操作 (统一从此处读写,避免散落) */
export const tokenStore = {
  get: () => session.get(TOKEN_KEY),
  set: (t: string) => session.set(TOKEN_KEY, t),
  clear: () => {
    session.remove(TOKEN_KEY);
    session.remove(USER_ID_KEY);
    session.remove(USER_NAME_KEY);
  },
};
