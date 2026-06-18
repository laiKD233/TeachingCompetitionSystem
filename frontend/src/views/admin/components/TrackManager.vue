<template>
  <el-dialog
    v-model="localVisible"
    :title="title"
    width="800px"
    @close="handleClose"
  >
    <!-- 赛道列表 -->
    <div class="track-list">
      <div v-if="tracks.length === 0" class="empty-state">
        <el-icon class="empty-icon"><List /></el-icon>
        <p>暂无赛道，点击下方按钮创建</p>
      </div>
      
      <el-table v-else :data="tracks" stripe size="small">
        <el-table-column prop="name" label="赛道名称" width="200" />
        <el-table-column prop="description" label="赛道描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" effect="plain" size="small">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEditTrack(scope.row)">
              编辑
            </el-button>
            <el-button 
              type="danger" 
              link 
              size="small" 
              @click="handleDeleteTrack(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 创建/编辑赛道表单 -->
    <div v-if="formVisible" class="form-container">
      <el-form :model="trackForm" :rules="formRules" ref="trackFormRef" label-width="100px">
        <el-form-item label="赛道名称" prop="name">
          <el-input v-model="trackForm.name" placeholder="请输入赛道名称" />
        </el-form-item>
        
        <el-form-item label="赛道描述" prop="description">
          <el-input
            v-model="trackForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入赛道描述"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="trackForm.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 操作按钮 -->
    <template #footer>
      <div v-if="!formVisible">
        <el-button @click="handleCloseDialog">关闭</el-button>
        <el-button type="primary" @click="handleAddTrack">
          <el-icon><Plus /></el-icon>
          创建赛道
        </el-button>
      </div>
      <div v-else>
        <el-button @click="cancelEdit">取消</el-button>
        <el-button type="primary" @click="handleSubmitTrack">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { List, Plus } from '@element-plus/icons-vue'
import { createTrack, updateTrack, deleteTrack, getTracksByCompetitionId } from '@/api/track'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  competitionId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['close', 'update:visible'])

const title = ref('赛道管理')
const tracks = ref([])
const formVisible = ref(false)
const trackFormRef = ref(null)
const localVisible = ref(false)

const trackForm = reactive({
  id: null,
  name: '',
  description: '',
  status: 1
})

const formRules = {
  name: [{ required: true, message: '请输入赛道名称', trigger: 'blur' }]
}

watch(() => props.visible, (newVal) => {
  localVisible.value = newVal
  if (newVal && props.competitionId) {
    fetchTracks()
  }
})

watch(localVisible, (newVal) => {
  if (!newVal) {
    emit('update:visible', false)
  }
})

const fetchTracks = async () => {
  try {
    const res = await getTracksByCompetitionId(props.competitionId)
    tracks.value = res.data
  } catch (error) {
    ElMessage.error('获取赛道列表失败')
    console.error(error)
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const handleAddTrack = () => {
  formVisible.value = true
  title.value = '创建赛道'
  resetForm()
}

const handleEditTrack = (row) => {
  formVisible.value = true
  title.value = '编辑赛道'
  Object.assign(trackForm, {
    id: row.id,
    name: row.name,
    description: row.description || '',
    status: row.status
  })
}

const handleDeleteTrack = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除赛道"${row.name}"吗？`, '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteTrack(row.id)
    ElMessage.success('删除成功')
    fetchTracks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }
}

const cancelEdit = () => {
  formVisible.value = false
  title.value = '赛道管理'
  resetForm()
}

const resetForm = () => {
  trackForm.id = null
  trackForm.name = ''
  trackForm.description = ''
  trackForm.status = 1
  trackFormRef.value?.resetFields()
}

const handleSubmitTrack = async () => {
  if (!trackFormRef.value) return
  
  try {
    await trackFormRef.value.validate()
    
    const data = {
      competitionId: props.competitionId,
      name: trackForm.name,
      description: trackForm.description,
      status: trackForm.status
    }
    
    if (trackForm.id) {
      data.id = trackForm.id
      await updateTrack(data)
      ElMessage.success('更新成功')
    } else {
      await createTrack(data)
      ElMessage.success('创建成功')
    }
    
    cancelEdit()
    fetchTracks()
  } catch (error) {
    ElMessage.error(trackForm.id ? '更新失败' : '创建失败')
    console.error(error)
  }
}

const handleClose = () => {
  localVisible.value = false
  emit('close')
}

const handleCloseDialog = () => {
  localVisible.value = false
}
</script>

<style scoped>
.track-list {
  max-height: 400px;
  overflow-y: auto;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.form-container {
  padding: 20px 0;
}
</style>