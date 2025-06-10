# JobParser (консольный парсер вакансий)

Учебный проект первого курса: маленький **CLI‑агрегатор** вакансий с hh.ru.  
Собирает вакансии по ключевой фразе, хранит их в PostgreSQL, позволяет смотреть, фильтровать, считать статистику и экспортировать в CSV / JSON / HTML.

---
## 1. Обзор

|                     |                           |
|---------------------|---------------------------|
| **Тип приложения**  | консоль (Java 17 + Gradle) |
| **Источник данных** | JSON‑API hh.ru            |
| **Хранение**        | PostgreSQL 15 (докер)     |
| **UI**              | текстовое меню команд     |

Проект написан «ручками» без Spring Boot, чтобы понять чистый JDBC, Gradle и работу с HTTP‑клиентом.

---
## 2. Возможности

- **update** — парсит вакансии с hh.ru (*по умолчанию 20 на страницу*) и кладёт/обновляет в БД;  
  старые вакансии помечаются `active = false`.
- **list / search** — вывод всего списка или выборки по ключевому слову.
- **stats** — две под‑команды:  
  `stats categories` (сколько вакансий в каждой категории) и  
  `stats salary_by_city` (count/avg/max по городам).
- **top N** — отображает N вакансий с максимальной зарплатой.
- **export** — сохраняет текущее отображение в CSV, JSON или HTML.

---
## 3. Технологический стек

|        | Версия |
|--------|--------|
| Java   | 17     |
| Gradle | Wrapper 8.x |
| PostgreSQL | 15‑alpine (Docker) |
| HTTP   | `java.net.http.HttpClient` |
| JSON   | Jackson Databind 2.17 |
| Тесты  | JUnit 5 |

Доп.библиотеки: Jsoup (для HTML‑экспорта).

---
## 4. Быстрый запуск (Docker + Gradle)

```bash
# 1 – клонируем проект
$ git clone <repo-url> jobparser && cd jobparser

# 2 – поднимаем Postgres в контейнере
$ docker compose up -d          # использует docker-compose.yml

# 3 – запускаем приложение (скачает зависимости)
$ ./gradlew run                 # Windows → gradlew.bat run
```

> **Замечание** для Windows‑консоли: чтобы русский текст отображался корректно, выполните `chcp 65001` перед запуском `gradlew.bat`.

При первом запуске появится меню:
```
> help
```

---
## 5. Доступные команды CLI

| Команда                               | Что делает |
|---------------------------------------|------------|
| `help`                                | справка по командам |
| `update [keyword] [pages]`            | парсинг вакансий (pages = 1 по умолчанию) |
| `list`                                | вывести все активные вакансии |
| `search <word>`                       | поиск слова в названии/описании |
| `stats categories`                    | сколько вакансий в каждой категории |
| `stats salary_by_city`                | count / avg / max зарплаты по городам |
| `top <N>`                             | топ N вакансий по `salary_max` |
| `export <csv|json|html> <file>`       | экспорт выборки |
| `exit`                                | выход |

### Пример сессии
```
> update java 2
С парсера пришло: 40 …
> stats categories
Java Developer: 28
QA Engineer:     8
> top 3
...
```

---
## 6. Структура пакетов / классов

| Пакет | Ключевые классы | Назначение |
|-------|-----------------|------------|
| **app**     | `Main` | точка входа, создаёт `ConsoleInterface`. |
| **ui**      | `ConsoleInterface` | читает команды, печатает результаты. |
| **parser**  | `HhParser` | HTTP‑запросы к API hh.ru, сбор данных. |
| **model**   | `Vacancy` | POJO со всеми полями вакансии. |
| **dao**     | `VacancyRepository` | JDBC‑доступ к таблице *vacancy*. |
| **service** | `VacancyService`, `AnalyticsService` | логика «обновления» и расчёта статистики. |
| **export**  | `CsvExporter`, `JsonExporter`, `HtmlExporter` | вывод в файл. |
| **db**      | `DatabaseManager` | выдаёт `Connection` (читает `config.properties`). |
| **utils**   | `CreateDatabase` | (один раз) создаёт БД/таблицу напрямую. |
| **test**    | JUnit 5 | тесты экспортеров и сервисов.

---
## 7. Схема таблицы vacancy

```sql
CREATE TABLE vacancy (
    id SERIAL PRIMARY KEY,
    hh_id VARCHAR(64) UNIQUE NOT NULL,
    title TEXT,
    company TEXT,
    city TEXT,
    salary_min INTEGER,
    salary_max INTEGER,
    category TEXT,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    last_updated TIMESTAMP DEFAULT NOW()
);
```
*Скрипт `init.sql` лежит в `src/main/resources/db` и выполняется Postgres‑контейнером при старте.*

---
## 8. Тестирование

```bash
./gradlew clean test   
```