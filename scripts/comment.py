import mysql.connector
from mysql.connector import Error
import random
from datetime import datetime, timedelta
from tqdm import tqdm  # 导入 tqdm 库

# 数据库连接参数
db_config = {
    'host': 'localhost',  # 数据库主机
    'user': 'root',  # 数据库用户名
    'password': 'Ok030828',  # 数据库密码
    'database': 'db_hospitalSys'  # 数据库名称
}

# 随机生成评论内容
def generate_comment_text():
    comments = [
        "医生态度很好，服务很周到。",
        "排队时间太长，希望能改善。",
        "医生很专业，给了我很好的建议。",
        "医院环境不错，但设施有些老旧。",
        "总体体验还不错，值得推荐。",
        "我对这次就诊不太满意，希望下次能改进。",
        "医生很耐心，解答了我所有的问题。",
        "希望能增加更多的医生，减少等待时间。"
    ]
    return random.choice(comments)

# 插入评论记录到数据库
def insert_comment(cursor, comment_id, patient_id, doctor_id, publish_time, comment_text, audit):
    sql = """  
    INSERT INTO Comment (comment_id, patient_id, doctor_id, publish_time, comment_text, audit)   
    VALUES (%s, %s, %s, %s, %s, %s)  
    """
    cursor.execute(sql, (comment_id, patient_id, doctor_id, publish_time, comment_text, audit))

def main(num_comments):
    try:
        # 连接到数据库
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()

            # 查询所有患者和医生记录
            cursor.execute("SELECT user_id FROM Patient")
            patients = cursor.fetchall()

            cursor.execute("SELECT user_id FROM Doctor")
            doctors = cursor.fetchall()

            for i in tqdm(range(num_comments), desc="插入评论记录", unit="条"):
                # 随机选择患者和医生
                patient_id = random.choice(patients)[0]
                doctor_id = random.choice(doctors)[0]

                # 随机生成发布时间
                publish_time = datetime.now() - timedelta(days=random.randint(0, 30))  # 随机生成过去30天内的时间

                # 生成评论内容
                comment_text = generate_comment_text()

                # 随机选择审核状态
                audit = random.choice([1, 2, 3])

                # 使用 i + 1 作为 comment_id
                comment_id = i + 1

                # 插入评论记录
                insert_comment(cursor, comment_id, patient_id, doctor_id, publish_time, comment_text, audit)

            # 提交事务
            connection.commit()
            print(f"{num_comments} 条评论记录插入成功。")

    except Error as e:
        print(f"错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

if __name__ == "__main__":
    # 设置要插入的评论记录条数
    num_comments = 100  # 修改为你需要插入的记录条数

    main(num_comments)