<script lang="ts" setup>
import router from '@/router';
import { ElMessage, ElMessageBox, useDisabled } from 'element-plus';
import { ref, computed, onMounted } from 'vue';

// 搜索表单
const searchForm = ref({
    audit: '',
    doctor_name: '',
    doctor_id: '',
    patient_id: '',
    publish_time: '',
    start_date: '',
    end_date: '',
});

// 审核状态映射
const auditMapping = {
    1: "未审核",
    2: "通过",
    3: "未通过"
}

// 性别映射
const genderMapping = {
    1: "男",
    2: "女"
}

// 解析日期
const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从0开始  
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

import { allDoctorInfoService } from '@/api/doctorInfo';
import { getCommentInfoService } from '@/api/comment';
// 医生信息/评论信息整合信息
const doctorInfoList = ref([]);
const comments = ref([{
    comment_id: '',
    publisher: {
        user_id: ''
    },
    commented: {
        user_id: ''
    },
    publish_time: '',
    comment_text: '',
    audit: ''
}]);
const combinedData = ref([]);
const combinedCommentData = async () => {
    let result = await allDoctorInfoService();
    doctorInfoList.value = result.data;
    console.log(doctorInfoList.value);
    result = await getCommentInfoService();
    comments.value = result.data;
    console.log(comments.value);
    let data = [];
    // 手动遍历commentInfo  
    for (let i = 0; i < comments.value.length; i++) {
        const comment = comments.value[i];
        let commentInfoData = {
            comment_id: comment.comment_id,
            doctor_id: comment.commented.user_id,
            doctor_name: '',
            gender: '',
            office: '',
            seniority: '',
            doctor_intro: '',
            patient_id: comment.publisher.user_id,
            publish_time: formatTimestamp(comment.publish_time),
            comment_text: comment.comment_text,
            audit: auditMapping[comment.audit],
        };
        // 手动遍历doctorAccountInfo  
        for (let j = 0; j < doctorInfoList.value.length; j++) {
            const info = doctorInfoList.value[j];
            // 如果user_id匹配，则合并数据  
            if (comment.commented.user_id === info.doctor.user_id) {
                commentInfoData.doctor_name = info.name;
                commentInfoData.gender = genderMapping[info.gender];
                commentInfoData.office = info.office.name;
                commentInfoData.seniority = info.seniority;
                commentInfoData.doctor_intro = info.intro;
                break; // 找到匹配项后可以跳出内层循环  
            }
        }
        data.push(commentInfoData);
    }
    combinedData.value = data;
    console.log(data);
};
combinedCommentData();


// 当前页码
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(comments.value.length);

// 过滤后的评价列表
const filteredComments = computed(() => {
    let result;
    result = combinedData.value.filter(comment => {
        const publish_time = new Date(comment.publish_time);
        const start_date = searchForm.value.start_date ? new Date(searchForm.value.start_date) : null;
        const end_date = searchForm.value.end_date ? new Date(searchForm.value.end_date) : null;

        if (end_date) {  
            end_date.setDate(end_date.getDate() + 1);  
        }  

        // 检查评论日期是否在开始日期和结束日期之间  
        const isWithinDateRange = (
            (!start_date || publish_time >= start_date) &&
            (!end_date || publish_time <= end_date)
        );
        return (
            isWithinDateRange && 
            comment.doctor_name.includes(searchForm.value.doctor_name) &&
            comment.patient_id.includes(searchForm.value.patient_id) &&
            comment.doctor_id.includes(searchForm.value.doctor_id) &&
            (searchForm.value.audit === '' || comment.audit === searchForm.value.audit));
    });
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});



/* 对话框显示区 */
// 医生信息对话框
// 显示医生信息对话框
const doctorInfoDialogVisible = ref(false);
const selectedDoctor = ref(null);

const showDoctorInfo = (doctorInfo) => {
    selectedDoctor.value = doctorInfo;
    doctorInfoDialogVisible.value = true;
};

const closeDoctorInfoDialog = () => {
    doctorInfoDialogVisible.value = false;
    selectedDoctor.value = null;
};

