import mysql.connector
from mysql.connector import Error
import random

# 数据库连接参数
db_config = {
    'host': 'localhost',  # 数据库主机
    'user': 'root',  # 数据库用户名
    'password': 'Ok030828',  # 数据库密码
    'database': 'db_hospitalSys'  # 数据库名称
}

# 科室名称数组
office_names = [
    "内科", "外科", "妇产科", "中医科", "康复医学科",
    "儿童保健科", "儿科", "感染科", "口腔科", "耳鼻喉科",
    "眼科", "皮肤科", "结核病科", "药剂科", "功能科",
    "影像科", "营养科"
]


# 随机生成科室介绍
def generate_intro():
    intros = [
        "本科室致力于提供优质的医疗服务。",
        "我们拥有一流的医疗设备和专业的医疗团队。",
        "为患者提供全面的健康管理服务。",
        "专注于疾病的预防和治疗。",
        "提供个性化的医疗方案和健康指导。"
    ]
    return random.choice(intros)


# 插入科室信息到数据库
def insert_office(cursor, office_id, name, intro):
    sql = "INSERT INTO Office (office_id, name, intro) VALUES (%s, %s, %s)"
    cursor.execute(sql, (office_id, name, intro))


def main():
    try:
        # 连接到数据库
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()

            # 批量插入科室信息
            num_offices = len(office_names)  # 要插入的科室数量

            for i in range(num_offices):
                name = random.choice(office_names)  # 随机选择科室名称
                intro = generate_intro()  # 生成科室介绍
                insert_office(cursor, i + 1, name, intro)

            # 提交事务
            connection.commit()
            print("科室信息插入成功。")

    except Error as e:
        print(f"错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()


if __name__ == "__main__":
    main()
