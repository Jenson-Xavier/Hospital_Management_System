import mysql.connector
from mysql.connector import Error
import random
import string

# Database connection parameters
db_config = {
    'host': 'localhost',  # Change to your database host
    'user': 'root',  # Change to your database username
    'password': 'Ok030828',  # Change to your database password
    'database': 'db_hospitalSys'  # Change to your database name
}


# Function to generate a random patient account (11-digit phone number or email)
def generate_patient_account(i, type):
    if type == "phone":
        # return f"{random.randint(10000000000, 19999999999)}"
        return f"{10000000000+i}"
    else:
        # return f"user{random.randint(1000, 9999)}@example.com"
        return f"user{1000+i}@example.com"


# Function to generate a random doctor account (8-digit number)
def generate_doctor_account(i):
    # return f"{random.randint(10000000, 99999999)}"
    return f"{10000000 + i}"


# Function to generate a random admin account (starts with 'admin')
def generate_admin_account(index):
    return f"admin{index}"


# Function to insert users into the database
def insert_users(cursor, user_type, account, password):
    sql = "INSERT INTO User (user_id, password, user_type) VALUES (%s, %s, %s)"
    cursor.execute(sql, (account, password, user_type))


def insert_patients(cursor, account):
    sql = "INSERT INTO Patient (user_id) VALUES (%s)"
    cursor.execute(sql, ([account]))


def insert_doctors(cursor, account):
    sql = "INSERT INTO Doctor (user_id) VALUES (%s)"
    cursor.execute(sql, ([account]))


def insert_admins(cursor, account):
    sql = "INSERT INTO Admin (user_id) VALUES (%s)"
    cursor.execute(sql, ([account]))


def main():
    try:
        # Connect to the database
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()

            # Password for all users
            password = "Ok123123"

            # Batch insert records
            num_patients = 20  # Number of patients to insert
            num_doctors = 20  # Number of doctors to insert
            num_admins = 1  # Number of admins to insert

            # Insert patients
            for i in range(num_patients):
                account = generate_patient_account(i, "phone")
                insert_users(cursor, 1, account, password)
                insert_patients(cursor, account)

            # Insert doctors
            for i in range(num_doctors):
                account = generate_doctor_account(i)
                insert_users(cursor, 2, account, password)
                insert_doctors(cursor, account)

            # Insert admins
            for i in range(1, num_admins + 1):
                account = generate_admin_account(i)
                insert_users(cursor, 3, account, password)
                insert_admins(cursor, account)

            # Commit the transaction
            connection.commit()
            print("Users inserted successfully.")

    except Error as e:
        print(f"Error: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()


if __name__ == "__main__":
    main()