// 评论内容详情显示对话框
const selectedIntro = ref(null);
const introDialogVisible = ref(false);
const showIntroDetails = (intro) => {
    selectedIntro.value = intro;
    introDialogVisible.value = true;
};

const closeIntroDialog = ()=>{
    introDialogVisible.value = false;
    selectedIntro.value = null;
}

// 评论信息查看对话框
const selectedComment = ref(null);
const commentDialogVisible = ref(false);
const showComment = (commentInfo) => {
    selectedComment.value = commentInfo;
    commentDialogVisible.value = true;
};

const closeCommentDialog = ()=>{
    commentDialogVisible.value = false;
    selectedComment.value = null;
}


// 日期选择框更改
const ElementChange = () => {
    currentPage.value = 1;
}


/* 逻辑功能实现区 */
import { auditPassCommentService, auditUnPassCommentService, deleteCommentService } from '@/api/comment';
const passComment = async (CommentInfo)=>{  
    if (CommentInfo.audit != "未审核") {
        ElMessage.error("当前评论已审核");
        return;
    }
    let result;
    console.log(CommentInfo);
    result = await auditPassCommentService({
        id: CommentInfo.comment_id
    });
    if (result.code == 0) {
        ElMessage.success("已通过该评论的发布");
        combinedCommentData();
    } else {
        ElMessage.error(result.message ? result.message : "通过审核失败");
    }
}

const unPassComment = async (CommentInfo)=>{
    if (CommentInfo.audit != "未审核") {
        ElMessage.error("当前评论已审核");
        return;
    }
    let result;
    result = await auditUnPassCommentService({
        id: CommentInfo.comment_id
    });
    if (result.code == 0) {
        ElMessage.success("已不通过该评论的发布");
        combinedCommentData();
    } else {
        ElMessage.error(result.message ? result.message : "不通过审核失败");
    }
}

const deleteComment = async (CommentId)=>{
    let result;
    result = await deleteCommentService({
        id: CommentId
    });
    if (result.code == 0) {
        ElMessage.success("已删除该评论");
        combinedCommentData();
    } else {
        ElMessage.error(result.message ? result.message : "删除评论失败");
    }
}

const dialog_goto_pass = (CommentInfo)=>{
    let message = '你确定要通过该评论的发表吗？';
    ElMessageBox.confirm(
        message,
        '确认删除',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            ElMessage({
                type: 'success',
                message: '成功确认',
            });
            passComment(CommentInfo);
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            });
        })
}

const dialog_goto_unpass = (CommentInfo)=>{
    let message = '你确定不通过该评论的发表吗？';
    ElMessageBox.confirm(
        message,
        '确认删除',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            ElMessage({
                type: 'success',
                message: '成功确认',
            });
            unPassComment(CommentInfo);
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            });
        })
}

const dialog_goto_del = (CommentId)=>{
    let message = '你确定要删除该评论信息吗？';
    ElMessageBox.confirm(
        message,
        '确认删除',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            ElMessage({
                type: 'success',
                message: '成功确认',
            });
            deleteComment(CommentId);
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            });
        })
}


</script>

