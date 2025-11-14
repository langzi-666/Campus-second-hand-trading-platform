-- 校园二手交易平台数据库初始化脚本
-- 创建时间: 2024-11-14
-- 版本: 1.0.0

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_trade;

-- 1. 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    student_id VARCHAR(20) COMMENT '学号',
    school VARCHAR(100) COMMENT '学校',
    college VARCHAR(100) COMMENT '学院',
    major VARCHAR(100) COMMENT '专业',
    grade YEAR COMMENT '年级',
    gender TINYINT COMMENT '性别：1男，2女，0未知',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常，0禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='用户表';

-- 2. 商品分类表
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    icon VARCHAR(200) COMMENT '分类图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort_order (sort_order),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='商品分类表';

-- 3. 商品表
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '发布用户ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    title VARCHAR(200) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    images TEXT COMMENT '商品图片，JSON格式存储',
    condition_level TINYINT NOT NULL COMMENT '新旧程度：1全新，2几乎全新，3轻微使用，4明显使用，5重度使用',
    location VARCHAR(200) COMMENT '交易地点',
    contact_info VARCHAR(500) COMMENT '联系方式',
    status TINYINT DEFAULT 1 COMMENT '状态：1在售，2已售，3下架，4删除',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    favorite_count INT DEFAULT 0 COMMENT '收藏次数',
    is_featured TINYINT DEFAULT 0 COMMENT '是否推荐：1是，0否',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_price (price),
    INDEX idx_created_at (created_at),
    FULLTEXT idx_title_desc (title, description),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB COMMENT='商品表';

-- 4. 商品收藏表
CREATE TABLE product_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='商品收藏表';

-- 5. 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(32) UNIQUE NOT NULL COMMENT '订单号',
    buyer_id BIGINT NOT NULL COMMENT '买家ID',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_title VARCHAR(200) NOT NULL COMMENT '商品标题快照',
    product_image VARCHAR(500) COMMENT '商品图片快照',
    price DECIMAL(10,2) NOT NULL COMMENT '交易价格',
    trade_method TINYINT DEFAULT 1 COMMENT '交易方式：1面交，2邮寄',
    trade_location VARCHAR(200) COMMENT '交易地点',
    buyer_contact VARCHAR(200) COMMENT '买家联系方式',
    seller_contact VARCHAR(200) COMMENT '卖家联系方式',
    status TINYINT DEFAULT 1 COMMENT '状态：1待确认，2已确认，3交易中，4已完成，5已取消',
    remark TEXT COMMENT '备注',
    buyer_confirm_at TIMESTAMP NULL COMMENT '买家确认时间',
    seller_confirm_at TIMESTAMP NULL COMMENT '卖家确认时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    cancelled_at TIMESTAMP NULL COMMENT '取消时间',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB COMMENT='订单表';

-- 6. 消息表
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    product_id BIGINT COMMENT '关联商品ID',
    order_id BIGINT COMMENT '关联订单ID',
    content TEXT NOT NULL COMMENT '消息内容',
    type TINYINT DEFAULT 1 COMMENT '消息类型：1文本，2图片，3商品卡片',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0未读，1已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_product_id (product_id),
    INDEX idx_order_id (order_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='消息表';

-- 7. 评价表
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    reviewer_id BIGINT NOT NULL COMMENT '评价人ID',
    reviewee_id BIGINT NOT NULL COMMENT '被评价人ID',
    rating TINYINT NOT NULL COMMENT '评分(1-5)',
    content TEXT COMMENT '评价内容',
    is_anonymous TINYINT DEFAULT 0 COMMENT '是否匿名：0否，1是',
    reply_content TEXT COMMENT '回复内容',
    reply_at TIMESTAMP NULL COMMENT '回复时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_order_id (order_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_reviewee_id (reviewee_id),
    INDEX idx_rating (rating),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES users(id),
    FOREIGN KEY (reviewee_id) REFERENCES users(id)
) ENGINE=InnoDB COMMENT='评价表';

-- 8. 系统通知表
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type TINYINT NOT NULL COMMENT '通知类型：1系统通知，2交易通知，3消息通知',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    related_id BIGINT COMMENT '关联ID（商品、订单等）',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0未读，1已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='系统通知表';

-- 插入初始数据

-- 插入商品分类
INSERT INTO categories (name, parent_id, sort_order) VALUES
('数码产品', 0, 1),
('学习用品', 0, 2),
('生活用品', 0, 3),
('服装鞋帽', 0, 4),
('运动器材', 0, 5),
('其他', 0, 6);

INSERT INTO categories (name, parent_id, sort_order) VALUES
('手机', 1, 1),
('电脑', 1, 2),
('相机', 1, 3),
('耳机音响', 1, 4),
('教材', 2, 1),
('文具', 2, 2),
('资料', 2, 3),
('家具', 3, 1),
('电器', 3, 2),
('日用品', 3, 3),
('男装', 4, 1),
('女装', 4, 2),
('鞋子', 4, 3),
('健身器材', 5, 1),
('球类', 5, 2),
('户外用品', 5, 3);

-- 插入测试用户（明文密码123456）
INSERT INTO users (username, email, password, nickname, student_id, school, college) VALUES
('admin', 'admin@campus.edu', '123456', '管理员', '2020001', '示例大学', '计算机学院'),
('student1', 'student1@campus.edu', '123456', '张三', '2020002', '示例大学', '计算机学院'),
('student2', 'student2@campus.edu', '123456', '李四', '2020003', '示例大学', '经济学院');

-- 插入测试商品
INSERT INTO products (user_id, category_id, title, description, price, original_price, condition_level, location) VALUES
(2, 7, 'iPhone 13 128G 蓝色', '9成新iPhone 13，无磕碰，功能正常，配件齐全', 4500.00, 5999.00, 2, '宿舍楼下'),
(2, 8, 'MacBook Air M1', '轻度使用，性能优秀，适合学习办公', 6800.00, 7999.00, 3, '图书馆'),
(3, 11, '高等数学教材', '同济版高等数学上下册，几乎全新', 45.00, 89.00, 2, '教学楼');

COMMIT;
