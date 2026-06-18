import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  function setUser(userData, tokenValue) {
    user.value = userData
    token.value = tokenValue
    localStorage.setItem('token', tokenValue)
    localStorage.setItem('userInfo', JSON.stringify(userData))
  }

  function getUserFromLocal() {
    const localUserInfo = localStorage.getItem('userInfo')
    if (localUserInfo) {
      user.value = JSON.parse(localUserInfo)
    }
  }

  function logout() {
    user.value = null
    token.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    user,
    token,
    setUser,
    getUserFromLocal,
    logout
  }
})
