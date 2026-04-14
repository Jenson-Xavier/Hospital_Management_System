<script setup>
import { computed, ref } from 'vue';

// 初始数据
const doctorAccountList = ref([]);
const doctorInfoList = ref([]);

const searchForm = ref({
    doctor_id: '',
    doctor_name: '',
    office: '',
    gender: ''
});

// 性别映射
const genderMapping = {
    1: "男",
    2: "女"
}

// 获取全部医生账户/信息
import { getAllDoctorAccountService } from '@/api/doctor';
import { allDoctorInfoService } from '@/api/doctorInfo';
import { officeService } from '@/api/office';
import { ElMessage, ElMessageBox } from 'element-plus';

// 获取科室数据
const officeInfoList = ref([]);
const getOfficeInfo = async () => {
    let result = await officeService();
    if (result.code == 0) {
        officeInfoList.value = result.data;
    } else {
        ElMessage.error(result.message ? result.message : "科室信息获取失败");
    }
}
getOfficeInfo();

// 整合数据  
const combinedData = ref([]);
const combinedDoctorData = async () => {
    let result = await allDoctorInfoService();
    doctorInfoList.value = result.data;
    result = await getAllDoctorAccountService();
    doctorAccountList.value = result.data;
    let data = [];
    console.log(doctorAccountList.value.length);
    // 手动遍历doctorInfo  
    for (let i = 0; i < doctorInfoList.value.length; i++) {
        const info = doctorInfoList.value[i];
        let doctorInfoData = {
            doctorInfo_id: info.doctor_info_id,
            doctor_id: '',
            doctor_name: info.name,
            office: info.office.name,
            gender: genderMapping[info.gender],
            intro: info.intro,
            seniority: info.seniority
        };
        // 手动遍历doctorAccountInfo  
        for (let j = 0; j < doctorAccountList.value.length; j++) {
            const account = doctorAccountList.value[j];

            // 如果user_id匹配，则合并数据  
            if (account.user_id === info.doctor.user_id) {
                doctorInfoData.doctor_id = account.user_id;
                break; // 找到匹配项后可以跳出内层循环  
            }
        }
        data.push(doctorInfoData);
    }
    combinedData.value = data;
    console.log(data);
};
combinedDoctorData();


// 当前页码
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);

// 过滤后的医生信息表
const filteredDoctorData = computed(() => {
    let result = combinedData.value.filter(data => {
        return (
            (data.doctor_id.includes(searchForm.value.doctor_id)) &&
            (data.doctor_name.includes(searchForm.value.doctor_name)) &&
            (searchForm.value.office === '' || data.office === searchForm.value.office) &&
            (searchForm.value.gender === '' || data.gender === searchForm.value.gender)
        );
    });
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

// 日期选择框更改
const ElementChange = () => {
    currentPage.value = 1;
}

// 删除医生信息
import { deleteDoctorInfoService } from '@/api/doctorInfo';
import router from '@/router';
const del_doctorInfo = async (doctorInfoId) => {
    let result;
    result = await deleteDoctorInfoService({
        id: doctorInfoId
    });
    if (result.code == 0) {
        ElMessage.success("成功删除该医生信息");
        combinedDoctorData();
    } else {
        ElMessage.error(result.message ? result.message : "注销失败");
    }
}

// 添加医生账户
const add_doctorInfo = () => {
    router.push("/admin/add_doctorInfo");
}

// 修改医生账户
const update_doctorInfo = (info) => {
    // router.push("/admin/update_doctorInfo");
}

// 删除医生信息对话框
const dialog_goto_del_doctorInfo = (doctorInfo_id) => {
    let message = '你确定要删除该医生信息吗？';
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
            del_doctorInfo(doctorInfo_id);
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            });
        })
}

// 显示医生简介详情
const selectedIntro = ref(null);
const introDialogVisible = ref(false);
const showIntroDetails = (intro) => {
    selectedIntro.value = intro;
    introDialogVisible.value = true;
};

// 关闭详情对话框
const closeIntroDialog = () => {
    introDialogVisible.value = false;
    selectedIntro.value = null;
};

