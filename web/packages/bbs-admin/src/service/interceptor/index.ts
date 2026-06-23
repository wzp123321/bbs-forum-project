import { FGetAuthorization, FGetStorageData } from '@/utils';
import { ElMessageBox } from 'element-plus';
import useLoginStore from '@/store/login';
import axios, { AxiosRequestConfig } from 'axios';
import store from 'element-plus/es/components/cascader-panel/src/store.mjs';

const { isTokenFailureFlag, setIsTokenFailureFlag } = useLoginStore();
const FORBIDDEN_CODE = 401;

/**
 * 添加请求前缀
 * @param config 请求配置参数
 */
const addPrefix = (config: AxiosRequestConfig): void => {
  config.timeout = import.meta.env.VITE_TIME_OUT as number;
  config.baseURL = import.meta.env.VITE_BASE_URL as string;
};

/**
 * 添加租户信息
 * @param config 请求配置参数
 */
const addToken = (config: AxiosRequestConfig): void => {
  const empId = FGetStorageData('ems-employeeId') ?? '';
  const empName = encodeURIComponent(FGetStorageData('ems-employeeName') ?? '');
  config.headers = {
    ...config.headers,
    'ts-token': FGetStorageData('energy-token') ?? '',
    tenantCode: FGetStorageData('energy-corpid') ?? '',
    loginName: encodeURIComponent(FGetStorageData('energy-loginName') ?? ''),
    'ts-repeat': FGetAuthorization(),
    empId,
    empName,
    'Content-Type': 'application/json',
  };
};
/**
 * 是否是云端加载
 * @returns
 */
const mapIsInCloudContainer = () => FGetStorageData('ems-sourceValue');
/**
 * 校验token是否过期
 * 如果不是云端加载，直接使用post message通信，如果是云端加载则打开确认框
 * @param code
 * @param message
 */
export const checkAxiosPermission = (code: number, message: string) => {
  if (+code === FORBIDDEN_CODE && !isTokenFailureFlag) {
    setIsTokenFailureFlag(true);
    if (!mapIsInCloudContainer()) {
      console.log('%c✨✨postmessage通信✨✨', 'font-size: 24px', code, message);
      window.parent.postMessage(
        {
          code,
          message,
          type: 'ems-login-failure',
        },
        window.location.origin,
      );
    } else {
      ElMessageBox.alert('登录信息已失效，请重新登录', '', {
        confirmButtonText: '确认',
        showClose: false,
        showCancelButton: false,
        type: 'warning',
      })
        .then(() => {
          console.log('%c✨✨确认跳转✨✨', 'font-size: 24px', message);
          window.location.href = message;
        })
        .catch(() => {
          console.warn('cancel');
        });
    }
  }
};
/**
 * 校验流文件，是否token过期
 * @param blob
 */
const checkBlobPermission = (blob: Blob) => {
  if (blob.size && blob.type === 'application/json') {
    const reader = new FileReader();
    reader.onloadend = (e) => {
      const res = JSON.parse(e.target?.result as string);
      const code = Number(res?.errcode) || res?.code;
      if (code === FORBIDDEN_CODE) {
        checkAxiosPermission(code, res?.message || res?.errmsg);
      }
    };
    reader.readAsText(blob);
  }
};

/**
 * 请求拦截器
 */
axios.interceptors.request.use(
  (config) => {
    addPrefix(config);
    addToken(config);
    return config;
  },
  (error) => {
    throw error.message;
  },
);
/**
 * 响应拦截器
 */
axios.interceptors.response.use(
  (res: any) => {
    if (res?.data?.size) {
      checkBlobPermission(res?.data);
    } else {
      checkAxiosPermission(+(res.data.code || res.data.errcode), res.data.message || res.data.errmsg);
    }
    return res;
  },
  (error: any) => {
    if (error?.response?.data?.size) {
      checkBlobPermission(error?.response.data);
    } else {
      const code = error?.response?.data?.errcode || error?.response?.data?.code;
      checkAxiosPermission(
        code ? +code : error?.response?.data?.errcode,
        error?.response?.data?.message || error?.response?.data?.errmsg,
      );
    }
    throw error.response;
  },
);
