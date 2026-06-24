/** 反馈 VO */
export interface FeedbackVO {
  id: number;
  userId?: string;
  userName?: string;
  type?: string;
  content?: string;
  reply?: string;
  replyUserId?: string;
  replyUserName?: string;
  replyTime?: string;
  status?: number;
  createTime?: string;
  updateTime?: string;
}

/** 反馈分页查询参数 */
export interface FeedbackPageParams {
  pageNum?: number;
  pageSize?: number;
  keyword?: string;
  type?: string;
  status?: number;
  userId?: string;
}

/** 提交反馈入参 */
export interface FeedbackSaveParams {
  type: string;
  content: string;
}

/** 回复反馈入参 */
export interface FeedbackReplyParams {
  reply: string;
}
