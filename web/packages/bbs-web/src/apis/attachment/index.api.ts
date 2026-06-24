/** 附件信息 */
export interface AttachmentVO {
  id: number;
  userId?: string;
  bizType?: string;
  bizId?: number;
  originName?: string;
  fileName?: string;
  filePath?: string;
  url?: string;
  contentType?: string;
  size?: number;
  storageType?: string;
  status?: number;
  createTime?: string;
}

/** 上传参数 */
export interface UploadOptions {
  bizType?: string;
  onProgress?: (percent: number) => void;
}
