<script setup lang="js">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter()

// 按钮跳转
const AppointmentManage = () => {
    router.push("/admin/appointment_manage");
}

const PatientManage = () => {
    router.push("/admin/patient_account");
}

const addDoctorManage = () => {
    router.push("/admin/add_doctor");
}

const delDoctorManage = () => {
    router.push("/admin/del_doctor");
}

const CommentManage = () => {
    router.push("/admin/comment_manage");
}

const PaymentManage = () => {
    router.push("/admin/payment");
}

const DoctorInfoManage = () => {
    router.push("/admin/del_doctorInfo");
}

const OfficeManage = () => {
    router.push("/admin/office_manage");
}

const ScheduleManage = () => {
    router.push("/admin/schedule_manage");
}

// 获取管理员id
const username = ref("12312312312");

import { ElMessage } from 'element-plus';
import { adminIdService } from '@/api/admin';

const getAdminId = async () => {
    let result = await adminIdService();
    username.value = result.data.user_id;
}
getAdminId();

// 图片url
const url1 = '/src/assets/imgs/hospital_icon.png';
const url2 = '/src/assets/imgs/doctor_icon.png';
const url3 = '/src/assets/imgs/work_icon.png';
const url4 = '/src/assets/imgs/hospital_icon2.png';
const url5 = '/src/assets/imgs/hospital_icon3.png';
const url6 = '/src/assets/imgs/hospital_icon4.png';
const url7 = '/src/assets/imgs/schedule.png';
const url8 = '/src/assets/imgs/account_management.png';
const url9 = '/src/assets/imgs/payment_management.png';

</script>

<template>
    <div class="home-container">
        <el-row class="content">
            <el-col :span="12" style="margin-right: 45px;">
                <el-card class="box-card">
                    <el-row style="margin-left: 20px;" class="layout_horizon">

                        <el-col :span="13" class="layout_vertical">
                            <span class="welcome_text">您好，管理员 <span :style="{ color: '#138ce4' }">{{ username
                                    }}</span>
                                !</span>
                            <div style="margin-top: 10px;" class="hint_text">关注身体健康，让生活更美好</div>
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="appoint_btn"
                                @click="AppointmentManage">
                                <span>预约记录管理</span>
                            </el-button>
                        </el-col>

                        <el-col :span="11" class="layout_vertical">
                            <el-image style="width: 150px; height: 150px" :src="url1" :fit="fit" />
                        </el-col>

                    </el-row>
                </el-card>
            </el-col>

            <el-col :span="11">
                <el-card class="box-card">
                    <el-row style="margin-left: 20px;" class="layout_horizon">

                        <el-col :span="12" class="layout_vertical">
                            <div style="margin-top: 10px; margin-right: 20px;" class="hint_text">
                                您需要审核患者对医生的评论信息，以确保评价内容得当。
                            </div>
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="appoint_btn"
                                @click="CommentManage">
                                <span>评价管理</span>
                            </el-button>
                        </el-col>

                        <el-col :span="12" class="layout_vertical">
                            <el-image style="width: 150px; height: 150px" :src="url3" :fit="fit" />
                        </el-col>

                    </el-row>
                </el-card>
            </el-col>

            <el-col :span="12" style="margin-right: 45px;">
                <el-card class="box-card">
                    <el-row class="layout_horizon">
                        <el-col :span="12" class="layout_vertical">
                            <span class="welcome_text">用户账户管理</span>
                            <el-image style="width: 150px; height: 150px" :src="url8" :fit="fit" />
                        </el-col>

                        <el-col :span="12" class="layout_vertical">
                            <el-row style="margin-left: 20px;" class="layout_vertical">
                                <el-col :span="6" class="layout_vertical">
                                    <el-button type="primary" style="margin-top: 20px;" size="large" class="second_btn"
                                        @click="PatientManage">
                                        <span>患者账户管理</span>
                                    </el-button>
                                </el-col>

                                <el-col :span="6" class="layout_vertical">
                                    <el-button type="primary" style="margin-top: 20px;" size="large" class="second_btn"
                                        @click="delDoctorManage">
                                        <span>医生账户管理</span>
                                    </el-button>
                                </el-col>
                            </el-row>
                        </el-col>



                    </el-row>
                </el-card>
            </el-col>

            <el-col :span="11">
                <el-card class="box-card">
                    <el-row style="margin-left: 20px;" class="layout_horizon">

                        <el-col :span="12" class="layout_vertical">
                            <el-image style="width: 150px; height: 150px" :src="url7" :fit="fit" />
                        </el-col>

                        <el-col :span="12" class="layout_vertical">
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="second_btn"
                                @click="ScheduleManage">
                                <span>排班信息管理</span>
                            </el-button>
                        </el-col>



                    </el-row>
                </el-card>
            </el-col>

            <el-col :span="12" style="margin-right: 45px;">
                <el-card class="box-card">
                    <el-row class="layout_horizon">

                        <el-col :span="12" class="layout_vertical">
                            <span class="welcome_text">用户账户管理</span>
                            <el-image style="width: 150px; height: 150px" :src="url2" :fit="fit" />
                        </el-col>

                        <el-col :span="12" class="layout_vertical">
                            <el-row style="margin-left: 20px;" class="layout_vertical">
                                <el-col :span="6" class="layout_vertical">
                                    <el-button type="primary" style="margin-top: 20px;" size="large" class="second_btn"
                                        @click="OfficeManage">
                                        <span>科室管理</span>
                                    </el-button>
                                </el-col>
                            </el-row>
                        </el-col>



                    </el-row>
                </el-card>
            </el-col>

            <el-col :span="11">
                <el-card class="box-card">
                    <el-row class="layout_horizon">

                        <el-col :span="12" class="layout_vertical">
                            <el-image style="width: 150px; height: 150px" :src="url9" :fit="fit" />
                        </el-col>

                        <el-col :span="12" class="layout_vertical">
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="second_btn"
                                @click="DoctorInfoManage">
                                <span>医生信息管理</span>
                            </el-button>
                        </el-col>
                    </el-row>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<style lang="scss" scoped>
.home-container {
    position: relative;
    width: 100%;
    height: 100%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    // 使得子组件居中设置
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
        width: 80%;

        .card-header {
            position: absolute;
            /* display: flex; */
            justify-content: space-between;
            align-items: center;
        }

        .box-card {
            background: rgba(218, 228, 236, 0.8);
            margin-bottom: 25px;
            width: 100%;
            height: 25vh;
            padding: 20px;

        }
    }
}

.layout_horizon {
    display: flex;
    align-items: center;
    justify-content: center;
}

.layout_vertical {
    display: flex;
    flex-direction: column;
    align-items: center;
    /* 水平居中 */
    justify-content: center;
    /* 垂直居中 */
}

.welcome_text {
    font-size: large;
    font-family: "微软雅黑";
    font-weight: 800;
}

.hint_text {
    font-size: large;
    font-family: "仿宋";
    font-weight: 200;
}

.appoint_btn {
    font-size: large;
    font-family: "微软雅黑";
    background-color: rgb(74, 187, 190);
    width: 250px;
}

.second_btn {
    font-size: large;
    background-color: rgb(74, 187, 190);
    width: 200px;
    height: 50px;
}
</style>
