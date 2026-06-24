// 举报搜索表单
export interface SearchForm {
  keyword: string;
  targetType?: number;
  reasonType?: string;
  status?: number;
}

// 处理表单
export interface HandleForm {
  status: number; // 1已处理 2已驳回
  handleRemark: string;
}

/** 举报原因展示映射 */
export const REASON_TEXT: Record<string, string> = {
  spam: '垃圾广告',
  porn: '色情低俗',
  violence: '暴力血腥',
  abuse: '人身攻击',
  illegal: '违法违规',
  plagiarism: '抄袭侵权',
  other: '其他',
};
