<script setup>
import router from '@/router';
import { ElMessage } from 'element-plus';
import { ref, computed, onMounted } from 'vue';
import { officeAllService } from '@/api/office.js';
import { doctorAllInfoService, updateDoctorInfoService } from '@/api/doctorInfo';
import { scheduleAllService, addNewScheduleService, deleteScheduleInfoService } from '@/api/schedule';
import { appointmentAllService, appointmentAddService } from '@/api/appointment';
import { ElMessageBox } from 'element-plus';

// 从后端获取信息
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
let ScheduleInfos = [
    {
        schedule_id: '',
        office: '',
        doctor: '',
        schedule_date: '',
        schedule_period: '',
        appointment_num: ''
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
// 获取所有信息的钩子
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
const doctors = ref([
    {
        name: '',
        id: '',
        office: '',
        allSlots:'',
        remainingSlots: '',
        date: '',
        period: '',
        schedule_id: ''
    },
]);
const doctorInfos = ref([
    {
        name: '',
        id: '',
        office: '',
        gender: '',
        seniority: '',
        intro: '',
        officeId:''
    },
])
const offices = ref([
    {
        office_id: '',
        name: '',
        intro: ''
    },
])
const periods = ref([
    { value: 1, name: '上午(8:30-12:00)' },
    { value: 2, name: '下午(13:30~17:00)' }
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
const getRemainingSlotsByScheduleId = (scheduleId, scheduleNum) => {
    let haveUsedSlots = (AppointmentInfos.filter(appointment => {
        return (appointment.schedule.schedule_id == scheduleId) && (appointment.order_state == 1 || appointment.order_state == 2)
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
        allSlots: item.appointment_num,
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
        intro: item.intro,
        officeId:item.office.office_id
    }))
}
const constructOffices = () => {
    offices.value = OfficeInfos.map(item => ({
        office_id: item.office_id,
        name: item.name,
        intro:item.intro
    }))
}
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(5);

// 过滤后的医生列表
const filteredDoctors = computed(() => {
    let result = doctors.value.filter(doctor => {
        return (doctor.name.includes(searchForm.value.name) &&
            doctor.id.includes(searchForm.value.id) &&
            (searchForm.value.office === '' || doctor.office === searchForm.value.office) &&
            (searchForm.value.date === '' || doctor.date === searchForm.value.date) &&
            (searchForm.value.period === '' || doctor.period === searchForm.value.period));
    });
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

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

const scheduleDialogVisible = ref(false);
const selectedSchedule = ref(null);
const showScheduleInfo = (scheduleRow) => {
    scheduleDialogVisible.value = true;
    selectedSchedule.value = scheduleRow;
}
const closeScheduleDialog = () => {
    scheduleDialogVisible.value = false;
    selectedSchedule.value = false;
}

const addScheduleForm = ref({
    officeId: '',
    doctorId: '',
    scheduleDate: '',
    schedulePeriod: '',
    appointmentNum: '',
})
const addScheduleDialogVisible = ref(false);
const submitAddNewSchedule = () => {
    let tmpForm = addScheduleForm.value;
    if (tmpForm.officeId == '' || tmpForm.doctorId == '' || tmpForm.scheduleDate == '' || tmpForm.schedulePeriod == '' ||
        tmpForm.appointmentNum == '') {
        ElMessage.error('请确保所有选项都有输入')
        return;
    }
    let check = false;
    for (let i = 0; i < doctorInfos.value.length; i++){
        let doctorInfo = doctorInfos.value[i];
        if (doctorInfo.id == tmpForm.doctorId && doctorInfo.officeId == tmpForm.officeId) {
            check = true;
            break;
        }
    }
    if (!check) {
        ElMessage.error('当前选择医生和其所属的科室信息不匹配，请重新输入')
        return;
    }
    ElMessageBox.confirm(
        '你确认要增加该排班信息吗？',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            let params = new URLSearchParams();
            params.append('office_id', tmpForm.officeId);
            params.append('doctor_id', tmpForm.doctorId);
            params.append('schedule_period', tmpForm.schedulePeriod);
            params.append('appointment_num', tmpForm.appointmentNum);
            params.append('schedule_date', tmpForm.scheduleDate);
            params.append('mode', 'add');
            params.append('schedule_id', 0);
            let result = await addNewScheduleService(params);
            if (result.code == 0) {
                ElMessage({
                    type: 'success',
                    message: '成功提交',
                })
                getAllBackInfos();
                addScheduleDialogVisible.value = false;
            }
            else {
                ElMessage({
                    type: 'error',
                    message: '服务异常' + result.message,
                })
                addScheduleDialogVisible.value = false;
            }

        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            })
        })
}

const updateScheduleDialogVisible = ref(false);
const updatedScheduleInfo = ref({
    officeId: '',
    doctorId: '',
    scheduleDate: '',
    schedulePeriod: '',
    appointmentNum: '',
    scheduleId:''
});
const updateScheduleInfo = (scheduleRow) => {
    let tmp = scheduleRow;
    let officeId;
    for (let i = 0;i<offices.value.length;i++){
        let tmpOffice = offices.value[i];
        if (tmpOffice.name == tmp.office) {
            officeId = tmpOffice.office_id;
            break;
        }
    }
    updatedScheduleInfo.value.officeId = officeId;
    updatedScheduleInfo.value.doctorId = tmp.id;
    updatedScheduleInfo.value.scheduleDate = tmp.date;
    updatedScheduleInfo.value.schedulePeriod = tmp.period == '上午' ? 1 : 2;
    updatedScheduleInfo.value.appointmentNum = tmp.allSlots;
    updatedScheduleInfo.value.scheduleId = tmp.schedule_id;
    updateScheduleDialogVisible.value = true;
}
const closeUpdateScheduleInfoDialog = () => {
    updateScheduleDialogVisible.value = false;
    updatedScheduleInfo.value.officeId = '';
    updatedScheduleInfo.value.doctorId = '';
    updatedScheduleInfo.value.scheduleDate = '';
    updatedScheduleInfo.value.schedulePeriod = '';
    updatedScheduleInfo.value.appointmentNum = '';
    updatedScheduleInfo.value.scheduleId = '';
}
const submitUpdateSchedule = () => {
    let tmpForm = updatedScheduleInfo.value;
    if (tmpForm.officeId == '' || tmpForm.doctorId == '' || tmpForm.scheduleDate == '' || tmpForm.schedulePeriod == '' ||
        tmpForm.appointmentNum == '' || tmpForm.scheduleId == '') {
        ElMessage.error('请确保所有选项都有输入')
        return;
    }
    console.log('test',tmpForm)
    let check = false;
    for (let i = 0; i < doctorInfos.value.length; i++){
        let doctorInfo = doctorInfos.value[i];
        if (doctorInfo.id == tmpForm.doctorId && doctorInfo.officeId == tmpForm.officeId) {
            check = true;
            break;
        }
    }
    if (!check) {
        ElMessage.error('当前选择医生和其所属的科室信息不匹配，请重新输入')
        return;
    }
    ElMessageBox.confirm(
        '你确认要修改该排班信息吗？',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            let params = new URLSearchParams();
            params.append('office_id', tmpForm.officeId);
            params.append('doctor_id', tmpForm.doctorId);
            params.append('schedule_period', tmpForm.schedulePeriod);
            params.append('appointment_num', tmpForm.appointmentNum);
            params.append('schedule_date', tmpForm.scheduleDate);
            params.append('mode', 'update');
            params.append('schedule_id', tmpForm.scheduleId);
            let result = await addNewScheduleService(params);
            if (result.code == 0) {
                ElMessage({
                    type: 'success',
                    message: '成功提交',
                })
                getAllBackInfos();
                updateScheduleDialogVisible.value = false;
            }
            else {
                ElMessage({
                    type: 'error',
                    message: '服务异常' + result.message,
                })
                updateScheduleDialogVisible.value = false;
            }

        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '操作取消',
            })
        })
}

