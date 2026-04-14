// 定制请求的实例

import axios from 'axios';
// 定义一个变量 用于记录公共的前缀
// const baseURL = "http://localhost:8080";
const baseURL = "/api";
const instance = axios.create({baseURL});

import { useTokenStore } from '@/stores/token';
import { ElMessage } from 'element-plus';
// 添加请求拦截头
instance.interceptors.request.use (
    (config)=>{
        // 请求前的回调
        // 添加token
        const tokenStore = useTokenStore();
        // 判断有无token
        if (tokenStore.token) {
            config.headers.Authorization = tokenStore.token;
        }
        return config;
    },
    (err)=>{
        // 请求错误的回调
        Promise.reject(err);
    }
)

// import { useRouter } from 'vue-router';
// const router = useRouter();

import router from '@/router'

// 添加响应拦截器
instance.interceptors.response.use(
    result=>{
        if (result.data.code === 0) {
            return result.data;
        }
        ElMessage.error(result.data.message?result.data.message:"服务异常1");
        return Promise.reject(result.data);
    },
    err=>{
        // 如果响应状态是401(访问后端服务器时未携带token) 代表未登录 给出对应的提示 并跳转到登录界面
        if (err.response.status === 401) {
            ElMessage.error("请先登录");
            router.push("/init");
        } else {
            ElMessage.error("服务异常2");
        }
        return Promise.reject(err); // 异步状态转化为失败的状态
    }
)

export default instance;
