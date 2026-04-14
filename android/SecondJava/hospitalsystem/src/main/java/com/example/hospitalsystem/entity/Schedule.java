package com.example.hospitalsystem.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;

@Entity(foreignKeys = {
                @ForeignKey(entity = Office.class,
                        parentColumns = "office_id",
                        childColumns = "office_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Doctor.class,
                        parentColumns = "userId",
                        childColumns = "doctor_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("office_id"), @Index("doctor_id")})
public class Schedule {

    public static final int FORENOON = 1;
    public static final int AFTERNOON = 2;

    public static final String[] periodStr = {
            "不限", "8:30-12:00", "13:30~17:00"
    };

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "schedule_id")
    private int scheduleId;

    @ColumnInfo(name = "office_id")
    private int officeId;

    @ColumnInfo(name = "doctor_id")
    private String doctorId;

    @ColumnInfo(name = "schedule_date")
    @NonNull
    private Date scheduleDate;

    @ColumnInfo(name = "schedule_period")
    private int schedulePeriod; // Consider using a different type if needed

    @ColumnInfo(name = "appointment_num")
    private int appointmentNum;

    // 默认添加数据
    private static final String[] mDoctorIdArray = {
            "00000000001", "00000000002", "00000000003", "00000000004", "00000000005",
            "00000000006", "00000000007", "00000000008", "00000000009", "00000000010", "00000000011",
            "00000000012", "00000000013", "00000000014", "00000000015", "00000000016", "00000000017",
            "00000000001", "00000000002", "00000000003", "00000000004", "00000000005",
            "00000000006", "00000000007", "00000000008", "00000000009", "00000000010", "00000000011",
            "00000000012", "00000000013", "00000000014", "00000000015", "00000000016", "00000000017",

            "00000000001", "00000000002", "00000000003", "00000000004", "00000000005",
            "00000000006", "00000000007", "00000000008", "00000000009", "00000000010", "00000000011",
            "00000000012", "00000000013", "00000000014", "00000000015", "00000000016", "00000000017",
            "00000000001", "00000000002", "00000000003", "00000000004", "00000000005",
            "00000000006", "00000000007", "00000000008", "00000000009", "00000000010", "00000000011",
            "00000000012", "00000000013", "00000000014", "00000000015", "00000000016", "00000000017"
    };

    private static final String[] mScheduleDateArray = {
            "2024-10-01","2024-10-01","2024-10-01","2024-10-01","2024-10-01",
            "2024-10-02","2024-10-02","2024-10-02","2024-10-02","2024-10-02","2024-10-02",
            "2024-10-03","2024-10-03","2024-10-03","2024-10-03","2024-10-03","2024-10-03",
            "2024-10-04","2024-10-04","2024-10-04","2024-10-04","2024-10-04",
            "2024-10-05","2024-10-05","2024-10-05","2024-10-05","2024-10-05","2024-10-05",
            "2024-10-06","2024-10-06","2024-10-06","2024-10-06","2024-10-06","2024-10-06",

            "2024-11-20","2024-11-20","2024-11-20","2024-11-20","2024-11-20",
            "2024-11-10","2024-11-10","2024-11-10","2024-11-10","2024-11-10","2024-11-10",
            "2024-11-03","2024-11-03","2024-11-03","2024-11-03","2024-11-03","2024-11-03",
            "2024-11-04","2024-11-04","2024-11-04","2024-11-04","2024-11-04",
            "2024-11-05","2024-11-05","2024-11-05","2024-11-05","2024-11-05","2024-11-05",
            "2024-11-06","2024-11-06","2024-11-06","2024-11-06","2024-11-06","2024-11-06",
    };

    private static final int[] mSchedulePeriodArray = {
            FORENOON, FORENOON, FORENOON, FORENOON, FORENOON,
            FORENOON, FORENOON, FORENOON, FORENOON, FORENOON, FORENOON,
            FORENOON, FORENOON, FORENOON, FORENOON, FORENOON, FORENOON,
            AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON,
            AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON,
            AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON,

            FORENOON, FORENOON, FORENOON, FORENOON, FORENOON,
            FORENOON, FORENOON, FORENOON, FORENOON, FORENOON, FORENOON,
            FORENOON, FORENOON, FORENOON, FORENOON, FORENOON, FORENOON,
            AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON,
            AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON,
            AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON, AFTERNOON
    };

    public static ArrayList<Schedule> getDefaultList() {
        ArrayList<Schedule> ScheduleList = new ArrayList<Schedule>();
        for (int i = 1; i <= mDoctorIdArray.length; ++i) {
            Schedule info = new Schedule();
            // 排班信息id
            info.setScheduleId(i);
            // 医生id
            info.setDoctorId(mDoctorIdArray[i-1]);
            // 科室id
            int DoctorId = Integer.parseInt(mDoctorIdArray[i-1]);
            info.setOfficeId(DoctorInfo.mOfficeIdArray[DoctorId-1]);
            // 排班日期
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = dateFormat.parse(mScheduleDateArray[i-1]);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            info.setScheduleDate(date);
            // 排班时间段
            info.setSchedulePeriod(mSchedulePeriodArray[i-1]);
            // 放号量
            info.setAppointmentNum(20);
            ScheduleList.add(info);
        }
        return ScheduleList;
    }

    public Schedule() {
        scheduleDate = null;
    }

    // Getters and Setters
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    @NonNull
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(@NonNull String doctorId) {
        this.doctorId = doctorId;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public int getSchedulePeriod() {
        return schedulePeriod;
    }

    public void setSchedulePeriod(int schedulePeriod) {
        this.schedulePeriod = schedulePeriod;
    }

    public int getAppointmentNum() {
        return appointmentNum;
    }

    public void setAppointmentNum(int appointmentNum) {
        this.appointmentNum = appointmentNum;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", officeId=" + officeId +
                ", doctorId='" + doctorId + '\'' +
                ", scheduleDate=" + scheduleDate +
                ", schedulePeriod=" + schedulePeriod +
                ", appointmentNum=" + appointmentNum +
                '}';
    }
}