-- ========== DROP ==========
DROP TABLE IF EXISTS chat CASCADE;
DROP TABLE IF EXISTS favorite_item CASCADE;
DROP TABLE IF EXISTS review CASCADE;
DROP TABLE IF EXISTS app_order CASCADE;
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS user_complaint CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ========== CREATE ==========

-- ユーザー情報を管理するテーブル
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ADMIN')), -- ロールを 'USER' / 'ADMIN' に限定
    line_notify_token VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    -- BAN 管理項目
    banned BOOLEAN NOT NULL DEFAULT FALSE,
    ban_reason TEXT,
    banned_at TIMESTAMP,
    banned_by_admin_id INT, -- BAN 実行管理者
    FOREIGN KEY (banned_by_admin_id) REFERENCES users(id) -- 自己参照キーを追加
);

-- カテゴリ（ファッション、家具、家電など）
CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 出品商品テーブル
CREATE TABLE item (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL, -- 出品者 ID
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0), -- 価格は 0 以上を保証
    category_id INT,
    status VARCHAR(20) NOT NULL DEFAULT '出品中' CHECK (status IN ('出品中', '売却済', '削除済')), -- 状態を限定
    image_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
);

-- 注文情報テーブル
CREATE TABLE app_order (
    id SERIAL PRIMARY KEY,
    item_id INT NOT NULL,
    buyer_id INT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT '購入済' CHECK (status IN ('購入済', '取引完了', 'キャンセル')), -- 状態を限定
    payment_intent_id VARCHAR(128) UNIQUE, -- Stripe PaymentIntent IDはユニーク
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES item(id),
    FOREIGN KEY (buyer_id) REFERENCES users(id)
);

-- チャット（交渉・質問用）
CREATE TABLE chat (
    id SERIAL PRIMARY KEY,
    item_id INT NOT NULL,
    sender_id INT NOT NULL, -- 発言者
    message TEXT NOT NULL, -- 内容は必須
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES item(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

-- お気に入り商品
CREATE TABLE favorite_item (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, item_id), -- 重複登録禁止
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (item_id) REFERENCES item(id)
);

-- 購入後レビュー
CREATE TABLE review (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    reviewer_id INT NOT NULL,
    seller_id INT NOT NULL,
    item_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5), -- 星 1〜5
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES app_order(id), -- 注文 1 件にレビュー1 件
    FOREIGN KEY (reviewer_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id),
    FOREIGN KEY (item_id) REFERENCES item(id)
);

-- 通報情報（ユーザー同士）
CREATE TABLE user_complaint (
    id SERIAL PRIMARY KEY,
    reported_user_id INT NOT NULL, -- 通報されたユーザー
    reporter_user_id INT NOT NULL, -- 通報者
    reason TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reported_user_id) REFERENCES users(id),
    FOREIGN KEY (reporter_user_id) REFERENCES users(id)
);

-- ========== INDEX ==========
-- BAN 状態、カテゴリー、検索などの高速化目的

CREATE INDEX idx_users_banned ON users(banned);
CREATE INDEX idx_users_banned_by ON users(banned_by_admin_id);

CREATE INDEX idx_item_user_id ON item(user_id);
CREATE INDEX idx_item_category_id ON item(category_id);

CREATE INDEX idx_order_item_id ON app_order(item_id);
CREATE INDEX idx_order_buyer_id ON app_order(buyer_id);
CREATE UNIQUE INDEX ux_order_pi ON app_order(payment_intent_id);

CREATE INDEX idx_chat_item_id ON chat(item_id);
CREATE INDEX idx_chat_sender_id ON chat(sender_id);

CREATE INDEX idx_fav_user_id ON favorite_item(user_id);
CREATE INDEX idx_fav_item_id ON favorite_item(item_id);

CREATE INDEX idx_review_order_id ON review(order_id);

CREATE INDEX idx_uc_reported ON user_complaint(reported_user_id);
CREATE INDEX idx_uc_reporter ON user_complaint(reporter_user_id);