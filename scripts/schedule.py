import mysql.connector
from mysql.connector import Error
import random
from datetime import datetime, timedelta
from tqdm import tqdm

# 数据库连接参数
db_config = {
    'host': 'localhost',  # 数据库主机
    'user': 'root',  # 数据库用户名
    'password': 'Ok030828',  # 数据库密码
    'database': 'db_hospitalSys'  # 数据库名称
}

# 随机生成排班日期
def generate_schedule_dates():
    start_date = datetime(2024, 10, 1)
    end_date = datetime(2024, 12, 31)
    delta = end_date - start_date
    return [start_date + timedelta(days=random.randint(0, delta.days)) for _ in range(20)]

# 从doctor_infos表中查询office_id
def get_office_id(cursor, doctor_id):
    cursor.execute("SELECT office_id FROM doctor_infos WHERE doctor_id = %s", (doctor_id,))
    result = cursor.fetchone()
    return result[0] if result else None

# 插入排班信息到数据库
def insert_schedule(cursor, schedule_id, office_id, doctor_id, schedule_date, schedule_period, appointment_num):
    sql = """  
    INSERT INTO Schedule (schedule_id, office_id, doctor_id, schedule_date, schedule_period, appointment_num)   
    VALUES (%s, %s, %s, %s, %s, %s)  
    """
    cursor.execute(sql, (schedule_id, office_id, doctor_id, schedule_date, schedule_period, appointment_num))

def main(total_schedules):
    try:
        # 连接到数据库
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()

            # 查询所有医生记录
            cursor.execute("SELECT user_id FROM Doctor")
            doctors = cursor.fetchall()
            doctor_ids = [doctor[0] for doctor in doctors]  # 提取医生ID列表

            # 随机生成排班日期
            schedule_dates = generate_schedule_dates()

            # 批量插入排班信息
            for i in tqdm(range(total_schedules), desc="插入排班信息记录", unit="条"):
                doctor_id = random.choice(doctor_ids)  # 随机选择医生ID
                office_id = get_office_id(cursor, doctor_id)  # 获取对应的office_id

                if office_id is None:
                    print(f"医生ID {doctor_id} 未找到对应的 office_id，跳过该医生。")
                    continue

                schedule_id = i + 1  # 这里可以根据需要设置schedule_id
                schedule_date = random.choice(schedule_dates)  # 随机选择排班日期
                schedule_period = random.choice([1, 2])  # 随机选择排班时段
                appointment_num = random.randint(1, 10)  # 随机生成放号量

                insert_schedule(cursor, schedule_id, office_id, doctor_id, schedule_date, schedule_period, appointment_num)

            # 提交事务
            connection.commit()
            print("排班信息插入成功。")

    except Error as e:
        print(f"错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

if __name__ == "__main__":
    total_schedules = 200  # 设置要插入的排班记录数量
    main(total_schedules)