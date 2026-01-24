-- 1. ユーザー（初期データ）
INSERT INTO users (name, email, password, role, enabled)
VALUES
('出品者 A', 'sellerA@example.com', '{noop}password', 'USER', TRUE),
('購入者 A', 'buyerA@example.com', '{noop}password', 'USER', TRUE),
('購入者 B', 'buyerB@example.com', '{noop}password', 'USER', TRUE),
('運営者 A', 'adminA@example.com', '{noop}adminpass', 'ADMIN', TRUE);

-- 2. カテゴリー（階層構造）
-- 第1階層: 親 (parent_id IS NULL)
INSERT INTO category (name, parent_id) VALUES 
('ファッション', NULL),             -- ID: 1
('ベビー・キッズ', NULL),           -- ID: 2
('ゲーム・おもちゃ・グッズ', NULL),  -- ID: 3
('ホビー・楽器・アート', NULL),      -- ID: 4
('チケット', NULL),                 -- ID: 5
('本・雑誌・漫画', NULL),           -- ID: 6
('CD・DVD・ブルーレイ', NULL),      -- ID: 7
('スマホ・タブレット・パソコン', NULL), -- ID: 8
('テレビ・オーディオ・カメラ', NULL), -- ID: 9
('生活家電・空調', NULL),           -- ID: 10
('スポーツ', NULL),                 -- ID: 11
('アウトドア・釣り・旅行用品', NULL), -- ID: 12
('コスメ・美容', NULL),             -- ID: 13
('ダイエット・健康', NULL),          -- ID: 14
('食品・飲料・酒', NULL),           -- ID: 15
('キッチン・日用品・その他', NULL),  -- ID: 16
('家具・インテリア', NULL),          -- ID: 17
('ペット用品', NULL),               -- ID: 18
('DIY・工具', NULL),                -- ID: 19
('フラワー・ガーデニング', NULL),    -- ID: 20
('ハンドメイド・手芸', NULL),        -- ID: 21
('車・バイク・自転車', NULL);         -- ID: 22

-- 第2階層: 子
INSERT INTO category (name, parent_id) VALUES 
('靴', 1),             -- ID: 23
('トップス', 1),       -- ID: 24
('ベビー服', 2),       -- ID: 25
('テレビゲーム', 3),   -- ID: 26
('楽器/機材', 4),      -- ID: 27
('コンピュータ', 6),   -- ID: 28
('PC周辺機器', 8),     -- ID: 29
('カメラ', 9),         -- ID: 30
('キッチン家電', 10),    -- ID: 31
('トレーニング用品', 11), -- ID: 32
('スキンケア/基礎化粧品', 13), -- ID: 33
('菓子', 15),          -- ID: 34
('キッチン/食器', 16),  -- ID: 35
('バッグ', 1),         -- ID: 36
('アクセサリー', 1),    -- ID: 37
('トレーディングカード', 3), -- ID: 38
('漫画', 6),           -- ID: 39
('スマートフォン本体', 8), -- ID: 40
('PC/タブレット', 8),   -- ID: 41
('メイクアップ', 13);    -- ID: 42

-- 第3階層: 孫
INSERT INTO category (name, parent_id) VALUES 
('レディーススニーカー', 23), -- ID: 43
('パーカー', 24),            -- ID: 44
('男の子用(～95cm)', 25),    -- ID: 45
('Nintendo Switch', 26),   -- ID: 46
('エレキギター', 27),        -- ID: 47
('Javaプログラミング', 28),  -- ID: 48
('キーボード', 29),          -- ID: 49
('デジタルカメラ', 30),      -- ID: 50
('炊飯器', 31),              -- ID: 51
('ダンベル', 32),            -- ID: 52
('フェイスクリーム', 33),     -- ID: 53
('チョコレート', 34),        -- ID: 54
('調理器具', 35),            -- ID: 55
('リュック/バックパック', 36), -- ID: 56
('ネックレス', 37),          -- ID: 57
('ポケモンカードゲーム', 38),  -- ID: 58
('少年漫画', 39),            -- ID: 59
('iPhone', 40),            -- ID: 60
('Android本体', 40),       -- ID: 61
('ノートPC', 41),           -- ID: 62
('アイシャドウ', 42);         -- ID: 63


-- 3. アイテム
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
    'iPhone 15 Pro 256GB',
    '最新機種に買い替えたため出品します。バッテリー最大容量100%。',
    135000.00,
    (SELECT id FROM category WHERE name='iPhone'),
    '出品中',
    'https://images.unsplash.com/photo-1696446701796-da61225697cc?q=80&w=400',
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    'ポケモンカード リザードンVMAX',
    '開封後すぐにスリーブに入れました。傷なしの美品です。',
    8800.00,
    (SELECT id FROM category WHERE name='ポケモンカードゲーム'),
    '出品中',
    'https://images.unsplash.com/photo-1613771404721-1f92d799e49f?q=80&w=400',
    CURRENT_TIMESTAMP
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    'ノースフェイス リュック 30L',
    '通勤で使用していましたが、買い替えのため出品します。収納力抜群。',
    9500.00,
    (SELECT id FROM category WHERE name='リュック/バックパック'),
    '出品中',
    'https://c.imgz.jp/223/64206223/64206223b_8_d_500.jpg',
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

--お問い合わせジャンルデータ
INSERT INTO subject (name) VALUES
('取引トラブル'),
('通報・違反'),
('アプリ不具合'),
('改善要望');

--お問い合わせデータ
-- 未対応のケース
INSERT INTO feedback (user_id, subject_id, content, status, created_at)
VALUES (1, 1, 'パスワードを忘れてしまい、再設定メールが届きません。', '未対応', CURRENT_TIMESTAMP - INTERVAL '2 days');

-- 対応中のケース
INSERT INTO feedback (user_id, subject_id, content, status, created_at)
VALUES (1, 2, '商品を購入したのですが、決済エラーが表示されました。確認をお願いします。', '対応中', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- 対応済みのケース
INSERT INTO feedback (user_id, subject_id, content, status, created_at)
VALUES (1, 4, 'アプリの動作が重い時があります。キャッシュクリア以外に方法はありますか？', '対応済み', CURRENT_TIMESTAMP - INTERVAL '5 hours');

-- 長文のケース（詳細画面での改行確認用）
INSERT INTO feedback (user_id, subject_id, content, status, created_at)
VALUES (1, 3, '【至急】商品が届きませんが、出品者と連絡が取れません。
注文番号：ORD-12345
配送状況を確認したところ、一週間前からステータスが「発送準備中」のままです。
キャンセル可能でしょうか。', '未対応', CURRENT_TIMESTAMP);