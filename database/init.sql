-- 创建数据库
CREATE DATABASE IF NOT EXISTS clothset DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE clothset;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    icon VARCHAR(100),
    parent_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 季节表
CREATE TABLE IF NOT EXISTS seasons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 衣物表
CREATE TABLE IF NOT EXISTS clothes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    category_id BIGINT,
    brand VARCHAR(100),
    color VARCHAR(50),
    material VARCHAR(100),
    purchase_date DATE,
    price DECIMAL(10, 2),
    wear_count INT DEFAULT 0,
    status ENUM('active', 'archived', 'discarded') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 衣物季节关联表
CREATE TABLE IF NOT EXISTS cloth_seasons (
    cloth_id BIGINT NOT NULL,
    season_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cloth_id, season_id),
    FOREIGN KEY (cloth_id) REFERENCES clothes(id) ON DELETE CASCADE,
    FOREIGN KEY (season_id) REFERENCES seasons(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 穿搭表
CREATE TABLE IF NOT EXISTS outfits (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100),
    description TEXT,
    image_url VARCHAR(255),
    occasion VARCHAR(100),
    is_favorite BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 穿搭衣物关联表
CREATE TABLE IF NOT EXISTS outfit_clothes (
    outfit_id BIGINT NOT NULL,
    cloth_id BIGINT NOT NULL,
    position INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (outfit_id, cloth_id),
    FOREIGN KEY (outfit_id) REFERENCES outfits(id) ON DELETE CASCADE,
    FOREIGN KEY (cloth_id) REFERENCES clothes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 穿搭日历表
CREATE TABLE IF NOT EXISTS calendar_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    entry_date DATE NOT NULL,
    outfit_id BIGINT,
    note TEXT,
    weather VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (outfit_id) REFERENCES outfits(id) ON DELETE SET NULL,
    UNIQUE KEY unique_user_date (user_id, entry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化分类数据
INSERT INTO categories (name, icon, parent_id) VALUES 
('上装', 'tshirt', NULL),
('下装', 'pants', NULL),
('外套', 'jacket', NULL),
('鞋履', 'shoes', NULL),
('配饰', 'accessories', NULL),
('包包', 'bag', NULL);

INSERT INTO categories (name, icon, parent_id) VALUES 
('T恤', 'tshirt', 1),
('衬衫', 'shirt', 1),
('毛衣', 'sweater', 1),
('卫衣', 'hoodie', 1),
('牛仔裤', 'jeans', 2),
('休闲裤', 'pants', 2),
('西裤', 'trousers', 2),
('短裤', 'shorts', 2),
('半身裙', 'skirt', 2),
('连衣裙', 'dress', 2),
('西装', 'suit', 3),
('风衣', 'trench', 3),
('夹克', 'jacket', 3),
('大衣', 'coat', 3),
('羽绒服', 'down', 3),
('休闲鞋', 'sneakers', 4),
('皮鞋', 'leather', 4),
('靴子', 'boots', 4),
('运动鞋', 'sports', 4),
('帽子', 'hat', 5),
('围巾', 'scarf', 5),
('皮带', 'belt', 5),
('手表', 'watch', 5),
('太阳镜', 'sunglasses', 5),
('手提包', 'handbag', 6),
('双肩包', 'backpack', 6),
('斜挎包', 'crossbody', 6),
('钱包', 'wallet', 6);

-- 初始化季节数据
INSERT INTO seasons (name, description) VALUES 
('春季', '3月-5月'),
('夏季', '6月-8月'),
('秋季', '9月-11月'),
('冬季', '12月-2月'),
('四季', '全年适用');
