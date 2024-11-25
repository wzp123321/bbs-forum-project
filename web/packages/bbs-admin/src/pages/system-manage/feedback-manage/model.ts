export interface FeedbackInfo {
  id: number;
  content: string;
  /**
   * 反馈类型，字典中维护
   */
  type: string;
  typeName: string;
  feedbackUser: string;
  reply: string;
  status: string;
  dealTime: number;
  createTime: number;
}
