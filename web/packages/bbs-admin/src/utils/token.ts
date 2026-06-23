const asmCrypto = require('asmcrypto.js');

/**
 * 获取地址参数项
 * @param key 参数名
 * @returns 参数名对应的值
 */
export const FGetQueryParam = (key: string): string | undefined => {
  const reg = new RegExp(`(^|&)${key}=([^&]*)(&|$)`, 'i');
  const match = window.location.search.substring(1).match(reg) ?? '';
  if (match.length > 2) {
    return decodeURIComponent(match[2]);
  }
  return '';
};

/**
 * 调用公共方法设置本地缓存数据
 * @param key
 * @param value
 */
export const FSetStorageData = (key: string, value?: string) => {
  window.sessionStorage.setItem(key, value || '');
};
/**
 * 调用公共方法获取本地缓存数据
 * @param key
 * @returns
 */
export const FGetStorageData = (key: string): string | undefined => window.sessionStorage.getItem(key) ?? '';
// 生成一组随机数
const getArrayRound = (): number[] => {
  const arr = [];
  for (let i = 0; i < 12; i++) {
    const randomNum6 = Math.round(Math.random() * 128);
    arr.push(randomNum6);
  }
  return arr;
};
/**
 * 加密
 * @param data 加密字段
 * @returns 参数名对应的加密值
 */
export const FEncrypto = (data: string): string => {
  const encoder = new TextEncoder();
  const text = encoder.encode(data);
  const key = encoder.encode('yPiwWYoeTGuUTAW7');
  const iv = new Uint8Array(getArrayRound());
  // 4、加密
  const encText = asmCrypto.AES_GCM.encrypt(text, key, iv);
  // 5.将iv向量与加密后的密文（Uint8Array）相加再转换成BASE64字符串
  return asmCrypto.bytes_to_base64([...iv, ...encText]);
};

const getRandomNum = () => {
  let number = String(Math.floor(Math.random() * 10));
  if (number === '0') {
    number = getRandomNum();
  }
  return number;
};

// 随机生成六位首位非0数
export const getRandomSixNum = () => {
  let RandomSixStr = '';
  for (let i = 0; i < 6; i++) {
    if (i === 0) {
      RandomSixStr += getRandomNum();
    } else {
      RandomSixStr += String(Math.floor(Math.random() * 10));
    }
  }
  return RandomSixStr;
};

/**
 * 生成权限校验字段
 * @returns
 */
export const FGetAuthorization = () => {
  const diffTime = Number(sessionStorage.getItem('dTimeValue')) ?? 0;
  const authorization = `${new Date().getTime() + diffTime}_${getRandomSixNum()}_${encodeURIComponent(
    FGetStorageData('energy-loginName') ?? '',
  )}`;
  return FEncrypto(authorization);
};

/**
 * 获取补充的院区字段
 * @returns
 */
export const getCampusParams = () => ({
  projectIds: FGetStorageData('ems-used-campus') ? JSON.parse(FGetStorageData('ems-used-campus') as string) : [],
  wholeHospitalFlag: FGetStorageData('ems-wholeHospitalFlag') === 'true',
});
