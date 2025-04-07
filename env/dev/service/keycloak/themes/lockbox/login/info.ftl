<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${msg("pageTitle")}</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body>
    <style>
        body {
            background-color: rgba(10, 10, 10, 1);
        }

        .container {
            max-width: 500px;
            margin: 5rem auto;
            padding: 2rem;
            background: rgba(24, 24, 24, 0.8);
            border-radius: 8px;
            border: 1px solid rgba(255, 255, 255, 0.1);
            text-align: center;
            color: white;
        }

        .heading {
            font-size: 1.5rem;
            font-weight: bold;
            margin-bottom: 1rem;
        }

        .message {
            font-size: 1rem;
            opacity: 0.8;
            margin-bottom: 1.5rem;
        }

        .button {
            padding: 0.8rem 1.5rem;
            background: #16A34A;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
            transition: background 0.3s ease;
        }

        .button:hover {
            background: #15803D;
        }
    </style>

    <div class="container">
        <#--  <p class="heading">Успешно сменен пароль</p>

        <a href="https://lockboxes.ru" class="button">Перейти к входу</a>  -->
    </div>

    <script>
        window.onload = function() {
            window.location.href = "https://lockboxes.ru";
        };
    </script>
</body>
</html>
