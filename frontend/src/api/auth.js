import { clearSession, getSession, request, saveSession, saveSessionUser, setAuthFailureHandler } from './request'

export { clearSession, getSession, setAuthFailureHandler }

export async function login(payload) {
  const auth = await request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
    skipAuthHandler: true
  })
  saveSession(auth)
  return auth
}

export async function register(payload) {
  const auth = await request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
    skipAuthHandler: true
  })
  saveSession(auth)
  return auth
}

export async function fetchCurrentUser() {
  const user = await request('/api/auth/me')
  saveSessionUser(user)
  return user
}

export function changePassword(payload) {
  return request('/api/auth/change-password', {
    method: 'POST',
    body: JSON.stringify(payload)
  })
}

export function logoutSession() {
  return request('/api/auth/logout', {
    method: 'POST'
  })
}
