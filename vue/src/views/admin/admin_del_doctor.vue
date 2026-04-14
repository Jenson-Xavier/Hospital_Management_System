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

const getDoctorAccount = async () => {
    let result = await getAllDoctorAccountService();
    if (result.code == 0) {
        ElMessage.success("成功查询全部医生账户信息");
        doctorAccountList.value = result.data;
    } else {
        ElMessage.error(result.message ? result.message : "医生账户信息获取失败");
    }
}
// getDoctorAccount();

const getDoctorInfo = async () => {
    let result = await allDoctorInfoService();
    if (result.code == 0) {
        ElMessage.success("成功查询全部医生信息");
        doctorInfoList.value = result.data;
    } else {
        ElMessage.error(result.message ? result.message : "医生信息获取失败");
    }
}
// getDoctorInfo();

// 获取科室数据
const officeInfoList = ref([]);
const getOfficeInfo = async () => {
    let result = await officeService();
    if (result.code == 0) {
        // ElMessage.success("成功查询所有科室信息");
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
    // 手动遍历doctorAccountInfo  
    for (let i = 0; i < doctorAccountList.value.length; i++) {
        const account = doctorAccountList.value[i];
        let doctorInfoData = {
            doctor_id: account.user_id,
            doctor_name: '',
            password: account.password,
            office: '',
            gender: ''
        };
        // 手动遍历doctorInfo  
        for (let j = 0; j < doctorInfoList.value.length; j++) {
            const info = doctorInfoList.value[j];

            // 如果user_id匹配，则合并数据  
            if (account.user_id === info.doctor.user_id) {
                doctorInfoData.doctor_name = info.name;
                doctorInfoData.gender = genderMapping[info.gender];
                doctorInfoData.office = info.office.name;
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

// 过滤后的评价列表
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

// 注销账户
import { delDoctorAccountService } from '@/api/doctor';
import router from '@/router';
const del_account = async (doctor_id) => {
    let result;
    result = await delDoctorAccountService({
        id: doctor_id
    });
    if (result.code == 0) {
        ElMessage.success("成功注销医生账户");
        combinedDoctorData();
    } else {
        ElMessage.error(result.message ? result.message : "注销失败");
    }
}

// 添加医生账户
const add_account = ()=>{
    router.push("/admin/add_doctor");
}

// 注销对话框
const dialog_goto_del_account = (doctor_id) => {
    let message = '你确定要删除医生账户 ' + doctor_id + ' 吗？';
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
            del_account(doctor_id);
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
            <el-form-item label="医生工号">
                <el-input style="width: 160px; margin-right: 0px;" v-model="searchForm.doctor_id" placeholder="输入医生工号"
                    clearable @clear="ElementChange" class="input" />
            </el-form-item>

            <el-form-item label="医生姓名">
                <el-input style="width: 160px; margin-right: 0px;" v-model="searchForm.doctor_name" placeholder="输入医生姓名"
                    clearable @clear="ElementChange" class="input" />
            </el-form-item>

            <el-form-item label="科室">
                <el-select v-model="searchForm.office" placeholder="选择科室" clearable @clear="searchForm.office = ''"
                    style="width: 150px;">
                    <el-option v-for="item in officeInfoList" :key="item.name" :label="item.name"
                        :value="item.name"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="性别">
                <el-select v-model="searchForm.gender" placeholder="选择性别" clearable @clear="searchForm.gender = ''"
                    style="width: 150px;">
                    <el-option v-for="(item, index) in genderMapping" :key="index" :label="item"
                        :value="item"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="onSearch">查询</el-button>
            </el-form-item>

            <el-form-item>
                <el-button type="success" @click="add_account">添加医生账户</el-button>
            </el-form-item>
        </el-form>

        <!-- 医生账户信息列表 -->
        <el-table :data="filteredDoctorData" style="width: 100%" :key="filteredDoctorData">
            <el-table-column prop="doctor_id" label="医生工号"></el-table-column>
            <el-table-column prop="doctor_name" label="医生姓名"></el-table-column>
            <el-table-column prop="password" label="密码"></el-table-column>
            <el-table-column prop="office" label="科室名称"></el-table-column>
            <el-table-column prop="gender" label="性别"></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="danger" @click="dialog_goto_del_account(scope.row.doctor_id)">注销账户</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
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