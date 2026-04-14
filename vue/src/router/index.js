import { createRouter, createWebHistory } from 'vue-router'

// 导入组件
import initialPageVue from '@/views/initial_page.vue'

import patientSignupVue from '@/views/patient/patient_signup.vue'
import patientLoginVue from '@/views/patient/patient_signin.vue'
import patientForgetPwdVue from '@/views/patient/patient_getback_password.vue'
import patientVue from '@/views/patient/patient.vue'
import patientHomeVue from '@/views/patient/patient_home.vue'
import patientAppointmentByDoctorVue from '@/views/patient/patient_appointment_by_doctor.vue'
import patientAppointmentByOfficeVue from '@/views/patient/patient_appointment_by_office.vue'
import patientAppointmentMyVue from '@/views/patient/patient_appointment_my.vue'
import patientCommentAllVue from '@/views/patient/patient_comment_all.vue'
import patientCommentMyVue from '@/views/patient/patient_comment_my.vue'
import patientCommentAddVue from '@/views/patient/patient_comment_add.vue'
import patientUserManagementVue from '@/views/patient/patient_user_management.vue'
import patientDelAccountVue from '@/views/patient/patient_del_account.vue'
import patientUpdatePwdVue from '@/views/patient/patient_updatePwd.vue'


import doctorLoginVue from '@/views/doctor/doctor_signin.vue'
import doctorVue from '@/views/doctor/doctor.vue'
import doctorHomeVue from '@/views/doctor/doctor_home.vue'
import doctorScheduleVue from '@/views/doctor/doctor_schedule.vue'
import doctorAppointmentVue from '@/views/doctor/doctor_appointment.vue'
import doctorCommentVue from '@/views/doctor/doctor_comment.vue'
import doctorOfficeVue from '@/views/doctor/doctor_office.vue'
import doctorUserManagementVue from '@/views/doctor/doctor_user_management.vue'
import doctorUpdatePwdVue from '@/views/doctor/doctor_updatePwd.vue'

import adminLoginVue from '@/views/admin/admin_signin.vue'
import adminVue from '@/views/admin/admin.vue'
import adminHomeVue from '@/views/admin/admin_home.vue'
import adminPatientManageVue from '@/views/admin/admin_patient_account.vue'
import adminDelDoctorVue from '@/views/admin/admin_del_doctor.vue'
import adminAddDoctorVue from '@/views/admin/admin_add_doctor.vue'
import adminDoctorInfoManageVue from '@/views/admin/admin_del_doctorInfo.vue'
import adminAddDoctorInfoVue from '@/views/admin/admin_add_doctorInfo.vue'
import adminCommentManageVue from '@/views/admin/admin_comment_manage.vue'
import adminAppointmentManageVue from '@/views/admin/admin_manage_appointment.vue'
import adminScheduleManageVue from '@/views/admin/admin_schedule_manage.vue'
import adminOfficeManageVue from '@/views/admin/admin_office_manage.vue'
import adminAddOfficeVue from '@/views/admin/admin_add_office.vue'


// 定义路由关系
const routes = [
    { path: '/', redirect: '/init' },
    { path: '/init', component: initialPageVue },
    { path: '/patient_login', component: patientLoginVue },
    { path: '/register', component: patientSignupVue },
    { path: '/forgetPwd', component: patientForgetPwdVue },
    {
        path: '/patient', component: patientVue, children: [
            { path: '/patient/home', component: patientHomeVue },
            { path: '/patient/appointment/by_doctor', component: patientAppointmentByDoctorVue },
            { path: '/patient/appointment/by_office', component: patientAppointmentByOfficeVue },
            { path: '/patient/appointment/my', component: patientAppointmentMyVue },
            { path: '/patient/comment/all', component: patientCommentAllVue },
            { path: '/patient/comment/my', component: patientCommentMyVue },
            { path: '/patient/comment/add', component: patientCommentAddVue },
            { path: '/patient/user_management', component: patientUserManagementVue },
            { path: '/patient/del_account', component: patientDelAccountVue },
            { path: '/patient/updatePwd', component: patientUpdatePwdVue },
        ]
    },

    { path: '/doctor_login', component: doctorLoginVue },
    {
        path: '/doctor', component: doctorVue, children: [
            { path: '/doctor/home', component: doctorHomeVue },
            { path: '/doctor/schedule', component: doctorScheduleVue },
            { path: '/doctor/appointment', component: doctorAppointmentVue },
            { path: '/doctor/comment', component: doctorCommentVue },
            { path: '/doctor/office', component: doctorOfficeVue },
            { path: '/doctor/user_management', component: doctorUserManagementVue },
            { path: '/doctor/updatePwd', component: doctorUpdatePwdVue },
        ]
    },
    { path: '/admin_login', component: adminLoginVue },
    {
        path: '/admin', component: adminVue, children: [
            { path: '/admin/home', component: adminHomeVue },
            { path: '/admin/patient_account', component: adminPatientManageVue },
            { path: '/admin/del_doctor', component: adminDelDoctorVue },
            { path: '/admin/add_doctor', component: adminAddDoctorVue },
            { path: '/admin/del_doctorInfo', component: adminDoctorInfoManageVue },
            { path: '/admin/add_doctorInfo', component: adminAddDoctorInfoVue },
            { path: '/admin/comment_manage', component: adminCommentManageVue },
            { path: '/admin/appointment_manage', component: adminAppointmentManageVue },
            { path: '/admin/schedule_manage', component: adminScheduleManageVue },
            { path: '/admin/office_manage', component: adminOfficeManageVue },
            { path: '/admin/add_office', component: adminAddOfficeVue },
        ]
    }
]

// 创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

// 导出路由
export default router
