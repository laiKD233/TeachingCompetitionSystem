<template>
  <div class="team-messages">
    <div class="messages-container" ref="messagesContainer">
      <div class="message-item" v-for="msg in messages" :key="msg.id" :class="{ 'is-self': msg.userId === currentUserId }">
        <el-avatar :size="36" class="avatar">{{ msg.userName?.charAt(0) }}</el-avatar>
        <div class="message-content">
          <div class="message-header">
            <span class="user-name">{{ msg.userName }}</span>
            <span class="time">{{ formatTime(msg.createdAt) }}</span>
          </div>
          <div class="message-text">{{ msg.content }}</div>
        </div>
      </div>
      <el-empty v-if="messages.length === 0" description="暂无消息，开始聊天吧" />
    </div>

    <div class="message-input">
      <el-input
        v-model="inputMessage"
        placeholder="输入消息..."
        @keyup.enter="handleSend"
        :disabled="sending"
      >
        <template #append>
          <el-button type="primary" @click="handleSend" :loading="sending">
            发送
          </el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getTeamMessages, sendMessage } from '@/api/team'
import dayjs from 'dayjs'

const props = defineProps({
  teamId: [String, Number],
  members: { type: Array, default: () => [] },
  currentUserId: [String, Number]
})

const messagesContainer = ref(null)
const messages = ref([])
const inputMessage = ref('')
const sending = ref(false)

const fetchMessages = async () => {
  try {
    const res = await getTeamMessages(props.teamId, 100)
    messages.value = res.data || []
    scrollToBottom()
  } catch (error) {
    console.error(error)
  }
}

const handleSend = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  sending.value = true
  try {
    await sendMessage(props.teamId, { content: inputMessage.value })
    inputMessage.value = ''
    await fetchMessages()
  } catch (error) {
    console.error(error)
  } finally {
    sending.value = false
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const formatTime = (time) => {
  return time ? dayjs(time).format('MM-DD HH:mm') : ''
}

watch(() => props.teamId, () => {
  if (props.teamId) fetchMessages()
}, { immediate: true })
</script>

<style scoped>
.team-messages {
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  height: 500px;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message-item.is-self {
  flex-direction: row-reverse;
}

.message-item.is-self .message-content {
  align-items: flex-end;
}

.message-item.is-self .message-text {
  background: #409eff;
  color: #fff;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 13px;
  font-weight: 500;
}

.time {
  font-size: 12px;
  color: #999;
}

.message-text {
  background: #f5f5f5;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  max-width: 400px;
  word-break: break-word;
}

.message-input {
  padding: 16px;
  border-top: 1px solid #eee;
}
</style>
