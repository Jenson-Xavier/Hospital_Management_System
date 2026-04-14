package com.example.hospitalsystem.db_Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "doctor_infos",
        foreignKeys = {
                @ForeignKey(entity = Doctor.class,
                        parentColumns = "userId",
                        childColumns = "doctor_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Office.class,
                        parentColumns = "office_id",
                        childColumns = "office_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("doctor_id"), @Index("office_id")})
public class DoctorInfo {

    public static final int MALE = 1;
    public static final int FEMALE = 2;

    public static final String[] ganderStr = {
      "", "男", "女"
    };

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "doctorInfo_id")
    private int doctorInfoId;

    @ColumnInfo(name = "doctor_id")
    private String doctorId;

    @ColumnInfo(name = "office_id")
    private int officeId;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "room")
    @NonNull
    private String room;

    @ColumnInfo(name = "gender")
    private int gender;

    @ColumnInfo(name = "seniority")
    private int seniority;

    @ColumnInfo(name = "intro")
    private String intro;

    // 预先插入若干医生信息
    private static final String[] mDoctorIdArray = {
            "00000000001", "00000000002", "00000000003", "00000000004", "00000000005",
            "00000000006", "00000000007", "00000000008", "00000000009", "00000000010", "00000000011",
            "00000000012", "00000000013", "00000000014", "00000000015", "00000000016", "00000000017"
    };

    public static final int[] mOfficeIdArray = {
            1, 2, 1, 3, 4,
            5, 2, 1, 10, 12,
            15, 7, 8, 9, 10,
            14, 13
    };

    // 医生姓名列表
    private static final String[] mDoctorNameArray = {
            "张伟", "李娜", "王芳", "刘洋", "陈杰",
            "杨静", "赵强", "黄丽", "周涛", "吴敏",
            "徐磊", "孙婷", "马超", "朱莉", "何伟",
            "高峰", "林娜"
    };

    // 医生科室列表
    private static final String[] mOfficeArray = {
            "内科", "外科", "内科", "中医科", "儿科",
            "耳鼻喉科", "皮肤科", "眼科", "内科", "影像科",
            "外科", "儿科", "药剂科", "口腔科", "眼科",
            "康复医学科", "儿童保健科"
    };

    // 医生性别列表
    private static final int[] mGenderArray = {
            MALE, MALE, FEMALE, MALE, FEMALE,
            MALE, FEMALE, FEMALE, MALE, MALE,
            FEMALE, MALE, MALE, MALE, FEMALE,
            MALE, FEMALE
    };

    // 医生工龄列表
    private static final int[] mSeniorityArray = {
            1, 2, 3, 4, 5,
            6, 7, 8, 9, 10,
            11, 12, 13, 14, 15,
            16, 17
    };

    // 医生介绍列表
    private static final String[] mIntroArray = {
            "一位内科医生，专注于高血压和高脂血症的管理。他通过生活方式干预和药物治疗帮助患者控制病情。",
            "一名心胸外科医生，专注于心脏和肺部手术。他的高超技术和细致的术后护理赢得了患者的信任。",
            "一位经验丰富的心脏病专家，拥有超过15年的临床经验。他专注于心血管疾病的诊断和治疗，致力于为患者提供最优质的医疗服务。",
            "一位知名的骨科医生，专注于关节置换和运动损伤的治疗。他的手术技术精湛，深受患者信赖。",
            "一名内分泌科专家，特别擅长糖尿病和甲状腺疾病的管理。他致力于通过个性化的治疗方案帮助患者改善生活质量。",
            "位妇产科医生，拥有丰富的产科经验。她以其细致入微的产前和产后护理而闻名。",
            "一名精神科医生，专注于抑郁症和焦虑症的治疗。他通过心理治疗和药物治疗相结合的方法帮助患者恢复心理健康。",
            "一位皮肤科专家，擅长治疗各种皮肤病，包括湿疹、牛皮癣和痤疮。他不断追求最新的治疗技术以提高疗效。",
            "一名眼科医生，专注于白内障和青光眼的手术治疗。他的精准手术技术为无数患者带来了光明。",
            "一位肿瘤科医生，专注于乳腺癌和肺癌的治疗。他通过多学科团队合作，为患者提供全面的治疗方案。",
            "一名耳鼻喉科专家，擅长耳部和鼻部手术。他的细致和专业赢得了患者的高度评价。",
            "一位肾脏科医生，专注于慢性肾病和透析治疗。他致力于通过创新的治疗方法改善患者的生活质量。",
            "一名神经科医生，专注于中风和癫痫的治疗。他通过先进的神经影像技术为患者提供精准的诊断。",
            "一位胃肠科专家，擅长胃肠道疾病的内镜检查和治疗。他的专业知识帮助许多患者恢复健康。",
            "一位麻醉科医生，拥有丰富的手术麻醉经验。他的精准麻醉技术确保了手术的安全和患者的舒适。",
            "一名放射科医生，专注于影像诊断和介入治疗。他通过先进的影像技术为临床医生提供重要的诊断信息。",
            "一位泌尿科专家，擅长泌尿系统疾病的微创手术。他的创新技术为患者带来了更快的康复。"
    };

    public static ArrayList<DoctorInfo> getDefaultList() {
        ArrayList<DoctorInfo> DoctorInfoList = new ArrayList<DoctorInfo>();
        for (int i = 1; i <= mDoctorIdArray.length; ++i) {
            DoctorInfo info = new DoctorInfo();
            info.setDoctorInfoId(i);
            info.setDoctorId(mDoctorIdArray[i-1]);
            info.setOfficeId(mOfficeIdArray[i-1]);
            info.setName(mDoctorNameArray[i-1]);
            info.setGender(mGenderArray[i-1]);
            info.setRoom(Office.mNameArray[mOfficeIdArray[i-1]]);
            info.setSeniority(mSeniorityArray[i-1]);
            info.setIntro(mIntroArray[i-1]);
            DoctorInfoList.add(info);
        }
        return DoctorInfoList;
    }

    public DoctorInfo() {
        room = "内科";
        name = "张伟";
    }

    // Getters and Setters
    public int getDoctorInfoId() {
        return doctorInfoId;
    }

    public void setDoctorInfoId(int doctorInfoId) {
        this.doctorInfoId = doctorInfoId;
    }

    @NonNull
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(@NonNull String doctorId) {
        this.doctorId = doctorId;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getSeniority() {
        return seniority;
    }

    public void setSeniority(int seniority) {
        this.seniority = seniority;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    @Override
    public String toString() {
        return "DoctorInfo{" +
                "doctorInfoId=" + doctorInfoId +
                ", doctorId='" + doctorId + '\'' +
                ", officeId=" + officeId +
                ", name='" + name + '\'' +
                ", room='" + room + '\'' +
                ", gender=" + gender +
                ", seniority=" + seniority +
                ", intro='" + intro + '\'' +
                '}';
    }
}