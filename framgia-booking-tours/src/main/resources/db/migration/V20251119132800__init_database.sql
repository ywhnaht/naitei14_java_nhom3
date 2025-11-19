create database if not exists framgia_booking_tours;
use framgia_booking_tours;

CREATE TABLE users (
                       id bigint PRIMARY KEY AUTO_INCREMENT,
                       email varchar(100) UNIQUE NOT NULL,
                       password varchar(255) NOT NULL,
                       provider ENUM ('LOCAL', 'FACEBOOK', 'GOOGLE', 'TWITTER') DEFAULT 'LOCAL',
                       status ENUM ('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
                       created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                       updated_at timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE roles (
                       id bigint PRIMARY KEY AUTO_INCREMENT,
                       name varchar(50) UNIQUE NOT NULL,
                       description varchar(255)
);

CREATE TABLE user_roles (
                            user_id bigint NOT NULL,
                            role_id bigint NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE profiles (
                          id bigint PRIMARY KEY AUTO_INCREMENT,
                          user_id bigint UNIQUE NOT NULL,
                          full_name varchar(100),
                          phone varchar(20),
                          address text,
                          avatar_url text,
                          bank_name varchar(100),
                          bank_account_number varchar(50),
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE categories (
                            id bigint PRIMARY KEY AUTO_INCREMENT,
                            name varchar(100) NOT NULL,
                            description text,
                            created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tours (
                       id bigint PRIMARY KEY AUTO_INCREMENT,
                       category_id bigint,
                       creator_id bigint NOT NULL,
                       name varchar(150) NOT NULL,
                       description text,
                       location varchar(150),
                       price decimal(12,2) NOT NULL,
                       duration_days int,
                       available_slots int DEFAULT 0,
                       image_url text,
                       status ENUM ('AVAILABLE', 'UNAVAILABLE') DEFAULT 'AVAILABLE',
                       created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                       updated_at timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       FOREIGN KEY (category_id) REFERENCES categories(id),
                       FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE bookings (
                          id bigint PRIMARY KEY AUTO_INCREMENT,
                          user_id bigint NOT NULL,
                          tour_id bigint NOT NULL,
                          booking_date timestamp DEFAULT CURRENT_TIMESTAMP,
                          start_date date NOT NULL,
                          num_people int NOT NULL,
                          total_price decimal(12,2) NOT NULL,
                          status ENUM ('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
                          payment_method ENUM ('BANKING', 'CASH') DEFAULT 'BANKING',
                          FOREIGN KEY (user_id) REFERENCES users (id),
                          FOREIGN KEY (tour_id) REFERENCES tours (id)
);

CREATE TABLE payments (
                          id bigint PRIMARY KEY AUTO_INCREMENT,
                          booking_id bigint NOT NULL,
                          amount decimal(12,2) NOT NULL,
                          payment_date timestamp DEFAULT CURRENT_TIMESTAMP,
                          payment_status ENUM ('SUCCESS', 'FAILED', 'PENDING') DEFAULT 'PENDING',
                          bank_name varchar(100),
                          account_number varchar(50),
                          FOREIGN KEY (booking_id) REFERENCES bookings (id)
);

CREATE TABLE reviews (
                         id bigint PRIMARY KEY AUTO_INCREMENT,
                         booking_id bigint UNIQUE NOT NULL,
                         title varchar(150),
                         content text,
                         rating int,
                         created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                         updated_at timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (booking_id) REFERENCES bookings (id),
                         CONSTRAINT chk_rating CHECK (rating >= 1 AND rating <= 5)
);

CREATE TABLE comments (
                          id bigint PRIMARY KEY AUTO_INCREMENT,
                          review_id bigint NOT NULL,
                          user_id bigint NOT NULL,
                          parent_comment_id bigint,
                          content text NOT NULL,
                          created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (review_id) REFERENCES reviews (id),
                          FOREIGN KEY (user_id) REFERENCES users (id),
                          FOREIGN KEY (parent_comment_id) REFERENCES comments (id)
);

CREATE TABLE review_likes (
                              id bigint PRIMARY KEY AUTO_INCREMENT,
                              user_id bigint NOT NULL,
                              review_id bigint NOT NULL,
                              created_at timestamp DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users (id),
                              FOREIGN KEY (review_id) REFERENCES reviews (id),
                              UNIQUE KEY uk_user_review_like (user_id, review_id)
);