<template>
    <div>
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="医生姓名">
                <el-input v-model="searchForm.doctor_name" placeholder="输入医生姓名" style="width: 120px;" clearable
                    @clear="ElementChange"></el-input>
            </el-form-item>
            <el-form-item label="医生工号">
                <el-input v-model="searchForm.doctor_id" placeholder="输入医生工号" style="width: 120px;" clearable
                    @clear="ElementChange"></el-input>
            </el-form-item>
            <el-form-item label="患者用户名">
                <el-input v-model="searchForm.patient_id" placeholder="输入患者用户名" style="width: 120px;" clearable
                    @clear="ElementChange"></el-input>
            </el-form-item>
            <el-form-item label="开始日期">
                <el-date-picker v-model="searchForm.start_date" type="date" placeholder="选择日期" clearable
                    @clear="searchForm.start_date = '', ElementChange" style="width: 100px;"></el-date-picker>
            </el-form-item>
            <el-form-item label="结束日期">
                <el-date-picker v-model="searchForm.end_date" type="date" placeholder="选择日期" clearable
                    @clear="searchForm.end_date = '', ElementChange" style="width: 100px;"></el-date-picker>
            </el-form-item>
            <el-form-item label="审核状态">
                <el-select v-model="searchForm.audit" placeholder="选择状态" clearable
                    @clear="searchForm.audit = '', ElementChange" style="width: 100px;">
                    <el-option v-for="(item, index) in auditMapping" :key="index" :label="item"
                        :value="item"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary">查询</el-button>
            </el-form-item>
        </el-form>

        <!-- 评价列表 -->
        <el-table :data="filteredComments" style="width: 100%" :key="filteredComments">
            <el-table-column prop="doctor_name" label="被评价医生">
                <template #default="scope">
                    {{ scope.row.doctor_name }}
                    <el-button type="primary" size="small" @click="showDoctorInfo(scope.row)">介绍</el-button>
                </template>
            </el-table-column>
            <el-table-column prop="doctor_id" label="医生工号"></el-table-column>
            <el-table-column prop="patient_id" label="评价者用户名"></el-table-column>
            <el-table-column prop="publish_time" label="评价发布时间"></el-table-column>
            <el-table-column prop="comment_text" label="评价内容" width="250">
                <template #default="scope">
                    <span style="margin-right: 5px;">{{ scope.row.comment_text.slice(0, 10) }}<el-text
                            v-if="scope.row.comment_text.length > 10">...</el-text></span>
                    <el-button type="primary" v-if="scope.row.comment_text.length > 10"
                        @click="showIntroDetails(scope.row.comment_text)" size="small">详情</el-button>
                </template>
            </el-table-column>
            <el-table-column prop="audit" label="审核状态" width="100"></el-table-column>
            <el-table-column label="审核" width="200">
                <template #default="scope">
                    <el-button type="success" :disabled="scope.row.audit!='未审核'" @click="dialog_goto_pass(scope.row)">通过</el-button>
                    <el-button type="warning" :disabled="scope.row.audit!='未审核'" @click="dialog_goto_unpass(scope.row)">不通过</el-button>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
                <template #default="scope">
                    <el-button type="primary" @click="showComment(scope.row)">查看</el-button>
                    <el-button type="danger" @click="dialog_goto_del(scope.row.comment_id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="total"
            style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>

        <!-- 医生信息对话框 -->
        <el-dialog v-model="doctorInfoDialogVisible" title="医生信息" width="600" align-center>
            <div v-if="selectedDoctor">
                <p class="dialog_item_content"><strong class="dialog_item_title">姓名:</strong> {{ selectedDoctor.doctor_name }}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">工号:</strong> {{ selectedDoctor.doctor_id }}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">科室:</strong> {{ selectedDoctor.office
                    }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">性别:</strong> {{ selectedDoctor.gender
                    }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">工龄:</strong> {{
                    selectedDoctor.seniority }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">介绍:</strong> {{ selectedDoctor.doctor_intro }}
                </p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeDoctorInfoDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 评论内容详情对话框 -->
        <el-dialog v-model="introDialogVisible" title="评论信息详情" width="50%" align-center>
            <div v-if="selectedIntro">
                <p class="dialog_item_content"><strong class="dialog_item_title">评论内容:</strong> {{ selectedIntro
                    }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeIntroDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

         <!-- 评价信息查看对话框 -->
         <el-dialog v-model="commentDialogVisible" title="评价详细信息" width="50%" align-center>
            <div v-if="selectedComment">
                <p class="dialog_item_content"><strong class="dialog_item_title">被评价医生:</strong> {{ selectedComment.doctor_name }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">医生所属科室:</strong> {{ selectedComment.office }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">评价发布时间:</strong> {{ selectedComment.publish_time }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">评价内容:</strong> {{ selectedComment.comment_text }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">审核状态:</strong> {{ selectedComment.audit }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeCommentDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>



<style scoped>
.dialog_item_title {
    font-family: "微软雅黑";
    font-size: large;
    font-weight: 600;
}

.dialog_item_content {
    font-family: "微软雅黑";
    font-size: large;
    font-weight: 200;
}
</style>
