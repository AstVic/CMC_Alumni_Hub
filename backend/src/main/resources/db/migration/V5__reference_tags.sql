-- Reference data: the standard set of professional tags.
-- Baseline data present in ALL environments (including production) so alumni
-- have tags to choose from. Demo alumni/cards/questions are seeded separately
-- (see db/demo/demo_data.sql, loaded only when app.seed-demo=true).
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
