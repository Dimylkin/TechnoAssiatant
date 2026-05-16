# TechnoAssistant

TechnoAssistant — десктопное JavaFX-приложение для оценки ПК с помощью локальной AI-модели.

Приложение позволяет пользователю ввести характеристики компьютера (CPU/RAM/GPU/Storage и т.д.) и получить оценку/результат от модели.

Проект состоит из:
- **JavaFX UI** (Java 21 + Maven)
- **Python inference** (venv + зависимости из `requirements.txt`)

---

## Стек

### Backend / AI
- Python 3.10+ (рекомендуется 3.12)
- `requirements.txt`

### UI
- Java 21+
- JavaFX
- Maven

---

## Требования

Перед запуском убедись, что установлены:

- **Java 21+**
- **Maven 3.9+**
- **Python 3.10+**

Проверка:

```bash
java -version
mvn -version
python3 --version
```

---

## Установка и запуск (рекомендуемый способ)

В корне проекта есть команды-лаунчеры `app` и `app.bat`.

### Linux / macOS

```bash
chmod +x app
./app install
./app run
```

### Windows

```bat
app install
app run
```

---

## Команды

### `app install`

Выполняет полный setup проекта:

1) создаёт Python окружение `.venv`  
2) ставит зависимости из `requirements.txt`  
3) собирает Java часть:

```bash
mvn clean package
```

---

### `app run`

Запускает JavaFX приложение:

```bash
mvn clean javafx:run
```

---

### `app build`

Собирает jar:

```bash
mvn clean package
```

---

### `app clean`

Очищает проект:

- удаляет `target`
- удаляет `.venv`
- удаляет Python cache (`__pycache__`, `*.pyc`)

---

## Запуск вручную (без app)

### 1) Python окружение

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install --upgrade pip
pip install -r requirements.txt
```

Windows:

```bat
python -m venv .venv
.venv\Scripts\activate
pip install --upgrade pip
pip install -r requirements.txt
```

### 2) Сборка Java

```bash
mvn clean package
```

### 3) Запуск UI

```bash
mvn clean javafx:run
```

---

## Структура проекта

```
TechnoAssistant/
├── src/main/java/             # JavaFX UI
├── src/main/resources/        # ресурсы (css, images, json)
├── requirements.txt           # зависимости Python части
├── pom.xml                    # Maven конфигурация
├── app                        # launcher (Linux/macOS)
├── app.bat                    # launcher (Windows)
└── README.md
```

---

## Важные особенности

### Модель v1.0.1
При версии модели **v1.0.1** поле цены **не отображается** на экране (нет Label/TextField).

---

## Troubleshooting

### `mvn: command not found`
Maven не установлен или не добавлен в PATH.

### `python3: command not found`
Python не установлен или не добавлен в PATH.

### Ошибка JavaFX / openjfx
Убедись что используешь Java 21 и что зависимости JavaFX подтягиваются Maven через `pom.xml`.

---

## Developer notes

Рекомендуемые инструменты:
- IntelliJ IDEA
- Java 21 SDK
- Python 3.12

---

## License
Internal / Educational use.
