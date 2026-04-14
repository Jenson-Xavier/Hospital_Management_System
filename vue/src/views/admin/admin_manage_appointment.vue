<script setup>
import { ref, computed, onMounted } from 'vue';
import router from '@/router';
import { officeAllService } from '@/api/office.js';
import { doctorAllInfoService } from '@/api/doctorInfo';
import { appointmentAllService, appointmentDeleteService } from '@/api/appointment';
import { adminIdService } from '@/api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';

let OfficeInfos = [
    {
        office_id: '',
        name: '',
        intro: ''
    },
]
let DoctorInfos = [
    {
        doctor_info_id: '',
        doctor: '',
        office: '',
        name: '',
        gender: '',
        seniority: '',
        intro: ''
    },
]
let AppointmentInfos = [
    {
        appointment_id: '',
        patient: '',
        doctor: '',
        schedule: '',
        visit_date: '',
        visit_period: '',
        serial_num: '',
        order_state: '',
        order_time: ''
    },
]
const getDoctorGenderByGenderId = (gender) => {
    if (gender == 1) return '男'
    return '女'
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
const getDoctorNameById = (doctorId) => {
    for (let i = 0; i < DoctorInfos.length; i++) {
        let temp = DoctorInfos[i];
        if (temp.doctor.user_id == doctorId) {
            return temp.name;
        }
    }
    return '未定义'
}
const getFrontOrderStateByAppointmentOrderState = (orderState) => {
    if (orderState == 1) return '待看诊';
    else if (orderState == 2) return '已看诊';
    return '已取消';
}
const getFrontPeriodBySchedulePeriod = (schedulePeriod) => {
    if (schedulePeriod == 1) return '上午';
    return '下午';
}
const getFrontDateByBackDate = (date) => {
    let tmpDate = new Date(date);
    let tmpStr = tmpDate.toISOString();
    return tmpStr.slice(0, 10);
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
const constructOffices = () => {
    offices.value = OfficeInfos.map(item => ({
        office_id: item.office_id,
        name: item.name,
        intro: item.intro
    }))
}
const constructAppointments = async () => {
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
    }))
}
const getAllBackInfos = async () => {
    let result;
    result = await officeAllService();
    OfficeInfos = result.data;
    result = await doctorAllInfoService();
    DoctorInfos = result.data;
    result = await appointmentAllService();
    AppointmentInfos = result.data;
    constructDoctorInfos();
    constructOffices();
    constructAppointments();
}
const searchForm = ref({
    patient_id: '',
    doctor_id: '',
    doctor_name: '',
    office: '',
    date: '',
    period: '',
    order_state: ''
});
const appointments = ref([
    {
        doctorName: '',
        doctorId: '',
        office: '',
        visitDate: '',
        visitTime: '',
        appointmentDate: '',
        orderTime: '',
        orderState: '',
        serialNum: '',
        patientId: '',
        scheduleId: '',
        appointmentId: ''
    },
]);
const offices = ref([
    {
        office_id: '',
        name: '',
        intro: ''
    },
]);
const doctorInfos = ref([
    {
        name: '',
        id: '',
        office: '',
        gender: '',
        seniority: '',
        intro: ''
    },
])

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(appointments.value.length);
const filteredAppointments = computed(() => {
    let result = appointments.value.filter(appointment => {
        return (appointment.doctorName.includes(searchForm.value.doctor_name) &&
            appointment.patientId.includes(searchForm.value.patient_id) &&
            appointment.doctorId.includes(searchForm.value.doctor_id) &&
            (searchForm.value.office === '' || appointment.office === searchForm.value.office) &&
            (searchForm.value.date === '' || appointment.visitDate === searchForm.value.date) &&
            (searchForm.value.period === '' || appointment.visitTime === searchForm.value.period) &&
            (searchForm.value.order_state === '' || appointment.orderState === searchForm.value.order_state));
    })
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});
// 显示医生信息对话框
const dialogVisible = ref(false);
const selectedDoctor = ref(null);
const showDoctorInfo = (doctorId) => {
    for (let i = 0; i < doctorInfos.value.length; i++) {
        let doctorInfo = doctorInfos.value[i];
        if (doctorId == doctorInfo.id) {
            selectedDoctor.value = doctorInfo;
            break;
        }
    }
    dialogVisible.value = true;
};
const closeDialog = () => {
    dialogVisible.value = false;
    selectedDoctor.value = null;
};
// 显示预约信息对话框
const appointmentDialogVisible = ref(false);
const selectedAppointment = ref(null);
const showAppointment = (appointmentRow) => {
    appointmentDialogVisible.value = true;
    selectedAppointment.value = appointmentRow;
}
const closeAppointmentDialog = () => {
    appointmentDialogVisible.value = false;
    selectedAppointment.value = null;
};
const deleteAppointment = (appointmentRow) => {
    ElMessageBox.confirm(
        '你确认要删除这条预约记录吗？',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            let params = new URLSearchParams();
            params.append('id', appointmentRow.appointmentId);
            let result = await appointmentDeleteService(params);
            if (result.code == 0) {
                ElMessage({
                    type: 'success',
                    message: '成功删除',
                })
                getAllBackInfos();
            }
            else {
                ElMessage({
                    type: 'error',
                    message: '服务异常' + result.message,
                })
            }

        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            })
        })
}
onMounted(() => {
    getAllBackInfos();
});

