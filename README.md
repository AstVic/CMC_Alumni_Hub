# CMC Alumni Hub

Платформа для взаимодействия студентов факультета **ВМК МГУ** с выпускниками
факультета. Выпускники рассказывают о карьере и опыте, отвечают на вопросы
студентов; посетители без регистрации просматривают карточки, фильтруют их и
задают вопросы конкретным выпускникам.

Регистрация выпускников — **только по приглашению администратора**. Вопросы
проходят двухуровневую модерацию (автоматическую + ручную).

---

## Стек

| Слой | Технологии |
|---|---|
| **Backend** | Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA, Flyway, Maven |
| **Frontend** | React 18, TypeScript, Vite, Tailwind CSS, TanStack Query, React Router |
| **База данных** | PostgreSQL 16 |
| **Инфраструктура** | Docker Compose (local dev + production с облачной PostgreSQL) |
| **Безопасность** | JWT-авторизация, пароли BCrypt, rate limiting, honeypot |

---

## Возможности

- **Посетители** (без регистрации): каталог выпускников с режимами «Доска» и
  «Список», поиск, фильтры (теги, год выпуска, компания), 7 вариантов сортировки
  (в т.ч. по количеству вопросов), детальная карточка, форма вопроса.
- **Выпускники** (по приглашению): личный кабинет, создание/редактирование
  карточки, загрузка фото, выбор тегов, отправка карточки на модерацию, просмотр
  входящих вопросов.
- **Администратор**: приглашения по email, модерация карточек и вопросов,
  управление выпускниками (блокировка), управление тегами, дашборд со статистикой.

---

## Быстрый старт (local dev)

Требуется только **Docker** (с Docker Compose).

```bash
# 1. Клонировать репозиторий и перейти в него
cd CMC_Alumni_Hub

# 2. Создать файл окружения из шаблона
cp .env.example .env

# 3. Поднять весь стек (PostgreSQL + backend + frontend)
docker compose up --build
```

После сборки откройте:

| Сервис | URL |
|---|---|
| **Frontend** | http://localhost:5173 |
| **Backend API** | http://localhost:8080/api |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **PostgreSQL** | localhost:5432 |

Остановить: `docker compose down` (данные сохраняются в volume).
Полностью очистить (включая БД): `docker compose down -v`.

### Демо-доступы

База автоматически заполняется демо-данными (Flyway `V5`). Пароль у всех
демо-аккаунтов — `admin123`.

| Роль | Email | Пароль |
|---|---|---|
| Администратор | `admin@cmc.msu.ru` | `admin123` |
| Выпускник | `anna@demo.cmc` (и др. `*@demo.cmc`) | `admin123` |

> ⚠️ Демо-данные и пароли предназначены только для локального ознакомления.
> Смените их перед реальным развёртыванием.

---

## Приглашение выпускника (как это работает)

1. Администратор в разделе **«Приглашения»** вводит email выпускника.
2. Система генерирует одноразовый токен, сохраняет в БД **только его SHA-256
   hash** и отправляет письмо со ссылкой (действует 7 дней).
3. Выпускник переходит по ссылке, видит форму (email привязан к приглашению),
   задаёт пароль и создаёт аккаунт.
4. Токен помечается как использованный; повторно и после истечения срока он не
   работает; администратор может отозвать приглашение.

### Email и SMTP

По умолчанию, если SMTP не настроен, приложение использует **логирующую
заглушку** — письмо (вместе со ссылкой-приглашением) выводится в лог backend:

```bash
docker compose logs -f backend
```

Для **реальной отправки** заполните в `.env` переменные SMTP, например для Yandex:

```dotenv
SMTP_HOST=smtp.yandex.ru
SMTP_PORT=587
SMTP_USERNAME=your-address@yandex.ru
SMTP_PASSWORD=your-app-password      # пароль приложения, не основной пароль
SMTP_FROM=CMC Alumni Hub <your-address@yandex.ru>
```

---

## Production (облачная PostgreSQL)

В production база данных — управляемый облачный экземпляр PostgreSQL (Neon,
Supabase, Yandex Cloud, RDS и т.п.), контейнер с БД не поднимается.

```bash
# 1. Заполнить .env реальными значениями (см. ниже)
cp .env.example .env

# 2. Поднять backend + frontend (без локальной БД)
docker compose -f docker-compose.prod.yml up --build -d
```

Обязательные переменные для production в `.env`:

```dotenv
APP_PROFILE=prod

# Облачная PostgreSQL — обязательно с SSL:
DB_URL=jdbc:postgresql://<host>:5432/<db>?sslmode=require
DB_USERNAME=...
DB_PASSWORD=...

# Длинный случайный секрет (обязателен, приложение не стартует без него):
JWT_SECRET=<не менее 64 случайных символов>

# Публичный адрес фронтенда (для CORS и ссылок в письмах):
APP_FRONTEND_URL=https://your-domain

# SMTP (см. выше)
SMTP_HOST=...
```

Профиль backend выбирается переменной `APP_PROFILE` (`local` | `prod`).
В production включён SSL к БД, скрыт SQL-лог, а секреты берутся только из
окружения.

---

## Локальная разработка без Docker

**Backend** (нужны JDK 21 и запущенная PostgreSQL):

```bash
cd backend
DB_URL=jdbc:postgresql://localhost:5432/alumnihub \
DB_USERNAME=alumnihub DB_PASSWORD=alumnihub \
APP_PROFILE=local ./mvnw spring-boot:run
```

> Проект компилируется под **Java 21**. Собирать нужно именно на JDK 21.

**Frontend** (нужен Node.js 20+):

```bash
cd frontend
npm install
npm run dev      # http://localhost:5173, проксирует /uploads на backend
```

---

## Структура проекта

```
backend/                     Spring Boot приложение
  src/main/java/ru/msu/cmc/alumnihub/
    user/ invite/ profile/ tag/ question/ moderation/   # feature-пакеты
    security/ config/ common/ email/ storage/ admin/
  src/main/resources/db/migration/                       # Flyway V1–V5
frontend/                    React + Vite приложение
  src/api/ auth/ components/ pages/ hooks/ router/ types/
docker-compose.yml           локальный стек (с PostgreSQL)
docker-compose.prod.yml      production (облачная PostgreSQL)
.env.example                 шаблон переменных окружения
```

---

## Архитектура и безопасность (кратко)

- Чистое разделение слоёв: `controller → service → repository`, DTO не
  раскрывают entity, единый обработчик ошибок, Bean Validation.
- Stateless JWT (access + refresh), проверка ролей на уровне URL и владения.
- Пароли — только BCrypt; invite-токены хранятся как SHA-256 hash.
- Модерация вопросов вынесена в интерфейс `ModerationProvider`: для MVP —
  правила и эвристики (`rule-based`), архитектурно готово к подключению
  реального AI API через `MODERATION_PROVIDER=ai` и переменные окружения.
- Защита от спама: honeypot-поле, ограничение частоты (rate limiting),
  валидация длины и содержимого вопроса.
- Миграции БД — через Flyway; денормализованное поле `question_count` для
  быстрой сортировки по популярности.

---

## Идеи для второй версии

Ответы выпускников на вопросы и публичные Q&A · закрытые вопросы ·
email-уведомления · календарь встреч и менторские сессии · интеграция с
Telegram · аналитика популярных тегов и направлений · рейтинг активности
выпускников · подбор выпускников по интересам студента · избранные карточки ·
экспорт вопросов для администратора.
