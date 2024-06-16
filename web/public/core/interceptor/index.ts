import { ElMessageBox } from 'element-plus';
import axios, { AxiosRequestConfig } from 'axios';
import { getSessionStorage } from '../../utils/storage';

const FORBIDDEN_CODE = 401;

/**
 * 请求拦截器
 */
axios.interceptors.request.use(
	(config) => {
		try {
			addPrefix(config);
			addToken(config);
			return config;
		} catch (error) {
			throw error;
		}
	},
	(error) => {
		throw error.message;
	}
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
			checkAxiosPermission(
				+error?.response?.data?.errcode || +error?.response?.data?.code,
				error?.response?.data?.message || error?.response?.data?.errmsg
			);
		}
		throw error.response;
	}
);
/**
 * 校验token是否过期
 * @param code
 * @param message
 */
export function checkAxiosPermission(code: number, message: string) {
	if (+code === FORBIDDEN_CODE) {
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
/**
 * 校验流文件，是否token过期
 * @param blob
 */
function checkBlobPermission(blob: Blob) {
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
}
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
	config.headers = {
		...config.headers,
		token: getSessionStorage('energy-token') ?? '',
		tenantCode: getSessionStorage('energy-corpid') ?? '',
		loginName: getSessionStorage('energy-loginName') ?? '',
		'Content-Type':
			Object.prototype.toString.call(config?.data) === '[object String]'
				? 'application/json; charset=utf-8'
				: 'application/json',
	};
};
