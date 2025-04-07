import os
import requests
import time
import json
import uuid
import random
import string
import pytest
from dotenv import load_dotenv

# Загружаем переменные из .env файла
load_dotenv()

# Параметры авторизации из .env
KC_TOKEN_ENDPOINT = os.getenv("kc_token_endpoint")        # например, https://api.lockboxes.ru:8443/realms/lockbox/protocol/openid-connect/token
KC_PUBLIC_CLIENT  = os.getenv("kc_public_client")         # например, public-client
KC_DEFAULT_USER   = os.getenv("kc_default_user")          # например, aboba
KC_DEFAULT_PASS   = os.getenv("kc_default_pass")          # например, aboba
KC_DEFAULT_MAIL   = os.getenv("kc_default_mail")          # например, aboba@mail.ru
USER_BASE_URL     = os.getenv("user_base_url")            # например, https://api.lockboxes.ru:8445/accounts

if not USER_BASE_URL:
    raise ValueError("Переменная 'user_base_url' не установлена в .env файле.")

# URL защищённых эндпоинтов
GET_ME_URL         = f"{USER_BASE_URL}/me"
GET_SESSIONS_URL   = f"{USER_BASE_URL}/sessions"
RESET_PASSWORD_URL = f"{USER_BASE_URL}/reset-password"
PATCH_ME_URL       = f"{USER_BASE_URL}/me"

