// 导入request.js请求工具
import request from '@/utils/request.js'

// 医生用户登录接口
export const adminLoginService = (loginData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in loginData) {
        params.append(key, loginData[key]);
        // console.log(key);
    }
    return request.post("/admin/login", params);
}

// 获取管理员信息
export const adminIdService = ()=>{
    return request.get("/admin/adminId");
}