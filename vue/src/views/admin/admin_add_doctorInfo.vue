<script lang="ts" setup>
import { computed, reactive, ref } from 'vue';

// 初始数据
const doctorAccountList = ref([]);
const doctorInfoList = ref([]);

const searchForm = ref({
    doctor_id: '',
    doctor_name: '',
    office: '',
    gender: ''
});

// 性别映射
const genderMapping = {
    1: "男",
    2: "女"
}
const genderReMapping = {
    "男": 1,
    "女": 2
}

const labelPosition = ref<FormProps['labelPosition']>('right')
const itemLabelPosition = ref<FormItemProps['labelPosition']>('')
// (修改/添加)医生信息表单
const formRef = ref<FormInstance>();

// 校验是否为正整数
const checkNum = (rule, value, callback) => {
    if (!/^[1-9]\d*$/.test(value)) {
        callback(new Error("工龄必须为正整数"));
    } else {
        callback(); // 验证通过  
    }
}

// 检查是否科室/性别选择框是否为框
const checkSelectedContent = (rule, value, callback) => {
    if (value == '') {
        callback(new Error("请在下拉列表中选择"));
    }
    else {
        callback();
    }
}


// 定义表单校验规则
const Rules = reactive<FormRules>({
    doctor_name: [
        { required: true, message: "医生姓名不得为空", trigger: "blur" },
    ],
    office: [
        { required: true, message: "医生所属科室不得为空", trigger: "blur" },
        { validator: checkSelectedContent, trigger: "blur" },
    ],
    gender: [
        { required: true, message: "医生性别不得为空", trigger: "blur" },
        { validator: checkSelectedContent, trigger: "blur" },
    ],
    seniority: [
        { required: true, message: "医生工龄不得为空", trigger: "blur" },
        { validator: checkNum, trigger: "blur" },
    ],
})


// 获取全部医生账户/信息
import { getAllDoctorAccountService } from '@/api/doctor';
import { allDoctorInfoService } from '@/api/doctorInfo';
import { officeService } from '@/api/office';
import { ElMessage, ElMessageBox, FormItemProps, FormProps, FormRules } from 'element-plus';

// 获取科室数据
const officeInfoList = ref([]);
// 科室名称=>科室id 映射
const nameToOfficeIdMap = ref([]);
const getOfficeInfo = async () => {
    let result;
    result = await officeService();
    if (result.code == 0) {
        officeInfoList.value = result.data;
        nameToOfficeIdMap.value = officeInfoList.value.reduce((acc, item) => {
            acc[item.name] = item.office_id;
            return acc;
        }, {});
    } else {
        ElMessage.error(result.message ? result.message : "科室信息获取失败");
    }
}
getOfficeInfo();

// 整合数据  
const combinedData = ref([]);
const combinedDoctorData = async () => {
    let result = await allDoctorInfoService();
    doctorInfoList.value = result.data;
    result = await getAllDoctorAccountService();
    doctorAccountList.value = result.data;
    let data = [];
    console.log(doctorAccountList.value.length);
    // 手动遍历doctorInfo  
    for (let i = 0; i < doctorAccountList.value.length; i++) {
        const account = doctorAccountList.value[i];
        let doctorInfoData = {
            doctorInfo_id: '',
            doctor_id: account.user_id,
            doctor_name: '',
            office: '',
            // office_id: '',
            gender: '',
            intro: '',
            seniority: ''
        };
        // 手动遍历doctorAccountInfo  
        for (let j = 0; j < doctorInfoList.value.length; j++) {
            const info = doctorInfoList.value[j];

            // 如果user_id匹配，则合并数据  
            if (account.user_id === info.doctor.user_id) {
                doctorInfoData.doctorInfo_id = info.doctor_info_id;
                doctorInfoData.doctor_name = info.name;
                doctorInfoData.office = info.office.name;
                // doctorInfoData.office_id = info.office.office_id;
                doctorInfoData.gender = genderMapping[info.gender];
                doctorInfoData.intro = info.intro;
                doctorInfoData.seniority = info.seniority;
                break; // 找到匹配项后可以跳出内层循环  
            }
        }
        data.push(doctorInfoData);
    }
    combinedData.value = data;
    console.log(data);
};
combinedDoctorData();


// 当前页码
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);

