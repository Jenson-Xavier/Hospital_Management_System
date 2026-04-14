import mysql.connector
from mysql.connector import Error
import random
from tqdm import tqdm

# 数据库连接参数
db_config = {
    'host': 'localhost',  # 数据库主机
    'user': 'root',  # 数据库用户名
    'password': 'Ok030828',  # 数据库密码
    'database': 'db_hospitalSys'  # 数据库名称
}

# 随机生成医生姓名
def generate_name():
    first_names = ["张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴", "谢", "钱", "孙", "沈", "肖", "林", "韩", "汤", "魏"]
    last_names = ["伟", "芳", "娜", "敏", "静", "磊", "强", "洋", "军", "涛", "雷", "佳", "明", "思", "文", "骏", "鑫", "国", "韬"]
    last_names_2 = ["伟", "芳", "娜", "敏", "静", "磊", "强", "洋", "军", "涛", "雷", "佳", "明", "思", "文", "骏", "鑫", "国", "韬", "洋", "博", "风", "学", "民", ""]
    return random.choice(first_names) + random.choice(last_names) + random.choice(last_names_2)

# 随机生成医生介绍
def generate_intro():
    intros = [
        "具有丰富的临床经验，擅长各种常见病的诊治。",
        "专注于患者的健康管理，提供个性化的医疗方案。",
        "在医疗领域有多年的工作经验，致力于为患者提供优质服务。",
        "热爱医学，关注患者的每一个细节。",
        "积极参与医学研究，不断提升专业技能。"
    ]
    return random.choice(intros)

# 插入医生信息到数据库
def insert_doctor_info(cursor, doctor_info_id, doctor_id, office_id, name, gender, seniority, intro):
    sql = """  
    INSERT INTO doctor_infos (doctor_info_id, doctor_id, office_id, name, gender, seniority, intro)   
    VALUES (%s, %s, %s, %s, %s, %s, %s)  
    """
    cursor.execute(sql, (doctor_info_id, doctor_id, office_id, name, gender, seniority, intro))

def main():
    try:
        # 连接到数据库
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()

            # 查询所有医生记录
            cursor.execute("SELECT user_id FROM Doctor")
            doctors = cursor.fetchall()

            # 查询所有科室记录
            cursor.execute("SELECT office_id FROM Office")
            offices = cursor.fetchall()

            office_ids = [office[0] for office in offices]  # 提取 office_id 列表
            i = 1
            # 批量插入医生信息
            for doctor in tqdm(doctors, desc="插入医生信息", unit="条"):
                doctor_id = doctor[0]  # 获取医生ID
                # 检查该医生是否已存在 doctor_info 记录
                cursor.execute("SELECT COUNT(*) FROM doctor_infos WHERE doctor_id = %s", (doctor_id,))
                exists = cursor.fetchone()[0]

                if exists > 0:
                    print(f"医生 {doctor_id} 的信息已存在，跳过插入。")
                    continue

                doctor_info_id = i  # 使用医生数量加一作为 doctor_info_id
                office_id = random.choice(office_ids)  # 随机选择 office_id
                name = generate_name()  # 生成医生姓名
                gender = random.choice([1, 2])  # 随机选择性别
                seniority = random.randint(1, 30)  # 随机生成工龄（1到30年）
                intro = generate_intro()  # 生成医生介绍

                insert_doctor_info(cursor, doctor_info_id, doctor_id, office_id, name, gender, seniority, intro)
                i += 1

            # 提交事务
            connection.commit()
            print("医生信息插入成功。")

    except Error as e:
        print(f"错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

if __name__ == "__main__":
    main()