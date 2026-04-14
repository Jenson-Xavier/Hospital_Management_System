<script setup>
import router from '@/router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, computed, onMounted } from 'vue';
import { officeAllService } from '@/api/office.js';
import { doctorAllInfoService } from '@/api/doctorInfo';
import { patientInfoService } from '@/api/patient';
import { commentAllService, commentAddService } from '@/api/comment';

let OfficeInfos = [
    { office_id: '', name: '', intro: '' },
]
let DoctorInfos = [
    { doctor_info_id: '', doctor: '', office: '', name: '', gender: '', seniority: '', intro: '' },
]
let CommentInfos = [
    { comment_id: '', publisher: '', commented: '', publish_time: '', comment_text: '', audit: '' },
]
const reviews = ref([
    { doctorName: '', doctorOffice: '', reviewerName: '', reviewTime: '', reviewContent: '', doctorId: '', commentId: '', audit: '' },
]);
const doctorInfos = ref([
    { name: '', id: '', office: '', gender: '', seniority: '', intro: '' },
])
const offices = ref([
    { office_id: '', name: '', intro: '' },
])
const getDoctorGenderByGenderId = (gender) => {
    if (gender == 1) return '男'
    return '女'
}
const getDoctorNameById = (doctorId) => {
    for (let i = 0; i < DoctorInfos.length; i++) {
        let temp = DoctorInfos[i];
        if (temp.doctor.user_id == doctorId) {
            return temp.name;
        }
    }
    return '未定义'
}
const getDoctorOfOfficeByDoctorId = (doctorId) => {
    for (let i = 0; i < DoctorInfos.length; i++) {
        let temp = DoctorInfos[i];
        if (temp.doctor.user_id == doctorId) {
            return temp.office.name;
        }
    }
    return '未定义'
}
const getReviewAuditByAuditId = (audit) => {
    if (audit == 1) return '未审核'
    else if (audit == 2) return '审核通过'
    return '审核未通过'
}
const getFrontDateByBackDate = (date) => {
    let tmpDate = new Date(date);
    let tmpStr = tmpDate.toISOString();
    return tmpStr.slice(0, 10);
}
const constructOffices = () => {
    offices.value = OfficeInfos.map(item => ({
        office_id: item.office_id,
        name: item.name,
        intro: item.intro
    }))
}
const constructDoctorInfos = () => {
    doctorInfos.value = DoctorInfos.map(item => ({
        name: item.name,
        id: item.doctor.user_id,
        office: item.office.name,
        gender: getDoctorGenderByGenderId(item.gender),
        seniority: item.seniority,
        intro: item.intro
    }))
}
const getAllBackInfos = async () => {
    let result;
    result = await officeAllService();
    OfficeInfos = result.data;
    result = await doctorAllInfoService();
    DoctorInfos = result.data;
    result = await commentAllService();
    CommentInfos = result.data;
    constructOffices();
    constructDoctorInfos();
}

const searchForm = ref({
    name: '',
    id: '',
    office: ''
});

// 当前页码
const currentPage = ref(1);
const pageSize = ref(12);
const totalDoctorInfos = ref(doctorInfos.value.length);

const filteredDoctorInfos = computed(() => {
    let result = doctorInfos.value.filter(doctorInfo => {
        return (
            (searchForm.value.name === '' || doctorInfo.name.includes(searchForm.value.name)) &&
            (searchForm.value.id === '' || doctorInfo.id.includes(searchForm.value.id)) &&
            (searchForm.value.office === '' || doctorInfo.office === searchForm.value.office)
        )
    })
    totalDoctorInfos.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

// 查看医生详细信息
const doctorDialogVisible = ref(false);
const selectedDoctor = ref(null);

const showDoctorDetails = (doctorId) => {
    const doctor = doctorInfos.value.find(d => d.id === doctorId);
    if (doctor) {
        selectedDoctor.value = doctor;
        doctorDialogVisible.value = true;
    }
};

const closeDoctorDialog = () => {
    doctorDialogVisible.value = false;
    selectedDoctor.value = null;
};

const addCommentForm = ref({
    doctorName: '',
    doctorId: '',
    doctorOffice: '',
    commentText:''
})
const addCommentDialogVisible = ref(false);
const addNewContent = (scopeRow) => {
    addCommentForm.value.doctorName = scopeRow.name;
    addCommentForm.value.doctorId = scopeRow.id;
    addCommentForm.value.doctorOffice = scopeRow.office;
    addCommentForm.value.commentText = '';
    addCommentDialogVisible.value = true;
}
const submitAddComment = async (submitData) => {
    let commentText = addCommentForm.value.commentText;
    if (commentText.length <= 0) {
        ElMessage({
            type: 'error',
            message: '请勿提交空的评论',
        })
        return
    }
    let tmpUsername = await patientInfoService();
    if (tmpUsername.code != 0) {
        ElMessage({
            type: 'error',
            message: '服务异常' + tmpUsername.message,
        })
        return
    }
    let username = tmpUsername.data.user_id;
    let paramType = {
        'patient_id': username,
        'doctor_id': addCommentForm.value.doctorId,
        'comment_text': commentText
    }
    let params = new URLSearchParams();
    for (let key in paramType) {
        params.append(key, paramType[key]);
    }
    ElMessageBox.confirm(
        '你确认要提交评论吗',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            let result = await commentAddService(params);
            if (result.code != 0) {
                ElMessage({
                    type: 'error',
                    message: '服务异常' + result.message,
                })
                addCommentDialogVisible.value = false;
                return
            }
            ElMessage({
                type: 'success',
                message: '成功提交',
            })
            addCommentDialogVisible.value = false;
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            })
        })
}

