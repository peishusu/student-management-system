const TOKEN_KEY = 'student_management_token'
const USER_KEY = 'student_management_user'

let unauthorizedHandler = null

export function setAuthFailureHandler(handler) {
  unauthorizedHandler = handler
}

export function getSession() {
  const token = window.localStorage.getItem(TOKEN_KEY)
  if (!token) return null

  const userText = window.localStorage.getItem(USER_KEY)
  let user = null
  if (userText) {
    try {
      user = JSON.parse(userText)
    } catch {
      user = null
    }
  }

  return { token, user }
}

export function saveSession(auth) {
  window.localStorage.setItem(TOKEN_KEY, auth.token)
  window.localStorage.setItem(USER_KEY, JSON.stringify(auth.user))
}

export function saveSessionUser(user) {
  window.localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearSession() {
  window.localStorage.removeItem(TOKEN_KEY)
  window.localStorage.removeItem(USER_KEY)
}

export async function request(url, options = {}) {
  const { skipAuthHandler = false, ...fetchOptions } = options
  const headers = new Headers(fetchOptions.headers || {})

  if (fetchOptions.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const session = getSession()
  if (session?.token) {
    headers.set('Authorization', `Bearer ${session.token}`)
  }

  const response = await fetch(url, {
    ...fetchOptions,
    headers
  })

  let result = {}
  try {
    result = await response.json()
  } catch {
    result = { message: response.statusText || '请求失败' }
  }

  if (response.status === 401 && !skipAuthHandler) {
    clearSession()
    unauthorizedHandler?.(result.message || '登录已过期，请重新登录')
  }

  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '请求失败')
  }

  return result.data
}
