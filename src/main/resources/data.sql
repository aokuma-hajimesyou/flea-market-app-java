-- ==========================================================================
-- 1. ユーザー
-- ==========================================================================
INSERT INTO users (name, email, password, role, enabled)
VALUES
('出品者 A', 'sellerA@example.com', '{noop}password', 'USER', TRUE),
('購入者 A', 'buyerA@example.com', '{noop}password', 'USER', TRUE),
('購入者 B', 'buyerB@example.com', '{noop}password', 'USER', TRUE),
('運営者 A', 'adminA@example.com', '{noop}adminpass', 'ADMIN', TRUE);

-- ==========================================================================
-- 2. カテゴリー（階層構造）
-- ==========================================================================
-- 第1階層: 親
INSERT INTO category (name, parent_id) VALUES 
('ファッション', NULL),             -- 1
('ベビー・キッズ', NULL),           -- 2
('ゲーム・おもちゃ・グッズ', NULL),  -- 3
('ホビー・楽器・アート', NULL),      -- 4
('チケット', NULL),                 -- 5
('本・雑誌・漫画', NULL),           -- 6
('CD・DVD・ブルーレイ', NULL),      -- 7
('スマホ・タブレット・パソコン', NULL), -- 8
('テレビ・オーディオ・カメラ', NULL), -- 9
('生活家電・空調', NULL),           -- 10
('スポーツ', NULL),                 -- 11
('アウトドア・釣り・旅行用品', NULL), -- 12
('コスメ・美容', NULL),             -- 13
('ダイエット・健康', NULL),          -- 14
('食品・飲料・酒', NULL),           -- 15
('キッチン・日用品・その他', NULL),  -- 16
('家具・インテリア', NULL),          -- 17
('ペット用品', NULL),               -- 18
('DIY・工具', NULL),                -- 19
('フラワー・ガーデニング', NULL),    -- 20
('ハンドメイド・手芸', NULL),        -- 21
('車・バイク・自転車', NULL),         -- 22
('エンタメ・ホビー', NULL);          -- 23 (新規追加)

-- 第2階層: 子
INSERT INTO category (name, parent_id) VALUES 
('靴', 1), ('トップス', 1), ('バッグ', 1), ('アクセサリー', 1),
('ベビー服', 2),
('テレビゲーム', 3), ('トレーディングカード', 3),
('楽器/機材', 4),
('コンピュータ', 6), ('漫画', 6),
('PC周辺機器', 8), ('スマートフォン本体', 8), ('PC/タブレット', 8),
('カメラ', 9),
('キッチン家電', 10),
('トレーニング用品', 11),
('スキンケア/基礎化粧品', 13), ('メイクアップ', 13),
('菓子', 15),
('キッチン/食器', 16),
('素材/材料', 21),
('おもちゃ', 23),            -- エンタメ・ホビーの子
('照明', 17),               -- 家具の子
('猫用品', 18),             -- ペットの子
('自転車', 22);             -- 車・バイク・自転車の子

-- 第3階層: 孫
INSERT INTO category (name, parent_id) VALUES 
('レディーススニーカー', (SELECT id FROM category WHERE name='靴')),
('パーカー', (SELECT id FROM category WHERE name='トップス')),
('男の子用(～95cm)', (SELECT id FROM category WHERE name='ベビー服')),
('Nintendo Switch', (SELECT id FROM category WHERE name='テレビゲーム')),
('エレキギター', (SELECT id FROM category WHERE name='楽器/機材')),
('Javaプログラミング', (SELECT id FROM category WHERE name='コンピュータ')),
('キーボード', (SELECT id FROM category WHERE name='PC周辺機器')),
('デジタルカメラ', (SELECT id FROM category WHERE name='カメラ')),
('炊飯器', (SELECT id FROM category WHERE name='キッチン家電')),
('ダンベル', (SELECT id FROM category WHERE name='トレーニング用品')),
('フェイスクリーム', (SELECT id FROM category WHERE name='スキンケア/基礎化粧品')),
('チョコレート', (SELECT id FROM category WHERE name='菓子')),
('調理器具', (SELECT id FROM category WHERE name='キッチン/食器')),
('リュック/バックパック', (SELECT id FROM category WHERE name='バッグ')),
('ネックレス', (SELECT id FROM category WHERE name='アクセサリー')),
('ポケモンカードゲーム', (SELECT id FROM category WHERE name='トレーディングカード')),
('少年漫画', (SELECT id FROM category WHERE name='漫画')),
('iPhone', (SELECT id FROM category WHERE name='スマートフォン本体')),
('Android本体', (SELECT id FROM category WHERE name='スマートフォン本体')),
('ノートPC', (SELECT id FROM category WHERE name='PC/タブレット')),
('アイシャドウ', (SELECT id FROM category WHERE name='メイクアップ')),
('ぬいぐるみ', (SELECT id FROM category WHERE name='おもちゃ')),
('フィギュア', (SELECT id FROM category WHERE name='おもちゃ')),
('各種パーツ', (SELECT id FROM category WHERE name='素材/材料')),
('デスクライト', (SELECT id FROM category WHERE name='照明')),
('キャットタワー', (SELECT id FROM category WHERE name='猫用品')),
('クロスバイク', (SELECT id FROM category WHERE name='自転車'));


--お問い合わせデータ
INSERT INTO subject (name) VALUES
('取引トラブル'),
('通報・違反'),
('アプリ不具合'),
('改善要望');


