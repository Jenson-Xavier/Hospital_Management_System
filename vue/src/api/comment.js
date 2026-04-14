import request from '@/utils/request.js'

export const commentAllService = () => {
    return request.get("/comment/getAll")
}

export const getCommentInfoService = () => {
    return request.get("/comment/getAll")
}

export const commentAddService = (commentData) => {
    return request.post('/comment/add', commentData)
}

export const addCommentService = (commentData) => {
    return request.post('/comment/add', commentData)
}

export const commentDeleteService = (commentData) => {
    return request.post('/comment/delete', commentData)
}

// 通过审核评论信息
export const auditPassCommentService = (commentData) => {
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in commentData) {
        params.append(key, commentData[key]);
    }
    return request.post("/comment/auditPass", params);
}

// 不通过审核评论信息
export const auditUnPassCommentService = (commentData) => {
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in commentData) {
        params.append(key, commentData[key]);
    }
    return request.post("/comment/auditUnPass", params);
}


// 删除评论信息
export const deleteCommentService = (commentData) => {
    // 借助于urlSearchParams完成传递
    const params = new URLSearchParams();
    for (let key in commentData) {
        params.append(key, commentData[key]);
    }
    return request.post("/comment/delete", params);
}
