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


# 插入预约记录到数据库
def insert_appointment(cursor, appointment_id, patient_id, doctor_id, schedule_id, visit_date, visit_period, serial_num,
                       order_state, order_time):
    sql = """  
    INSERT INTO Appointment (appointment_id, patient_id, doctor_id, schedule_id, visit_date, visit_period, serial_num, order_state, order_time)   
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)  
    """
    cursor.execute(sql, (
    appointment_id, patient_id, doctor_id, schedule_id, visit_date, visit_period, serial_num, order_state, order_time))


def main(num_appointments):
    try:
        # 连接到数据库
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()

            # 查询所有患者和排班记录
            cursor.execute("SELECT user_id FROM Patient")
            patients = cursor.fetchall()

            cursor.execute("SELECT schedule_id, doctor_id, schedule_date, schedule_period FROM Schedule")
            schedules = cursor.fetchall()

            for i in tqdm(range(num_appointments), desc="插入预约记录", unit="条"):
                # 随机选择患者和排班记录
                patient_id = random.choice(patients)[0]
                schedule = random.choice(schedules)
                schedule_id = schedule[0]
                doctor_id = schedule[1]
                visit_date = schedule[2]
                visit_period = schedule[3]

                # 查询当前 schedule_id 下 order_state 不为 3 的记录数量
                cursor.execute("SELECT COUNT(*) FROM Appointment WHERE schedule_id = %s AND order_state != 3",
                               (schedule_id,))
                appointed_count = cursor.fetchone()[0]

                # serial_num 为 appointed_count + 1
                serial_num = appointed_count + 1

                # 随机选择 order_state
                order_state = random.choice([1, 2, 3])  # 1: 待看诊, 2: 已看诊, 3: 已取消

                # 确保 order_time 在 visit_date 之前
                order_time = visit_date - timedelta(days=random.randint(1, 30))  # 随机生成一个在 visit_date 之前的时间

                # 使用 i + 1 作为 appointment_id
                appointment_id = i + 1

                # 插入预约记录
                insert_appointment(cursor, appointment_id, patient_id, doctor_id, schedule_id, visit_date, visit_period,
                                   serial_num, order_state, order_time)

            # 提交事务
            connection.commit()
            print(f"{num_appointments} 条预约记录插入成功。")

    except Error as e:
        print(f"错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()


if __name__ == "__main__":
    # 设置要插入的预约记录条数
    num_appointments = 100  # 修改为你需要插入的记录条数

    main(num_appointments)
