const RULES = [
  [/redis|spring|java|docker/i, ['Redis', 'Java', '后端']],
  [/vue|typescript|前端|element/i, ['Vue', 'TypeScript']],
  [/elastic|搜索|ik|索引/i, ['Elasticsearch', '搜索']],
  [/mysql|数据库|explain|慢查询/i, ['MySQL', '数据库']],
  [/playwright|e2e|测试|vitest/i, ['测试']],
];

/** 离线确定性适配器：用于验证评测链路本身，不调用任何模型。 */
export const run = async (item) => {
  const hits = RULES.filter(([pattern]) => pattern.test(item.input)).flatMap(([, tags]) => tags);
  return { output: hits.length ? hits : ['生活'] };
};