-- 3. アイテム
-- ==========================================================================
INSERT INTO item (user_id, name, description, price, category_id, status, image_url, created_at)
VALUES
(1, 'ナイキ エアマックス 270', '数回使用した程度の美品です。', 12800.00, (SELECT id FROM category WHERE name='レディーススニーカー'), '売却済', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'オーバーサイズ グレーパーカー', '厚手パーカー。Lサイズ。', 4500.00, (SELECT id FROM category WHERE name='パーカー'), '出品中', 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?q=80&w=400', CURRENT_TIMESTAMP),
(1, '圧力IH式 炊飯器 5.5合', '2024年製。', 18000.00, (SELECT id FROM category WHERE name='炊飯器'), '出品中', 'https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'Spring Boot 実践ガイド', 'Javaフレームワークの解説書。', 3200.00, (SELECT id FROM category WHERE name='Javaプログラミング'), '出品中', 'https://images.unsplash.com/photo-1589998059171-988d887df646?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'iPhone 15 Pro 256GB', 'バッテリー100%。', 135000.00, (SELECT id FROM category WHERE name='iPhone'), '出品中', 'https://images.unsplash.com/photo-1696446701796-da61225697cc?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'ポケモンカード リザードンVMAX', 'スリーブ保存。', 8800.00, (SELECT id FROM category WHERE name='ポケモンカードゲーム'), '出品中', 'https://images.unsplash.com/photo-1613771404721-1f92d799e49f?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'ノースフェイス リュック 30L', '収納力抜群。', 9500.00, (SELECT id FROM category WHERE name='リュック/バックパック'), '出品中', 'https://c.imgz.jp/223/64206223/64206223b_8_d_500.jpg', CURRENT_TIMESTAMP),
(1, '徹底攻略 Java SE 11 Gold', '試験対策本。', 4200.00, (SELECT id FROM category WHERE name='Javaプログラミング'), '出品中', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?q=80&w=400', CURRENT_TIMESTAMP),
(1, '限定品！くまのぬいぐるみ', 'タグ付き新品。', 3800.00, (SELECT id FROM category WHERE name='ぬいぐるみ'), '出品中', 'https://images.unsplash.com/photo-1546238232-20216dec9f72?q=80&w=400', CURRENT_TIMESTAMP),
(1, '癒し顔 うさぎのぬいぐるみ', '全長約40cm。', 2500.00, (SELECT id FROM category WHERE name='ぬいぐるみ'), '出品中', 'https://images.unsplash.com/photo-1590431306482-f700ee050c59?q=80&w=400', CURRENT_TIMESTAMP),
(1, '人気アニメ フィギュア', '未開封。', 7800.00, (SELECT id FROM category WHERE name='フィギュア'), '出品中', 'https://images.unsplash.com/photo-1587652721822-1f3a25434193?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'フィギュアセット', '3体セット。', 11000.00, (SELECT id FROM category WHERE name='フィギュア'), '出品中', 'https://images.unsplash.com/photo-1606011334314-a158aa558c4f?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'ハンドメイド チャーム', '100個セット。', 1800.00, (SELECT id FROM category WHERE name='各種パーツ'), '出品中', 'https://images.unsplash.com/photo-1495639148545-a2295a4675a5?q=80&w=400', CURRENT_TIMESTAMP),
(1, '天然石ビーズ', 'アソート。', 3200.00, (SELECT id FROM category WHERE name='各種パーツ'), '出品中', 'https://images.unsplash.com/photo-1599388136367-27a331a90a20?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'Canon EOS Kiss M', 'レンズセット。', 55000.00, (SELECT id FROM category WHERE name='デジタルカメラ'), '出品中', 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?q=80&w=400', CURRENT_TIMESTAMP),
(1, '人気漫画 全巻セット', '1巻から最終巻まで。', 15000.00, (SELECT id FROM category WHERE name='少年漫画'), '出品中', 'https://images.unsplash.com/photo-1618365908642-a73a33b8a355?q=80&w=400', CURRENT_TIMESTAMP),
-- 追加分 (既存カテゴリ)
(1, 'Nintendo Switch 本体', '有機ELモデル。', 31000.00, (SELECT id FROM category WHERE name='Nintendo Switch'), '出品中', 'https://images.unsplash.com/photo-1578303372290-3e9c75fe474a?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'Fender ストラト', 'ソフトケース付き。', 48000.00, (SELECT id FROM category WHERE name='エレキギター'), '出品中', 'https://images.unsplash.com/photo-1550291652-6ea9114a47b1?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'MacBook Air M2', '16GB/512GB。', 142000.00, (SELECT id FROM category WHERE name='ノートPC'), '出品中', 'https://images.unsplash.com/photo-1517336714460-457883bc1276?q=80&w=400', CURRENT_TIMESTAMP),
-- 追加分 (新設カテゴリ)
(1, '北欧風 デスクライト', '温かみのあるLED。', 5200.00, (SELECT id FROM category WHERE name='デスクライト'), '出品中', 'https://images.unsplash.com/photo-1534073828943-f801091bb18c?q=80&w=400', CURRENT_TIMESTAMP),
(1, '木製 キャットタワー', '突っ張り型。', 12800.00, (SELECT id FROM category WHERE name='キャットタワー'), '出品中', 'https://images.unsplash.com/photo-1545249390-6bdfa286032f?q=80&w=400', CURRENT_TIMESTAMP),
(1, 'GIANT クロスバイク', '室内保管美品。', 38000.00, (SELECT id FROM category WHERE name='クロスバイク'), '出品中', 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?q=80&w=400', CURRENT_TIMESTAMP);