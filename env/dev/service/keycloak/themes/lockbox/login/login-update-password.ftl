<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Сброс пароля</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body>
    <style>
        body {
            background-color: rgba(10, 10, 10, 1);
        }

        .heading {
            color: white;
            font-size: 1.5rem;
            margin: 0.2rem 0 0 0;
        }

        .text {
            color: white;
            font-weight: 200;
            font-size: 0.91rem;
            opacity: 0.5;
            margin-bottom: 1.3rem;
        }

        .button {
            width: 100%;
            height: 3rem;
            display: flex;
            justify-content: center;
            align-items: center;
            border-radius: 8px;
            color: white;
            background: #16A34A;
            transition: all 0.1s ease-in-out;
            pointer-events: none;
            opacity: 0.3;
        }

        .button.active {
            pointer-events: auto;
            opacity: 1;
        }

        .input {
            width: 100%;
            height: 3rem;
            padding: 0 1rem;
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid transparent;
            border-radius: 0.6rem;
            color: white;
        }

        .input:focus {
            border-color: rgba(255, 255, 255, 0.4);
            background: rgba(255, 255, 255, 0.2);
            outline: none;
        }
    </style>

    <div class="w-full h-screen flex justify-center items-center">
        <main class="w-full max-w-md bg-[#181818]/70 rounded-2xl border border-white/10 p-6">
            <p class="heading">Сброс пароля</p>
            <p class="text mt-2">Введите новый пароль и подтвердите его</p>
            <form id="reset-password-form" method="post" action="${url.loginAction}">
                <label for="password-new" class="block text-white opacity-70 text-sm mb-2">Новый пароль</label>
                <input 
                    id="password-new" 
                    name="password-new" 
                    type="password" 
                    class="input mb-4" 
                    placeholder="Введите новый пароль" 
                    autocomplete="new-password"
                    required 
                />

                <label for="password-confirm" class="block text-white opacity-70 text-sm mb-2">Подтвердите пароль</label>
                <input 
                    id="password-confirm" 
                    name="password-confirm" 
                    type="password" 
                    class="input mb-4" 
                    placeholder="Повторите новый пароль" 
                    autocomplete="new-password"
                    required 
                />

                <button id="submit-button" class="button" type="submit">Сбросить пароль</button>
            </form>
        </main>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const passwordInput = document.getElementById('password-new');
            const confirmPasswordInput = document.getElementById('password-confirm');
            const submitButton = document.getElementById('submit-button');

            function validateInputs() {
                if (passwordInput.value.trim() !== '' && confirmPasswordInput.value.trim() !== '') {
                    submitButton.classList.add('active');
                } else {
                    submitButton.classList.remove('active');
                }
            }

            passwordInput.addEventListener('input', validateInputs);
            confirmPasswordInput.addEventListener('input', validateInputs);
        });
    </script>
</body>
</html>
