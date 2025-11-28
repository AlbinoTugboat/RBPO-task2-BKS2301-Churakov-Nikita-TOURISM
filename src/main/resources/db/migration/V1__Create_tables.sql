-- Таблица направлений
CREATE TABLE destinations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица гидов
CREATE TABLE guides (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50),
    specialization VARCHAR(255),
    experience_years INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица туров
CREATE TABLE tours (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    max_participants INTEGER NOT NULL CHECK (max_participants > 0),
    current_participants INTEGER DEFAULT 0 CHECK (current_participants >= 0),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    guide_id BIGINT REFERENCES guides(id),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Связующая таблица туров и направлений
CREATE TABLE tour_destinations (
    tour_id BIGINT REFERENCES tours(id) ON DELETE CASCADE,
    destination_id BIGINT REFERENCES destinations(id) ON DELETE CASCADE,
    visit_order INTEGER DEFAULT 1,
    PRIMARY KEY (tour_id, destination_id)
);

-- Таблица бронирований
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    tour_id BIGINT REFERENCES tours(id),
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'CONFIRMED' CHECK (status IN ('CONFIRMED', 'CANCELLED', 'COMPLETED')),
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица отзывов
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    tour_id BIGINT REFERENCES tours(id),
    booking_id BIGINT REFERENCES bookings(id) UNIQUE,
    review_text TEXT NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    author_name VARCHAR(255) NOT NULL,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_approved BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для улучшения производительности
CREATE INDEX idx_tours_guide_id ON tours(guide_id);
CREATE INDEX idx_tours_dates ON tours(start_date, end_date);
CREATE INDEX idx_bookings_tour_id ON bookings(tour_id);
CREATE INDEX idx_bookings_customer_email ON bookings(customer_email);
CREATE INDEX idx_reviews_tour_id ON reviews(tour_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);