<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { doctorIdService } from '@/api/doctor';
import { ElMessage, FormInstance, FormItemProps, FormProps, FormRules } from 'element-plus';

const labelPosition = ref<FormProps['labelPosition']>('right');
const itemLabelPosition = ref<FormItemProps['labelPosition']>('');
const formRef = ref<FormInstance>();

const resetPwdData = ref({
    oldPassword: '',
    newPassword: '',
    rePassword: ''
})

// 获取医生用户工号
const username = ref('12312312312')
const getDoctorId = async () => {
    let result = await doctorIdService();
    username.value = result.data.user_id;
}
getDoctorId();

// 校验密码函数
const checkPwd = (rule, value, callback) => {
    let hasUpperCase = false;
    let hasLowerCase = false;
    let hasDigit = false;

    for (let i = 0; i < value.length; i++) {
        const c = value.charAt(i);
        if (/[A-Z]/.test(c)) {          // 大写字母
            hasUpperCase = true;
        } else if (/[a-z]/.test(c)) {   // 小写字母
            hasLowerCase = true;
        } else if (/[0-9]/.test(c)) {   // 数字
            hasDigit = true;
        }
        if (hasUpperCase && hasLowerCase && hasDigit) {
            callback(); // Validation passed  
            return;
        }
    }
    callback(new Error("密码须同时包含大小写字母和数字"));
}

// 确认密码校验函数
const checkRePwd = (rule, value, callback) => {
    if (value == '') {
        callback(new Error("请再次输入密码"));
    } else if (value != resetPwdData.value.newPassword) {
        callback(new Error("两次密码输入不一致"));
    } else {
        callback();
    }
}

// 手机号找回密码方式校验规则
const Rules = reactive<FormRules>({
    oldPassword: [
        { required: true, message: "请输入原密码", trigger: "blur" },
    ],
    newPassword: [
        { required: true, message: "请重新设置密码", trigger: "blur" },
        { min: 8, message: "长度不得少于8位", trigger: "blur" },
        { validator: checkPwd, trigger: "blur" }
    ],
    rePassword: [
        { required: true, message: "请再次输入密码", trigger: "blur" },
        { validator: checkRePwd, trigger: "blur" }
    ]
})

// 更新密码
import { updatePwdService } from '@/api/doctor';
const updatePwd = async () => {

    // 表单准备提交进行校验
    await formRef.value.validate().catch(err => {
        ElMessage.error('修改失败(前端))')
        throw err
    })

    let result;
    result = await updatePwdService(resetPwdData.value);
    if (result.code == 0) {
        ElMessage.success("成功修改密码");
    } else {
        ElMessage.error(result.message ? result.message : "修改失败");
    }
}

</script>

<template>
    <div style="padding: 20px;" class="updatePwd_container">

        <h1 class="title">修改密码</h1>

        <el-card class="getBackPwd-card">
            <!-- 表单 -->
            <el-form class="form" :label-position="labelPosition" label-width="auto" :model="resetPwdData"
                :rules="Rules" ref="formRef" style="max-width: 600px">

                <el-form-item label="原密码" :label-position="itemLabelPosition" prop="oldPassword" class="form_item">
                    <el-input v-model="resetPwdData.oldPassword" placeholder="请输入原密码" type="password" show-password clearable class="input"/>
                </el-form-item>

                <el-form-item label="新密码" :label-position="itemLabelPosition" prop="newPassword" class="form_item">
                    <el-input v-model="resetPwdData.newPassword" placeholder="请设置新密码" type="password" show-password />
                </el-form-item>

                <el-form-item label="确认密码" :label-position="itemLabelPosition" prop="rePassword" class="form_item">
                    <el-input v-model="resetPwdData.rePassword" placeholder="请再次输入密码" type="password" show-password />
                </el-form-item>


                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="updatePwd" prop="tmp">
                        确认设置
                    </el-button>
                </el-form-item>

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
    top: 20%;
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