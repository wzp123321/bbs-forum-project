/**
 * 搜索
 * - 历史记录 + 热门搜索
 * - 搜索结果: 帖子列表
 */
import React, { useState, useEffect } from 'react';
import { View, Text, Input } from '@tarojs/components';
import Taro from '@tarojs/taro';
import { getPostList } from '@/services/api';
import type { PostItem } from '@/types/model';
import PostCard from '@/components/PostCard';
import Empty from '@/components/Empty';
import styles from './index.module.scss';

const HOT_TAGS = ['前端', '广州', '西藏', 'Mac', 'Figma', '健身', '摄影', 'Java'];
const HISTORY_KEY = 'search_history';

const SearchPage: React.FC = () => {
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState<PostItem[]>([]);
  const [history, setHistory] = useState<string[]>([]);
  const [searched, setSearched] = useState(false);

  useEffect(() => {
    try {
      const h = Taro.getStorageSync<string[]>(HISTORY_KEY) || [];
      setHistory(h);
    } catch (e) {
      console.error('[Search] 读取历史失败', e);
    }
  }, []);

  const saveHistory = (kw: string) => {
    const next = [kw, ...history.filter((x) => x !== kw)].slice(0, 8);
    setHistory(next);
    Taro.setStorageSync(HISTORY_KEY, next);
  };

  const doSearch = async (kw: string) => {
    if (!kw.trim()) return;
    setKeyword(kw);
    setSearched(true);
    saveHistory(kw.trim());
    try {
      const res = await getPostList({ keyword: kw.trim(), pageSize: 20 });
      setResults(res.list);
    } catch (e) {
      console.error('[Search] 搜索失败', e);
      Taro.showToast({ title: '搜索失败', icon: 'none' });
    }
  };

  const clearHistory = () => {
    Taro.removeStorageSync(HISTORY_KEY);
    setHistory([]);
  };

  return (
    <View className={styles.page}>
      <View className={styles.searchBar}>
        <Input
          className={styles.searchInput}
          value={keyword}
          onInput={(e) => setKeyword(e.detail.value)}
          onConfirm={() => doSearch(keyword)}
          placeholder="搜索帖子、话题、用户"
          focus
          confirmType="search"
        />
        <Text className={styles.cancelBtn} onClick={() => Taro.navigateBack()}>取消</Text>
      </View>

      {!searched && (
        <>
          {history.length > 0 && (
            <View className={styles.history}>
              <View className={styles.sectionHeader}>
                <Text className={styles.sectionTitle}>搜索历史</Text>
                <Text className={styles.clearBtn} onClick={clearHistory}>清空</Text>
              </View>
              <View className={styles.tagList}>
                {history.map((h) => (
                  <Text key={h} className={styles.tagItem} onClick={() => doSearch(h)}>{h}</Text>
                ))}
              </View>
            </View>
          )}

          <View className={styles.history}>
            <View className={styles.sectionHeader}>
              <Text className={styles.sectionTitle}>热门搜索</Text>
            </View>
            <View className={styles.tagList}>
              {HOT_TAGS.map((t) => (
                <Text key={t} className={styles.tagItem} onClick={() => doSearch(t)}>#{t}</Text>
              ))}
            </View>
          </View>
        </>
      )}

      {searched && (
        <View className={styles.list}>
          {results.length === 0 ? (
            <Empty text={`没有找到与 "${keyword}" 相关的内容`} icon="🔍" />
          ) : (
            results.map((p) => <PostCard key={p.id} post={p} />)
          )}
        </View>
      )}
    </View>
  );
};

export default SearchPage;
