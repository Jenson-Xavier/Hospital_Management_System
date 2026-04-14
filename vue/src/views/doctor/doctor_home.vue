<script setup lang="js">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter()

// 按钮跳转
const myAppointRequest = ()=>{
    router.push("/doctor/appointment")
}

const mySchedule = ()=>{
    router.push("/doctor/schedule")
}

const myInfo = ()=>{
    router.push("/doctor/user_management")
}

const myOffice = ()=>{
    router.push("/doctor/office")
}

const CommentZone = ()=>{
    router.push("/doctor/comment")
    
}

const username = ref("12312312312");
const doctor_name = ref('');

import { doctorInfoService } from '@/api/doctorInfo';
import { ElMessage } from 'element-plus';

const getDoctorInfo = async () => {
  let result = await doctorInfoService();
//   ElMessage.success(result.data.name);
  doctor_name.value = result.data.name;
}
getDoctorInfo();

// 图片url
const url1 = '/src/assets/imgs/hospital_icon.png';
const url2 = '/src/assets/imgs/doctor_icon.png';
const url3 = '/src/assets/imgs/work_icon.png';

</script>

<template>
    <div class="home-container">
        <el-row class="content">
            <el-col :span="24">
                <el-card class="box-card">
                    <el-row style="margin-left: 20px;">

                        <el-col :span="12">
                            <span class="welcome_text">您好，医生用户 <span :style="{ color: '#138ce4' }">{{ doctor_name }}</span>
                                !</span>
                            <div style="margin-top: 10px;" class="hint_text">关注身体健康，让生活更美好</div>
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="appoint_btn" @click="myAppointRequest">
                                <span>查看今日的预约请求</span>
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

                        <el-col :span="6" class="btn_col">
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="second_btn" @click="mySchedule">
                                <span>排班信息</span>
                            </el-button>
                        </el-col>
                        <el-col :span="6" class="btn_col">
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="second_btn" @click="myInfo">
                                <span>个人信息</span>
                            </el-button>
                        </el-col>
                        <el-col :span="6" class="btn_col">
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="second_btn" @click="myOffice">
                                <span>我的科室</span>
                            </el-button>
                        </el-col>

                    </el-row>
                </el-card>
            </el-col>

            <el-col :span="24">
                <el-card class="box-card">
                    <el-row style="margin-left: 20px;">

                        <el-col :span="16">
                            <div style="margin-top: 10px;" class="hint_text">想知道患者对您的评价如何吗？
                                <div>在评论社区，您可以看到您在患者心目中的形象</div>
                            </div>
                            <el-button type="primary" style="margin-top: 20px;" size="large" class="appoint_btn" @click="CommentZone">
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

        .box-card {
            background: rgba(218, 228, 236, 0.8);
            margin-bottom: 25px;
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
    width: 250px;
}
</style>
