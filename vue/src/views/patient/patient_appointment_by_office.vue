<script setup>
import { ref, computed, onMounted } from 'vue';
import router from '@/router';
import { officeAllService } from '@/api/office.js';
import { doctorAllInfoService } from '@/api/doctorInfo';
import { scheduleAllService } from '@/api/schedule';
import { appointmentAllService, appointmentAddService } from '@/api/appointment';
import { patientInfoService } from '@/api/patient';
import { ElMessage, ElMessageBox } from 'element-plus';

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
const getRemainingSlotsByScheduleId = (scheduleId, scheduleNum) => {
    let haveUsedSlots = (AppointmentInfos.filter(appointment => {
        return (appointment.schedule.schedule_id === scheduleId) && (appointment.order_state === 1 || appointment.order_state === 2)
    })).length;
    return scheduleNum - haveUsedSlots;
}
const getFrontPeriodBySchedulePeriod = (schedulePeriod) => {
    if (schedulePeriod == 1) return '上午';
    return '下午';
}
const constructDoctors = () => {
    doctors.value = ScheduleInfos.map(item => ({
        name: getDoctorNameById(item.doctor.user_id),
        id: item.doctor.user_id,
        office: item.office.name,
        remainingSlots: getRemainingSlotsByScheduleId(item.schedule_id, item.appointment_num),
        date: item.schedule_date,
        period: getFrontPeriodBySchedulePeriod(item.schedule_period),
        schedule_id: item.schedule_id
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
const constructOffices = () => {
    offices.value = OfficeInfos.map(item => ({
        office_id: item.office_id,
        name: item.name,
        intro: item.intro
    }))
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
    constructDoctors();
    constructDoctorInfos();
    constructOffices();
}

const searchForm = ref({
    name: '',
    id: '',
    office: '',
    date: '',
    period: ''
});
const searchFormOffice = ref({
    office: ''
})
const doctors = ref([
    { name: '张医生', id: '00000000001', office: '内科', remainingSlots: 10, date: '2024-11-15', period: '上午', schedule_id: '' },
    { name: '李医生', id: '00000000002', office: '外科', remainingSlots: 5, date: '2024-11-16', period: '下午', schedule_id: '' },
    { name: '王医生', id: '00000000003', office: '儿科', remainingSlots: 5, date: '2024-11-16', period: '下午', schedule_id: '' },
    { name: '刘医生', id: '00000000004', office: '耳鼻喉科', remainingSlots: 5, date: '2024-11-16', period: '下午', schedule_id: '' },
]);
const doctorInfo = ref({
    name: '',
    id: '',
    office: '',
    gender: '',
    seniority: '',
    intro: ''
})
const doctorInfos = ref([
    { name: '张医生', id: '00000000001', office: '内科', gender: '男', seniority: '3', intro: '一位内科医生，专注于高血压和高脂血症的管理。他通过生活方式干预和药物治疗帮助患者控制病情。' },
    { name: '李医生', id: '00000000002', office: '外科', gender: '女', seniority: '4', intro: '一名心胸外科医生，专注于心脏和肺部手术。他的高超技术和细致的术后护理赢得了患者的信任。' },
    { name: '王医生', id: '00000000003', office: '儿科', gender: '男', seniority: '5', intro: '一位经验丰富的心脏病专家，拥有超过15年的临床经验。他专注于心血管疾病的诊断和治疗，致力于为患者提供最优质的医疗服务。' },
    { name: '刘医生', id: '00000000004', office: '耳鼻喉科', gender: '女', seniority: '6', intro: '一位知名的骨科医生，专注于关节置换和运动损伤的治疗。他的手术技术精湛，深受患者信赖。' },
])

const offices = ref([
    { office_id: '', name: '', intro: '' },
])

// 当前页码
const currentPage = ref(1);
const currentPageOffice = ref(1);

// 每页显示的数量
const pageSize = ref(12);
const pageSizeOffice = ref(12);

// 总条数
const total = ref(doctors.value.length);
const totalOffice = ref(offices.value.length);

const filteredDoctors = computed(() => {
    let result = doctors.value.filter(doctor => {
        return (doctor.name.includes(searchForm.value.name) &&
            doctor.id.includes(searchForm.value.id) &&
            (searchForm.value.office === '' || doctor.office === searchForm.value.office) &&
            (searchForm.value.date === '' || doctor.date === searchForm.value.date) &&
            (searchForm.value.period === '' || doctor.period === searchForm.value.period));
    })
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});
const filteredOffices = computed(() => {
    let result = offices.value.filter(office => {
        return searchFormOffice.value.office === '' || office.name.includes(searchFormOffice.value.office);
    })
    totalOffice.value = result.length;
    return result.slice((currentPageOffice.value - 1) * pageSizeOffice.value, currentPageOffice.value * pageSizeOffice.value);
});

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
const handleAppointment = async (scheduleInfoRow) => {
    let result;
    result = await patientInfoService();
    let username = result.data.user_id;
    if (scheduleInfoRow.remainingSlots <= 0) {
        ElMessage({
            message: '预约失败: 当前医生的余号数量不足以再进行预约',
            type: 'error',
            plain: true,
        })
        return
    }
    let scheduleDate = new Date(scheduleInfoRow.date);
    let appointDate = new Date();
    if (appointDate >= scheduleDate) {
        ElMessage({
            message: '预约失败: 请选择合适的日期进行预约',
            type: 'error',
            plain: true,
        })
        return
    }
    result = await appointmentAllService();
    let tmpAppointmentInfos = result.data;
    let isNeedUpdate = false;
    let paramType = {
        'appointment_id': '',
        'patient_id': '',
        'doctor_id': '',
        'schedule_id': '',
        'visit_date': '',
        'visit_period': '',
        'serial_num': '',
        'order_state': '',
        'order_time': '',
        'mode': ''
    }
    for (let i = 0; i < tmpAppointmentInfos.length; i++) {
        let temp = tmpAppointmentInfos[i];
        if (temp.patient.user_id == username && temp.schedule.schedule_id == scheduleInfoRow.schedule_id) {
            if (temp.order_state == 1 || temp.order_state == 2) {
                ElMessage({
                    message: '预约失败: 你已经预约过当前医生的排班信息，不可重复预约',
                    type: 'error',
                    plain: true,
                })
                return
            }
            else {
                isNeedUpdate = true;
                paramType.appointment_id = temp.appointment_id;
                paramType.patient_id = temp.patient.user_id;
                paramType.doctor_id = temp.doctor.user_id;
                paramType.schedule_id = temp.schedule.schedule_id;
                paramType.visit_date = temp.visit_date;
                paramType.visit_period = temp.visit_period;
                paramType.serial_num = temp.serial_num;
                paramType.order_state = 1;
                paramType.order_time = formatTimestamp(temp.order_time);
                paramType.mode = 'update';
                break;
            }
        }
    }
    let haveGotAppointmentNum = (tmpAppointmentInfos.filter(appointment => {
        return (appointment.schedule.schedule_id === scheduleInfoRow.schedule_id) && (appointment.order_state === 1)
    })).length;
    let serialNum = haveGotAppointmentNum + 1;
    if (isNeedUpdate) {
        paramType.serial_num = serialNum
    }
    else {
        paramType.appointment_id = 0;
        paramType.patient_id = username;
        paramType.doctor_id = scheduleInfoRow.id;
        paramType.schedule_id = scheduleInfoRow.schedule_id
        paramType.visit_date = scheduleInfoRow.date;
        paramType.visit_period = scheduleInfoRow.period == '上午' ? 1 : 2;
        paramType.serial_num = serialNum;
        paramType.order_state = 1;
        paramType.order_time = '';
        paramType.mode = 'add';
    }
    let params = new URLSearchParams();
    for (let key in paramType) {
        params.append(key, paramType[key]);
    }
    ElMessageBox.confirm(
        '当前医生的排班信息可以预约，你确认要预约吗？',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            let apiResult = await appointmentAddService(params);
            if (apiResult.code == 0) {
                ElMessage({
                    type: 'success',
                    message: '预约成功',
                })
                getAllBackInfos();
            }
            else {
                ElMessage({
                    type: 'error',
                    message: '预约失败: ' + apiResult.message,
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

// 分页变化方法
const handlePageChange = (page) => {
    currentPage.value = page;
};

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

const dialogVisibleOffice = ref(false);
const selectedOffice = ref(null);

const showOfficeInfo = (office) => {
    selectedOffice.value = office;
    dialogVisibleOffice.value = true;
}

// 关闭医生信息对话框
const closeDialog = () => {
    dialogVisible.value = false;
    selectedDoctor.value = null;
};

const closeDialogOffice = () => {
    dialogVisibleOffice.value = false;
    selectedOffice.value = null;
};

const outerVisible = ref(false);
const innerVisible = ref(false);

const goToAppointment = (officeName) => {
    let message = '你确定要选择科室 ' + officeName + ' 进行预约吗？';
    ElMessageBox.confirm(
        message,
        '确认预约',
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
            searchForm.value.office = officeName;
            outerVisible.value = false;
            innerVisible.value = true;
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            });
            searchForm.value.office = '';
            outerVisible.value = true;
            innerVisible.value = false;
        })
};

const goToDoctor = () => {
    router.push('/patient/appointment/by_doctor')
};

const goToOffice = () => {
    router.push('/patient/appointment/by_office')
    outerVisible.value = true;
    innerVisible.value = false;
}

const goToMyAppointment = () => {
    router.push('/patient/appointment/my')
}

onMounted(() => {
    getAllBackInfos();
    outerVisible.value = true;
    innerVisible.value = false;
});
</script>

<template>
    <div v-show="outerVisible">
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchFormOffice" class="demo-form-inline">
            <el-form-item label="科室名称">
                <el-select v-model="searchFormOffice.office" filterable placeholder="选择或输入你要预约的科室名称"
                    style="width: 260px;" clearable @clear="searchFormOffice.office = ''">
                    <el-option v-for="office in offices" :key="office.name" :label="office.name"
                        :value="office.name"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary">查询</el-button>
                <el-button type="success" @click="goToDoctor">去找医生</el-button>
                <el-button color="#626aef" :dark="isDark" @click="goToMyAppointment">我的预约</el-button>
            </el-form-item>
        </el-form>

        <!-- 科室列表 -->
        <el-table :data="filteredOffices" style="width: 100%">
            <el-table-column prop="name" label="科室名称" width="400"></el-table-column>
            <el-table-column prop="intro" label="科室简介" width="700">
                <template #default="scope">
                    {{ scope.row.intro.slice(0, 20) }}<el-text v-if="scope.row.intro.length > 15">...</el-text>
                    <el-button type="primary" size="small" @click="showOfficeInfo(scope.row)" v-if="scope.row.intro.length > 15">详情</el-button>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="success" @click="goToAppointment(scope.row.name)">选择</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination v-model:current-page="currentPageOffice" v-model:page-size="pageSizeOffice"
            layout="jumper, total, prev, pager, next" background :total="totalOffice"
            style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>

        <!-- 科室详细信息对话框 -->
        <el-dialog v-model="dialogVisibleOffice" title="科室详细信息" width="50%" align-center>
            <div v-if="selectedOffice">
                <p><strong>科室名称:</strong> {{ selectedOffice.name }}</p>
                <p><strong>科室简介:</strong> {{ selectedOffice.intro }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeDialogOffice" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>

    <div v-show="innerVisible">
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="姓名">
                <el-input v-model="searchForm.name" placeholder="输入医生姓名" style="width: 120px;"></el-input>
            </el-form-item>
            <el-form-item label="工号">
                <el-input v-model="searchForm.id" placeholder="输入医生工号" style="width: 120px;"></el-input>
            </el-form-item>
            <el-form-item label="科室">
                <el-select v-model="searchForm.office" placeholder="选择科室" disabled clearable
                    @clear="searchForm.office = ''" style="width: 150px;">
                </el-select>
            </el-form-item>
            <el-form-item label="就诊日期">
                <el-date-picker v-model="searchForm.date" type="date" value-format="YYYY-MM-DD" placeholder="选择日期"
                    clearable @clear="searchForm.date = ''" style="width: 120px;"></el-date-picker>
            </el-form-item>
            <el-form-item label="就诊时段">
                <el-select v-model="searchForm.period" placeholder="选择时段" clearable @clear="searchForm.period = ''"
                    style="width: 160px;">
                    <el-option label="上午(8:30-12:00)" value="上午"></el-option>
                    <el-option label="下午(13:30~17:00)" value="下午"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary">查询</el-button>
                <el-button type="success" @click="goToOffice">返回科室</el-button>
                <el-button color="#626aef" :dark="isDark" @click="goToMyAppointment">我的预约</el-button>
            </el-form-item>
        </el-form>

        <!-- 医生列表 -->
        <el-table :data="filteredDoctors" :key="filteredDoctors">
            <el-table-column prop="name" label="姓名" width="180">
                <template #default="scope">
                    {{ scope.row.name }}
                    <el-button type="primary" size="small" @click="showDoctorInfo(scope.row.id)">介绍</el-button>
                </template>
            </el-table-column>
            <el-table-column prop="id" label="工号" width="180"></el-table-column>
            <el-table-column prop="office" label="科室"></el-table-column>
            <el-table-column prop="date" label="就诊日期"></el-table-column>
            <el-table-column prop="period" label="就诊时段"></el-table-column>
            <el-table-column prop="remainingSlots" label="号量余额"></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="success" @click="handleAppointment(scope.row)">预约</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="handlePageChange" style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>

        <!-- 医生信息对话框 -->
        <el-dialog v-model="dialogVisible" title="医生信息" width="600" align-center>
            <div v-if="selectedDoctor">
                <p><strong>姓名:</strong> {{ selectedDoctor.name }}</p>
                <p><strong>工号:</strong> {{ selectedDoctor.id }}</p>
                <p><strong>科室:</strong> {{ selectedDoctor.office }}</p>
                <p><strong>性别:</strong> {{ selectedDoctor.gender }}</p>
                <p><strong>工龄:</strong> {{ selectedDoctor.seniority }}</p>
                <p><strong>介绍:</strong> {{ selectedDoctor.intro }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>



<style scoped></style>