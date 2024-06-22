import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { CommonHttpRes } from '../model';

export const useFileHandler = () => {
	// 文件列表
	const fileList = ref<File[]>([]);
	/**
	 * 文件上传处理
	 * @param accept 允许的后缀
	 * @param multiple 是否支持多选
	 * @returns 目标文件域
	 */
	const handleFileChoose = (accept: string, multiple: boolean = false) => {
		const element = document.createElement('input');
		element.multiple = multiple;
		element.type = 'file';
		accept && (element.accept = accept);
		element.click();
		element.onchange = () => {
			if (element.files) {
				console.log(123);
				fileList.value = [...(element.files as any)];
			}
			element.remove();
		};
	};
	/**
	 * 文件转本地url
	 * @param file
	 * @returns
	 */
	const transferFileToUrl = (file: File) => {
		let url: string = '';
		if ((window as any).createObjectURL != undefined) {
			url = (window as any).createObjectURL(file);
		} else if (window.URL != undefined) {
			url = window.URL.createObjectURL(file) ?? '';
		} else if (window.webkitURL != undefined) {
			url = window.webkitURL.createObjectURL(file) ?? '';
		}
		return url;
	};

	// 校验文件
	const verifyUpload = (
		list: File[],
		target: File,
		maxSize: number,
		accept: { [key: string]: string },
		total: number
	): boolean => {
		if (target?.size > maxSize * 1024 * 1024) {
			ElMessage.error(`上传${target?.name ?? ''}失败，文件大小不能超过${maxSize}MB！`);
			return false;
		}

		if (!Object.values(accept).includes(target?.type)) {
			ElMessage.error(`上传${target?.name ?? ''}失败，当前页面只支持上传${Object.keys(accept).join('/')}格式文件！`);
			return false;
		}

		if (list.map((file) => file.name).includes(target?.name)) {
			ElMessage.error(`上传${target?.name ?? ''}失败，已存在同名文件，请修改文件名称重新上传！`);
			return false;
		}

		let totalSize = list.reduce((total: number, currentValue: File) => {
			console.log(currentValue.size);
			return total + currentValue.size;
		}, 0);

		totalSize += target.size;
		console.log(totalSize);
		if (totalSize > total * 1024 * 1024) {
			ElMessage.error(`上传${target?.name ?? ''}失败，待上传附件总大小不能超过${total}MB！`);
			return false;
		}

		return true;
	};

	/**
	 * 二进制响应处理
	 * @param blob 二进制流
	 * @param name 输出文件名
	 */
	const FBlobHandler = (blob: Blob, name?: string): Promise<void> => {
		return new Promise((resolve, reject) => {
			if (blob.size) {
				const reader = new FileReader();

				if (blob.type.includes('json')) {
					reader.onloadend = (e) => {
						const res: CommonHttpRes<void> = JSON.parse(e.target?.result as string);
						reject(res?.message ?? '未知原因');
					};
					reader.readAsText(blob);
				} else {
					reader.onloadend = (e) => {
						FDownLoadHandler(e.target?.result as string, name)
							.then(() => resolve())
							.catch((error) => reject(error));
					};
					reader.readAsDataURL(blob);
				}
			} else {
				reject(`无法获取${name || '文件'}`);
			}
		});
	};

	/**
	 * 文件下载处理
	 * @param url 源路径
	 * @param name 输出文件名
	 */
	const FDownLoadHandler = (url: string, name?: string): Promise<void> => {
		return new Promise((resolve, reject) => {
			if (url) {
				const element = document.createElement('a');
				element.href = url;
				name && (element.download = name);
				element.click();
				resolve();
				element.remove();
			} else {
				reject(`无法下载${name || '文件'}`);
			}
		});
	};

	/**
	 * 文件分片
	 * @param file
	 * @returns
	 */
	const mapFileChunks = (file: File) => {
		const CHUNK_SIZE = 1024 * 1024 * 10; // 每个文件切片大小定为10MB .
		const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
		const chunks: { index: number; data: Blob }[] = [];
		for (let index = 0; index < totalChunks; index++) {
			const start = index * CHUNK_SIZE;
			const end = Math.min(start + CHUNK_SIZE, file.size);
			const chunk = file.slice(start, end);

			chunks.push({
				index,
				data: chunk,
			});
		}

		return chunks;
	};
	return {
		fileList,
		handleFileChoose,
		transferFileToUrl,
		mapFileChunks,
		verifyUpload,
		FBlobHandler,
		FDownLoadHandler,
	};
};
