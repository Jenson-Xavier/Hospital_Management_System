// 导入request.js请求工具
import request from '@/utils/request.js'

// 患者用户注册接口
export const patientRegisterService = (registerData) => {
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in registerData) {
        params.append(key, registerData[key]);
        // console.log(key);
    }
    return request.post("/patient/register", params);
}

// 患者用户登录接口
export const patientLoginService = (loginData) => {
    return request.post("/patient/login", loginData);
}

// 患者用户找回密码接口
export const patientGetBackPwdService = (getBackData) => {
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in getBackData) {
        params.append(key, getBackData[key]);
        // console.log(key);
    }
    return request.post("/patient/forgetPwd", params);
}

// 发送邮件验证码接口
export const sendEmailService = (emailData) => {
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    params.append("email", emailData["username"]);
    return request.post("/patient/sendEmail", params);
}


// 获取指定患者用户的信息
export const patientInfoService = () => {
    return request.get('/patient/patientInfo')
}

// 获取全部患者用户的信息
export const allPatientInfoService = ()=>{
    return request.get('/patient/getAll')
}

// 注销患者账户(管理员)
export const delPatientAccountService = (patientIdData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in patientIdData) {
        params.append(key, patientIdData[key]);
    }
    return request.post('/patient/del', params);
}

// 注销患者账户(患者本人)
export const delMyAccountService = (patientIdData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in patientIdData) {
        params.append(key, patientIdData[key]);
    }
    return request.post('/patient/delMy', params);
}


// 测试接口函数
export const testTmpService = (tmp) => {
    const params = new URLSearchParams();
    params.append('tmp', "你好");
    return request.post("/patient/test", params);
}

// 修改密码接口函数
export const updatePwdService = (resetData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in resetData) {
        params.append(key, resetData[key]);
    }
    return request.post("/patient/updatePwd", params);
}