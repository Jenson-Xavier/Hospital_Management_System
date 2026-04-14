package com.example.hospitalsystem.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(indices = {@Index(value = "name", unique = true)})
public class Office {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "office_id")
    private int officeId;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "intro")
    private String intro;

    public Office() {
        name = "内科";
    }

    // 科室名称
    public static final String[] mNameArray = {
            "内科", "外科", "妇产科", "中医科", "康复医学科", "儿童保健科", "儿科", "感染科", "口腔科", "耳鼻喉科",
            "眼科", "皮肤科", "结核病科", "药剂科", "功能科", "影像科", "营养科"
    };

    // 科室介绍
    private static final String[] mDescArray = {
            "内科主要负责诊断和治疗内科疾病，包括心血管、呼吸、消化、内分泌、血液、肾脏等系统的疾病。内科医生通过病史采集、体格检查和实验室检查等手段，制定个性化的治疗方案，帮助患者恢复健康。",
            "外科专注于通过手术手段治疗疾病和损伤。外科医生进行手术操作以修复、切除或替换身体的病变组织或器官，涉及领域包括普通外科、骨科、神经外科、心胸外科等。",
            "妇产科负责女性生殖系统的健康管理，包括妊娠、分娩和产后护理。妇产科医生提供产前检查、分娩指导、产后恢复以及妇科疾病的诊断和治疗。",
            "中医科运用中医理论和方法进行疾病的预防和治疗。通过中药、针灸、推拿、拔罐等传统疗法，中医科医生调理患者的身体状态，促进自愈能力。",
            "康复医学科致力于帮助患者恢复身体功能，提高生活质量。通过物理治疗、作业治疗、语言治疗等手段，康复医学科为患者提供个性化的康复计划。",
            "儿童保健科关注儿童的生长发育和健康管理。提供疫苗接种、健康检查、营养指导等服务，确保儿童在生理和心理上的健康成长。",
            "儿科专注于儿童和青少年的疾病诊断和治疗。儿科医生处理从新生儿到青少年时期的各种健康问题，包括感染、慢性病、发育障碍等。",
            "感染科负责传染病的诊断、治疗和预防。感染科医生处理细菌、病毒、真菌等病原体引起的感染性疾病，制定隔离和治疗方案。",
            "口腔科专注于口腔健康，包括牙齿、牙龈、口腔黏膜等的疾病预防和治疗。口腔科医生提供洁牙、补牙、拔牙、正畸等服务。",
            "耳鼻喉科负责耳、鼻、喉及相关结构的疾病诊断和治疗。耳鼻喉科医生处理听力障碍、鼻炎、咽喉炎等常见问题。",
            "眼科专注于眼部疾病的诊断和治疗。眼科医生处理视力问题、白内障、青光眼、眼外伤等，提供视力矫正和手术治疗。",
            "皮肤科负责皮肤、毛发、指甲的健康管理。皮肤科医生诊断和治疗皮炎、湿疹、痤疮、银屑病等皮肤病，提供美容和激光治疗。",
            "结核病科专门处理结核病的诊断、治疗和预防。结核病科医生制定抗结核治疗方案，监测患者的治疗进展，防止疾病传播。",
            "药剂科负责药品的管理和使用指导。药剂师提供药物咨询、配药服务，确保药物的安全、有效使用，并参与临床药物研究。",
            "功能科通过功能检查评估身体各系统的健康状况。功能科医生进行心电图、肺功能、脑电图等检查，辅助临床诊断。",
            "影像科利用医学影像技术进行疾病诊断。影像科医生通过X光、CT、MRI、超声等影像手段，提供精准的诊断信息。",
            "营养科关注个体的营养状况和饮食管理。营养师根据患者的健康需求，制定个性化的营养计划，促进健康和疾病康复。"
    };

    // 获取默认的科室信息列表
    public static ArrayList<Office> getDefaultList() {
        ArrayList<Office> officeList = new ArrayList<Office>();
        for (int i = 1; i <= mNameArray.length; ++i) {
            Office info = new Office();
            info.setOfficeId(i);
            info.setName(mNameArray[i-1]);
            info.setIntro(mDescArray[i-1]);
            officeList.add(info);
        }
        return officeList;
    }


    public static String[] getOfficeNames() {
        return mNameArray;
    }

    // Getters and Setters
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    @Override
    public String toString() {
        return "Office{" +
                "officeId=" + officeId +
                ", name='" + name + '\'' +
                ", intro='" + intro + '\'' +
                '}';
    }
}
