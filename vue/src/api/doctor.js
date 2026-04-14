// 导入request.js请求工具
import request from '@/utils/request.js'

// 医生用户登录接口
export const doctorLoginService = (loginData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in loginData) {
        params.append(key, loginData[key]);
        // console.log(key);
    }
    return request.post("/doctor/login", params);
}

// 获取医生用户名
export const doctorIdService = ()=>{
    return request.get("/doctor/doctorId");
}

// 修改医生密码
export const updatePwdService = (resetData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in resetData) {
        params.append(key, resetData[key]);
    }
    return request.post("/doctor/updatePwd", params);
}

// 添加医生账户
export const addDoctorAccount = (accountData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in accountData) {
        params.append(key, accountData[key]);
    }
    return request.post("/doctor/add", params);
}

// 获取全部医生账户信息
export const getAllDoctorAccountService = ()=>{
    return request.get("/doctor/getAll");
}

// 删除指定医生账户
export const delDoctorAccountService = (doctorIdData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in doctorIdData) {
        params.append(key, doctorIdData[key]);
    }
    return request.post('/doctor/del', params);
}