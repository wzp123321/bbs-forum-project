/** 举报 VO */
export interface ReportVO {
  id: number;
  userId?: string;
  userName?: string;
  targetType?: number;
  targetTypeText?: string;
  targetId?: number;
  targetTitle?: string;
  targetContent?: string;
  targetUserId?: string;
  targetUserName?: string;
  reasonType?: string;
  content?: string;
  status?: number;
  handleUserId?: string;
  handleUserName?: string;
  handleRemark?: string;
  handleTime?: string;
  createTime?: string;
}

/** 举报分页查询参数 */
export interface ReportPageParams {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
  targetType?: number;
  reasonType?: string;
  status?: number;
  userId?: string;
}

/** 处理举报入参 */
export interface ReportHandleParams {
  status: number; // 0驳回 1已处理
  handleRemark?: string;
}
