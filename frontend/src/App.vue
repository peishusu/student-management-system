<template>
  <div class="app-root">
    <section v-if="!isAuthenticated" class="auth-shell">
      <form class="auth-card" @submit.prevent="submitAuth">
        <div class="auth-brand">
          <span>学</span>
          <div>
            <h1>学生管理系统</h1>
            <p>请登录后继续管理学生档案</p>
          </div>
        </div>

        <div class="auth-tabs" role="tablist">
          <button type="button" :class="{ active: authMode === 'login' }" @click="switchAuthMode('login')">登录</button>
          <button type="button" :class="{ active: authMode === 'register' }" @click="switchAuthMode('register')">注册</button>
        </div>

        <label>
          <span>用户名</span>
          <input v-model.trim="authForm.username" autocomplete="username" placeholder="3-32 位字母、数字或下划线" required />
        </label>

        <label v-if="authMode === 'register'">
          <span>显示名称</span>
          <input v-model.trim="authForm.displayName" autocomplete="name" placeholder="例如：教务管理员" required />
        </label>

        <label v-if="authMode === 'register'">
          <span>角色</span>
          <select v-model="authForm.role" required>
            <option value="ADMIN">管理员</option>
            <option value="TEACHER">教师</option>
            <option value="VIEWER">只读用户</option>
          </select>
        </label>

        <label>
          <span>密码</span>
          <input
            v-model="authForm.password"
            :autocomplete="authMode === 'login' ? 'current-password' : 'new-password'"
            type="password"
            placeholder="至少 8 位，包含字母和数字"
            required
          />
        </label>

        <label v-if="authMode === 'register'">
          <span>确认密码</span>
          <input v-model="authForm.confirmPassword" autocomplete="new-password" type="password" required />
        </label>

        <p v-if="authError" class="auth-error">{{ authError }}</p>

        <button type="submit" class="primary-btn auth-submit" :disabled="authLoading">
          {{ authLoading ? '处理中...' : authMode === 'login' ? '登录系统' : '创建账号' }}
        </button>
      </form>
    </section>

    <div v-else class="app-shell">
      <aside class="sidebar">
        <div class="brand">
          <span>学</span>
          <div>
            <h1>学生管理系统</h1>
            <p>Vue + Spring Boot</p>
          </div>
        </div>

        <nav class="nav-list">
          <button :class="{ active: view === 'students' || view === 'studentDetail' }" @click="backToStudents">学生档案</button>
          <button :class="{ active: view === 'stats' }" @click="view = 'stats'">统计分析</button>
          <button :class="{ active: view === 'database' }" @click="view = 'database'">数据库说明</button>
        </nav>
      </aside>

      <main class="main">
        <header class="topbar">
          <div>
            <p class="eyebrow">教务管理平台</p>
            <h2>{{ titles[view] }}</h2>
          </div>
          <div class="topbar-actions">
            <button v-if="view === 'students' && canCreateStudent" class="primary-btn" @click="openCreate">新增学生</button>
            <div class="user-chip" :title="currentUser.username">
              <span>{{ userInitial }}</span>
              <div>
                <strong>{{ currentUser.displayName }}</strong>
                <small>{{ currentRoleLabel }} · @{{ currentUser.username }}</small>
              </div>
            </div>
            <button class="ghost-btn" @click="openPasswordDialog">修改密码</button>
            <button class="ghost-btn" @click="logout">退出</button>
          </div>
        </header>

        <section class="stats-grid">
          <article>
            <span>学生总数</span>
            <strong>{{ stats.total || 0 }}</strong>
          </article>
          <article>
            <span>平均成绩</span>
            <strong>{{ stats.averageScore || 0 }}</strong>
          </article>
          <article>
            <span>优秀学生</span>
            <strong>{{ stats.excellentCount || 0 }}</strong>
          </article>
          <article>
            <span>班级数量</span>
            <strong>{{ stats.classCount || 0 }}</strong>
          </article>
        </section>

        <section v-if="view === 'students'">
          <div class="toolbar">
            <label>
              <span>搜索</span>
              <input v-model="filters.keyword" placeholder="姓名、学号、班级、电话" @input="resetPageAndLoad" />
            </label>
            <label>
              <span>班级</span>
              <select v-model="filters.className" @change="resetPageAndLoad">
                <option value="">全部班级</option>
                <option v-for="className in classes" :key="className" :value="className">{{ className }}</option>
              </select>
            </label>
            <label>
              <span>状态</span>
              <select v-model="filters.status" @change="resetPageAndLoad">
                <option value="">全部状态</option>
                <option value="在读">在读</option>
                <option value="休学">休学</option>
                <option value="毕业">毕业</option>
              </select>
            </label>
            <button class="ghost-btn" @click="resetFilters">重置</button>
          </div>

          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>学号</th>
                  <th>姓名</th>
                  <th>性别</th>
                  <th>年龄</th>
                  <th>班级</th>
                  <th>成绩</th>
                  <th>电话</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="student in students" :key="student.id">
                  <td>{{ student.studentNo }}</td>
                  <td>{{ student.name }}</td>
                  <td>{{ student.gender }}</td>
                  <td>{{ student.age }}</td>
                  <td>{{ student.className }}</td>
                  <td>{{ student.score }}</td>
                  <td>{{ student.phone }}</td>
                  <td><span class="badge" :class="statusClass(student.status)">{{ student.status }}</span></td>
                  <td>
                    <div class="row-actions">
                      <button @click="openDetail(student)">查看</button>
                      <button v-if="canEditStudent" @click="openEdit(student)">编辑</button>
                      <button v-if="canDeleteStudent" class="danger-text" @click="remove(student)">删除</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-if="!students.length" class="empty-state">暂无学生数据</div>
          </div>

          <div class="pagination-bar">
            <div class="pagination-summary">
              共 {{ pagination.total }} 条 / 第 {{ pagination.page }} 页，共 {{ pagination.pages || 1 }} 页
            </div>
            <div class="pagination-actions">
              <button class="ghost-btn" :disabled="pagination.page <= 1" @click="goToPage(pagination.page - 1)">上一页</button>
              <button
                v-for="page in pageNumbers"
                :key="page"
                class="page-btn"
                :class="{ active: page === pagination.page }"
                @click="goToPage(page)"
              >
                {{ page }}
              </button>
              <button class="ghost-btn" :disabled="pagination.page >= pagination.pages" @click="goToPage(pagination.page + 1)">下一页</button>
              <select v-model.number="pagination.pageSize" class="page-size-select" @change="changePageSize">
                <option :value="10">10 条/页</option>
                <option :value="20">20 条/页</option>
                <option :value="50">50 条/页</option>
              </select>
            </div>
          </div>
        </section>

        <section v-if="view === 'studentDetail'" class="detail-shell">
          <div v-if="detailLoading" class="panel detail-loading">正在加载学生详情...</div>
          <div v-else-if="selectedStudent" class="detail-card">
            <div class="detail-head">
              <div class="detail-avatar">{{ detailInitial }}</div>
              <div class="detail-title">
                <span class="detail-no">{{ selectedStudent.studentNo }}</span>
                <h3>{{ selectedStudent.name }}</h3>
                <div class="detail-tags">
                  <span class="badge" :class="statusClass(selectedStudent.status)">{{ selectedStudent.status }}</span>
                  <span>{{ selectedStudent.gender }}</span>
                  <span>{{ selectedStudent.age }} 岁</span>
                  <span>{{ selectedStudent.className }}</span>
                </div>
              </div>
              <div class="detail-actions">
                <button class="ghost-btn" @click="backToStudents">返回列表</button>
                <button v-if="canEditStudent" class="primary-btn" @click="openEdit(selectedStudent)">编辑资料</button>
              </div>
            </div>

            <div class="detail-score">
              <span>综合成绩</span>
              <strong>{{ selectedStudent.score }}</strong>
            </div>

            <div class="detail-grid">
              <article>
                <span>电话</span>
                <strong>{{ selectedStudent.phone }}</strong>
              </article>
              <article>
                <span>邮箱</span>
                <strong>{{ selectedStudent.email || '未填写' }}</strong>
              </article>
              <article>
                <span>地址</span>
                <strong>{{ selectedStudent.address || '未填写' }}</strong>
              </article>
              <article>
                <span>备注</span>
                <strong>{{ selectedStudent.remark || '无' }}</strong>
              </article>
              <article>
                <span>创建时间</span>
                <strong>{{ formatDateTime(selectedStudent.createdAt) }}</strong>
              </article>
              <article>
                <span>更新时间</span>
                <strong>{{ formatDateTime(selectedStudent.updatedAt) }}</strong>
              </article>
            </div>
          </div>
          <div v-else class="panel detail-loading">未找到学生详情</div>
        </section>

        <section v-if="view === 'stats'" class="analytics-layout">
          <div class="panel">
            <h3>班级人数</h3>
            <BarList :items="stats.classDistribution" />
          </div>
          <div class="panel">
            <h3>成绩分布</h3>
            <BarList :items="stats.scoreDistribution" />
          </div>
        </section>

        <section v-if="view === 'database'" class="panel database-panel">
          <h3>数据库设计</h3>
          <p>数据库脚本位于项目根目录的 <code>database</code> 文件夹。</p>
          <ul>
            <li><code>schema.sql</code>：创建数据库、学生表、索引、系统用户表</li>
            <li><code>seed.sql</code>：初始化演示学生数据</li>
            <li><code>crud-examples.sql</code>：常用增删改查 SQL 示例</li>
          </ul>
          <p>后端连接配置在 <code>backend/src/main/resources/application.yml</code>，默认 MySQL 用户名和密码都是 <code>root</code>，Redis 使用本机 <code>6379</code>。</p>
        </section>
      </main>

      <div v-if="dialogVisible" class="modal-backdrop">
        <form class="modal" @submit.prevent="save">
          <div class="modal-head">
            <h3>{{ form.id ? '编辑学生' : '新增学生' }}</h3>
            <button type="button" class="icon-btn" @click="dialogVisible = false">×</button>
          </div>

          <div class="form-grid">
            <label>
              <span>学号</span>
              <input v-model.trim="form.studentNo" required />
            </label>
            <label>
              <span>姓名</span>
              <input v-model.trim="form.name" required />
            </label>
            <label>
              <span>性别</span>
              <select v-model="form.gender">
                <option value="男">男</option>
                <option value="女">女</option>
              </select>
            </label>
            <label>
              <span>年龄</span>
              <input v-model.number="form.age" type="number" min="6" max="30" required />
            </label>
            <label>
              <span>班级</span>
              <input v-model.trim="form.className" required />
            </label>
            <label>
              <span>成绩</span>
              <input v-model.number="form.score" type="number" min="0" max="100" step="0.1" required />
            </label>
            <label>
              <span>电话</span>
              <input v-model.trim="form.phone" required />
            </label>
            <label>
              <span>邮箱</span>
              <input v-model.trim="form.email" type="email" />
            </label>
            <label>
              <span>状态</span>
              <select v-model="form.status">
                <option value="在读">在读</option>
                <option value="休学">休学</option>
                <option value="毕业">毕业</option>
              </select>
            </label>
            <label>
              <span>地址</span>
              <input v-model.trim="form.address" />
            </label>
            <label class="full">
              <span>备注</span>
              <textarea v-model.trim="form.remark" rows="3"></textarea>
            </label>
          </div>

          <div class="modal-actions">
            <button type="button" class="ghost-btn" @click="dialogVisible = false">取消</button>
            <button type="submit" class="primary-btn">保存</button>
          </div>
        </form>
      </div>

      <div v-if="passwordDialogVisible" class="modal-backdrop">
        <form class="modal password-modal" @submit.prevent="submitPasswordChange">
          <div class="modal-head">
            <h3>修改密码</h3>
            <button type="button" class="icon-btn" @click="closePasswordDialog">×</button>
          </div>

          <div class="form-grid password-form">
            <label class="full">
              <span>当前密码</span>
              <input v-model="passwordForm.currentPassword" type="password" autocomplete="current-password" required />
            </label>
            <label>
              <span>新密码</span>
              <input
                v-model="passwordForm.newPassword"
                type="password"
                autocomplete="new-password"
                placeholder="至少 8 位，包含字母和数字"
                required
              />
            </label>
            <label>
              <span>确认新密码</span>
              <input v-model="passwordForm.confirmPassword" type="password" autocomplete="new-password" required />
            </label>
          </div>

          <p v-if="passwordError" class="auth-error">{{ passwordError }}</p>

          <div class="modal-actions">
            <button type="button" class="ghost-btn" @click="closePasswordDialog">取消</button>
            <button type="submit" class="primary-btn" :disabled="passwordLoading">
              {{ passwordLoading ? '保存中...' : '保存并重新登录' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="toast" class="toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { changePassword, clearSession, fetchCurrentUser, getSession, login, logoutSession, register, setAuthFailureHandler } from './api/auth'
import { createStudent, deleteStudent, fetchClasses, fetchStats, fetchStudent, fetchStudents, updateStudent } from './api/students'

const titles = {
  students: '学生档案',
  studentDetail: '学生详情',
  stats: '统计分析',
  database: '数据库说明'
}

const session = getSession()
const currentUser = ref(session?.user || null)
const authMode = ref('login')
const authLoading = ref(false)
const authError = ref('')
const view = ref('students')
const students = ref([])
const classes = ref([])
const stats = ref({})
const toast = ref('')
const dialogVisible = ref(false)
const detailLoading = ref(false)
const selectedStudent = ref(null)
const passwordDialogVisible = ref(false)
const passwordLoading = ref(false)
const passwordError = ref('')

const authForm = reactive({
  username: '',
  displayName: '',
  role: 'VIEWER',
  password: '',
  confirmPassword: ''
})

const filters = reactive({
  keyword: '',
  className: '',
  status: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0,
  pages: 0
})

const emptyForm = {
  id: null,
  studentNo: '',
  name: '',
  gender: '男',
  age: 16,
  className: '',
  phone: '',
  email: '',
  address: '',
  score: 80,
  status: '在读',
  remark: ''
}

const form = reactive({ ...emptyForm })
const isAuthenticated = computed(() => Boolean(currentUser.value))
const userInitial = computed(() => {
  const name = currentUser.value?.displayName || currentUser.value?.username || '用'
  return name.slice(0, 1).toUpperCase()
})

const detailInitial = computed(() => {
  const name = selectedStudent.value?.name || '学'
  return name.slice(0, 1).toUpperCase()
})

const currentRole = computed(() => currentUser.value?.role || 'VIEWER')
const currentRoleLabel = computed(() => roleLabel(currentRole.value))
const canCreateStudent = computed(() => currentRole.value === 'ADMIN' || currentRole.value === 'TEACHER')
const canEditStudent = computed(() => currentRole.value === 'ADMIN' || currentRole.value === 'TEACHER')
const canDeleteStudent = computed(() => currentRole.value === 'ADMIN')

const pageNumbers = computed(() => {
  const pages = pagination.pages || 1
  const current = pagination.page
  const start = Math.max(1, current - 2)
  const end = Math.min(pages, current + 2)
  const numbers = []
  for (let page = start; page <= end; page += 1) {
    numbers.push(page)
  }
  return numbers
})

const BarList = {
  props: {
    items: {
      type: Object,
      default: () => ({})
    }
  },
  setup(props) {
    const rows = computed(() => Object.entries(props.items || {}))
    const max = computed(() => Math.max(1, ...rows.value.map(([, value]) => Number(value))))
    return { rows, max }
  },
  template: `
    <div class="bar-list" v-if="rows.length">
      <div class="bar-row" v-for="[label, value] in rows" :key="label">
        <span>{{ label }}</span>
        <div class="bar-track">
          <div class="bar-fill" :style="{ width: (Number(value) / max * 100) + '%' }"></div>
        </div>
        <strong>{{ value }}</strong>
      </div>
    </div>
    <p v-else class="muted">暂无数据</p>
  `
}

setAuthFailureHandler((message) => {
  currentUser.value = null
  clearData()
  showToast(message)
})

async function submitAuth() {
  authLoading.value = true
  authError.value = ''
  try {
    const action = authMode.value === 'login' ? login : register
    const payload = authMode.value === 'login'
      ? { username: authForm.username, password: authForm.password }
      : { ...authForm }
    const auth = await action(payload)
    currentUser.value = auth.user
    clearAuthForm()
    await refreshAll()
    showToast(authMode.value === 'login' ? '登录成功' : '注册成功')
  } catch (error) {
    authError.value = error.message
  } finally {
    authLoading.value = false
  }
}

function switchAuthMode(mode) {
  authMode.value = mode
  authError.value = ''
  authForm.password = ''
  authForm.confirmPassword = ''
}

function clearAuthForm() {
  authForm.username = ''
  authForm.displayName = ''
  authForm.role = 'VIEWER'
  authForm.password = ''
  authForm.confirmPassword = ''
}

async function restoreSession() {
  if (!getSession()?.token) return
  try {
    currentUser.value = await fetchCurrentUser()
    await refreshAll()
  } catch (error) {
    clearSession()
    currentUser.value = null
    authError.value = error.message
  }
}

async function logout() {
  try {
    await logoutSession()
  } catch (error) {
    showToast(error.message)
  } finally {
    clearSession()
    currentUser.value = null
    clearData()
    view.value = 'students'
    dialogVisible.value = false
    passwordDialogVisible.value = false
    clearPasswordForm()
    showToast('已退出登录')
  }
}

function clearData() {
  students.value = []
  classes.value = []
  stats.value = {}
  selectedStudent.value = null
  pagination.page = 1
  pagination.total = 0
  pagination.pages = 0
}

async function refreshAll() {
  await Promise.all([loadStudents(), loadStats(), loadClasses()])
}

async function loadStudentsSafely() {
  try {
    await loadStudents()
  } catch (error) {
    showToast(error.message)
  }
}

async function loadStudents() {
  const result = await fetchStudents({
    ...filters,
    page: pagination.page,
    pageSize: pagination.pageSize
  })
  students.value = result.records || []
  pagination.total = Number(result.total || 0)
  pagination.page = Number(result.page || 1)
  pagination.pageSize = Number(result.pageSize || 20)
  pagination.pages = Number(result.pages || 0)
}

async function loadStats() {
  stats.value = await fetchStats()
}

async function loadClasses() {
  classes.value = await fetchClasses()
}

function resetFilters() {
  filters.keyword = ''
  filters.className = ''
  filters.status = ''
  pagination.page = 1
  loadStudentsSafely()
}

function resetPageAndLoad() {
  pagination.page = 1
  loadStudentsSafely()
}

function goToPage(page) {
  const pages = pagination.pages || 1
  const nextPage = Math.min(Math.max(Number(page) || 1, 1), pages)
  if (nextPage === pagination.page) return
  pagination.page = nextPage
  loadStudentsSafely()
}

function changePageSize() {
  pagination.page = 1
  loadStudentsSafely()
}

function openPasswordDialog() {
  clearPasswordForm()
  passwordDialogVisible.value = true
}

function closePasswordDialog() {
  passwordDialogVisible.value = false
  clearPasswordForm()
}

async function submitPasswordChange() {
  passwordLoading.value = true
  passwordError.value = ''
  try {
    await changePassword({ ...passwordForm })
    clearSession()
    currentUser.value = null
    clearData()
    view.value = 'students'
    passwordDialogVisible.value = false
    clearPasswordForm()
    showToast('密码已修改，请重新登录')
  } catch (error) {
    passwordError.value = error.message
  } finally {
    passwordLoading.value = false
  }
}

function clearPasswordForm() {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordError.value = ''
}

async function openDetail(student) {
  detailLoading.value = true
  selectedStudent.value = student
  view.value = 'studentDetail'
  try {
    selectedStudent.value = await fetchStudent(student.id)
  } catch (error) {
    showToast(error.message)
    view.value = 'students'
  } finally {
    detailLoading.value = false
  }
}

function backToStudents() {
  view.value = 'students'
  selectedStudent.value = null
}

function openCreate() {
  if (!canCreateStudent.value) {
    showToast('当前角色无权新增学生')
    return
  }
  Object.assign(form, emptyForm)
  dialogVisible.value = true
}

function openEdit(student) {
  if (!canEditStudent.value) {
    showToast('当前角色无权编辑学生')
    return
  }
  Object.assign(form, student)
  dialogVisible.value = true
}

async function save() {
  const payload = { ...form }
  delete payload.id
  try {
    if (form.id) {
      await updateStudent(form.id, payload)
      showToast('学生信息已更新')
    } else {
      await createStudent(payload)
      showToast('学生已新增')
    }
    dialogVisible.value = false
    await refreshAll()
    if (view.value === 'studentDetail' && form.id) {
      selectedStudent.value = await fetchStudent(form.id)
    }
  } catch (error) {
    showToast(error.message)
  }
}

async function remove(student) {
  if (!canDeleteStudent.value) {
    showToast('当前角色无权删除学生')
    return
  }
  if (!confirm(`确定删除 ${student.name} 吗？`)) return
  try {
    await deleteStudent(student.id)
    showToast('学生已删除')
    await refreshAll()
    if (!students.value.length && pagination.page > 1) {
      pagination.page -= 1
      await loadStudentsSafely()
    }
  } catch (error) {
    showToast(error.message)
  }
}

function statusClass(status) {
  if (status === '休学') return 'paused'
  if (status === '毕业') return 'done'
  return 'active'
}

function formatDateTime(value) {
  if (!value) return '未记录'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN', { hour12: false })
}

function roleLabel(role) {
  if (role === 'ADMIN') return '管理员'
  if (role === 'TEACHER') return '教师'
  return '只读用户'
}

function showToast(message) {
  toast.value = message
  window.clearTimeout(showToast.timer)
  showToast.timer = window.setTimeout(() => {
    toast.value = ''
  }, 1800)
}

onMounted(() => {
  restoreSession()
})
</script>
