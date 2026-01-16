INSERT INTO users (name, email, password, role, enabled)
VALUES
('出品者 A', 'sellerA@example.com', '{noop}password', 'USER', TRUE),
('購入者 A', 'buyerA@example.com', '{noop}password', 'USER', TRUE),
('購入者 B', 'buyerB@example.com', '{noop}password', 'USER', TRUE),
('運営者 A', 'adminA@example.com', '{noop}adminpass', 'ADMIN', TRUE);

-- 2. カテゴリー（階層構造）
-- 第1階層: 親 (parent_id IS NULL)
INSERT INTO category (name, parent_id) VALUES ('ファッション', NULL); -- ID: 1
INSERT INTO category (name, parent_id) VALUES ('家電', NULL);         -- ID: 2
INSERT INTO category (name, parent_id) VALUES ('本', NULL);           -- ID: 3

-- 第2階層: 子
INSERT INTO category (name, parent_id) VALUES ('靴', 1);             -- ID: 4 (ファッションの子)
INSERT INTO category (name, parent_id) VALUES ('トップス', 1);       -- ID: 5 (ファッションの子)
INSERT INTO category (name, parent_id) VALUES ('キッチン家電', 2);     -- ID: 6 (家電の子)
INSERT INTO category (name, parent_id) VALUES ('コンピュータ', 3);     -- ID: 7 (本の子)

-- 第3階層: 孫
INSERT INTO category (name, parent_id) VALUES ('レディーススニーカー', 4); -- ID: 8
INSERT INTO category (name, parent_id) VALUES ('パーカー', 5);            -- ID: 9
INSERT INTO category (name, parent_id) VALUES ('炊飯器', 6);              -- ID: 10
INSERT INTO category (name, parent_id) VALUES ('Javaプログラミング', 7);  -- ID: 11

INSERT INTO item (user_id, name, description, price, category_id, status, image_url, created_at)
VALUES
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    'ナイキ エアマックス 270',
    '数回使用した程度の美品です。履き心地がとても良いです。',
    12800.00,
    (SELECT id FROM category WHERE name='レディーススニーカー'),
    '出品中',
    'https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=400',
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    'オーバーサイズ グレーパーカー',
    'これからの季節にぴったりの厚手パーカーです。Lサイズ。',
    4500.00,
    (SELECT id FROM category WHERE name='パーカー'),
    '出品中',
    'https://images.unsplash.com/photo-1556821840-3a63f95609a7?q=80&w=400',
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    '圧力IH式 炊飯器 5.5合',
    '2024年製。モチモチのご飯が炊けます。箱なし。',
    18000.00,
    (SELECT id FROM category WHERE name='炊飯器'),
    '出品中',
    'https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?q=80&w=400',
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    'Spring Boot 実践ガイド',
    'Javaフレームワークの解説書です。背表紙に少し汚れあり。',
    3200.00,
    (SELECT id FROM category WHERE name='Javaプログラミング'),
    '出品中',
    'https://images.unsplash.com/photo-1589998059171-988d887df646?q=80&w=400',
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    'ハイカット 厚底スニーカー',
    'スタイルアップできるスニーカーです。色はホワイト。',
    7800.00,
    (SELECT id FROM category WHERE name='レディーススニーカー'),
    '出品中',
    'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?q=80&w=400',
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    'ロゴプリント スウェット',
    'シンプルなデザインで着回しやすい一着です。綿100%で肌触りも抜群です。',
    3500.00,
    (SELECT id FROM category WHERE name='パーカー'),
    '出品中',
    'https://images.unsplash.com/photo-1521223890158-f9f7c3d5d504?q=80&w=400',
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    '省エネ マイコン炊飯ジャー',
    '一人暮らしに最適な3合炊きです。タイマー機能付きで便利。',
    6800.00,
    (SELECT id FROM category WHERE name='炊飯器'),
    '出品中',
    -- より軽量で安定した画像URLに変更
    'https://images.unsplash.com/photo-1544233726-9f1d2b27be8b?q=80&w=400',
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    '徹底攻略 Java SE 11 Gold',
    '試験対策のために購入しました。書き込みはなく綺麗な状態です。',
    4200.00,
    (SELECT id FROM category WHERE name='Javaプログラミング'),
    '出品中',
    'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?q=80&w=400',
    CURRENT_TIMESTAMP
);
