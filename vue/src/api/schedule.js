import request from '@/utils/request.js'

export const scheduleAllService = () => {
    return request.get("/schedule/getAll")
    // 导入request.js请求工具
}
// 获取全部排班信息
export const getScheduleInfoService = () => {
    return request.get("/schedule/getAll");
}

export const addNewScheduleService = (scheduleData) => {
    return request.post("/schedule/add", scheduleData)
}

export const deleteScheduleInfoService = (scheduleData) => {
    return request.post("/schedule/delete", scheduleData)
}

// 根据科室Id删除排班信息
export const deleteScheduleByOfficeIdService = (scheduleData)=>{
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in scheduleData) {
        params.append(key, scheduleData[key]);
    }
    return request.post("/schedule/delByOfficeId", params);
}