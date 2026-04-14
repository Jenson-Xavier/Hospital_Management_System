import request from '@/utils/request.js'

export const appointmentAllService = () => {
    return request.get("/appointment/getAll")
}

export const getAppointmentInfoService = () => {
    return request.get("/appointment/getAll")
}

export const appointmentAddService = (appointmentData) => {
    return request.post("/appointment/add", appointmentData)
}

export const appointmentDeleteService = (appointmentData) => {
    return request.post("/appointment/delete", appointmentData)
}

// 处理预约请求
export const solveAppointmentService = (appointmentData) => {
        // 借助于urlSearchParams完成传递
        const params = new URLSearchParams();
        for (let key in appointmentData) {
            params.append(key, appointmentData[key]);
        }
        return request.post("/appointment/solve", params);
}