// 过滤后的医生信息表
const filteredDoctorData = computed(() => {
    let result = combinedData.value.filter(data => {
        return (
            (data.doctor_id.includes(searchForm.value.doctor_id)) &&
            (data.doctor_name.includes(searchForm.value.doctor_name)) &&
            (searchForm.value.office === '' || data.office === searchForm.value.office) &&
            (searchForm.value.gender === '' || data.gender === searchForm.value.gender)
        );
    });
    total.value = result.length;
    return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

// 日期选择框更改
const ElementChange = () => {
    currentPage.value = 1;
}

// 显示医生简介详情
const selectedIntro = ref(null);
const introDialogVisible = ref(false);
const showIntroDetails = (intro) => {
    selectedIntro.value = intro;
    introDialogVisible.value = true;
};

// 关闭详情对话框
const closeIntroDialog = () => {
    introDialogVisible.value = false;
    selectedIntro.value = null;
};

// 跳转到删除医生信息界面
const del_doctorInfo = () => {
    router.push("/admin/del_doctorInfo");
}

// 修改医生信息时，显示更改页面，否则显示原页面
const updateVisible = ref(false);

// 修改医生信息
const updateDoctorInfoData = ref({
    doctor_info_id: '',
    doctor_id: '',
    doctor_name: '',
    office: '',
    // office_id: '',
    gender: '',
    seniority: '',
    intro: ''
});
const update_doctorInfo = (info) => {
    // 手动深拷贝
    updateDoctorInfoData.value = {
        doctor_info_id: info.doctor_info_id,
        doctor_id: info.doctor_id,
        doctor_name: info.doctor_name,
        office: info.office,
        gender: info.gender,
        intro: info.intro,
        seniority: info.seniority
    }
    updateVisible.value = true;
}

// 删除医生信息
import { updateDoctorInfoService } from '@/api/doctorInfo';
import router from '@/router';
import { FormInstance } from 'element-plus';
const updateDoctorInfo = async () => {
    // 表单校验
    // 对表单进行校验
    await formRef.value?.validate().catch(err => {
        ElMessage.error('登录失败(前端)')
        throw err
    })
    // console.log(updateDoctorInfoData.value);
    let result;
    result = await updateDoctorInfoService({
        office: nameToOfficeIdMap.value[updateDoctorInfoData.value.office],
        gender: genderReMapping[updateDoctorInfoData.value.gender],
        seniority: updateDoctorInfoData.value.seniority,
        doctor_id: updateDoctorInfoData.value.doctor_id,
        doctor_name: updateDoctorInfoData.value.doctor_name,
        intro: updateDoctorInfoData.value.intro,
    });
    if (result.code == 0) {
        ElMessage.success("成功更改医生信息");
        combinedDoctorData();
        updateVisible.value = false;
    } else {
        ElMessage.error(result.message ? result.message : "更改失败");
    }
}

</script>

<template>
    <div v-if="!updateVisible">
        <!-- 搜索和过滤栏 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="医生工号">
                <el-input style="width: 160px; margin-right: 0px;" v-model="searchForm.doctor_id" placeholder="输入医生工号"
                    clearable @clear="ElementChange" class="input" />
            </el-form-item>

            <el-form-item label="医生姓名">
                <el-input style="width: 160px; margin-right: 0px;" v-model="searchForm.doctor_name" placeholder="输入医生姓名"
                    clearable @clear="ElementChange" class="input" />
            </el-form-item>

            <el-form-item label="科室">
                <el-select v-model="searchForm.office" placeholder="选择科室" clearable @clear="searchForm.office = '', ElementChange"
                    style="width: 150px;">
                    <el-option v-for="item in officeInfoList" :key="item.name" :label="item.name"
                        :value="item.name"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="性别">
                <el-select v-model="searchForm.gender" placeholder="选择性别" clearable @clear="searchForm.gender = '', ElementChange"
                    style="width: 150px;">
                    <el-option v-for="(item, index) in genderMapping" :key="index" :label="item"
                        :value="item"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item>
                <el-button type="primary">查询</el-button>
            </el-form-item>

            <el-form-item>
                <el-button type="danger" @click="del_doctorInfo">删除医生信息</el-button>
            </el-form-item>
        </el-form>

        <!-- 医生信息列表 -->
        <el-table :data="filteredDoctorData" style="width: 100%" :key="filteredDoctorData">
            <el-table-column prop="doctor_id" label="医生工号"></el-table-column>
            <el-table-column prop="doctor_name" label="医生姓名"></el-table-column>
            <el-table-column prop="office" label="科室名称"></el-table-column>
            <el-table-column prop="gender" label="性别"></el-table-column>
            <el-table-column prop="seniority" label="工龄"></el-table-column>
            <!-- <el-table-column v-if="false" prop="office_id" label="工龄"></el-table-column> -->
            <el-table-column prop="intro" label="简介信息" width="400px">
                <template #default="scope">
                    <span style="margin-right: 5px;">{{ scope.row.intro.slice(0, 10) }}<el-text
                            v-if="scope.row.intro.length > 10">...</el-text></span>
                    <el-button type="primary" v-if="scope.row.intro.length > 10"
                        @click="showIntroDetails(scope.row.intro)" size="small">详情</el-button>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="success" size="medium" @click="update_doctorInfo(scope.row)">修改</el-button>
                    <!-- <el-button type="success" size="medium" @click="showSelectedDoctorInfo(scope.row)">新增</el-button> -->
                </template>
            </el-table-column>
        </el-table>

        <!-- 医生简介详情对话框 -->
        <el-dialog v-model="introDialogVisible" title="医生简介详情" width="50%" align-center>
            <div v-if="selectedIntro">
                <p class="dialog_item_content"><strong class="dialog_item_title">简介内容:</strong> {{ selectedIntro
                    }}</p>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="closeIntroDialog" class="close-button">关 闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 分页 -->
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize"
            layout="jumper, total, prev, pager, next" background :total="total"
            style="margin-top: 20px; justify-content: flex-end">
        </el-pagination>

    </div>

    <!-- 修改医生信息表单 -->
    <div v-if="updateVisible" style="padding: 20px;" class="addDoctor_container">

        <h1 class="title">更改医生信息</h1>

        <el-card class="card">
            <!-- 表单 -->
            <el-form class="form" :label-position="labelPosition" label-width="auto" :model="updateDoctorInfoData"
                ref="formRef" :rules="Rules" style="max-width: 600px">

                <el-form-item label="医生工号" :label-position="itemLabelPosition" prop="doctor_id" class="form_item">
                    <el-input v-model="updateDoctorInfoData.doctor_id" placeholder="请输入医生工号" clearable class="input"
                        disabled />
                </el-form-item>

                <el-form-item label="医生姓名" :label-position="itemLabelPosition" prop="doctor_name" class="form_item">
                    <el-input v-model="updateDoctorInfoData.doctor_name" placeholder="请输入医生姓名" clearable
                        class="input" />
                </el-form-item>

                <el-form-item label="医生所属科室" prop="office" class="form_item">
                    <el-select v-model="updateDoctorInfoData.office" placeholder="请选择科室" clearable class="input"
                        @clear="updateDoctorInfoData.office = ''">
                        <el-option v-for="item in officeInfoList" :key="item.name" :label="item.name"
                            :value="item.name"></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="医生性别" prop="gender" class="form_item">
                    <el-select v-model="updateDoctorInfoData.gender" placeholder="请选择性别" clearable class="input"
                        @clear="updateDoctorInfoData.gender = ''">
                        <el-option v-for="(item, index) in genderMapping" :key="index" :label="item"
                            :value="item"></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="医生工龄" :label-position="itemLabelPosition" prop="seniority" class="form_item">
                    <el-input v-model="updateDoctorInfoData.seniority" placeholder="请输入工龄" clearable class="input" />
                </el-form-item>

                <el-form-item label="医生简介" :label-position="itemLabelPosition" prop="intro" class="form_item">
                    <el-input v-model="updateDoctorInfoData.intro" placeholder="请输入医生简介" clearable class="input"
                        type="textarea" :rows="4" />
                </el-form-item>

                <el-form-item class="form_item">
                    <el-row style="width: 100%; display: flex; align-items: center; justify-items: center;">

                        <el-col :span="12" style="display: flex; justify-content: center;">
                            <el-button type="primary" auto-insert-space @click="updateVisible = false"
                                class="update_btn">
                                返回
                            </el-button>
                        </el-col>
                        <el-col :span="12" style="display: flex; justify-content: center;">
                            <el-button type="primary" auto-insert-space @click="updateDoctorInfo" class="update_btn">
                                确认更改
                            </el-button>
                        </el-col>

                    </el-row>

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
    top: 5%;
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

            .update_btn {
                width: 45%;
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