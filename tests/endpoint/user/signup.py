import os
import requests
import time
import json
import uuid
import random
import string
import pytest

USER_BASE_URL = os.getenv("user_base_url")
if not USER_BASE_URL:
    raise ValueError("Переменная окружения 'user_base_url' не установлена.")

SIGNUP_URL = f"{USER_BASE_URL}/signup"

def print_test_result(func):
    """
    Декоратор для вывода результата теста в консоль.
    При успешном выполнении выводит PASSED, при ошибке – FAILED с сообщением.
    """
    def wrapper(*args, **kwargs):
        try:
            func(*args, **kwargs)
            print(f"{func.__name__}: PASSED")
        except AssertionError as e:
            print(f"{func.__name__}: FAILED - {e}")
            raise
    return wrapper

def log_result(test_name: str, input_data: dict, expected_status: int,
               actual_status: int, response_body, response_time: float):
    """Логирует результаты теста в файл test_results.json."""
    result = {
        "test_name": test_name,
        "input_data": input_data,
        "expected_status": expected_status,
        "actual_status": actual_status,
        "response_body": response_body,
        "response_time": response_time
    }
    with open("test_results.json", "a", encoding="utf-8") as f:
        f.write(json.dumps(result, ensure_ascii=False) + "\n")

def perform_post_request(input_data: dict):
    """Отправляет POST-запрос на эндпоинт регистрации и возвращает время, статус и тело ответа."""
    headers = {"Content-Type": "application/json"}
    start_time = time.time()
    response = requests.post(SIGNUP_URL, json=input_data, headers=headers)
    elapsed_time = time.time() - start_time
    try:
        response_body = response.json()
    except Exception:
        response_body = None
    return response.status_code, response_body, elapsed_time

def random_password(length: int = 8) -> str:
    """Генерирует случайный пароль указанной длины."""
    alphabet = string.ascii_lowercase + string.digits
    return ''.join(random.choices(alphabet, k=length))

def random_email(valid: bool = True) -> str:
    """Генерирует случайный email. Если valid=False, email будет иметь неверный формат."""
    unique_part = uuid.uuid4().hex[:8]
    if valid:
        return f"{unique_part}@mail.ru"
    else:
        return f"{unique_part}mail.ru"  # пропущен символ @

# 1. Корректная регистрация
@print_test_result
def test_signup_valid():
    """
    Входные данные:
      - password: случайный пароль
      - password_repeat: тот же случайный пароль
      - mail: уникальный корректный email
    Ожидаемый результат:
      - HTTP-статус 201 (created)
      - В теле ответа присутствует объект user с полями:
        id, username, email, first_name, last_name, email_verified, enabled
    """
    password = random_password(10)
    email = random_email(valid=True)
    input_data = {
        "password": password,
        "password_repeat": password,
        "mail": email
    }
    expected_status = 201
    status, response_body, elapsed_time = perform_post_request(input_data)
    log_result("test_signup_valid", input_data, expected_status, status, response_body, elapsed_time)
    
    assert status == expected_status, f"Ожидаемый статус {expected_status}, получен {status}"
    if response_body and "body" in response_body:
        user = response_body["body"].get("user")
        assert user is not None, "Поле user отсутствует в теле ответа"
        for field in ["id", "username", "email", "first_name", "last_name", "email_verified", "enabled"]:
            assert field in user, f"Поле {field} отсутствует в объекте user"

# 2. Неверный формат email
@print_test_result
def test_signup_invalid_email():
    """
    Входные данные:
      - password: случайный пароль
      - password_repeat: тот же пароль
      - mail: уникальный некорректный email
    Ожидаемый результат:
      - HTTP-статус 400 (bad request)
    """
    password = random_password(10)
    email = random_email(valid=False)
    input_data = {
        "password": password,
        "password_repeat": password,
        "mail": email
    }
    expected_status = 400
    status, response_body, elapsed_time = perform_post_request(input_data)
    log_result("test_signup_invalid_email", input_data, expected_status, status, response_body, elapsed_time)
    
    assert status == expected_status, f"Ожидаемый статус {expected_status}, получен {status}"

# 3. Пустой пароль
@print_test_result
def test_signup_blank_password():
    """
    Входные данные:
      - password: ""
      - password_repeat: ""
      - mail: уникальный корректный email
    Ожидаемый результат:
      - HTTP-статус 400 (bad request)
    """
    email = random_email(valid=True)
    input_data = {
        "password": "",
        "password_repeat": "",
        "mail": email
    }
    expected_status = 400
    status, response_body, elapsed_time = perform_post_request(input_data)
    log_result("test_signup_blank_password", input_data, expected_status, status, response_body, elapsed_time)
    
    assert status == expected_status, f"Ожидаемый статус {expected_status}, получен {status}"

