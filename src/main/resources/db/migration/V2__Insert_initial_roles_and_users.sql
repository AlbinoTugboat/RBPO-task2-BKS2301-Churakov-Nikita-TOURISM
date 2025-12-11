-- Добавляем базовые роли
INSERT INTO roles (name) VALUES
('ROLE_USER'),
('ROLE_GUIDE'),
('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Вставляем тестовых пользователей (пароли закодированы BCrypt "Password123!")
INSERT INTO users (username, email, password, first_name, last_name, is_active) VALUES
('admin', 'admin@tourism.com', '$2a$12$8i9zJ4lrqraFpdnFJYDH8O3RtbrdRGF7qZi8yt9JWYsH4kcc8zR/S', 'Admin', 'User', true),
('guide1', 'guide1@tourism.com', '$2a$12$8i9zJ4lrqraFpdnFJYDH8O3RtbrdRGF7qZi8yt9JWYsH4kcc8zR/S', 'Anna', 'Guide', true),
('user1', 'user1@tourism.com', '$2a$12$8i9zJ4lrqraFpdnFJYDH8O3RtbrdRGF7qZi8yt9JWYsH4kcc8zR/S', 'John', 'User', true)
ON CONFLICT (username) DO NOTHING;

-- Назначаем роли
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'guide1' AND r.name = 'ROLE_GUIDE'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'user1' AND r.name = 'ROLE_USER'
ON CONFLICT (user_id, role_id) DO NOTHING;