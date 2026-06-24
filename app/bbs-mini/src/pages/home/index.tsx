/**
 * 首页 - 推荐 feed 流
 * - 顶部搜索 + 发布按钮
 * - 分类横向切换 (最新/精华/科技/美食/...)
 * - 帖子卡片列表 + 下拉刷新 + 上拉加载
 */
import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, ScrollView } from '@tarojs/components';
import Taro, { usePullDownRefresh, useReachBottom } from '@tarojs/taro';
import { getPostList } from '@/services/api';
import type { PostItem } from '@/types/model';
import PostCard from '@/components/PostCard';
import Empty from '@/components/Empty';
import styles from './index.module.scss';

const TABS = [
  { id: 'all', name: '最新' },
  { id: 'essence', name: '精华' },
  { id: 'c1', name: '科技' },
  { id: 'c2', name: '美食' },
  { id: 'c3', name: '旅行' },
  { id: 'c4', name: '生活' },
];

const HomePage: React.FC = () => {
  const [tab, setTab] = useState('all');
  const [list, setList] = useState<PostItem[]>([]);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const pageSize = 10;

  const loadList = useCallback(async (p: number, replace: boolean) => {
    if (loading) return;
    setLoading(true);
    try {
      const params: { page: number; pageSize: number; categoryId?: string } = { page: p, pageSize };
      if (tab !== 'all' && tab !== 'essence') params.categoryId = tab;
      const res = await getPostList(params);
      const items = tab === 'essence' ? res.list.filter((x) => x.essence) : res.list;
      setList((prev) => (replace ? items : [...prev, ...items]));
      setHasMore(res.hasMore);
      setPage(p);
    } catch (e) {
      console.error('[Home] 加载帖子失败', e);
      Taro.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      setLoading(false);
    }
  }, [tab, loading]);

  useEffect(() => {
    setList([]);
    setPage(1);
    setHasMore(true);
    loadList(1, true);
  }, [tab]);

  usePullDownRefresh(async () => {
    await loadList(1, true);
    Taro.stopPullDownRefresh();
  });

  useReachBottom(() => {
    if (hasMore && !loading) loadList(page + 1, false);
  });

  return (
    <View className={styles.page}>
      <View className={styles.searchBar}>
        <View className={styles.searchBox} onClick={() => Taro.navigateTo({ url: '/pages/search/index' })}>
          <Text className={styles.searchIcon}>🔍</Text>
          <Text className={styles.searchPlaceholder}>搜索帖子、话题、用户</Text>
        </View>
        <View className={styles.publishBtn} onClick={() => Taro.navigateTo({ url: '/pages/post-publish/index' })}>
          发布
        </View>
      </View>

      <ScrollView className={styles.tabs} scrollX>
        {TABS.map((t) => (
          <View
            key={t.id}
            className={`${styles.tabItem} ${tab === t.id ? styles.tabItemActive : ''}`}
            onClick={() => setTab(t.id)}
          >
            {t.name}
          </View>
        ))}
      </ScrollView>

      <View className={styles.list}>
        {list.length === 0 && !loading ? (
          <Empty text="还没有帖子,快去发布吧~" icon="📝" />
        ) : (
          list.map((p) => <PostCard key={p.id} post={p} />)
        )}
        {loading && <View className={styles.loading}>加载中...</View>}
        {!hasMore && list.length > 0 && <View className={styles.loading}>— 没有更多了 —</View>}
      </View>
    </View>
  );
};

export default HomePage;
