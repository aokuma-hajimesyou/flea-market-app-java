INSERT INTO users (name, email, password, role, enabled)
VALUES
('出品者 A', 'sellerA@example.com', '{noop}password', 'USER', TRUE),
('購入者 A', 'buyerA@example.com', '{noop}password', 'USER', TRUE),
('購入者 B', 'buyerB@example.com', '{noop}password', 'USER', TRUE),
('運営者 A', 'adminA@example.com', '{noop}adminpass', 'ADMIN', TRUE);

INSERT INTO category (name) VALUES
('本'),
('家電'),
('ファッション'),
('おもちゃ'),
('文房具');

INSERT INTO item (user_id, name, description, price, category_id, status)
VALUES
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    'Java プログラミング入門',
    '初心者向けの Java 入門書です。',
    1500.00,
    (SELECT id FROM category WHERE name='本'),
    '出品中'
),
(
    (SELECT id FROM users WHERE email='sellerA@example.com'),
    'ワイヤレスイヤホン',
    'ノイズキャンセリング機能付き。',
    8000.00,
    (SELECT id FROM category WHERE name='家電'),
    '出品中'
);