const goToAllComment = () => {
    router.push('/patient/comment/all');
};

const goToMyComment = () => {
    router.push('/patient/comment/my');
};

onMounted(async () => {
    getAllBackInfos();
});
</script>

<template>
    <div>
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="医生姓名">
                <el-input v-model="searchForm.name" placeholder="输入要评价医生的姓名"></el-input>
            </el-form-item>
            <el-form-item label="医生工号">
                <el-input v-model="searchForm.id" placeholder="输入要评价医生的工号"></el-input>
            </el-form-item>
            <el-form-item label="科室">
                <el-select v-model="searchForm.office" filterable placeholder="选择或输入要评价医生所属科室的名称" style="width: 300px;"
                    clearable @clear="searchForm.office = ''">
                    <el-option v-for="office in offices" :key="office.name" :label="office.name"
                        :value="office.name"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSearch">查询</el-button>
                <el-button type="success" @click="goToAllComment">所有评价</el-button>
                <el-button color="#626aef" :dark="isDark" @click="goToMyComment">我的评价</el-button>
            </el-form-item>
        </el-form>

        <!-- 待评价列表 -->
        <el-table :data="filteredDoctorInfos" style="width: 100%">
            <el-table-column prop="name" label="医生姓名" width="220">
                <template #default="scope">
                    <span style="margin-right: 5px;">{{ scope.row.name }}</span>
                    <el-button type="primary" @click="showDoctorDetails(scope.row.id)" size="small">查看</el-button>
                </template>
            </el-table-column>
            <el-table-column prop="gender" label="医生性别" width="220"></el-table-column>
            <el-table-column prop="id" label="医生工号" width="220"></el-table-column>
            <el-table-column prop="office" label="医生所属科室" width="220"></el-table-column>
            <el-table-column prop="seniority" label="医生工龄" width="220"></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="success" @click="addNewContent(scope.row)">新增评价</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="totalDoctorInfos" @size-change="onSizeChange"
            @current-change="handlePageChange" style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>

        <!-- 评价内容详细信息对话框 -->
        <el-dialog v-model="reviewDialogVisible" title="评价内容详情" width="50%" align-center>
            <div v-if="selectedReview">
                <p>{{ selectedReview }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeReviewDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 医生详细信息对话框 -->
        <el-dialog v-model="doctorDialogVisible" title="医生详细信息" width="50%" align-center>
            <div v-if="selectedDoctor">
                <p><strong>医生姓名:</strong> {{ selectedDoctor.name }}</p>
                <p><strong>医生工号:</strong> {{ selectedDoctor.id }}</p>
                <p><strong>所属科室:</strong> {{ selectedDoctor.office }}</p>
                <p><strong>医生性别:</strong> {{ selectedDoctor.gender }}</p>
                <p><strong>医生工龄:</strong> {{ selectedDoctor.seniority }}</p>
                <p><strong>医生简介:</strong> {{ selectedDoctor.intro }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeDoctorDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 添加评论对话框 -->
        <el-dialog v-model="addCommentDialogVisible" title="添加评论" width="50%" align-center>
            <el-form label-width="auto" style="max-width: 600px">
                <el-form-item label="评价医生姓名">
                    <el-input v-model="addCommentForm.doctorName" disabled/>
                </el-form-item>
                <el-form-item label="评价医生工号">
                    <el-input v-model="addCommentForm.doctorId" disabled/>
                </el-form-item>
                <el-form-item label="评价医生所属科室">
                    <el-input v-model="addCommentForm.doctorOffice" disabled/>
                </el-form-item>
            </el-form>
            <el-input type="textarea" :rows="8" placeholder="请输入评论内容，请留下友善的评论哦！" v-model="addCommentForm.commentText"></el-input>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="danger" @click="addCommentDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitAddComment">提交</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>



<style scoped></style>