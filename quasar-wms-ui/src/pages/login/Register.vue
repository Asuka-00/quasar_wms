<template>
    <div class="login-container">
        <div class="login-box">
            <div class="text-center q-mb-md">
                <h4 class="text-h4 text-weight-bold text-primary">WMS系统</h4>
                <p class="text-subtitle1 text-grey-7">用户注册</p>
            </div>
            <q-form @submit="onSubmit" class="q-gutter-md">
                <q-input v-model="formData.username" label="用户名" :rules="[
                    val => !!val || '请输入用户名',
                    val => val.length >= 3 || '用户名至少3个字符'
                ]" outlined dense>
                    <template v-slot:prepend>
                        <q-icon name="person" />
                    </template>
                </q-input>

                <q-input v-model="formData.nickname" label="昵称" :rules="[val => !!val || '请输入昵称']"
                    outlined dense>
                    <template v-slot:prepend>
                        <q-icon name="badge" />
                    </template>
                </q-input>

                <q-input v-model="formData.password" label="密码" :type="isPwd ? 'password' : 'text'"
                    :rules="[
                        val => !!val || '请输入密码',
                        val => val.length >= 6 || '密码至少6个字符'
                    ]" outlined dense>
                    <template v-slot:prepend>
                        <q-icon name="lock" />
                    </template>
                    <template v-slot:append>
                        <q-icon :name="isPwd ? 'visibility_off' : 'visibility'" class="cursor-pointer"
                            @click="isPwd = !isPwd" />
                    </template>
                </q-input>

                <q-input v-model="formData.confirmPassword" label="确认密码" :type="isPwd ? 'password' : 'text'"
                    :rules="[
                        val => !!val || '请确认密码',
                        val => val === formData.password || '两次输入的密码不一致'
                    ]" outlined dense>
                    <template v-slot:prepend>
                        <q-icon name="lock" />
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
                    <q-btn label="注册" type="submit" color="primary" class="full-width" size="lg" />
                    <q-btn label="返回登录" color="secondary" class="full-width" size="lg" @click="onBackToLogin" />
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

const $q = useQuasar();
const router = useRouter();

const isPwd = ref(true);
const captchaUrl = ref('');

const formData = ref({
    username: '',
    nickname: '',
    password: '',
    confirmPassword: '',
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
        const res = await axios.post('/web/register', formData.value);
        if (res.data.code === 200) {
            $q.notify({
                message: '注册成功',
                color: 'positive',
                position: 'top',
                timeout: 2000,
            });
            router.push('/login');
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
            message: '注册失败，请稍后重试',
            color: 'negative',
            position: 'top',
            timeout: 2000,
        });
        refreshCaptcha();
    }
};

const onBackToLogin = () => {
    router.push('/login');
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
