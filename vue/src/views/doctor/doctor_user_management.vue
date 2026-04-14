<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { doctorIdService } from '@/api/doctor';
import { ElMessage, FormInstance, FormItemProps, FormProps, FormRules } from 'element-plus';
import { doctorInfoService } from '@/api/doctorInfo';

const labelPosition = ref<FormProps['labelPosition']>('right');
const itemLabelPosition = ref<FormItemProps['labelPosition']>('');
const formRef = ref<FormInstance>();

const doctorInfoData = ref({
    name: '',
    username: '',
    office: '',
    gender: '',
    seniority: '',
    intro: ''
});


const genderOptions = [
    { label: "男", value: "1" },
    { label: "女", value: "2" },
];

const officeNameList = ref({});

// 校验是否为正整数
const checkNum = (rule, value, callback) => {
    if (!/^[1-9]\d*$/.test(value)) {
        callback(new Error("工龄必须为正整数"));
    } else {
        callback(); // 验证通过  
    }
}

// 定义表单校验规则
const Rules = reactive<FormRules>({
    name: [
        { required: true, message: "姓名不得为空", trigger: "blur" },
    ],
    office: [
        { required: true, message: "科室不得为空", trigger: "blur" },
    ],
    gender: [
        { required: true, message: "性别不得为空", trigger: "blur" },
    ],
    seniority: [
        { required: true, message: "工龄不得为空", trigger: "blur" },
        { validator: checkNum, trigger: "blur" }
    ],
})

// 获取医生用户工号
const username = ref('12312312312')
const getDoctorInfo = async () => {
    let result = await doctorInfoService();
    username.value = result.data.doctor.user_id;
    // 赋值
    doctorInfoData.value.name = result.data.name;
    doctorInfoData.value.username = result.data.doctor.user_id;
    doctorInfoData.value.office = result.data.office.name;
    doctorInfoData.value.gender = genderOptions[result.data.gender - 1].value;
    doctorInfoData.value.seniority = result.data.seniority;
    doctorInfoData.value.intro = result.data.intro;
}
getDoctorInfo();

// 获取科室列表
import { officeService } from '@/api/office';
const getOfficeInfo = async () => {
    let result;
    result = await officeService();
    if (result.code == 0) {
        // ElMessage.success("成功查询到科室信息");
        // 将科室信息映射为科室名称列表
        // officeNameList.value = result.data;
        officeNameList.value = result.data.map(item => item.name);
    }
    else {
        ElMessage.success("科室信息查询失败");
    }
}
getOfficeInfo();

// 更新医生信息
import { updateDoctorInfoService } from '@/api/doctorInfo';
import router from '@/router';
const updateInfo = async () => {
    let result;
    result = await updateDoctorInfoService(doctorInfoData.value);
    if (result.code == 0) {
        ElMessage.success("成功修改");
    }
    else {
        ElMessage.success("修改失败");
    }
}

// 查看科室
const goto_office = ()=>{
    router.push("/doctor/office")
}

</script>

<template>
    <div style="padding: 20px;" class="updatePwd_container">

        <h1 class="title">医生个人信息主页</h1>

        <el-card class="getBackPwd-card">
            <!-- 表单 -->
            <el-form class="form" :label-position="labelPosition" label-width="auto" :model="doctorInfoData"
                ref="formRef" style="max-width: 600px">

                <el-form-item label="姓名" :label-position="itemLabelPosition" prop="name" class="form_item">
                    <el-input v-model="doctorInfoData.name" placeholder="请设置姓名" clearable class="input" disabled />
                </el-form-item>

                <el-form-item label="工号" :label-position="itemLabelPosition" prop="username" class="form_item">
                    <el-input v-model="doctorInfoData.username" placeholder="工号不可修改" clearable class="input" disabled />
                </el-form-item>

                <el-form-item label="性别" class="form_item" prop="gender">
                    <el-select v-model="doctorInfoData.gender" placeholder="选择性别" clearable disabled
                        @clear="doctorInfoData.gender = ''">
                        <el-option v-for="item in genderOptions" :key="item.value" :label="item.label"
                            :value="item.value"></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="科室" class="form_item" prop="office">
                    <div class="form-item-content">
                        <el-select v-model="doctorInfoData.office" placeholder="选择科室" clearable disabled class="office_select"
                            @clear="doctorInfoData.office = ''">
                            <el-option v-for="(item) in officeNameList" :key="item" :label="item"
                                :value="item"></el-option>
                        </el-select>
                        <el-button type="primary" auto-insert-space @click="goto_office">查看科室</el-button>
                    </div>
                </el-form-item>

                <el-form-item label="工龄" :label-position="itemLabelPosition" prop="seniority" class="form_item">
                    <el-input v-model="doctorInfoData.seniority" placeholder="请输入原密码" clearable class="input"
                        disabled />
                </el-form-item>

                <el-form-item label="简介信息" :label-position="itemLabelPosition" prop="intro" class="form_item">
                    <el-input v-model="doctorInfoData.intro" placeholder="请输入原密码" clearable class="input"
                        type="textarea" :rows="4" disabled />
                </el-form-item>


                <!-- <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="updateInfo" prop="tmp">
                        确认设置
                    </el-button>
                </el-form-item> -->

            </el-form>
        </el-card>
    </div>
</template>



<style lang="scss" scoped>
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

.updatePwd_container {

    position: relative;
    top: 10%;
    display: flex;
    align-items: center;
    justify-content: center;

    .title {
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        font-size: 36px;
        font-weight: bold;
        color: #050613;
        // background-color: #eca452;
        margin-bottom: 30px;
    }

    .getBackPwd-card {
        position: absolute;
        top: 100px;
        width: 600px;
        flex-grow: 0;
        padding: 20px;
        border-radius: 10px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        background-color: white;


        .form {
            display: flex;
            flex-direction: column;
            justify-content: center;
            user-select: none;

            .form_item {
                margin-bottom: 30px;

                .form-item-content {
                    display: flex;  
                    align-items: center; /* 垂直居中对齐 */  
                
                    .office_select {
                        width: 360px;
                        margin-right: 5px;
                    }
                }
            }

            .button {
                width: 100%;
            }

            .flex {
                width: 100%;
                display: flex;
                justify-content: space-between;
            }
        }
    }
}
</style>