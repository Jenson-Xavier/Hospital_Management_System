<script lang="ts" setup>
import { ElMessage, FormInstance, FormItemProps, FormProps, FormRules } from 'element-plus';
import { reactive, ref } from 'vue';

const labelPosition = ref<FormProps['labelPosition']>('right');
const itemLabelPosition = ref<FormItemProps['labelPosition']>('');
const formRef = ref<FormInstance>();

const doctorForm = ref({
    user_id: '',
    password: '',
    rePassword: '',
})

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
    } else if (value != doctorForm.value.password) {
        callback(new Error("两次密码输入不一致"));
    } else {
        callback();
    }
}

// 检验是否全部由数字组成
const checkNum = (rule, value, callback) => {
    if (!/^[0-9]\d*$/.test(value)) {
        callback(new Error("工龄必医生工号需全部由数字组成须为整数"));
    } else {
        callback(); // 验证通过  
    }
}

// 表单校验规则
const Rules = reactive<FormRules>({
    user_id: [
        { required: true, message: "请设置医生工号", trigger: "blur" },
        { min: 8, max: 8, message: "医生工号需为8位", trigger: "blur" },
        { validator: checkNum, trigger: "blur" },
    ],
    password: [
        { required: true, message: "请设置密码", trigger: "blur" },
        { min: 8, message: "长度不得少于8位", trigger: "blur" },
        { validator: checkPwd, trigger: "blur" }
    ],
    rePassword: [
        { required: true, message: "请再次输入密码", trigger: "blur" },
        { validator: checkRePwd, trigger: "blur" }
    ]
})

// 添加医生账户
import { addDoctorAccount } from '@/api/doctor';
const addAccount = async () => {

    // 表单准备提交进行校验
    await formRef.value.validate().catch(err => {
        ElMessage.error('添加失败(前端))')
        throw err
    })

    let result;
    result = await addDoctorAccount(doctorForm.value);
    if (result.code == 0) {
        ElMessage.success("成功添加医生账户");
    }
    else {
        ElMessage.error(result.message ? result.message : "添加失败");
    }
}

</script>

<template>
    <div style="padding: 20px;" class="addDoctor_container">

        <h1 class="title">添加医生账户</h1>

        <el-card class="card">
            <!-- 表单 -->
            <el-form class="form" :label-position="labelPosition" label-width="auto" :model="doctorForm" ref="formRef" :rules="Rules"
                style="max-width: 600px">

                <el-form-item label="医生工号" :label-position="itemLabelPosition" prop="user_id" class="form_item">
                    <el-input v-model="doctorForm.user_id" placeholder="请设置医生工号" clearable class="input" />
                </el-form-item>

                <el-form-item label="密码" :label-position="itemLabelPosition" prop="password" class="form_item">
                    <el-input v-model="doctorForm.password" placeholder="请设置密码" type="password" clearable show-password
                        class="input" />
                </el-form-item>

                <el-form-item label="确认密码" :label-position="itemLabelPosition" prop="rePassword" class="form_item">
                    <el-input v-model="doctorForm.rePassword" placeholder="请确认密码" type="password" clearable show-password
                        class="input" />
                </el-form-item>

                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="addAccount" prop="tmp">
                        确认添加
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

.addDoctor_container {

    position: relative;
    top: 10%;
    // 设置子组件水平居中
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
        margin-bottom: 30px;
    }

    .card {
        position: absolute;
        top: 100px;
        width: 600px;
        flex-grow: 0;
        padding: 20px;
        // 设置边框弧度
        border-radius: 10px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        background-color: white;

        .form {
            display: flex;
            // 设置垂直居中
            flex-direction: column;
            justify-content: center;
            user-select: none;

            .form_item {
                margin-bottom: 30px;

                .form-item-content {
                    display: flex;
                    align-items: center;
                    /* 垂直居中对齐 */

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