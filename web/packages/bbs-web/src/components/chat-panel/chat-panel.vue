<template>
  <div class="web-chat" :class="{ minimized }">
    <div class="wc-header" @click="minimized = !minimized">
      <el-icon><ChatDotRound /></el-icon>
      <span class="wc-title">公共聊天室</span>
      <span v-if="unread" class="wc-badge">{{ unread }}</span>
      <el-icon class="wc-toggle">
        <component :is="minimized ? 'Plus' : 'Minus'" />
      </el-icon>
    </div>
    <div v-show="!minimized" class="wc-body">
      <div ref="listRef" class="wc-list">
        <div v-for="(m, i) in messages" :key="i" class="wc-msg" :class="wcMsgClass(m)">
          <template v-if="m.type === 2">
            <div class="wc-sys">{{ m.content }} · {{ m.time }}</div>
          </template>
          <template v-else>
            <div class="wc-meta">
              <span class="wc-name">{{ m.userName || m.userId || '匿名' }}</span>
              <span class="wc-time">{{ m.time }}</span>
            </div>
            <div class="wc-text">{{ m.content }}</div>
          </template>
        </div>
        <div v-if="!connected && !messages.length" class="wc-empty">
          正在连接聊天室...
        </div>
      </div>
      <div class="wc-input">
        <el-input
          v-model="input"
          size="small"
          placeholder="说点什么..."
          :maxlength="200"
          @keydown.enter.prevent="onSend"
        >
          <template #append>
            <el-button :disabled="!connected || !input.trim()" type="primary" @click="onSend">
              发送
            </el-button>
          </template>
        </el-input>
        <div v-if="!connected" class="wc-status">连接已断开,自动重连中...</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { ChatDotRound, Plus, Minus } from '@element-plus/icons-vue';
import { resolveWsUrl } from '@/utils/ws';
import { tokenStore, userStore, isLoggedIn } from '@/utils';

defineOptions({ name: 'WebChatPanel' });

interface ChatMessage {
  type?: number;
  userId?: string;
  userName?: string;
  content?: string;
  time?: string;
}

const minimized = ref(false);
const messages = ref<ChatMessage[]>([]);
const input = ref('');
const connected = ref(false);
const unread = ref(0);
const listRef = ref<HTMLElement>();
let ws: WebSocket | null = null;
let reconnectTimer: number | null = null;
let reconnectDelay = 1000;

const meId = computed(() => userStore.getUserId?.() || '');
const meName = computed(() => userStore.getUserName?.() || '');

const wcMsgClass = (m: ChatMessage) => ({
  'wc-msg-self': m.type !== 2 && m.userId === meId.value,
});

const scrollToBottom = async () => {
  await nextTick();
  const el = listRef.value;
  if (el) el.scrollTop = el.scrollHeight;
};

const setupWs = () => {
  const token = tokenStore.get();
  const url = `${resolveWsUrl('/ws/chat')}?token=${encodeURIComponent(token || '')}`;
  try {
    ws = new WebSocket(url);
  } catch {
    scheduleReconnect();
    return;
  }
  ws.onopen = () => {
    connected.value = true;
    reconnectDelay = 1000;
  };
  ws.onclose = () => {
    connected.value = false;
    scheduleReconnect();
  };
  ws.onerror = () => {
    connected.value = false;
  };
  ws.onmessage = (ev) => {
    let payload: ChatMessage | ChatMessage[] | null = null;
    try {
      payload = JSON.parse(ev.data);
    } catch {
      return;
    }
    if (Array.isArray(payload)) {
      // 历史消息
      messages.value = payload.filter((m) => m && m.type !== undefined);
    } else if (payload && typeof payload === 'object') {
      messages.value.push(payload);
      if (minimized.value && payload.type === 1 && payload.userId !== meId.value) {
        unread.value += 1;
      }
      scrollToBottom();
    }
  };
};

const scheduleReconnect = () => {
  if (reconnectTimer) return;
  reconnectTimer = window.setTimeout(() => {
    reconnectTimer = null;
    setupWs();
    reconnectDelay = Math.min(reconnectDelay * 2, 15000);
  }, reconnectDelay);
};

const onSend = () => {
  const text = input.value.trim();
  if (!text || !ws || ws.readyState !== WebSocket.OPEN) return;
  ws.send(text);
  input.value = '';
};

watch(minimized, (v) => {
  if (!v) {
    unread.value = 0;
    scrollToBottom();
  }
});

onMounted(() => {
  // 未登录不连接
  if (!isLoggedIn()) return;
  setupWs();
});

onBeforeUnmount(() => {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
  }
  if (ws) {
    ws.onclose = null;
    ws.close();
    ws = null;
  }
});
</script>

<style lang="less" scoped>
.web-chat {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 320px;
  max-height: 480px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  z-index: 999;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: max-height 0.2s ease;

  &.minimized {
    max-height: 40px;
  }
}

.wc-header {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 40px;
  padding: 0 12px;
  background: var(--el-color-primary);
  color: #fff;
  cursor: pointer;
  user-select: none;
  .wc-title {
    flex: 1;
    font-weight: 500;
  }
  .wc-badge {
    background: #f56c6c;
    color: #fff;
    border-radius: 10px;
    padding: 0 6px;
    font-size: 12px;
    line-height: 18px;
    min-width: 18px;
    text-align: center;
  }
  .wc-toggle {
    margin-left: 4px;
  }
}

.wc-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.wc-list {
  flex: 1;
  min-height: 220px;
  max-height: 320px;
  overflow-y: auto;
  padding: 8px 12px;
  background: var(--el-fill-color-light);
}

.wc-msg {
  margin: 6px 0;
  &-self {
    .wc-text {
      background: var(--el-color-primary-light-9);
    }
  }
}

.wc-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  display: flex;
  gap: 8px;
}

.wc-text {
  background: #fff;
  border-radius: 4px;
  padding: 6px 8px;
  word-break: break-word;
  margin-top: 2px;
}

.wc-sys {
  text-align: center;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  background: transparent;
  padding: 2px 0;
}

.wc-input {
  padding: 8px;
  border-top: 1px solid var(--el-border-color-lighter);
  background: var(--el-bg-color);
}

.wc-status {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-color-warning);
}

.wc-empty {
  text-align: center;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  padding: 20px 0;
}
</style>
