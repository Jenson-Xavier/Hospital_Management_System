// 导入request.js请求工具
import request from '@/utils/request.js'

// 获取医生信息
export const doctorInfoService = ()=>{
    return request.get("/doctorInfo/get");
}

// 获取医生信息 tjy
export const allDoctorInfoService = ()=>{
    return request.get("/doctorInfo/getAll");
}

// 获取医生信息 xjx
export const doctorAllInfoService = ()=>{
    return request.get("/doctorInfo/getAll");
}

// 更新医生信息
export const updateDoctorInfoService = (doctorInfoData)=>{
     // 借助于urlSearchParams完成传递
     const params = new URLSearchParams();
     for (let key in doctorInfoData) {
         params.append(key, doctorInfoData[key]);
     }
     return request.post("/doctorInfo/update", params);
}

// 删除医生信息
export const deleteDoctorInfoService = (doctorInfoData)=>{
     // 借助于urlSearchParams完成传递
     const params = new URLSearchParams();
     for (let key in doctorInfoData) {
         params.append(key, doctorInfoData[key]);
     }
     return request.post("/doctorInfo/del", params);
}

// 根据科室id删除医生信息
export const deleteDoctorInfoByOfficeIdService = (doctorInfoData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in doctorInfoData) {
        params.append(key, doctorInfoData[key]);
    }
    return request.post("/doctorInfo/delByOfficeId", params);
}