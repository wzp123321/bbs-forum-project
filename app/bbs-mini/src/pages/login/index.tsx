/**
 * 登录页
 * - 极简账号密码登录 (mock)
 * - 真实环境应接入微信一键登录
 */
import React, { useState } from 'react';
import { View, Text, Input } from '@tarojs/components';
import Taro from '@tarojs/taro';
import { login } from '@/services/api';
import { useUserStore } from '@/store/user';
import styles from './index.module.scss';

const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const setLogin = useUserStore((s) => s.setLogin);

  const handleLogin = async () => {
    if (!username.trim()) {
      Taro.showToast({ title: '请输入账号', icon: 'none' });
      return;
    }
    if (!password) {
      Taro.showToast({ title: '请输入密码', icon: 'none' });
      return;
    }
    setLoading(true);
    try {
      const res = await login({ username, password });
      setLogin(res.token, res.user);
      Taro.showToast({ title: '登录成功', icon: 'success' });
      setTimeout(() => Taro.navigateBack(), 800);
    } catch (e: any) {
      console.error('[Login] 登录失败', e);
      Taro.showToast({ title: e?.message || '登录失败', icon: 'none' });
    } finally {
      setLoading(false);
    }
  };

  const canSubmit = username.trim() && password;

  return (
    <View className={styles.page}>
      <View className={styles.brand}>
        <Text className={styles.logo}>💬</Text>
        <Text className={styles.title}>BBS 论坛</Text>
        <Text className={styles.subtitle}>记录你的想法与生活</Text>
      </View>

      <View className={styles.form}>
        <View className={styles.field}>
          <Text className={styles.fieldLabel}>账号</Text>
          <Input
            className={styles.fieldInput}
            value={username}
            onInput={(e) => setUsername(e.detail.value)}
            placeholder="请输入账号"
            maxlength={20}
          />
        </View>
        <View className={styles.field}>
          <Text className={styles.fieldLabel}>密码</Text>
          <Input
            className={styles.fieldInput}
            value={password}
            onInput={(e) => setPassword(e.detail.value)}
            placeholder="请输入密码"
            password
            maxlength={20}
          />
        </View>
        <View
          className={`${styles.submitBtn} ${!canSubmit || loading ? styles.submitBtnDisabled : ''}`}
          onClick={handleLogin}
        >
          {loading ? '登录中...' : '登录'}
        </View>
        <Text className={styles.tip}>
          登录即表示同意<Text className={styles.tipLink}>《用户协议》</Text>和<Text className={styles.tipLink}>《隐私政策》</Text>
        </Text>
      </View>
    </View>
  );
};

export default LoginPage;
