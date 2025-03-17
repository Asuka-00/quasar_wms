<template>
    <div class="home-container q-pa-md">
        <div class="background-animation">
            <div class="particles"></div>
            <div class="gradient-overlay"></div>
        </div>
        <div class="row q-col-gutter-md">
            <!-- 欢迎卡片 -->
            <div class="col-12">
                <q-card class="welcome-card">
                    <q-card-section class="welcome-content">
                        <div class="text-h3 text-weight-bold q-mb-lg animate__animated animate__fadeIn">
                            欢迎回来，{{ userStore.nickname }}
                        </div>
                        <div class="row q-col-gutter-md">
                            <div class="col-12 col-md-6">
                                <div class="time-card animate__animated animate__fadeInUp">
                                    <div class="text-h4 text-weight-bold">{{ currentTime }}</div>
                                    <div class="text-subtitle1">{{ currentDate }}</div>
                                </div>
                            </div>
                            <div class="col-12 col-md-6">
                                <div class="stats-card animate__animated animate__fadeInUp">
                                    <div class="text-h6">系统状态</div>
                                    <div class="stats-grid">
                                        <div class="stat-item" :class="{ 'selected': selectedStatus === 'normal' }" @click="selectedStatus = 'normal'">
                                            <q-icon name="inventory" size="2rem" />
                                            <div class="stat-value">正常</div>
                                        </div>
                                        <div class="stat-item" :class="{ 'selected': selectedStatus === 'good' }" @click="selectedStatus = 'good'">
                                            <q-icon name="speed" size="2rem" />
                                            <div class="stat-value">良好</div>
                                        </div>
                                        <div class="stat-item" :class="{ 'selected': selectedStatus === 'safe' }" @click="selectedStatus = 'safe'">
                                            <q-icon name="security" size="2rem" />
                                            <div class="stat-value">安全</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </q-card-section>
                </q-card>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
const currentTime = ref('')
const currentDate = ref('')
const selectedStatus = ref('normal')

// 更新时间
const updateTime = () => {
    const now = new Date()
    currentTime.value = now.toLocaleTimeString('zh-CN', { 
        hour12: false,
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    })
    currentDate.value = now.toLocaleDateString('zh-CN', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric', 
        weekday: 'long' 
    })
}

let timer: number

onMounted(() => {
    updateTime()
    timer = window.setInterval(updateTime, 10000)
})

onUnmounted(() => {
    if (timer) {
        clearInterval(timer)
    }
})
</script>

<style scoped>
.home-container {
    max-width: 1200px;
    margin: 0 auto;
    position: relative;
    z-index: 1;
}

.background-animation {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(45deg, #1a237e, #0d47a1, #1565c0);
    background-size: 400% 400%;
    animation: gradientBG 15s ease infinite;
    z-index: 0;
}

.particles {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: 
        radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.1) 1px, transparent 1px),
        radial-gradient(circle at 75% 75%, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
    background-size: 50px 50px;
    animation: particleMove 20s linear infinite;
}

.gradient-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle at center, transparent 0%, rgba(0, 0, 0, 0.3) 100%);
}

.welcome-card {
    background: rgba(255, 255, 255, 0.1);
    color: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.welcome-content {
    padding: 2rem;
}

.time-card {
    background: rgba(255, 255, 255, 0.1);
    padding: 1.5rem;
    border-radius: 12px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.stats-card {
    background: rgba(255, 255, 255, 0.1);
    padding: 1.5rem;
    border-radius: 12px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    height: 100%;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
    margin-top: 1rem;
}

.stat-item {
    text-align: center;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 8px;
    transition: all 0.3s ease;
    cursor: pointer;
}

.stat-item:hover {
    transform: translateY(-5px);
    background: rgba(255, 255, 255, 0.1);
}

.stat-item.selected {
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.4);
    transform: translateY(-5px);
}

.stat-value {
    margin-top: 0.5rem;
    font-size: 0.9rem;
    opacity: 0.9;
}

.text-h3 {
    background: linear-gradient(45deg, #fff, #e3f2fd);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.text-h4 {
    font-size: 2.5rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
}

.text-subtitle1 {
    opacity: 0.9;
    font-size: 1.1rem;
}

/* 添加动画延迟 */
.animate__fadeInUp:nth-child(2) {
    animation-delay: 0.2s;
}

@keyframes gradientBG {
    0% {
        background-position: 0% 50%;
    }
    50% {
        background-position: 100% 50%;
    }
    100% {
        background-position: 0% 50%;
    }
}

@keyframes particleMove {
    0% {
        background-position: 0 0;
    }
    100% {
        background-position: 50px 50px;
    }
}
</style> 