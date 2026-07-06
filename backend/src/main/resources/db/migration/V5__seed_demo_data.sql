-- Demo/seed data for showcasing the MVP.
-- All seeded accounts share the password: admin123
-- (BCrypt hash below). Change credentials before any real deployment.

-- ---- Admin ----
INSERT INTO users (email, password_hash, role, enabled) VALUES
 ('admin@cmc.msu.ru', '$2a$10$T4dl.LggT803QaUsdJHf7e1i5eQGtLDOIleiGQAtnUNedJNsZkUtS', 'ADMIN', true);

-- ---- Alumni accounts ----
INSERT INTO users (email, password_hash, role, enabled) VALUES
 ('anna@demo.cmc',    '$2a$10$T4dl.LggT803QaUsdJHf7e1i5eQGtLDOIleiGQAtnUNedJNsZkUtS', 'ALUMNI', true),
 ('boris@demo.cmc',   '$2a$10$T4dl.LggT803QaUsdJHf7e1i5eQGtLDOIleiGQAtnUNedJNsZkUtS', 'ALUMNI', true),
 ('vera@demo.cmc',    '$2a$10$T4dl.LggT803QaUsdJHf7e1i5eQGtLDOIleiGQAtnUNedJNsZkUtS', 'ALUMNI', true),
 ('grigory@demo.cmc', '$2a$10$T4dl.LggT803QaUsdJHf7e1i5eQGtLDOIleiGQAtnUNedJNsZkUtS', 'ALUMNI', true),
 ('darya@demo.cmc',   '$2a$10$T4dl.LggT803QaUsdJHf7e1i5eQGtLDOIleiGQAtnUNedJNsZkUtS', 'ALUMNI', true),
 ('egor@demo.cmc',    '$2a$10$T4dl.LggT803QaUsdJHf7e1i5eQGtLDOIleiGQAtnUNedJNsZkUtS', 'ALUMNI', true);

-- ---- Tags ----
INSERT INTO tags (name, slug, category) VALUES
 ('Backend',            'backend',            'Роль'),
 ('Frontend',           'frontend',           'Роль'),
 ('DevOps',             'devops',             'Роль'),
 ('QA',                 'qa',                 'Роль'),
 ('Тестирование',       'testing',            'Роль'),
 ('Data Science',       'data-science',       'Направление'),
 ('ML',                 'ml',                 'Направление'),
 ('Аналитика',          'analytics',          'Направление'),
 ('Product Management',  'product-management', 'Роль'),
 ('GameDev',            'gamedev',            'Направление'),
 ('Cybersecurity',      'cybersecurity',      'Направление'),
 ('Research',           'research',           'Направление'),
 ('C++',                'cpp',                'Технология'),
 ('Java',               'java',               'Технология'),
 ('Python',             'python',             'Технология'),
 ('Startups',           'startups',           'Индустрия'),
 ('Big Tech',           'big-tech',           'Индустрия');

-- ---- Profiles (linked to users by email) ----
INSERT INTO alumni_profiles
 (user_id, full_name, graduation_year, department, current_position, company, city, country,
  career_description, interests_description, status, question_count, published_at)
SELECT id, 'Анна Смирнова', 2015, 'Кафедра АСВК', 'Backend Lead', 'Yandex', 'Москва', 'Россия',
  'Прошла путь от стажёра до руководителя backend-команды. Работаю с высоконагруженными сервисами на Java и Kotlin, строю распределённые системы.',
  'Распределённые системы, JVM, менторство, собеседования.',
  'PUBLISHED', 3, now() - interval '2 day'
 FROM users WHERE email = 'anna@demo.cmc';

INSERT INTO alumni_profiles
 (user_id, full_name, graduation_year, department, current_position, company, city, country,
  career_description, interests_description, status, question_count, published_at)
SELECT id, 'Борис Иванов', 2019, 'Кафедра МОСИТ', 'Frontend Developer', 'VK', 'Санкт-Петербург', 'Россия',
  'Разрабатываю пользовательские интерфейсы на React и TypeScript. Увлекаюсь производительностью веба и доступностью.',
  'React, TypeScript, UI/UX, web performance.',
  'PUBLISHED', 1, now() - interval '6 hour'
 FROM users WHERE email = 'boris@demo.cmc';

INSERT INTO alumni_profiles
 (user_id, full_name, graduation_year, department, current_position, company, city, country,
  career_description, interests_description, status, question_count, published_at)
