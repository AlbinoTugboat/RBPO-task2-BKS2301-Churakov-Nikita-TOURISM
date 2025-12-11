-- Тестовые направления
INSERT INTO destinations (name, description, country, city) VALUES
('Париж', 'Столица Франции, город огней и романтики', 'Франция', 'Париж'),
('Рим', 'Вечный город с богатой историей и культурой', 'Италия', 'Рим'),
('Бали', 'Тропический рай с прекрасными пляжами', 'Индонезия', 'Денпасар'),
('Токио', 'Современный мегаполис с традиционной культурой', 'Япония', 'Токио'),
('Нью-Йорк', 'Город, который никогда не спит', 'США', 'Нью-Йорк')
ON CONFLICT DO NOTHING;

-- Тестовые гиды
INSERT INTO guides (name, email, phone, specialization, experience_years) VALUES
('Анна Иванова', 'anna@example.com', '+7-123-456-7890', 'Исторические туры', 5),
('Петр Сидоров', 'peter@example.com', '+7-987-654-3210', 'Гастрономические туры', 3),
('Мария Козлова', 'maria@example.com', '+7-111-222-3333', 'Приключенческие туры', 7),
('Алексей Петров', 'alex@example.com', '+7-444-555-6666', 'Культурные туры', 4)
ON CONFLICT (email) DO NOTHING;

-- Тестовые туры
INSERT INTO tours (title, description, start_date, end_date, max_participants, price, guide_id) VALUES
('Романтический Париж', 'Недельный тур по самым романтичным местам Парижа', '2025-06-01', '2025-06-07', 15, 800.00, 1),
('Исторический Рим', 'Путешествие по древним руинам и музеям Рима', '2025-07-15', '2025-07-22', 12, 750.00, 2),
('Экзотический Бали', 'Отдых на райских пляжах Бали с экскурсиями', '2025-08-10', '2025-08-17', 20, 1200.00, 3),
('Японские приключения', 'Исследование Токио и окрестностей', '2025-09-05', '2025-09-12', 10, 1500.00, 4)
ON CONFLICT DO NOTHING;

-- Связи туров с направлениями
INSERT INTO tour_destinations (tour_id, destination_id, visit_order) VALUES
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1)
ON CONFLICT DO NOTHING;

-- Тестовые бронирования
INSERT INTO bookings (customer_name, customer_email, tour_id, total_price) VALUES
('Иван Петров', 'ivan@example.com', 1, 800.00),
('Елена Смирнова', 'elena@example.com', 1, 800.00),
('Дмитрий Ковалев', 'dmitry@example.com', 2, 750.00)
ON CONFLICT DO NOTHING;

-- Тестовые отзывы
INSERT INTO reviews (tour_id, booking_id, review_text, rating, author_name) VALUES
(1, 1, 'Прекрасный тур! Организация на высшем уровне.', 5, 'Иван Петров'),
(1, 2, 'Очень понравилось, особенно экскурсия по Лувру.', 4, 'Елена Смирнова')
ON CONFLICT DO NOTHING;