def print_test_result(func):
    """
    Декоратор для вывода результата теста в консоль.
    При успешном выполнении выводит PASSED, при ошибке — FAILED с сообщением.
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
    """Логирует результаты теста в файл test_results_protected.json."""
    result = {
        "test_name": test_name,
        "input_data": input_data,
        "expected_status": expected_status,
        "actual_status": actual_status,
        "response_body": response_body,
        "response_time": response_time
    }
    with open("test_results_protected.json", "a", encoding="utf-8") as f:
        f.write(json.dumps(result, ensure_ascii=False) + "\n")

def perform_get_request(url: str, headers: dict):
    start_time = time.time()
    response = requests.get(url, headers=headers)
    elapsed_time = time.time() - start_time
    try:
        response_body = response.json()
    except Exception:
        response_body = None
    return response.status_code, response_body, elapsed_time

def perform_post_request(url: str, data, headers: dict):
    start_time = time.time()
    response = requests.post(url, headers=headers, data=data)
    elapsed_time = time.time() - start_time
    try:
        response_body = response.json()
    except Exception:
        response_body = None
    return response.status_code, response_body, elapsed_time

def perform_patch_request(url: str, json_data: dict, headers: dict):
    start_time = time.time()
    response = requests.patch(url, headers=headers, json=json_data)
    elapsed_time = time.time() - start_time
    try:
        response_body = response.json()
    except Exception:
        response_body = None
    return response.status_code, response_body, elapsed_time

def get_tokens():
    """
    Получает пару access и refresh токенов, используя данные из .env файла.
    """
    payload = f"grant_type=password&client_id={KC_PUBLIC_CLIENT}&username={KC_DEFAULT_MAIL}&password={KC_DEFAULT_PASS}"
    headers = {"Content-Type": "application/x-www-form-urlencoded"}
    status, response_body, _ = perform_post_request(KC_TOKEN_ENDPOINT, data=payload, headers=headers)
    assert status == 200, f"Ошибка получения токена, статус {status}"
    access_token = response_body.get("access_token")
    refresh_token = response_body.get("refresh_token")
    assert access_token, "Нет access_token в ответе"
    assert refresh_token, "Нет refresh_token в ответе"
    return access_token, refresh_token

def random_string(length=8):
    return ''.join(random.choices(string.ascii_lowercase + string.digits, k=length))

# ===================== Тесты для GET /me =====================

@print_test_result
def test_get_me_missing_token():
    """GET /me без токена."""
    headers = {}
    status, response_body, elapsed_time = perform_get_request(GET_ME_URL, headers)
    log_result("test_get_me_missing_token", {"endpoint": GET_ME_URL, "headers": headers}, 401, status, response_body, elapsed_time)
    # Ожидаем 401 или 403 (Unauthorized / Forbidden)
    assert status in [401, 403], f"Ожидаемый статус 401 или 403, получен {status}"

@print_test_result
def test_get_me_invalid_token():
    """GET /me с неверным токеном."""
    headers = {"Authorization": "Bearer invalid-token"}
    status, response_body, elapsed_time = perform_get_request(GET_ME_URL, headers)
    log_result("test_get_me_invalid_token", {"endpoint": GET_ME_URL, "headers": headers}, 401, status, response_body, elapsed_time)
    assert status in [401, 403], f"Ожидаемый статус 401 или 403, получен {status}"

# ===================== Тесты для GET /sessions =====================

@print_test_result
def test_get_sessions_valid():
    """GET /sessions с валидным токеном."""
    access_token, _ = get_tokens()
    headers = {"Authorization": f"Bearer {access_token}"}
    status, response_body, elapsed_time = perform_get_request(GET_SESSIONS_URL, headers)
    log_result("test_get_sessions_valid", {"endpoint": GET_SESSIONS_URL}, 200, status, response_body, elapsed_time)
    assert status == 200, f"Ожидаемый статус 200, получен {status}"
    assert response_body and "body" in response_body
    body = response_body["body"]
    assert "sessions" in body and "credentials" in body

@print_test_result
def test_get_sessions_missing_token():
    """GET /sessions без токена."""
    headers = {}
    status, response_body, elapsed_time = perform_get_request(GET_SESSIONS_URL, headers)
    log_result("test_get_sessions_missing_token", {"endpoint": GET_SESSIONS_URL, "headers": headers}, 401, status, response_body, elapsed_time)
    assert status in [401, 403], f"Ожидаемый статус 401 или 403, получен {status}"

@print_test_result
def test_get_sessions_invalid_token():
    """GET /sessions с неверным токеном."""
    headers = {"Authorization": "Bearer invalid-token"}
    status, response_body, elapsed_time = perform_get_request(GET_SESSIONS_URL, headers)
    log_result("test_get_sessions_invalid_token", {"endpoint": GET_SESSIONS_URL, "headers": headers}, 401, status, response_body, elapsed_time)
    assert status in [401, 403], f"Ожидаемый статус 401 или 403, получен {status}"

# ===================== Тесты для POST /reset-password =====================

@print_test_result
def test_reset_password_valid():
    """POST /reset-password с валидным токеном."""
    access_token, _ = get_tokens()
    headers = {"Authorization": f"Bearer {access_token}"}
    status, response_body, elapsed_time = perform_post_request(RESET_PASSWORD_URL, data="", headers=headers)
    log_result("test_reset_password_valid", {"endpoint": RESET_PASSWORD_URL}, 200, status, response_body, elapsed_time)
    assert status == 200, f"Ожидаемый статус 200, получен {status}"
    message = response_body.get("body")
    assert message and "password_reset_email_sent" in message, "Сообщение о сбросе пароля не соответствует ожиданиям"

@print_test_result
def test_reset_password_missing_token():
    """POST /reset-password без токена."""
    headers = {}
    status, response_body, elapsed_time = perform_post_request(RESET_PASSWORD_URL, data="", headers=headers)
    log_result("test_reset_password_missing_token", {"endpoint": RESET_PASSWORD_URL, "headers": headers}, 401, status, response_body, elapsed_time)
    assert status in [401, 403], f"Ожидаемый статус 401 или 403, получен {status}"

@print_test_result
def test_reset_password_invalid_token():
    """POST /reset-password с неверным токеном."""
    headers = {"Authorization": "Bearer invalid-token"}
    status, response_body, elapsed_time = perform_post_request(RESET_PASSWORD_URL, data="", headers=headers)
    log_result("test_reset_password_invalid_token", {"endpoint": RESET_PASSWORD_URL, "headers": headers}, 401, status, response_body, elapsed_time)
    assert status in [401, 403], f"Ожидаемый статус 401 или 403, получен {status}"

# ===================== Тесты для PATCH /me =====================

@print_test_result
def test_patch_me_valid():
    """PATCH /me с валидным токеном и корректными данными."""
    access_token, _ = get_tokens()
    headers = {"Authorization": f"Bearer {access_token}", "Content-Type": "application/json"}
    new_username   = f"user_{random_string(6)}"
    new_first_name = f"First_{random_string(4)}"
    new_last_name  = f"Last_{random_string(4)}"
    new_email      = f"{random_string(8)}@mail.ru"
    payload = {
        "username": new_username,
        "first_name": new_first_name,
        "last_name": new_last_name,
        # "email": new_email,
        "enabled": True
    }
    status, response_body, elapsed_time = perform_patch_request(PATCH_ME_URL, json_data=payload, headers=headers)
    log_result("test_patch_me_valid", payload, 200, status, response_body, elapsed_time)
    assert status == 200, f"Ожидаемый статус 200, получен {status}"
    user = response_body["body"]
    # assert user.get("username") == new_username, "Имя пользователя не обновилось"
    assert user.get("first_name") == new_first_name, "first_name не обновился"
    assert user.get("last_name") == new_last_name, "last_name не обновился"
    # assert user.get("email") == new_email, "email не обновился"

@print_test_result
def test_patch_me_invalid_data():
    """PATCH /me с валидным токеном, но с некорректными данными (например, неверный email)."""
    access_token, _ = get_tokens()
    headers = {"Authorization": f"Bearer {access_token}", "Content-Type": "application/json"}
    payload = {
        "username": f"user_{random_string(6)}",
        "first_name": f"First_{random_string(4)}",
        "last_name": f"Last_{random_string(4)}",
        "email": "invalid_email_format",  # некорректный формат email
        "enabled": True
    }
    status, response_body, elapsed_time = perform_patch_request(PATCH_ME_URL, json_data=payload, headers=headers)
    log_result("test_patch_me_invalid_data", payload, 400, status, response_body, elapsed_time)
    # Ожидаем ошибку валидации (например, 400 или 422)
    assert status in [400, 422], f"Ожидаемый статус 400 или 422, получен {status}"

@print_test_result
def test_patch_me_missing_token():
    """PATCH /me без токена."""
    headers = {"Content-Type": "application/json"}
    payload = {
        "username": f"user_{random_string(6)}",
        "first_name": f"First_{random_string(4)}",
        "last_name": f"Last_{random_string(4)}",
        "email": f"{random_string(8)}@mail.ru",
        "enabled": True
    }
    status, response_body, elapsed_time = perform_patch_request(PATCH_ME_URL, json_data=payload, headers=headers)
    log_result("test_patch_me_missing_token", {"payload": payload, "headers": headers}, 401, status, response_body, elapsed_time)
    assert status in [401, 403], f"Ожидаемый статус 401 или 403, получен {status}"

@print_test_result
def test_patch_me_invalid_token():
    """PATCH /me с неверным токеном."""
    headers = {"Authorization": "Bearer invalid-token", "Content-Type": "application/json"}
    payload = {
        "username": f"user_{random_string(6)}",
        "first_name": f"First_{random_string(4)}",
        "last_name": f"Last_{random_string(4)}",
        # "email": f"{random_string(8)}@mail.ru",
        "enabled": True
    }
    status, response_body, elapsed_time = perform_patch_request(PATCH_ME_URL, json_data=payload, headers=headers)
    log_result("test_patch_me_invalid_token", {"payload": payload, "headers": headers}, 401, status, response_body, elapsed_time)
    assert status in [401, 403], f"Ожидаемый статус 401 или 403, получен {status}"

if __name__ == "__main__":
    pytest.main([__file__])