SELECT id, 'Вера Кузнецова', 2017, 'Кафедра ММП', 'ML Engineer', 'Sber', 'Москва', 'Россия',
  'Занимаюсь машинным обучением и рекомендательными системами. От исследований до продакшена: обучение моделей, MLOps, A/B-тесты.',
  'ML, рекомендательные системы, NLP, эксперименты.',
  'PUBLISHED', 5, now() - interval '5 day'
 FROM users WHERE email = 'vera@demo.cmc';

INSERT INTO alumni_profiles
 (user_id, full_name, graduation_year, department, current_position, company, city, country,
  career_description, interests_description, status, question_count, published_at)
SELECT id, 'Григорий Орлов', 2012, 'Кафедра СП', 'Senior C++ Developer', 'JetBrains', 'Прага', 'Чехия',
  'Пишу инструменты для разработчиков на C++. Компиляторы, статический анализ, производительность.',
  'C++, компиляторы, системное программирование.',
  'PUBLISHED', 0, now() - interval '9 day'
 FROM users WHERE email = 'grigory@demo.cmc';

INSERT INTO alumni_profiles
 (user_id, full_name, graduation_year, department, current_position, company, city, country,
  career_description, interests_description, status, question_count, published_at)
SELECT id, 'Дарья Белова', 2021, 'Кафедра АСВК', 'Product Manager', 'Tinkoff', 'Москва', 'Россия',
  'Развиваю финтех-продукты: от исследования пользователей до запуска фич. Раньше была аналитиком.',
  'Продуктовая аналитика, финтех, стартапы.',
  'PUBLISHED', 2, now() - interval '1 day'
 FROM users WHERE email = 'darya@demo.cmc';

-- Egor's card is on moderation (for the admin demo), not public.
INSERT INTO alumni_profiles
 (user_id, full_name, graduation_year, department, current_position, company, city, country,
  career_description, interests_description, status, question_count, published_at)
SELECT id, 'Егор Соколов', 2020, 'Кафедра ИБ', 'Security Engineer', 'Positive Technologies', 'Москва', 'Россия',
  'Занимаюсь информационной безопасностью: пентест, анализ защищённости приложений.',
  'Кибербезопасность, пентест, исследования уязвимостей.',
  'PENDING_MODERATION', 0, NULL
 FROM users WHERE email = 'egor@demo.cmc';

-- ---- Profile <-> Tag links ----
INSERT INTO alumni_profile_tags (profile_id, tag_id)
SELECT p.id, t.id FROM alumni_profiles p JOIN users u ON u.id = p.user_id, tags t
WHERE u.email = 'anna@demo.cmc'    AND t.slug IN ('backend', 'java', 'big-tech');
INSERT INTO alumni_profile_tags (profile_id, tag_id)
SELECT p.id, t.id FROM alumni_profiles p JOIN users u ON u.id = p.user_id, tags t
WHERE u.email = 'boris@demo.cmc'   AND t.slug IN ('frontend', 'python');
INSERT INTO alumni_profile_tags (profile_id, tag_id)
SELECT p.id, t.id FROM alumni_profiles p JOIN users u ON u.id = p.user_id, tags t
WHERE u.email = 'vera@demo.cmc'    AND t.slug IN ('ml', 'data-science', 'python', 'research');
INSERT INTO alumni_profile_tags (profile_id, tag_id)
SELECT p.id, t.id FROM alumni_profiles p JOIN users u ON u.id = p.user_id, tags t
WHERE u.email = 'grigory@demo.cmc' AND t.slug IN ('cpp', 'backend');
INSERT INTO alumni_profile_tags (profile_id, tag_id)
SELECT p.id, t.id FROM alumni_profiles p JOIN users u ON u.id = p.user_id, tags t
WHERE u.email = 'darya@demo.cmc'   AND t.slug IN ('product-management', 'analytics', 'startups');
INSERT INTO alumni_profile_tags (profile_id, tag_id)
SELECT p.id, t.id FROM alumni_profiles p JOIN users u ON u.id = p.user_id, tags t
WHERE u.email = 'egor@demo.cmc'    AND t.slug IN ('cybersecurity', 'research');