// 显示选中医生信息
const selectedDoctorInfo = ref(null);
const doctorInfoDialogVisible = ref(false);
const showSelectedDoctorInfo = (info) => {
    selectedDoctorInfo.value = info;
    doctorInfoDialogVisible.value = true;
}

// 关闭医生信息对话框
const closeDoctorInfoDialog = () => {
    doctorInfoDialogVisible.value = false;
    selectedDoctorInfo.value = null;
}


</script>

<template>
    <div>
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="医生工号">
                <el-input style="width: 160px; margin-right: 0px;" v-model="searchForm.doctor_id" placeholder="输入医生工号"
                    clearable @clear="ElementChange" class="input" />
            </el-form-item>

            <el-form-item label="医生姓名">
                <el-input style="width: 160px; margin-right: 0px;" v-model="searchForm.doctor_name" placeholder="输入医生姓名"
                    clearable @clear="ElementChange" class="input" />
            </el-form-item>

            <el-form-item label="科室">
                <el-select v-model="searchForm.office" placeholder="选择科室" clearable @clear="searchForm.office = '', ElementChange"
                    style="width: 150px;">
                    <el-option v-for="item in officeInfoList" :key="item.name" :label="item.name"
                        :value="item.name"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="性别">
                <el-select v-model="searchForm.gender" placeholder="选择性别" clearable @clear="searchForm.gender = '', ElementChange"
                    style="width: 150px;">
                    <el-option v-for="(item, index) in genderMapping" :key="index" :label="item"
                        :value="item"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="onSearch">查询</el-button>
            </el-form-item>

            <el-form-item>
                <el-button type="success" @click="add_doctorInfo">添加医生账户</el-button>
            </el-form-item>
        </el-form>

        <!-- 医生信息列表 -->
        <el-table :data="filteredDoctorData" style="width: 100%" :key="filteredDoctorData">
            <el-table-column prop="doctor_id" label="医生工号"></el-table-column>
            <el-table-column prop="doctor_name" label="医生姓名"></el-table-column>
            <el-table-column prop="office" label="科室名称"></el-table-column>
            <el-table-column prop="gender" label="性别"></el-table-column>
            <el-table-column prop="seniority" label="工龄"></el-table-column>
            <el-table-column prop="intro" label="简介信息" width="300px">
                <template #default="scope">
                    <span style="margin-right: 5px;">{{ scope.row.intro.slice(0, 10) }}<el-text v-if="scope.row.intro.length>10">...</el-text></span>
                    <el-button type="primary"  v-if="scope.row.intro.length>10" @click="showIntroDetails(scope.row.intro)" size="small">详情</el-button>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="300px">
                <template #default="scope">
                    <el-button type="danger" size="medium"
                        @click="dialog_goto_del_doctorInfo(scope.row.doctorInfo_id)">删除</el-button>
                    <!-- <el-button type="primary" size="medium" @click="update_doctorInfo(scope.row)">修改</el-button> -->
                    <el-button type="success" size="medium" @click="showSelectedDoctorInfo(scope.row)">查看</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 选中医生信息展示 -->
        <el-dialog v-model="doctorInfoDialogVisible" title="医生信息" width="50%" align-center>
            <div v-if="selectedDoctorInfo">
                <p class="dialog_item_content"><strong class="dialog_item_title">工号:</strong> {{ selectedDoctorInfo.doctor_id }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">姓名:</strong> {{ selectedDoctorInfo.doctor_name }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">所属科室:</strong> {{ selectedDoctorInfo.office }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">性别:</strong> {{ selectedDoctorInfo.gender }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">工龄:</strong> {{ selectedDoctorInfo.seniority }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">简介信息:</strong> {{ selectedDoctorInfo.intro }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeDoctorInfoDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 医生简介详情对话框 -->
        <el-dialog v-model="introDialogVisible" title="医生简介详情" width="50%" align-center>
            <div v-if="selectedIntro">
                <p class="dialog_item_content"><strong class="dialog_item_title">简介内容:</strong> {{ selectedIntro
                    }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeIntroDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="total"
            style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>
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