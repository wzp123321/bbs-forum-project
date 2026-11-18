/** 仪表盘最近动态 */
export interface ActivityVO {
  /** post / comment / report */
  type: string;
  id: number;
  summary: string;
  time: string;
}

/** 仪表盘统计 */
export interface StatOverviewVO {
  userCount: number;
  postCount: number;
  categoryCount: number;
  todayPostCount: number;
  pendingReportCount: number;
  recentActivities: ActivityVO[];
}