-- ---- Invitations (various statuses) ----
INSERT INTO alumni_invites (email, token_hash, status, created_by_admin_id, expires_at, used_at, revoked_at, note)
SELECT 'invited1@demo.cmc', 'seedhash_sent_0001',    'SENT',    a.id, now() + interval '5 day', NULL, NULL, 'Ожидает регистрации' FROM users a WHERE a.email='admin@cmc.msu.ru';
INSERT INTO alumni_invites (email, token_hash, status, created_by_admin_id, expires_at, used_at, revoked_at, note)
SELECT 'invited2@demo.cmc', 'seedhash_created_0002', 'CREATED', a.id, now() + interval '7 day', NULL, NULL, NULL FROM users a WHERE a.email='admin@cmc.msu.ru';
INSERT INTO alumni_invites (email, token_hash, status, created_by_admin_id, expires_at, used_at, revoked_at, note)
SELECT 'anna@demo.cmc',     'seedhash_used_0003',    'USED',    a.id, now() - interval '20 day', now() - interval '25 day', NULL, 'Зарегистрировалась' FROM users a WHERE a.email='admin@cmc.msu.ru';
INSERT INTO alumni_invites (email, token_hash, status, created_by_admin_id, expires_at, used_at, revoked_at, note)
SELECT 'expired@demo.cmc',  'seedhash_expired_0004', 'EXPIRED', a.id, now() - interval '3 day', NULL, NULL, NULL FROM users a WHERE a.email='admin@cmc.msu.ru';
INSERT INTO alumni_invites (email, token_hash, status, created_by_admin_id, expires_at, used_at, revoked_at, note)
SELECT 'revoked@demo.cmc',  'seedhash_revoked_0005', 'REVOKED', a.id, now() + interval '4 day', NULL, now() - interval '1 day', 'Отозвано по ошибке' FROM users a WHERE a.email='admin@cmc.msu.ru';

-- ---- Questions ----
-- Helper pattern: link by alumni email. Statuses show every part of the flow.
-- Anna: 3 visible (mix read/unread), 1 pending admin review, 1 AI-rejected.
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, 'Иван', 'ivan@student.msu', 'Как вы попали в Yandex и что помогло вырасти до лида?', 'VISIBLE_TO_ALUMNI', 'APPROVED', false
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='anna@demo.cmc';
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, 'Мария', NULL, 'Какие книги по распределённым системам посоветуете?', 'VISIBLE_TO_ALUMNI', 'APPROVED', true
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='anna@demo.cmc';
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, NULL, NULL, 'Стоит ли учить Kotlin, если знаешь Java?', 'VISIBLE_TO_ALUMNI', 'APPROVED', false
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='anna@demo.cmc';
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, ai_moderation_reason)
SELECT p.id, 'Пётр', 'petr@student.msu', 'Можно ли к вам на стажировку и как подготовиться к алгоритмической секции?', 'PENDING_ADMIN_REVIEW', 'NEEDS_REVIEW', 'Очень длинное сообщение'
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='anna@demo.cmc';
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, ai_moderation_reason)
SELECT p.id, NULL, NULL, 'заходите на http://spam.example купить дёшево', 'AI_REJECTED', 'REJECTED', 'Сообщение содержит ссылки или рекламу'
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='anna@demo.cmc';

-- Boris: 1 visible.
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, 'Алексей', NULL, 'React или Vue для нового проекта в 2026?', 'VISIBLE_TO_ALUMNI', 'APPROVED', false
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='boris@demo.cmc';

-- Vera: 5 visible.
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, 'Ольга', NULL, 'С чего начать путь в ML, если я на 3 курсе?', 'VISIBLE_TO_ALUMNI', 'APPROVED', false
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='vera@demo.cmc';
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, 'Сергей', NULL, 'Насколько важна математика в реальных ML-задачах?', 'VISIBLE_TO_ALUMNI', 'APPROVED', true
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='vera@demo.cmc';
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, NULL, NULL, 'Как выглядит типичный день ML-инженера?', 'VISIBLE_TO_ALUMNI', 'APPROVED', false
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='vera@demo.cmc';
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, 'Нина', 'nina@student.msu', 'Какие соревнования (Kaggle) полезны для старта?', 'VISIBLE_TO_ALUMNI', 'APPROVED', true
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='vera@demo.cmc';
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, NULL, NULL, 'Стоит ли идти в аспирантуру ради ML-исследований?', 'VISIBLE_TO_ALUMNI', 'APPROVED', false
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='vera@demo.cmc';

-- Darya: 2 visible.
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, 'Игорь', NULL, 'Как перейти из аналитика в продакт-менеджеры?', 'VISIBLE_TO_ALUMNI', 'APPROVED', false
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='darya@demo.cmc';
INSERT INTO questions (alumni_profile_id, sender_name, sender_email, question_text, status, ai_moderation_status, is_read_by_alumni)
SELECT p.id, 'Катя', NULL, 'Какие метрики самые важные для финтех-продукта?', 'VISIBLE_TO_ALUMNI', 'APPROVED', true
 FROM alumni_profiles p JOIN users u ON u.id=p.user_id WHERE u.email='darya@demo.cmc';
