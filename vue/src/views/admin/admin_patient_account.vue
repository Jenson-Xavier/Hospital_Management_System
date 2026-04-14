<script setup>
import { computed, ref } from 'vue';

// 初始数据
const patientInfos = ref([]);

const searchForm = ref({
    patient_id: '',
});

// 获取全部评论信息
import { ElMessage, ElMessageBox } from 'element-plus';
import { allPatientInfoService, delPatientAccountService } from '@/api/patient';

const getPatientInfo = async () => {
    let result = await allPatientInfoService();
    if (result.code == 0) {
        // ElMessage.success("成功查询全部患者信息");
        patientInfos.value = result.data;
    } else {
        ElMessage.error(result.message ? result.message : "查询失败");
    }
}
getPatientInfo();

// 当前页码
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(patientInfos.value.length);

// 过滤后的评价列表
const filteredPatient = computed(() => {
    let result = patientInfos.value.filter(patientInfo => {
        return patientInfo.user_id.includes(searchForm.value.patient_id);
    });
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

// 日期选择框更改
const ElementChange = () => {
    currentPage.value = 1;
}

// 注销账户
const del_account = async (patient_id) => {
    let result;
    result = await delPatientAccountService({
        id: patient_id
    });
    if (result.code == 0) {
        ElMessage.success("成功注销患者账户" + patient_id);
        getPatientInfo();
    } else {
        ElMessage.error(result.message ? result.message : "注销失败");
    }
}

// 弹出对话框
const dialog_goto_del_account = (patient_id) => {
    let message = '你确定要删除患者账户 ' + patient_id + ' 吗？';
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
            del_account(patient_id);
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
            <el-form-item label="患者用户名">
                <el-input style="width: 160px; margin-right: 0px;" v-model="searchForm.patient_id" clearable
                    placeholder="输入患者用户名" @clear="ElementChange" class="input" />
            </el-form-item>

            <el-form-item>
                <el-button type="primary">查询</el-button>
            </el-form-item>
        </el-form>

        <!-- 评价列表 -->
        <el-table :data="filteredPatient" style="width: 100%" :key="filteredPatient">
            <el-table-column prop="user_id" label="患者用户名"></el-table-column>
            <el-table-column prop="password" label="账户密码"></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="danger" @click="dialog_goto_del_account(scope.row.user_id)">注销账户</el-button>
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