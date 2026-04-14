<script lang="ts" setup>
import { computed, ref } from 'vue';

// 初始数据
const appointmentInfos = ref([]);

const searchForm = ref({
    patient_id: '',
    start_date: '',
    end_date: '',
    period: '',
    serial: '',
    state: ''
});

// 获取全部预约记录信息
import { doctorIdService } from '@/api/doctor';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getAppointmentInfoService } from '@/api/appointment';

const getAppointment = async () => {
    let result;
    result = await getAppointmentInfoService();
    if (result.code == 0) {
        // ElMessage.success("成功查询全部预约记录");
        appointmentInfos.value = result.data;
    } else {
        ElMessage.error(result.message ? result.message : "查询失败");
    }
}
getAppointment();

// 获取医生用户工号
const username = ref('12312312312')
const getDoctorId = async () => {
    let result = await doctorIdService();
    username.value = result.data.user_id;
}
getDoctorId();

// 当前页码
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(appointmentInfos.value.length);

// 排班时段格式转换
const periodMapping = {
    1: '8:30-12:00',
    2: '13:30~17:00',
};

// 处理状态格式转换
const stateMapping = {
    1: '待处理',
    2: '已处理',
};

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

// 过滤后的评价列表
const filteredAppointmentInfos = computed(() => {
    let result = appointmentInfos.value.map(item => ({
        ...item,
        visit_period: periodMapping[item.visit_period] || 'Unknown',
        order_state: stateMapping[item.order_state] || 'Unknown',
    })).filter(appointment => {
        const appointmentDate = new Date(appointment.visit_date);
        const start_date = searchForm.value.start_date ? new Date(searchForm.value.start_date) : null;
        const end_date = searchForm.value.end_date ? new Date(searchForm.value.end_date) : null;

        if (end_date) {  
            end_date.setDate(end_date.getDate() + 1);  
        }  

        // 检查评论日期是否在开始日期和结束日期之间  
        const isWithinDateRange = (
            (!start_date || appointmentDate >= start_date) &&
            (!end_date || appointmentDate <= end_date)
        );

        return (
            // true
            isWithinDateRange &&
            (appointment.doctor.user_id === username.value) &&
            (searchForm.value.period === '' || appointment.visit_period === searchForm.value.period) &&
            (searchForm.value.state === '' || appointment.order_state === searchForm.value.state) &&
            (appointment.order_state != "Unknown") &&
            (appointment.patient.user_id.includes(searchForm.value.patient_id))
    )});
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});


// 日期选择框更改
const ElementChange = () => {
    currentPage.value = 1;
}

// 处理预约请求
import { solveAppointmentService } from '@/api/appointment';
const solveAppointment = async (appointment_id)=>{
    let result;
    result = await solveAppointmentService({
        id: appointment_id
    });
    if (result.code == 0) {
        ElMessage.success("成功处理请求");
        getAppointment();
    } else {
        ElMessage.error(result.message ? result.message : "处理失败");
    }
}

const dialog_goto_solve = (appointment_id)=>{
    let message = '您确定要处理该请求吗？';
    ElMessageBox.confirm(
        message,
        '确认处理',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            ElMessage({
                type: 'success',
                message: '成功处理',
            });
            solveAppointment(appointment_id);
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
            <el-form-item label="开始日期">
                <el-date-picker v-model="searchForm.start_date" value-format="YYYY-MM-DD" placeholder="选择日期" clearable
                    @clear="searchForm.start_date = ''" style="width: 150px;" @change="ElementChange"></el-date-picker>
            </el-form-item>

            <el-form-item label="结束日期">
                <el-date-picker v-model="searchForm.end_date" value-format="YYYY-MM-DD" placeholder="选择日期" clearable
                    @clear="searchForm.end_date = ''" style="width: 150px;" @change="ElementChange"></el-date-picker>
            </el-form-item>

            <el-form-item label="就诊时段">
                <el-select v-model="searchForm.period" placeholder="选择时段" clearable @clear="searchForm.period = ''"
                    style="width: 150px;" @change="ElementChange">
                    <el-option v-for="(label, index) in periodMapping" :key="index" :label="label" :value="label">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="处理状态">
                <el-select v-model="searchForm.state" placeholder="选择处理状态" clearable @clear="searchForm.state = ''"
                    style="width: 150px;" @change="ElementChange">
                    <el-option v-for="(label, index) in stateMapping" :key="index" :label="label" :value="label">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="患者用户名" prop="name" class="form_item">
                <el-input style="width: 160px; margin-right: 0px;" v-model="searchForm.patient_id" clearable class="input" 
                placeholder="请输入患者用户名"/>
            </el-form-item>

            <el-form-item>
                <el-button type="primary">查询</el-button>
            </el-form-item>
        </el-form>

        <!-- 预约记录列表 -->
        <el-table :data="filteredAppointmentInfos" style="width: 100%" :key="filteredAppointmentInfos">
            <el-table-column prop="patient.user_id" label="患者用户名"></el-table-column>
            <el-table-column prop="visit_date" label="就诊日期"></el-table-column>
            <el-table-column prop="visit_period" label="就诊时段"></el-table-column>
            <el-table-column prop="serial_num" label="就诊序号"></el-table-column>
            <el-table-column prop="order_state" label="处理状态"></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button :disabled="scope.row.order_state=='已处理'" type="primary" @click="dialog_goto_solve(scope.row.appointment_id)">处理请求</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="total" style="margin-top: 20px; justify-content: flex-end">
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