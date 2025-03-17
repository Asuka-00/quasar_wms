<template>
    <q-layout view="hHh lpR fFf">

        <q-header elevated class="bg-primary text-white">
            <q-toolbar>
                <q-btn dense flat round icon="menu" @click="toggleLeftDrawer" />

                <q-toolbar-title>
                    <q-avatar>
                        <img src="https://cdn.quasar.dev/logo-v2/svg/logo-mono-white.svg">
                    </q-avatar>
                    WMS系统

                    <!-- 返回主页 -->
                    <q-btn dense flat round icon="home" @click="handleHome" class="q-ml-sm">
                        <q-tooltip>返回主页</q-tooltip>
                    </q-btn>

                </q-toolbar-title>

                <!-- 消息通知 -->
                <q-btn flat round icon="notifications">
                    <q-badge v-if="unreadCount > 0" color="red" floating :label="String(unreadCount)" />
                    <q-tooltip>消息通知</q-tooltip>
                </q-btn>

                <q-menu anchor="bottom right" self="top right" :offset="[0, 8]">
                    <q-list style="min-width: 500px">
                        <q-item v-if="unreadAlerts.length === 0">
                            <q-item-section>
                                <q-item-label>暂无未读消息</q-item-label>
                            </q-item-section>
                        </q-item>
                        <q-item v-for="alert in unreadAlerts" :key="alert.id" clickable v-ripple>
                            <q-item-section>
                                <q-item-label>{{ alert.msg }}</q-item-label>
                                <q-item-label caption>{{ formatTime(alert.createdTime) }}</q-item-label>
                            </q-item-section>
                            <q-item-section side>
                                <q-btn flat round color="primary" icon="check_circle" size="sm"
                                    @click.stop="markAsRead(alert.id)">
                                    <q-tooltip>标记已读</q-tooltip>
                                </q-btn>
                            </q-item-section>
                        </q-item>
                    </q-list>
                </q-menu>

                <q-btn-dropdown flat icon="person" :label="userStore.nickname">
                    <q-list>
                        <q-item clickable v-close-popup @click="handleLogout">
                            <q-item-section avatar>
                                <q-icon name="logout" color="negative" />
                            </q-item-section>
                            <q-item-section>
                                <q-item-label>退出登录</q-item-label>
                            </q-item-section>
                        </q-item>
                    </q-list>
                </q-btn-dropdown>
            </q-toolbar>
        </q-header>

        <q-drawer v-model="leftDrawerOpen" side="left" bordered behavior="desktop">
            <q-scroll-area class="fit">
                <q-list>
                    <template v-for="route in menuRoutes" :key="route.path">
                        <q-expansion-item v-if="route.children" :icon="route.meta?.icon || ''"
                            :label="route.meta?.title || ''">
                            <q-item v-for="child in route.children" :key="child.path"
                                :to="`/web/${route.path}/${child.path}`" exact clickable v-ripple class="q-pl-xl">
                                <q-item-section avatar>
                                    <q-icon :name="child.meta?.icon || ''" size="sm" />
                                </q-item-section>
                                <q-item-section>
                                    {{ child.meta?.title }}
                                </q-item-section>
                            </q-item>
                        </q-expansion-item>
                    </template>
                </q-list>
            </q-scroll-area>
        </q-drawer>

        <q-page-container>
            <router-view />
        </q-page-container>

    </q-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import '@/router/types'
import { useUserStore } from '@/stores/userStore'
import { useQuasar, type QBtn } from 'quasar'
import axios from '@/axios/index'

const router = useRouter()
const userStore = useUserStore()
const $q = useQuasar()

const menuRoutes = computed(() => {
    const webRoute = router.options.routes.find(route => route.path === '/web')
    const routes = webRoute?.children || []

    // 过滤菜单项
    return routes.filter(route => {
        // 检查父菜单权限
        if (!hasPermission(route.meta?.roles)) {
            return false
        }

        // 如果有子菜单，过滤子菜单
        if (route.children) {
            route.children = route.children.filter(child => hasPermission(child.meta?.roles))
            // 如果子菜单被过滤完了，父菜单也不显示
            return route.children.length > 0
        }

        return true
    })
})

// 检查用户是否有权限访问
const hasPermission = (roles: any) => {
    if (!roles) return true
    return roles.includes(userStore.role)
}

const leftDrawerOpen = ref(true)

// 返回主页
const handleHome = () => {
    router.push('/')
}

function toggleLeftDrawer() {
    leftDrawerOpen.value = !leftDrawerOpen.value
}

// 处理登出
const handleLogout = () => {
    $q.dialog({
        title: '确认退出',
        message: '确定要退出登录吗？',
        cancel: true,
        persistent: true,
        ok: {
            label: '确定',
            color: 'negative'
        }
    }).onOk(() => {
        userStore.clearUser()
        router.push('/login')
    })
}

interface StockAlertDetail {
    id: number;
    msg: string;
    sendTo: number;
    status: number;
    createdTime: string;
}

const alerts = ref<StockAlertDetail[]>([]);
const unreadCount = computed(() => alerts.value.filter(alert => alert.status === 0).length);
const unreadAlerts = computed(() => alerts.value.filter(alert => alert.status === 0));

const getAlerts = async () => {
    try {
        const res = await axios.get('/web/whAlert/getAlert', {
            params: {
                id: userStore.id
            }
        });
        if (res.data.code === 200) {
            alerts.value = res.data.data;
        }
    } catch (error) {
        console.error('获取消息失败:', error);
    }
};

const formatTime = (time: string) => {
    if (!time) return '';
    return new Date(time).toLocaleString();
};

const markAsRead = async (id: number) => {
    try {
        const res = await axios.get('/web/whAlert/readAlert', {
            params: {
                id: id
            }
        });
        if (res.data.code === 200) {
            // 更新本地消息状态
            const alert = alerts.value.find(a => a.id === id);
            if (alert) {
                alert.status = 1;
            }
            $q.notify({
                message: '已标记为已读',
                color: 'positive',
                position: 'top',
                timeout: 2000
            });
        }
    } catch (error) {
        console.error('标记已读失败:', error);
        $q.notify({
            message: '标记已读失败',
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
    }
};

onMounted(() => {
   if(userStore.getUser().token!=null&&userStore.getUser().token!=''){
     getAlerts();
   }
});
</script>

<style scoped>
.q-drawer {
    background-color: #f5f5f5;
}

.q-item {
    min-height: 40px;
}

.q-item.q-router-link-active {
    background-color: #e3f2fd;
}

.q-btn-dropdown {
    margin-right: 8px;
}

.q-btn-dropdown .q-icon {
    margin-right: 4px;
}
</style>