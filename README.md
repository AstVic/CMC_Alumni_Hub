# CMC Alumni Hub

Платформа для взаимодействия студентов факультета ВМК МГУ с выпускниками
факультета. Выпускники рассказывают о карьере и опыте, отвечают на вопросы
студентов; посетители без регистрации просматривают карточки и задают вопросы.

> MVP. Полная инструкция по запуску появится по мере реализации (см. раздел
> «Запуск» ниже — заполняется на финальном этапе).

## Стек

- **Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data JPA, Flyway, Maven
- **Frontend:** React + TypeScript + Vite, Tailwind CSS
- **База данных:** PostgreSQL
- **Инфраструктура:** Docker Compose (local dev + production с облачной PostgreSQL)
- **Авторизация:** JWT, пароли — BCrypt

## Режимы запуска

Проект поддерживает два режима:

- **local dev** — полный стек в Docker (PostgreSQL в контейнере), Spring-профиль `local`.
- **production** — облачная PostgreSQL (SSL), Spring-профиль `prod`.

Подробности — в разделе «Запуск» (в разработке).

## Структура репозитория

```
backend/            Spring Boot приложение
frontend/           React + Vite приложение
docker-compose.yml        локальный стек (с PostgreSQL)
docker-compose.prod.yml   production (облачная PostgreSQL)
.env.example              шаблон переменных окружения
```

## Запуск

_Раздел заполняется на финальном этапе реализации._
