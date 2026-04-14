package com.example.hospitalsystem.util;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.example.hospitalsystem.AppointmentManageFragment;
import com.example.hospitalsystem.IndexFragment;
import com.example.hospitalsystem.PaymentFindFragment;
import com.example.hospitalsystem.R;
import com.example.hospitalsystem.UserManageFragment;
import com.example.hospitalsystem.patient_index;

public class NavUtil {

    public static boolean switchAcrossFragment(Fragment currentFragment, MenuItem item, String user_name) {
        if (item.getItemId() == R.id.nv_index) {
            if (patient_index.indexFragment == null) {
                patient_index.indexFragment = IndexFragment.newInstance(user_name);
            }
            switchContent(currentFragment, patient_index.indexFragment);
            return true;
        }
        else if (item.getItemId() == R.id.nv_appointment) {
            if (patient_index.appointmentManageFragment == null) {
                patient_index.appointmentManageFragment = AppointmentManageFragment.newInstance(user_name);
            }
            switchContent(currentFragment, patient_index.appointmentManageFragment);
            return true;
        }
        else if (item.getItemId() == R.id.nv_payment) {
            if (patient_index.paymentFindFragment == null) {
                patient_index.paymentFindFragment = PaymentFindFragment.newInstance(user_name);
            }
            switchContent(currentFragment, patient_index.paymentFindFragment);
            return true;
        }
//        else if (item.getItemId() == R.id.nv_refund) {
//
//        }
        else if (item.getItemId() == R.id.nv_user) {
            if (patient_index.userManageFragment == null) {
                patient_index.userManageFragment = UserManageFragment.newInstance(user_name);
            }
            switchContent(currentFragment, patient_index.userManageFragment);
            return true;
        }
        return false;
    }

    public static void switchContent(Fragment from, Fragment to) {
        if (from == null || to == null) return;
        if (!to.isAdded()) {
            //fragment parent layout id
            from.getActivity().getSupportFragmentManager().beginTransaction()
                    .hide(from).add(R.id.fragment_container, to).commit();
        } else {
            from.getActivity().getSupportFragmentManager().beginTransaction()
                    .hide(from).show(to).commit();
        }
    }

}