const deleteScheduleInfo = (scheduleRow) => {
    let deletedScheduleId = scheduleRow.schedule_id;
    ElMessageBox.confirm(
        '你确认要删除该排班信息吗？',
        '提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            let params = new URLSearchParams();
            params.append('id', deletedScheduleId);
            let result = await deleteScheduleInfoService(params);
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

onMounted(async () => {
    getAllBackInfos();
});
</script>

<template>
    <div>
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="姓名">
                <el-input v-model="searchForm.name" placeholder="输入医生姓名" style="width: 120px;" clearable></el-input>
            </el-form-item>
            <el-form-item label="工号">
                <el-input v-model="searchForm.id" placeholder="输入医生工号" style="width: 120px;" clearable></el-input>
            </el-form-item>
            <el-form-item label="科室">
                <el-select v-model="searchForm.office" placeholder="选择科室" clearable @clear="searchForm.office = ''"
                    style="width: 150px;">
                    <el-option v-for="office in offices" :key="office.name" :label="office.name"
                        :value="office.name"></el-option>
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
                <el-button type="success" @click="addScheduleDialogVisible = true">新增排班信息</el-button>
            </el-form-item>
        </el-form>

        <el-table :data="filteredDoctors" :key="filteredDoctors">
            <el-table-column prop="name" label="姓名" width="180">
                <template #default="scope">
                    {{ scope.row.name }}
                    <el-button type="primary" size="small" @click="showDoctorInfo(scope.row.id)">介绍</el-button>
                </template>
            </el-table-column>
            <el-table-column prop="id" label="工号" width="180"></el-table-column>
            <el-table-column prop="office" label="科室" width="160"></el-table-column>
            <el-table-column prop="date" label="就诊日期" width="160"></el-table-column>
            <el-table-column prop="period" label="就诊时段" width="160"></el-table-column>
            <el-table-column prop="allSlots" label="所有放号量" width="160"></el-table-column>
            <el-table-column prop="remainingSlots" label="号量余额" width="160"></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="primary" @click="showScheduleInfo(scope.row)">查看</el-button>
                    <el-button type="success" @click="updateScheduleInfo(scope.row)">更改</el-button>
                    <el-button type="danger" @click="deleteScheduleInfo(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
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
                <p class="dialog_item_content"><strong class="dialog_item_title">科室:</strong> {{ selectedDoctor.office}}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">性别:</strong> {{ selectedDoctor.gender}}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">工龄:</strong> {{selectedDoctor.seniority
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

        <el-dialog v-model="scheduleDialogVisible" title="医生信息" width="600" align-center>
            <div v-if="selectedSchedule">
                <p class="dialog_item_content"><strong class="dialog_item_title">排班信息ID:</strong> {{
                    selectedSchedule.schedule_id }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">医生姓名:</strong> {{ selectedSchedule.name
                    }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">医生工号:</strong> {{ selectedSchedule.id}}
                </p>
                <p class="dialog_item_content"><strong class="dialog_item_title">科室:</strong> {{
                    selectedSchedule.office}}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">所有放号量:</strong>
                    {{selectedSchedule.allSlots }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">号量余额:</strong> {{
                    selectedSchedule.remainingSlots }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">就诊日期:</strong> {{selectedSchedule.date
                    }}</p>
                <p class="dialog_item_content"><strong class="dialog_item_title">就诊时段:</strong> {{
                    selectedSchedule.period }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeScheduleDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 新增排班信息 -->
        <el-dialog v-model="addScheduleDialogVisible" title="修改排班信息" width="600" align-center>
            <el-form :model="addScheduleForm">
                <el-form-item label="选择医生">
                    <el-select v-model="addScheduleForm.doctorId" placeholder="输入或选择医生工号" clearable
                        @clear="addScheduleForm.doctorId = ''" style="width: 180px;">
                        <el-option v-for="doctorInfo in doctorInfos" :key="doctorInfo.id"
                            :label="`${doctorInfo.id}(${doctorInfo.name})`" :value="doctorInfo.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="选择科室">
                    <el-select v-model="addScheduleForm.officeId" placeholder="输入或选择科室" clearable
                        @clear="addScheduleForm.officeId = ''" style="width: 180px;">
                        <el-option v-for="office in offices" :key="office.office_id" :label="office.name"
                            :value="office.office_id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="选择就诊日期">
                    <el-date-picker v-model="addScheduleForm.scheduleDate" type="date" value-format="YYYY-MM-DD"
                        placeholder="选择日期" clearable @clear="addScheduleForm.scheduleDate = ''"
                        style="width: 160px;"></el-date-picker>
                </el-form-item>
                <el-form-item label="选择就诊时段">
                    <el-select v-model="addScheduleForm.schedulePeriod" placeholder="选择时段" clearable
                        @clear="addScheduleForm.schedulePeriod = ''" style="width: 160px;">
                        <el-option label="上午(8:30-12:00)" value=1></el-option>
                        <el-option label="下午(13:30~17:00)" value=2></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="总放号量">
                    <el-input-number v-model="addScheduleForm.appointmentNum" :min="1" :max="100" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="addScheduleDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitAddNewSchedule">提交</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 修改排班信息 -->
        <el-dialog v-model="updateScheduleDialogVisible" title="修改排班信息" width="600" align-center>
            <el-form :model="updatedScheduleInfo">
                <el-form-item label="选择医生">
                    <el-select v-model="updatedScheduleInfo.doctorId" clearable
                        @clear="updatedScheduleInfo.doctorId = ''" style="width: 180px;">
                        <el-option v-for="doctorInfo in doctorInfos" :key="doctorInfo.id"
                            :label="`${doctorInfo.id}(${doctorInfo.name})`" :value="doctorInfo.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="选择科室">
                    <el-select v-model="updatedScheduleInfo.officeId" clearable
                        @clear="updatedScheduleInfo.officeId = ''" style="width: 180px;">
                        <el-option v-for="office in offices" :key="office.name" :label="office.name"
                            :value="office.office_id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="选择就诊日期">
                    <el-date-picker v-model="updatedScheduleInfo.scheduleDate" type="date" value-format="YYYY-MM-DD"
                        clearable @clear="updatedScheduleInfo.scheduleDate = ''" style="width: 160px;"></el-date-picker>
                </el-form-item>
                <el-form-item label="选择就诊时段">
                    <el-select v-model="updatedScheduleInfo.schedulePeriod" clearable
                        @clear="updatedScheduleInfo.schedulePeriod = ''" style="width: 160px;">
                        <el-option v-for="period in periods" :key="period.value" :label="period.name"
                            :value="period.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="总放号量">
                    <el-input-number v-model="updatedScheduleInfo.appointmentNum" :min="1" :max="100" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="closeUpdateScheduleInfoDialog()">取消</el-button>
                    <el-button type="primary" @click="submitUpdateSchedule">提交</el-button>
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