// 导入request.js请求工具
import request from '@/utils/request.js'

// 获取全部科室信息
export const officeService = ()=>{
    return request.get("/office/getAll");
}

// 获取全部科室信息
export const officeAllService = ()=>{
    return request.get("/office/getAll");
}

// 获取单名医生的科室信息
export const singleOfficeService = (officeIdData)=>{
    const params = new URLSearchParams();
    for (let key in officeIdData) {
        params.append(key, officeIdData[key]);
    }
    return request.post("/office/getById", params);
}

// 删除科室
export const deleteOfficeService = (officeData) => {
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in officeData) {
        params.append(key, officeData[key]);
    }
    return request.post("/office/delete", params);
}

// 更新科室信息
export const updateOfficeService = (officeData) => {
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in officeData) {
        params.append(key, officeData[key]);
    }
    return request.post("/office/update", params);
}