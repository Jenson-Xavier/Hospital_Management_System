<script setup>
import router from '@/router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, computed, onMounted } from 'vue';
import { officeAllService } from '@/api/office.js';
import { doctorAllInfoService } from '@/api/doctorInfo';
import { patientInfoService } from '@/api/patient';
import { commentAllService } from '@/api/comment';

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
    if (audit == 1) return '待审核'
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
const constructReviews = () => {
    reviews.value = CommentInfos.map(item => ({
        doctorName: getDoctorNameById(item.commented.user_id),
        doctorOffice: getDoctorOfOfficeByDoctorId(item.commented.user_id),
        reviewerName: item.publisher.user_id,
        reviewTime: getFrontDateByBackDate(item.publish_time),
        reviewContent: item.comment_text,
        doctorId: item.commented.user_id,
        commentId: item.comment_id,
        audit: getReviewAuditByAuditId(item.audit)
    })).filter(item => {
        return item.audit == '审核通过'
    })
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
    constructReviews();
}

// 搜索表单
const searchForm = ref({
    name: '',
    id: '',
    office: '',
    publishTime: '',
    auditState: ''
});

// 当前页码
const currentPage = ref(1);
const pageSize = ref(11);
const totalReviews = ref(reviews.value.length);

// 过滤后的评价列表
const filteredReviews = computed(() => {
    let result = reviews.value.filter(review => {
        return (
            (searchForm.value.name === '' || review.doctorName.includes(searchForm.value.name)) &&
            (searchForm.value.id === '' || review.doctorId.includes(searchForm.value.id)) &&
            (searchForm.value.office === '' || review.doctorOffice === searchForm.value.office) &&
            (searchForm.value.publishTime === '' || review.reviewTime === searchForm.value.publishTime) &&
            (searchForm.value.auditState === '' || review.audit === searchForm.value.auditState)
        )
    })
    totalReviews.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

// 查看评价详细信息
const reviewDialogVisible = ref(false);
const selectedReview = ref(null);
const showReviewDetails = (review) => {
    selectedReview.value = review;
    reviewDialogVisible.value = true;
};
const closeReviewDialog = () => {
    reviewDialogVisible.value = false;
    selectedReview.value = null;
};

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

// 查看完整评价
const contentDialogVisible = ref(false);
const selectedContent = ref(null);
const showFullContent = (content) => {
    contentDialogVisible.value = true;
    selectedContent.value = content;
};
const closeContentDialog = () => {
    contentDialogVisible.value = false;
    selectedContent.value = null;
};

const goToMyComment = () => {
    router.push('/patient/comment/my');
};

const goToAddComment = () => {
    router.push('/patient/comment/add')
}

onMounted(async () => {
    getAllBackInfos();
});
</script>

<template>
    <div>
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="医生姓名">
                <el-input v-model="searchForm.name" placeholder="输入医生姓名"></el-input>
            </el-form-item>
            <el-form-item label="医生工号">
                <el-input v-model="searchForm.id" placeholder="输入医生工号"></el-input>
            </el-form-item>
            <el-form-item label="科室">
                <el-select v-model="searchForm.office" filterable placeholder="选择或输入科室名称" style="width: 200px;"
                    clearable @clear="searchForm.office = ''">
                    <el-option v-for="office in offices" :key="office.name" :label="office.name"
                        :value="office.name"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="评价发布日期" style="width: 300px;">
                <el-date-picker v-model="searchForm.publishTime" type="date" value-format="YYYY-MM-DD"
                    placeholder="选择评价发布日期" clearable @clear="searchForm.publishTime = ''"></el-date-picker>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSearch">查询</el-button>
                <el-button type="success" @click="goToMyComment">我的评价</el-button>
                <el-button color="#626aef" :dark="isDark" @click="goToAddComment">新增评价</el-button>
            </el-form-item>
        </el-form>

        <!-- 评价列表 -->
        <el-table :data="filteredReviews" style="width: 100%">
            <el-table-column prop="doctorName" label="被评价医生">
                <template #default="scope">
                    <span style="margin-right: 5px;">{{ scope.row.doctorName }}</span>
                    <el-button type="primary" @click="showDoctorDetails(scope.row.doctorId)" size="small">查看</el-button>
                </template>
            </el-table-column>
            <el-table-column prop="doctorOffice" label="医生所属科室" width="200"></el-table-column>
            <el-table-column prop="reviewerName" label="评价者用户名"></el-table-column>
            <el-table-column prop="reviewTime" label="评价发布时间"></el-table-column>
            <el-table-column label="评价内容" width="300">
                <template #default="scope">
                    <span style="margin-right: 5px;">{{ scope.row.reviewContent.slice(0, 10) }}<el-text v-if="scope.row.reviewContent.length > 10">...</el-text></span>
                    <el-button type="primary" @click="showReviewDetails(scope.row.reviewContent)"
                        size="small" v-if="scope.row.reviewContent.length > 10">详情</el-button>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="success" @click="showFullContent(scope.row)">查看评价</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="totalReviews"
            style="margin-top: 20px; justify-content: flex-end">
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

        <!-- 评价详细信息对话框 -->
        <el-dialog v-model="contentDialogVisible" title="评价详细信息" width="50%" align-center>
            <div v-if="selectedContent">
                <p><strong>被评价医生姓名:</strong> {{ selectedContent.doctorName }}</p>
                <p><strong>被评价医生工号:</strong> {{ selectedContent.doctorId }}</p>
                <p><strong>医生所属科室:</strong> {{ selectedContent.doctorOffice }}</p>
                <p><strong>评价者用户名:</strong> {{ selectedContent.reviewerName }}</p>
                <p><strong>评价发布时间:</strong> {{ selectedContent.reviewTime }}</p>
                <p><strong>评价内容:</strong> {{ selectedContent.reviewContent }}</p>
                <p><strong>审核状态:</strong> {{ selectedContent.audit }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeContentDialog" class="close-button">关 闭</el-button>
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
    </div>
</template>



<style scoped></style>