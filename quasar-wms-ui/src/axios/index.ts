import axios from 'axios';
import type { AxiosRequestConfig, AxiosResponse } from 'axios';
import { useUserStore } from '@/stores/userStore';
import router from '@/router/index';
// 创建一个 axios 实例
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // 替换为您的 API 基础 URL
  timeout: 5000 // 请求超时时间
});

// 标记是否正在刷新token
let isRefreshing = false;
// 存储等待刷新token的请求
let pendingRequests: Array<{
  config: AxiosRequestConfig;
  resolve: (value: any) => void;
  reject: (reason?: any) => void;
}> = [];

// 添加请求拦截器
axiosInstance.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    config.headers['access-token'] = useUserStore().getUser().token;
    // 标记是否是刷新token的请求
    if (config.url === '/web/login/refreshToken') {
      (config as any).isRefreshTokenRequest = true;
    }
    return config;
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

// 执行队列中的请求
const processQueue = (error: any = null) => {
  pendingRequests.forEach(request => {
    if (error) {
      request.reject(error);
    } else {
      // 使用新的token重试请求
      if (request.config.headers) {
        request.config.headers['access-token'] = useUserStore().getUser().token;
      }
      request.resolve(axiosInstance(request.config));
    }
  });
  // 清空队列
  pendingRequests = [];
};

// 提示登录过期并跳转到登录页
const handleLoginExpired = () => {
  // 显示简单的提示
  alert('登录已过期，请重新登录');
  
  // 跳转登录页
  router.push('/login');

  // 清除token
  const tokenStore = useUserStore();
  tokenStore.clearUser();
  
};

// 添加响应拦截器
axiosInstance.interceptors.response.use(
  async function (response: AxiosResponse): Promise<AxiosResponse> {
    // 如果是刷新token的请求，直接返回
    if ((response.config as any).isRefreshTokenRequest) {
      return response;
    }

    // 如果响应状态码为 401，尝试使用 refreshToken 刷新 token
    if (response.data.code === 401) {
      const tokenStore = useUserStore();
      const refreshToken = tokenStore.getUser().refreshToken;
      
      if (!refreshToken) {
        // 没有refreshToken，直接登出
        handleLoginExpired();
        return Promise.reject(new Error('登录过期'));
      }

      // 创建一个Promise，添加到队列中
      const retryRequest = new Promise<AxiosResponse>((resolve, reject) => {
        pendingRequests.push({
          config: response.config,
          resolve,
          reject
        });
      });

      if (!isRefreshing) {
        isRefreshing = true;

        try {
          const res = await axiosInstance.get('/web/login/refreshToken', {
            params: {
              refreshToken: refreshToken
            },
          });

          if (res.data.code === 200) {
            let user = tokenStore.getUser();
            user.token = res.data.data;
            tokenStore.setUser(user);
            
            // 处理队列中的请求
            processQueue();
          } else {
            // 刷新token失败，处理登录过期
            handleLoginExpired();
            
            // 处理队列中的请求，传递错误
            processQueue(new Error('登录过期'));
          }
        } catch (error) {
          // 处理队列中的请求，传递错误
          processQueue(error);
          // 处理登录过期
          handleLoginExpired();
        } finally {
          isRefreshing = false;
        }
      }

      return retryRequest;
    }
    
    return response;
  },
  async error => {
    // 对响应错误做些什么
    return Promise.reject(error);
  }
);

export default axiosInstance;