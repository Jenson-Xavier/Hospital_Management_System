<script setup>
import router from '@/router';
import { ref, computed, watch, onMounted } from 'vue';
import { ElMessage, ElMessageBox, valueEquals } from 'element-plus';
import { patientInfoService } from '@/api/patient';
import { officeAllService } from '@/api/office.js';
import { doctorAllInfoService } from '@/api/doctorInfo';
import { scheduleAllService } from '@/api/schedule';
import { appointmentAllService, appointmentAddService } from '@/api/appointment';

// 初始数据
const appointments = ref([
    { doctorName: '', doctorId: '', office: '', visitDate: '', visitTime: '', appointmentDate: '', orderTime: '', orderState: '', serialNum: '', patientId: '', scheduleId: '', appointmentId: '' },
]);
const offices = ref([
    { office_id: '', name: '内科', intro: '内科' },
    { office_id: '', name: '外科', intro: '外科' },
    { office_id: '', name: '儿科', intro: '儿科' },
]);
const searchForm = ref({
    visitDate: '',
    appointmentDate: '',
    office: '',
    doctorInfo: '',
    visitTime: '',
    orderState: ''
});

let OfficeInfos = [
    { office_id: '', name: '', intro: '' },
]
let DoctorInfos = [
    { doctor_info_id: '', doctor: '', office: '', name: '', gender: '', seniority: '', intro: '' },
]
let ScheduleInfos = [
    { schedule_id: '', office: '', doctor: '', schedule_date: '', schedule_period: '', appointment_num: '' },
]
let AppointmentInfos = [
    { appointment_id: '', patient: '', doctor: '', schedule: '', visit_date: '', visit_period: '', serial_num: '', order_state: '', order_time: '' },
]
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
const getFrontPeriodBySchedulePeriod = (schedulePeriod) => {
    if (schedulePeriod == 1) return '上午';
    return '下午';
}
const getFrontOrderStateByAppointmentOrderState = (orderState) => {
    if (orderState == 1) return '待看诊';
    else if (orderState == 2) return '已看诊';
    return '已取消';
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
const constructAppointments = async () => {
    let temp = await patientInfoService();
    let tempUserName = '';
    if (temp.code == 0) {
        tempUserName = temp.data.user_id;
    }
    else {
        ElMessage({
            message: '服务异常',
            type: 'error',
            plain: true,
        })
        return;
    }
    appointments.value = AppointmentInfos.map(item => ({
        doctorName: getDoctorNameById(item.doctor.user_id),
        doctorId: item.doctor.user_id,
        office: getDoctorOfOfficeByDoctorId(item.doctor.user_id),
        visitDate: item.visit_date,
        visitTime: getFrontPeriodBySchedulePeriod(item.visit_period),
        appointmentDate: getFrontDateByBackDate(item.order_time),
        orderTime: item.order_time,
        orderState: getFrontOrderStateByAppointmentOrderState(item.order_state),
        serialNum: item.serial_num,
        patientId: item.patient.user_id,
        scheduleId: item.schedule.schedule_id,
        appointmentId: item.appointment_id
    })).filter(appointment => {
        return appointment.patientId == tempUserName;
    })
}
const getAllBackInfos = async () => {
    let result;
    result = await officeAllService();
    OfficeInfos = result.data;
    result = await doctorAllInfoService();
    DoctorInfos = result.data;
    result = await scheduleAllService();
    ScheduleInfos = result.data;
    result = await appointmentAllService();
    AppointmentInfos = result.data;
    constructOffices();
    constructAppointments();
}

const currentPage = ref(1);
const pageSize = ref(10);
const totalAppointments = ref(20);

const filteredAppointments = computed(() => {
    let result = appointments.value.filter(appointment => {
        return (
            (!searchForm.value.visitDate || appointment.visitDate === searchForm.value.visitDate) &&
            (!searchForm.value.appointmentDate || appointment.appointmentDate === searchForm.value.appointmentDate) &&
            (!searchForm.value.office || appointment.office === searchForm.value.office) &&
            (!searchForm.value.doctorInfo || appointment.doctorName.includes(searchForm.value.doctorInfo) || appointment.doctorId.includes(searchForm.value.doctorInfo)) &&
            (!searchForm.value.visitTime || appointment.visitTime === searchForm.value.visitTime) &&
            (!searchForm.value.orderState || appointment.orderState === searchForm.value.orderState)
        );
    });
    totalAppointments.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

// 查看预约记录详细信息
const appointmentDialogVisible = ref(false);
const selectedAppointment = ref(null);
const showAppointmentDetails = async (appointment) => {
    selectedAppointment.value = appointment;
    appointmentDialogVisible.value = true;
};
const closeAppointmentDialog = () => {
    appointmentDialogVisible.value = false;
    selectedAppointment.value = null;
};

// 解析日期
const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

// 取消预约
const cancelAppointment = (appointmentRow) => {
    let scheduleDate = new Date(appointmentRow.visitDate);
    let cancelAppointDate = new Date();
    if (cancelAppointDate >= scheduleDate) {
        ElMessage({
            message: '取消预约失败: 请在就诊日期之前提前取消预约',
            type: 'error',
            plain: true,
        })
        return
    }
    if (appointmentRow.orderState == '已取消') {
        ElMessage({
            message: '取消预约失败: 当前预约记录已经取消',
            type: 'error',
            plain: true,
        })
        return
    }
    if (appointmentRow.orderState == '已看诊') {
        ElMessage({
            message: '取消预约失败 当前预约记录已经看诊',
            type: 'error',
            plain: true,
        })
        return
    }
    let paramType = {
        'appointment_id': appointmentRow.appointmentId,
        'patient_id': appointmentRow.patientId,
        'doctor_id': appointmentRow.doctorId,
        'schedule_id': appointmentRow.scheduleId,
        'visit_date': appointmentRow.visitDate,
        'visit_period': appointmentRow.visitTime == '上午' ? 1 : 2,
        'serial_num': appointmentRow.serialNum,
        'order_state': 3,
        'order_time': formatTimestamp(appointmentRow.orderTime),
        'mode': 'update'
    }
    let params = new URLSearchParams();
    for (let key in paramType) {
        params.append(key, paramType[key]);
    }
    ElMessageBox.confirm(
        '你可以取消当前选中的预约记录，你确认要取消预约吗？',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            console.log('ss',paramType)
            let apiResult = await appointmentAddService(params);
            if (apiResult.code == 0) {
                ElMessage({
                    type: 'success',
                    message: '取消预约成功',
                })
                getAllBackInfos();
            }
            else {
                ElMessage({
                    type: 'error',
                    message: '取消预约失败: ' + apiResult.message,
                })
            }
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            })
        })
};

