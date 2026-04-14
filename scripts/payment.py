import mysql.connector
from mysql.connector import Error
import random
from datetime import datetime
from tqdm import tqdm

# 数据库连接参数
db_config = {
    'host': 'localhost',  # 数据库主机
    'user': 'root',  # 数据库用户名
    'password': 'Ok030828',  # 数据库密码
    'database': 'db_hospitalSys'  # 数据库名称
}


# 插入支付记录到数据库
def insert_payment(cursor, payment_id, appointment_id, patient_id, payment_amount, pay_time):
    sql = """  
    INSERT INTO Payment (payment_id, appointment_id, patient_id, payment_amount, pay_time)   
    VALUES (%s, %s, %s, %s, %s)  
    """
    cursor.execute(sql, (payment_id, appointment_id, patient_id, payment_amount, pay_time))


def main():
    try:
        # 连接到数据库
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()

            # 查询所有预约记录
            cursor.execute("SELECT appointment_id, patient_id, order_time FROM Appointment")
            appointments = cursor.fetchall()

            for appointment in tqdm(appointments, desc="插入支付记录", unit="条"):
                appointment_id = appointment[0]
                patient_id = appointment[1]
                pay_time = appointment[2]

                # 检查该预约是否已有支付记录
                cursor.execute("SELECT COUNT(*) FROM Payment WHERE appointment_id = %s", (appointment_id,))
                payment_exists = cursor.fetchone()[0]

                if payment_exists > 0:
                    print(f"预约记录 {appointment_id} 已存在支付记录，跳过。")
                    continue

                # 随机生成支付金额（大于0）
                payment_amount = round(random.uniform(1.0, 100.0), 2)  # 随机生成 1.0 到 1000.0 之间的金额

                # 使用 i + 1 作为 payment_id
                payment_id = appointment_id  # 这里假设 payment_id 与 appointment_id 一致

                # 插入支付记录
                insert_payment(cursor, payment_id, appointment_id, patient_id, payment_amount, pay_time)

            # 提交事务
            connection.commit()
            print("支付记录插入成功。")

    except Error as e:
        print(f"错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()


if __name__ == "__main__":
    main()