# 4. Несовпадение паролей
@print_test_result
def test_signup_password_mismatch():
    """
    Входные данные:
      - password: случайный пароль
      - password_repeat: другой случайный пароль
      - mail: уникальный корректный email
    Ожидаемый результат:
      - HTTP-статус 400 (bad request)
    """
    password1 = random_password(10)
    password2 = random_password(10)
    email = random_email(valid=True)
    input_data = {
        "password": password1,
        "password_repeat": password2,
        "mail": email
    }
    expected_status = 400
    status, response_body, elapsed_time = perform_post_request(input_data)
    log_result("test_signup_password_mismatch", input_data, expected_status, status, response_body, elapsed_time)
    
    assert status == expected_status, f"Ожидаемый статус {expected_status}, получен {status}"

# 5. Пароль слишком короткий (< 4 символов)
@print_test_result
def test_signup_password_too_short():
    """
    Входные данные:
      - password: случайная строка из 3 символов
      - password_repeat: такая же строка
      - mail: уникальный корректный email
    Ожидаемый результат:
      - HTTP-статус 400 (bad request)
    """
    password = ''.join(random.choices(string.ascii_lowercase, k=3))
    email = random_email(valid=True)
    input_data = {
        "password": password,
        "password_repeat": password,
        "mail": email
    }
    expected_status = 400
    status, response_body, elapsed_time = perform_post_request(input_data)
    log_result("test_signup_password_too_short", input_data, expected_status, status, response_body, elapsed_time)
    
    assert status == expected_status, f"Ожидаемый статус {expected_status}, получен {status}"

# 6. Пароль слишком длинный (> 50 символов)
@print_test_result
def test_signup_password_too_long():
    """
    Входные данные:
      - password: случайная строка из 51 символа
      - password_repeat: такая же строка
      - mail: уникальный корректный email
    Ожидаемый результат:
      - HTTP-статус 400 (bad request)
    """
    password = ''.join(random.choices(string.ascii_lowercase + string.digits, k=51))
    email = random_email(valid=True)
    input_data = {
        "password": password,
        "password_repeat": password,
        "mail": email
    }
    expected_status = 400
    status, response_body, elapsed_time = perform_post_request(input_data)
    log_result("test_signup_password_too_long", input_data, expected_status, status, response_body, elapsed_time)
    
    assert status == expected_status, f"Ожидаемый статус {expected_status}, получен {status}"

# 7. Пустой email
@print_test_result
def test_signup_blank_email():
    """
    Входные данные:
      - password: случайный пароль
      - password_repeat: такой же пароль
      - mail: ""
    Ожидаемый результат:
      - HTTP-статус 400 (bad request)
    """
    password = random_password(10)
    input_data = {
        "password": password,
        "password_repeat": password,
        "mail": ""
    }
    expected_status = 400
    status, response_body, elapsed_time = perform_post_request(input_data)
    log_result("test_signup_blank_email", input_data, expected_status, status, response_body, elapsed_time)
    
    assert status == expected_status, f"Ожидаемый статус {expected_status}, получен {status}"

# 8. Email превышает максимальную длину (более 255 символов)
@print_test_result
def test_signup_email_too_long():
    """
    Входные данные:
      - password: случайный пароль
      - password_repeat: такой же пароль
      - mail: случайно сгенерированная строка, длина которой превышает 255 символов, с добавлением домена
    Ожидаемый результат:
      - HTTP-статус 400 (bad request)
    """
    password = random_password(10)
    # Генерируем случайную строку длиной 250 символов и добавляем домен, итоговая длина > 255
    local_part = ''.join(random.choices(string.ascii_lowercase, k=250))
    email = f"{local_part}@mail.ru"
    input_data = {
        "password": password,
        "password_repeat": password,
        "mail": email
    }
    expected_status = 400
    status, response_body, elapsed_time = perform_post_request(input_data)
    log_result("test_signup_email_too_long", input_data, expected_status, status, response_body, elapsed_time)
    
    assert status == expected_status, f"Ожидаемый статус {expected_status}, получен {status}"

# 9. Отсутствует обязательное поле (например, password_repeat)
@print_test_result
def test_signup_missing_field():
    """
    Входные данные:
      - password: случайный пароль
      - mail: уникальный корректный email
      (Отсутствует поле password_repeat)
    Ожидаемый результат:
      - HTTP-статус 400 (bad request)
    """
    password = random_password(10)
    email = random_email(valid=True)
    input_data = {
        "password": password,
        "mail": email
    }
    expected_status = 400
    status, response_body, elapsed_time = perform_post_request(input_data)
    log_result("test_signup_missing_field", input_data, expected_status, status, response_body, elapsed_time)
    
    assert status == expected_status, f"Ожидаемый статус {expected_status}, получен {status}"

if __name__ == "__main__":
    pytest.main([__file__])