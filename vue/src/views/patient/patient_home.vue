<script setup lang="js">
import { CENTERED_ALIGNMENT } from 'element-plus/es/components/virtual-list/src/defaults';
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { patientInfoService } from '@/api/patient.js';

const router = useRouter()

// 按钮跳转
const myAppointRequest = () => {
    router.push("/patient/appointment/by_office")
}

const myUserManagement = () => {
    router.push("/patient/user_management")
}

const mySchedule = () => {
    router.push("/patient/appointment/by_doctor")
}

const myInfo = () => {
    router.push("/patient/appointment/by_office")
}

const CommentZone = () => {
    router.push("/patient/comment/all")
}


onMounted(async () => {
    let result = await patientInfoService();
    username.value = result.data.user_id;
});

// 图片url
const url1 = '/src/assets/imgs/hospital_icon.png';
const url2 = '/src/assets/imgs/doctor_icon.png';
const url3 = '/src/assets/imgs/work_icon.png';

// 获取患者用户个人信息数据

const username = ref('13800138000');
const password = ref('123456');

// 考量到安全性，后端不会返回密码信息，故密码为null
const getPatientInfo = async () => {
  let result = await patientInfoService();
  username.value = result.data.user_id;
  password.value = result.data.password;
}

getPatientInfo();

</script>

<template>
    <div class="home-container">
        <el-row class="content">
            <el-col :span="24">
                <el-card class="box-card1">
                    <el-row style="margin-left: 20px;">

                        <el-col :span="12">
                            <span class="welcome_text">您好，用户 <span :style="{ color: '#138ce4' }">{{ username }}</span>
                                !</span>
                            <div style="margin-top: 10px;" class="hint_text">关注身体健康，让生活更美好</div>
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="appoint_btn"
                                @click="myAppointRequest">
                                <span>预约挂号</span>
                            </el-button><br>
                            <el-button type="primary" style="margin-top: 10px;" size="large" class="appoint_btn"
                                @click="myUserManagement">
                                <span>账户管理</span>
                            </el-button>
                        </el-col>

                        <el-col :span="12" class="image_layout">
                            <el-image style="width: 150px; height: 150px" :src="url1" :fit="fit" />
                        </el-col>

                    </el-row>
                </el-card>
            </el-col>

            <el-col :span="24">
                <el-card class="box-card">
                    <el-row style="margin-left: 10px;">

                        <el-col :span="6" class="image_layout">
                            <el-image style="width: 150px; height: 150px" :src="url2" :fit="fit" />
                        </el-col>

                        <el-col :span="9" class="btn_col">
                            <el-button type="primary" style="margin-top: 10px;" size="large" class="second_btn"
                                @click="mySchedule">
                                <span>找医生</span>
                            </el-button>
                        </el-col>
                        <el-col :span="9" class="btn_col">
                            <el-button type="primary" style="margin-top: 10px;" size="large" class="second_btn"
                                @click="myInfo">
                                <span>找科室</span>
                            </el-button>
                        </el-col>

                    </el-row>
                </el-card>
            </el-col>

            <el-col :span="24">
                <el-card class="box-card">
                    <el-row style="margin-left: 20px;">

                        <el-col :span="16">
                            <div style="margin-top: 10px;" class="hint_text">就诊体验如何?
                                <div>在评论社区，您可以向医生们反馈您的感受！</div>
                            </div>
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="appoint_btn"
                                @click="CommentZone">
                                <span>进入评论社区</span>
                            </el-button>
                        </el-col>

                        <el-col :span="8">
                            <el-image style="width: 150px; height: 150px" :src="url3" :fit="fit" />
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
        width: 60%;

        .card-header {
            position: absolute;
            /* display: flex; */
            justify-content: space-between;
            align-items: center;
        }

        .box-card1 {
            background: rgba(218, 228, 236, 0.8);
            margin-bottom: 20px;
            width: 100%;
            height: 28vh;
            padding: 10px;

            .btn_col {
                display: flex;
                align-items: center;
                justify-content: center;

                .second_btn {
                    width: 100px;
                    height: 50px;
                }

            }

        }

        .box-card {
            background: rgba(218, 228, 236, 0.8);
            margin-bottom: 20px;
            width: 100%;
            height: 25vh;
            padding: 20px;

            .btn_col {
                display: flex;
                align-items: center;
                justify-content: center;

                .second_btn {
                    width: 100px;
                    height: 50px;
                }

            }

        }
    }
}

.image_layout {
    display: flex;
    align-items: center;
    justify-content: center;
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
    width: 200px;
}
</style>
