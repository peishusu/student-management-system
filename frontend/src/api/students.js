const API_BASE = '/api/students'

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  })

  const result = await response.json()
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '请求失败')
  }
  return result.data
}

export function fetchStudents(params = {}) {
  const query = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value) query.set(key, value)
  })
  const suffix = query.toString() ? `?${query.toString()}` : ''
  return request(`${API_BASE}${suffix}`)
}

export function fetchStats() {
  return request(`${API_BASE}/stats`)
}

export function fetchClasses() {
  return request(`${API_BASE}/classes`)
}

export function createStudent(payload) {
  return request(API_BASE, {
    method: 'POST',
    body: JSON.stringify(payload)
  })
}

export function updateStudent(id, payload) {
  return request(`${API_BASE}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  })
}

export function deleteStudent(id) {
  return request(`${API_BASE}/${id}`, {
    method: 'DELETE'
  })
}
