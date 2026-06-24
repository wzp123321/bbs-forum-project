/** 举报目标: 1帖子 2评论 */
export type ReportTargetType = 1 | 2;

/** 举报原因枚举 */
export const REPORT_REASON_OPTIONS: { value: string; label: string }[] = [
  { value: 'spam', label: '垃圾广告' },
  { value: 'porn', label: '色情低俗' },
  { value: 'violence', label: '暴力血腥' },
  { value: 'abuse', label: '人身攻击' },
  { value: 'illegal', label: '违法违规' },
  { value: 'plagiarism', label: '抄袭侵权' },
  { value: 'other', label: '其他' },
];

/** 举报提交参数 */
export interface ReportSaveParams {
  targetType: ReportTargetType;
  targetId: number;
  reasonType: string;
  content?: string;
}

/** 举报详情 */
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
