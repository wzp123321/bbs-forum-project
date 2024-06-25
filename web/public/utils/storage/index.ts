/**
 * 设置本地缓存数据
 * @param key 键
 * @param value 值
 */
export const setSessionData = (key: string, value: string) => {
  if (!key) {
    return;
  }
  window.sessionStorage.setItem(key, encodeURIComponent(value ?? ''));
};
/**
 * 通过key取本地缓存数据
 * @param key 键
 * @returns { string | undefined}
 */
export const getSessionData = (key: string): string | undefined => {
  const value = window.sessionStorage.getItem(key);
  if (value) {
    return decodeURIComponent(value);
  }
};
/**
 * 通过键清除缓存，不传参代表全量清除
 * @param keys
 */
export const batchRemoveSessionData = (keys?: string[]) => {
  if (!keys || !keys?.length) {
    window.sessionStorage.clear();
  } else {
    keys?.forEach((key) => {
      window.sessionStorage.removeItem(key);
    });
  }
};
