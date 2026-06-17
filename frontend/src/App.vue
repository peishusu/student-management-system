<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span>学</span>
        <div>
          <h1>学生管理系统</h1>
          <p>Vue + Spring Boot</p>
        </div>
      </div>

      <nav class="nav-list">
        <button :class="{ active: view === 'students' }" @click="view = 'students'">学生档案</button>
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
        <button v-if="view === 'students'" class="primary-btn" @click="openCreate">新增学生</button>
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
            <input v-model="filters.keyword" placeholder="姓名、学号、班级、电话" @input="loadStudents" />
          </label>
          <label>
            <span>班级</span>
            <select v-model="filters.className" @change="loadStudents">
              <option value="">全部班级</option>
              <option v-for="className in classes" :key="className" :value="className">{{ className }}</option>
            </select>
          </label>
          <label>
            <span>状态</span>
            <select v-model="filters.status" @change="loadStudents">
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
                    <button @click="openEdit(student)">编辑</button>
                    <button class="danger-text" @click="remove(student)">删除</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="!students.length" class="empty-state">暂无学生数据</div>
        </div>
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
          <li><code>schema.sql</code>：创建数据库、学生表、索引</li>
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

    <div v-if="toast" class="toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { createStudent, deleteStudent, fetchClasses, fetchStats, fetchStudents, updateStudent } from './api/students'

const titles = {
  students: '学生档案',
  stats: '统计分析',
  database: '数据库说明'
}

const view = ref('students')
const students = ref([])
const classes = ref([])
const stats = ref({})
const toast = ref('')
const dialogVisible = ref(false)
const filters = reactive({
  keyword: '',
  className: '',
  status: ''
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

async function refreshAll() {
  await Promise.all([loadStudents(), loadStats(), loadClasses()])
}

async function loadStudents() {
  students.value = await fetchStudents(filters)
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
  loadStudents()
}

function openCreate() {
  Object.assign(form, emptyForm)
  dialogVisible.value = true
}

function openEdit(student) {
  Object.assign(form, student)
  dialogVisible.value = true
}

async function save() {
  const payload = { ...form }
  delete payload.id
  if (form.id) {
    await updateStudent(form.id, payload)
    showToast('学生信息已更新')
  } else {
    await createStudent(payload)
    showToast('学生已新增')
  }
  dialogVisible.value = false
  await refreshAll()
}

async function remove(student) {
  if (!confirm(`确定删除 ${student.name} 吗？`)) return
  await deleteStudent(student.id)
  showToast('学生已删除')
  await refreshAll()
}

function statusClass(status) {
  if (status === '休学') return 'paused'
  if (status === '毕业') return 'done'
  return 'active'
}

function showToast(message) {
  toast.value = message
  window.clearTimeout(showToast.timer)
  showToast.timer = window.setTimeout(() => {
    toast.value = ''
  }, 1800)
}

onMounted(() => {
  refreshAll().catch((error) => showToast(error.message))
})
</script>
