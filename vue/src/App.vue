<script setup>
  import patientLoginVue from '@/views/patient/patient_signin.vue'
  import patientRegisterVue from '@/views/patient/patient_signup.vue'
  import ForgetPwdVue from '@/views/patient/patient_getback_password.vue'
  import doctorRegisterVue from '@/views/doctor/doctor_signin.vue'
  import doctorHomeVue from '@/views/doctor/doctor_home.vue'
  import adminVue from '@/views/admin/admin.vue'
</script>

<template>
  <!-- <patientLoginVue/> -->
  <!-- <patientRegisterVue/> -->
  <!-- <ForgetPwdVue/> -->
  <!-- <doctorHomeVue/> -->
  <!-- <admin-vue></admin-vue> -->
  <router-view></router-view>
  <!-- <doctorRegisterVue></doctorRegisterVue> -->

</template>

<style scoped>

</style>