</script>

<template>
    <div style="padding: 20px;">
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="患者ID">
                <el-input v-model="searchForm.patient_id" placeholder="输入患者ID" style="width: 140px;"
                    clearable></el-input>
            </el-form-item>
            <el-form-item label="医生姓名">
                <el-input v-model="searchForm.doctor_name" placeholder="输入医生姓名" style="width: 140px;"
                    clearable></el-input>
            </el-form-item>
            <el-form-item label="医生工号">
                <el-input v-model="searchForm.doctor_id" placeholder="输入医生工号" style="width: 140px;"
                    clearable></el-input>
            </el-form-item>
            <el-form-item label="科室">
                <el-select v-model="searchForm.office" placeholder="选择科室" clearable @clear="searchForm.office = ''"
                    style="width: 140px;">
                    <el-option v-for="office in offices" :key="office.name" :label="office.name"
                        :value="office.name"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="就诊日期">
                <el-date-picker v-model="searchForm.date" type="date" value-format="YYYY-MM-DD" placeholder="选择日期"
                    clearable @clear="searchForm.date = ''" style="width: 160px;"></el-date-picker>
            </el-form-item>
            <el-form-item label="就诊时段">
                <el-select v-model="searchForm.period" placeholder="选择时段" clearable @clear="searchForm.period = ''"
                    style="width: 160px;">
                    <el-option label="上午(8:30-12:00)" value="上午"></el-option>
                    <el-option label="下午(13:30~17:00)" value="下午"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="预约状态">
                <el-select v-model="searchForm.order_state" placeholder="选择预约状态" clearable
                    @clear="searchForm.order_state = ''" style="width: 160px;">
                    <el-option label="待看诊" value="待看诊"></el-option>
                    <el-option label="已看诊" value="已看诊"></el-option>
                    <el-option label="已取消" value="已取消"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSearch">查询</el-button>
            </el-form-item>
        </el-form>

        <el-table :data="filteredAppointments" :key="filteredAppointments">
            <el-table-column prop="patientId" label="患者ID" width="180"></el-table-column>
            <el-table-column prop="doctorName" label="医生姓名" width="180">
                <template #default="scope">
                    {{ scope.row.doctorName }}
                    <el-button type="primary" size="small" @click="showDoctorInfo(scope.row.doctorId)">介绍</el-button>
                </template>
            </el-table-column>
            <el-table-column prop="doctorId" label="医生工号" width="180"></el-table-column>
            <el-table-column prop="office" label="科室"></el-table-column>
            <el-table-column prop="visitDate" label="就诊日期"></el-table-column>
            <el-table-column prop="visitTime" label="就诊时段"></el-table-column>
            <el-table-column prop="orderState" label="预约状态"></el-table-column>
            <el-table-column label="操作" width="200">
                <template #default="scope">
                    <el-button type="primary" @click="showAppointment(scope.row)">查看</el-button>
                    <el-button type="danger" @click="deleteAppointment(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="total"
            style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>

        <!-- 医生信息对话框 -->
        <el-dialog v-model="dialogVisible" title="医生信息" width="600" align-center>
            <div v-if="selectedDoctor">
                <p class="dialog_item_content"><strong class="dialog_item_title">姓名:</strong> {{ selectedDoctor.name }}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">工号:</strong> {{ selectedDoctor.id }}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">科室:</strong> {{ selectedDoctor.office }}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">性别:</strong> {{ selectedDoctor.gender }}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">工龄:</strong> {{ selectedDoctor.seniority
                    }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">介绍:</strong> {{ selectedDoctor.intro }}
                </p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 预约详细信息对话框 -->
        <el-dialog v-model="appointmentDialogVisible" title="预约信息" width="600" align-center>
            <div v-if="selectedAppointment">
                <p class="dialog_item_content"><strong class="dialog_item_title">预约ID:</strong>
                    {{ selectedAppointment.appointmentId
                    }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">预约患者用户名:</strong> {{
                    selectedAppointment.patientId }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">预约医生姓名:</strong> {{
                    selectedAppointment.doctorName }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">预约医生工号:</strong> {{
                    selectedAppointment.doctorId }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">预约医生科室:</strong> {{
                    selectedAppointment.office}}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">就诊日期:</strong> {{
                    selectedAppointment.visitDate}}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">就诊时段:</strong>
                    {{ selectedAppointment.visitTime
                    }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">预约日期:</strong> {{
                    selectedAppointment.appointmentDate }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">预约序号:</strong> {{
                    selectedAppointment.serialNum}}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">预约状态:</strong>
                    {{ selectedAppointment.orderState
                    }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeAppointmentDialog" class="close-button">关 闭</el-button>
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