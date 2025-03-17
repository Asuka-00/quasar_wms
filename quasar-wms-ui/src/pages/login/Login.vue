<template>
    <div class="login-container">
        <div class="login-box">
            <div class="text-center q-mb-md">
                <h4 class="text-h4 text-weight-bold text-primary">WMS系统</h4>
                <p class="text-subtitle1 text-grey-7">欢迎登录</p>
            </div>
            <q-form @submit="onSubmit" class="q-gutter-md">
                <q-input v-model="formData.username" label="用户名" :rules="[val => !!val || '请输入用户名']"
                    outlined dense>
                    <template v-slot:prepend>
                        <q-icon name="person" />
                    </template>
                </q-input>

                <q-input v-model="formData.password" label="密码" :type="isPwd ? 'password' : 'text'"
                    :rules="[val => !!val || '请输入密码']" outlined dense>
                    <template v-slot:prepend>
                        <q-icon name="lock" />
                    </template>
                    <template v-slot:append>
                        <q-icon :name="isPwd ? 'visibility_off' : 'visibility'" class="cursor-pointer"
                            @click="isPwd = !isPwd" />
                    </template>
                </q-input>

                <div class="row">
                    <div class="col-8">
                        <q-input v-model="formData.captchaCode" label="验证码" :rules="[val => !!val || '请输入验证码']"
                            outlined dense>
                            <template v-slot:prepend>
                                <q-icon name="security" />
                            </template>
                        </q-input>
                    </div>
                    <div class="col-4">
                        <q-img :src="captchaUrl" class="captcha-img cursor-pointer" @click="refreshCaptcha" />
                    </div>
                </div>


                <div class="q-gutter-sm">
                    <q-btn label="登录" type="submit" color="primary" class="full-width" size="lg" />
                    <q-btn label="注册" color="secondary" class="full-width" size="lg" @click="onRegister" />
                </div>
            </q-form>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { useRouter } from 'vue-router';
import axios from '@/axios/index';
import { useUserStore } from '@/stores/userStore';
const $q = useQuasar();
const router = useRouter();

const isPwd = ref(true);
const captchaUrl = ref('');

const formData = ref({
    username: '',
    password: '',
    captchaKey: '',
    captchaCode: ''
});

const refreshCaptcha = async () => {
    const res = await axios.get('/web/login/captcha');
    if (res.data.code === 200) {
        captchaUrl.value = res.data.data.image;
        formData.value.captchaKey = res.data.data.key;
    } else {
        $q.notify({
            message: "获取验证码失败",
            color: 'negative',
            position: 'top',
            timeout: 2000,
        });
    }
};

const onSubmit = async () => {
    try {
        const res = await axios.post('/web/login', formData.value);
        if (res.data.code === 200) {
            $q.notify({
                message: '登录成功',
                color: 'positive',
                position: 'top',
                timeout: 2000,
            });
            router.push('/');
            // 保存用户信息
            const userStore = useUserStore();
            userStore.setUser(res.data.data);
            console.log(userStore.getUser());
        } else {
            $q.notify({
                message: res.data.message,
                color: 'negative',
                position: 'top',
                timeout: 2000,
            });
            refreshCaptcha();
        }
    } catch (error) {
        $q.notify({
            message: '登录失败，请稍后重试',
            color: 'negative',
            position: 'top',
            timeout: 2000,
        });
        refreshCaptcha();
    }
};

const onRegister = () => {
    router.push('/register');
};

onMounted(() => {
    refreshCaptcha();
});
</script>

<style scoped>
.login-container {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
    width: 100%;
    max-width: 500px;
    padding: 2rem;
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.captcha-img {
    height: 40px;
    border-radius: 4px;
    border: 1px solid #ddd;
}
</style>
