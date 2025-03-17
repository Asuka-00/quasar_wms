import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { PiniaPluginContext } from 'pinia'

interface UserData {
    id: number;
    username: string;
    role: string;
    nickname: string;
    token: string;
    refreshToken: string;
}

export const useUserStore = defineStore('user', () => {
    const id = ref(0)
    const username = ref('')
    const role = ref('')
    const nickname = ref('')
    const token = ref('')
    const refreshToken = ref('')

    function setUser(user: UserData) {
        id.value = user.id
        username.value = user.username
        role.value = user.role
        nickname.value = user.nickname
        token.value = user.token
        refreshToken.value = user.refreshToken
    }

    function clearUser() {
        id.value = 0
        username.value = ''
        role.value = ''
        nickname.value = ''
        token.value = ''
        refreshToken.value = ''
    }

    function getUser(): UserData {
        return {
            id: id.value,
            username: username.value,
            role: role.value,
            nickname: nickname.value,
            token: token.value,
            refreshToken: refreshToken.value
        }
    }

    function setToken(token: string) {
        token = token
    }

    function getToken() {
        return token
    }

    return {
        id,
        username,
        role,
        nickname,
        token,
        refreshToken,
        setUser,
        clearUser,
        getUser,
        setToken,
        getToken
    }
}, {
    persist: {
        key: 'user-store',
        storage: localStorage,
        paths: ['id', 'username', 'role', 'nickname', 'token', 'refreshToken']
    }
} as any)