const goToDoctor = () => {
    router.push('/patient/appointment/by_doctor')
}

const goToOffice = () => {
    router.push('/patient/appointment/by_office')
}

onMounted(() => {
    getAllBackInfos();
});
</script>

<template>
    <div>
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="医生姓名或工号" style="width: 300px;">
                <el-input v-model="searchForm.doctorInfo" placeholder="输入医生姓名或工号" clearable
                    @clear="searchForm.doctorInfo = ''"></el-input>
            </el-form-item>
            <el-form-item label="就诊科室">
                <el-select v-model="searchForm.office" filterable placeholder="选择或输入科室名称" clearable
                    @clear="searchForm.office = ''" style="width: 240px;">
                    <el-option v-for="office in offices" :key="office.name" :label="office.name"
                        :value="office.name"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="就诊日期" style="width: 240px;">
                <el-date-picker v-model="searchForm.visitDate" type="date" value-format="YYYY-MM-DD"
                    placeholder="选择就诊日期" clearable @clear="searchForm.visitDate = ''"></el-date-picker>
            </el-form-item>
            <el-form-item label="预约日期" style="width: 240px;">
                <el-date-picker v-model="searchForm.appointmentDate" type="date" value-format="YYYY-MM-DD"
                    placeholder="选择预约日期" clearable @clear="searchForm.appointmentDate = ''"></el-date-picker>
            </el-form-item>
            <el-form-item label="就诊时段" style="width: 240px;">
                <el-select v-model="searchForm.visitTime" placeholder="选择时段" clearable
                    @clear="searchForm.visitTime = ''">
                    <el-option label="上午(8:30-12:00)" value="上午"></el-option>
                    <el-option label="下午(13:30~17:00)" value="下午"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="预约状态" style="width: 240px;">
                <el-select v-model="searchForm.orderState" placeholder="选择预约状态" clearable
                    @clear="searchForm.orderState = ''">
                    <el-option label="待看诊" value="待看诊"></el-option>
                    <el-option label="已看诊" value="已看诊"></el-option>
                    <el-option label="已取消" value="已取消"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSearch">查询</el-button>
                <el-button type="success" @click="goToDoctor">去找医生预约</el-button>
                <el-button color="#626aef" :dark="isDark" @click="goToOffice">去找科室预约</el-button>
            </el-form-item>
        </el-form>

        <!-- 预约记录列表 -->
        <el-table :data="filteredAppointments" :key="filteredAppointments" style="width: 100%">
            <el-table-column prop="doctorName" label="医生姓名"></el-table-column>
            <el-table-column prop="doctorId" label="医生工号"></el-table-column>
            <el-table-column prop="office" label="就诊科室"></el-table-column>
            <el-table-column prop="visitDate" label="就诊日期"></el-table-column>
            <el-table-column prop="visitTime" label="就诊时段" width="120"></el-table-column>
            <el-table-column prop="appointmentDate" label="预约日期"></el-table-column>
            <el-table-column prop="orderState" label="预约状态" width="120"></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="primary" size="small"
                        @click.stop="showAppointmentDetails(scope.row)">查看</el-button>
                    <el-button type="danger" size="small" @click.stop="cancelAppointment(scope.row)">取消预约</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="totalAppointments"
            style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>

        <!-- 预约记录详细信息对话框 -->
        <el-dialog v-model="appointmentDialogVisible" title="预约记录详细信息" width="50%" align-center>
            <div v-if="selectedAppointment">
                <p><strong>预约患者用户名:</strong> {{ selectedAppointment.patientId }}</p>
                <p><strong>预约医生姓名:</strong> {{ selectedAppointment.doctorName }}</p>
                <p><strong>预约医生工号:</strong> {{ selectedAppointment.doctorId }}</p>
                <p><strong>就诊科室:</strong> {{ selectedAppointment.office }}</p>
                <p><strong>就诊日期:</strong> {{ selectedAppointment.visitDate }}</p>
                <p><strong>就诊时段:</strong> {{ selectedAppointment.visitTime }}</p>
                <p><strong>预约日期:</strong> {{ selectedAppointment.appointmentDate }}</p>
                <p><strong>预约序号:</strong> {{ selectedAppointment.serialNum }}</p>
                <p><strong>预约状态:</strong> {{ selectedAppointment.orderState }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeAppointmentDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped></style>