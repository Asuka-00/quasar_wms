import axios from 'axios';
import { useUserStore } from '@/stores/userStore';

// 创建一个 axios 实例
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // 替换为您的 API 基础 URL
  timeout: 5000 // 请求超时时间
});

// 添加请求拦截器
axiosInstance.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    config.headers['access-token'] = useUserStore().getUser().token;
    return config;
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

// 添加响应拦截器
axiosInstance.interceptors.response.use(
  response => {
    // 如果响应状态码为 401，尝试使用 refreshToken 刷新 token
    if (response.data.code === 401) {
      const tokenStore = useUserStore();
      const refreshToken = tokenStore.getUser().refreshToken;
      if (refreshToken) {
        return axiosInstance.get('/web/login/refreshToken', {
          params: {
            refreshToken: refreshToken
          },
        }).then((res) => {
          if (res.data.code === 200) {
            let user = tokenStore.getUser();
            user.token = res.data.data;
            tokenStore.setUser(user);
            // 重新发送请求
            return axiosInstance(response.config);
          }
          return Promise.reject(new Error('刷新token失败'));
        }).catch((error) => {
          console.log(error);
          return Promise.reject(error);
        });
      }
    }
    return response;
  },
  error => {
    // 对响应错误做些什么
    return Promise.reject(error);
  }
);

export default axiosInstance;