<script setup>
import router from '@/router';
import { ElMessage } from 'element-plus';
import { ref, computed, onMounted } from 'vue';

// 搜索表单
const searchForm = ref({
    date: '',
    period: '',
    appointNum: ''
});

// 初始排班信息数据
let ScheduleInfos = ref([]);
import { getScheduleInfoService } from '@/api/schedule';
const getScheduleInfos = async () => {
    let result = await getScheduleInfoService();
    if (result.code == 0) {
        // ElMessage.success("成功查询全部排班信息");
        ScheduleInfos.value = result.data;
    } else {
        ElMessage.error(result.message ? result.message : "成功查询全部排班信息");
    }
}
getScheduleInfos();

// 获取医生用户工号
const username = ref('12312312312')
import { doctorIdService } from '@/api/doctor';
const getDoctorId = async () => {
    let result = await doctorIdService();
    username.value = result.data.user_id;
}
getDoctorId();

// 当前页码
const currentPage = ref(1);

// 每页显示的数量
const pageSize = ref(12);

// 总条数
const total = ref(5);

// 排班时段格式转换
const periodMapping = {
    1: '8:30-12:00',
    2: '13:30~17:00',
};

// 字段值映射+过滤
const formattedScheduleInfo = computed(() => {
    let result = ScheduleInfos.value.map(item => ({
        ...item,
        schedule_period: periodMapping[item.schedule_period] || 'Unknown',
    })).filter(schedule => {
        return (
            (schedule.doctor.user_id === username.value) &&
            (searchForm.value.date === '' || schedule.schedule_date === searchForm.value.date) &&
            (searchForm.value.period === '' || schedule.schedule_period === searchForm.value.period));
    });
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});


// 显示排班信息对话框
const dialogVisible = ref(false);
const selectedSchedule = ref(null);

const viewScheduleInfo = (scheduleId) => {
    for (let i = 0; i < ScheduleInfos.value.length; i++) {
        let scheduleInfo = ScheduleInfos.value[i];
        if (scheduleId == scheduleInfo.schedule_id) {
            selectedSchedule.value = scheduleInfo;
            break;
        }
    }
    dialogVisible.value = true;
};

// 关闭排班信息对话框
const closeDialog = () => {
    dialogVisible.value = false;
    selectedSchedule.value = null;
};

// 日期选择框/时段更改
const ElementChange = ()=>{
    currentPage.value = 1;
}

</script>

<template>
    <div>
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="就诊日期">
                <el-date-picker v-model="searchForm.date" value-format="YYYY-MM-DD" placeholder="选择日期" clearable
                    @clear="searchForm.date = ''" style="width: 120px;" @change="ElementChange"></el-date-picker>
            </el-form-item>
            <el-form-item label="就诊时段">
                <el-select v-model="searchForm.period" placeholder="选择时段" clearable @clear="searchForm.period = ''"
                    style="width: 160px;" @change="ElementChange">
                    <el-option v-for="(label, index) in periodMapping" :key="index" :label="label" :value="label">
                    </el-option>
                </el-select>
                <el-button type="primary" @click="onSearch" style="margin-left: 20px">查看</el-button>
            </el-form-item>
        </el-form>

        <!-- 排班信息列表 -->
        <el-table :data="formattedScheduleInfo">
            <el-table-column prop="schedule_date" label="排班日期"></el-table-column>
            <el-table-column prop="schedule_period" label="排班时段"></el-table-column>
            <el-table-column prop="appointment_num" label="放号总量"></el-table-column>
            <el-table-column label="查看">
                <template #default="scope">
                    <el-button type="success" @click="viewScheduleInfo(scope.row.schedule_id)">查看</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
            layout="jumper, total, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="handlePageChange" style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>

        <!-- 排班信息对话框 -->
        <el-dialog v-model="dialogVisible" title="排班信息" width="600" align-center>
            <div v-if="selectedSchedule">
                <p class="dialog_item_content"><strong class="dialog_item_title">排班日期:</strong> {{ selectedSchedule.schedule_date }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">排班时段:</strong> {{ selectedSchedule.schedule_period }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">放号总量:</strong> {{ selectedSchedule.appointment_num}}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeDialog" class="close-button">关 闭</el-button>
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