# AI 评测报告

- 数据集: bbs-tag-smoke.jsonl
- 适配器: mock.mjs
- 用例数: 6
- Recall@3: 1（阈值 0.7）
- 完全匹配率: 0.833
- 平均延迟: 0ms
- 结论: PASS

| id | expected | output | recall | latency |
| --- | --- | --- | --- | --- |
| tag-001 | ["Java","Redis"] | ["Redis","Java","后端"] | 1.00 | 1ms |
| tag-002 | ["Vue","TypeScript"] | ["Vue","TypeScript"] | 1.00 | 0ms |
| tag-003 | ["Elasticsearch","搜索"] | ["Elasticsearch","搜索"] | 1.00 | 0ms |
| tag-004 | ["生活"] | ["生活"] | 1.00 | 0ms |
| tag-005 | ["MySQL","数据库"] | ["MySQL","数据库"] | 1.00 | 1ms |
| tag-006 | ["测试"] | ["测试"] | 1.00 | 0ms |
