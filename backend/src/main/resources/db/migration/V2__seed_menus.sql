INSERT INTO menus
    (id, name, group_name, category, description, base_price, image_url, temperature, active, sort_order)
VALUES
    (1, '아이스 아메리카노', '아메리카노', 'coffee', '차갑고 깔끔하게 즐기는 기본 커피', 3000, 'https://images.pexels.com/photos/34932738/pexels-photo-34932738.jpeg?auto=compress&cs=tinysrgb&w=900', 'ICE', TRUE, 10),
    (2, '핫 아메리카노', '아메리카노', 'coffee', '따뜻하고 진한 향이 살아 있는 기본 커피', 3000, 'https://images.unsplash.com/photo-1517701604599-bb29b565090c?auto=format&fit=crop&w=900&q=80', 'HOT', TRUE, 11),
    (3, '아이스 카페라떼', '카페라떼', 'coffee', '우유 풍미가 부드럽게 어우러지는 차가운 라떼', 3800, 'https://images.unsplash.com/photo-1521017432531-fbd92d768814?auto=format&fit=crop&w=900&q=80', 'ICE', TRUE, 20),
    (4, '핫 카페라떼', '카페라떼', 'coffee', '우유와 에스프레소가 부드럽게 어우러지는 따뜻한 라떼', 3800, 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=900&q=80', 'HOT', TRUE, 21),
    (5, '아이스 바닐라라떼', '바닐라라떼', 'coffee', '달콤한 바닐라 향이 살아 있는 시그니처 라떼', 4300, 'https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?auto=format&fit=crop&w=900&q=80', 'ICE', TRUE, 30),
    (6, '핫 바닐라라떼', '바닐라라떼', 'coffee', '따뜻하고 달콤한 바닐라 향이 퍼지는 라떼', 4300, 'https://images.unsplash.com/photo-1517701550927-30cf4ba1f4d0?auto=format&fit=crop&w=900&q=80', 'HOT', TRUE, 31),
    (7, '아이스 카라멜마키아또', '카라멜마키아또', 'coffee', '달콤한 카라멜 풍미가 살아 있는 차가운 커피', 4500, 'https://images.pexels.com/photos/34969154/pexels-photo-34969154.jpeg?auto=compress&cs=tinysrgb&w=900', 'ICE', TRUE, 40),
    (8, '핫 카라멜마키아또', '카라멜마키아또', 'coffee', '달콤한 카라멜 향이 풍부한 따뜻한 커피', 4500, 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=900&q=80', 'HOT', TRUE, 41),
    (9, '블루베리스무디', '블루베리스무디', 'beverage', '블루베리 풍미가 진하게 살아 있는 아이스 스무디', 4800, 'https://images.pexels.com/photos/6671856/pexels-photo-6671856.png?auto=compress&cs=tinysrgb&w=900', 'ICE', TRUE, 50),
    (10, '딸기라떼', '딸기라떼', 'beverage', '딸기와 우유가 어우러진 달콤한 아이스 라떼', 4700, 'https://images.pexels.com/photos/33039199/pexels-photo-33039199.jpeg?auto=compress&cs=tinysrgb&w=900', 'ICE', TRUE, 60),
    (11, '녹차라떼', '녹차라떼', 'beverage', '부드러운 우유와 녹차 향이 어우러진 아이스 라떼', 4600, 'https://images.pexels.com/photos/33792899/pexels-photo-33792899.jpeg?auto=compress&cs=tinysrgb&w=900', 'ICE', TRUE, 70),
    (12, '캐모마일', '캐모마일', 'tea', '은은한 꽃향기로 편안하게 즐기는 따뜻한 티', 3900, 'https://images.pexels.com/photos/34151717/pexels-photo-34151717.jpeg?auto=compress&cs=tinysrgb&w=900', 'HOT', TRUE, 80),
    (13, '녹차', '녹차', 'tea', '깔끔하고 담백한 풍미가 좋은 따뜻한 녹차', 3900, 'https://images.unsplash.com/photo-1464305795204-6f5bbfc7fb81?auto=format&fit=crop&w=900&q=80', 'HOT', TRUE, 90),
    (14, '유자차', '유자차', 'tea', '상큼한 유자 향을 따뜻하게 즐기는 티', 4200, 'https://images.pexels.com/photos/30216025/pexels-photo-30216025.jpeg?auto=compress&cs=tinysrgb&w=900', 'HOT', TRUE, 100),
    (15, '아이스 레몬에이드', '레몬에이드', 'ade', '레몬 향이 톡 쏘는 상큼한 아이스 에이드', 4500, 'https://images.unsplash.com/photo-1497534446932-c925b458314e?auto=format&fit=crop&w=900&q=80', 'ICE', TRUE, 110),
    (16, '핫 레몬에이드', '레몬에이드', 'ade', '따뜻하게 즐기는 레몬 베이스 에이드', 4500, 'https://images.unsplash.com/photo-1556881286-fc6915169721?auto=format&fit=crop&w=900&q=80', 'HOT', TRUE, 111),
    (17, '아이스 자몽에이드', '자몽에이드', 'ade', '자몽 풍미가 시원하게 살아 있는 에이드', 4700, 'https://images.pexels.com/photos/30446324/pexels-photo-30446324.jpeg?auto=compress&cs=tinysrgb&w=900', 'ICE', TRUE, 120),
    (18, '핫 자몽에이드', '자몽에이드', 'ade', '자몽 향을 따뜻하게 즐기는 에이드', 4700, 'https://images.pexels.com/photos/15529728/pexels-photo-15529728.jpeg?auto=compress&cs=tinysrgb&w=900', 'HOT', TRUE, 121),
    (19, '아이스 청포도에이드', '청포도에이드', 'ade', '청포도의 산뜻한 풍미가 살아 있는 아이스 에이드', 4700, 'https://images.pexels.com/photos/31133903/pexels-photo-31133903.jpeg?auto=compress&cs=tinysrgb&w=900', 'ICE', TRUE, 130),
    (20, '핫 청포도에이드', '청포도에이드', 'ade', '청포도의 달콤함을 따뜻하게 즐기는 에이드', 4700, 'https://images.pexels.com/photos/31133903/pexels-photo-31133903.jpeg?auto=compress&cs=tinysrgb&w=900', 'HOT', TRUE, 131);

INSERT INTO menu_aliases (menu_id, alias) VALUES
    (1, '아아'), (1, '아이스아메리카노'), (1, '차가운 아메리카노'),
    (2, '뜨아'), (2, '핫아메리카노'), (2, '따뜻한 아메리카노'),
    (3, '아이스라떼'), (3, '아이스 카페 라떼'), (3, '카페라떼'), (3, '라떼'),
    (4, '핫라떼'), (4, '따뜻한 라떼'), (4, '카페라떼'), (4, '라떼'),
    (5, '아바라'), (5, '아이스바닐라라떼'),
    (6, '핫바닐라라떼'), (6, '따뜻한 바닐라라떼'),
    (7, '아이스카라멜마키아또'), (7, '카라멜마끼아또'),
    (8, '핫카라멜마키아또'), (8, '따뜻한 카라멜마키아또'),
    (9, '블루베리 스무디'), (10, '딸기 라떼'),
    (11, '말차라떼'), (11, '말차 라떼'),
    (12, '캐모마일티'), (13, '그린티'), (14, '유자 티'),
    (15, '아이스레몬에이드'), (16, '따뜻한 레몬에이드'),
    (17, '아이스자몽에이드'), (18, '따뜻한 자몽에이드'),
    (19, '아이스청포도에이드'), (20, '따뜻한 청포도에이드');

INSERT INTO menu_options (menu_id, code, name, type, additional_price, active)
SELECT id, 'EXTRA_SHOT', '샷 추가', 'ADDITION', 500, TRUE FROM menus WHERE category = 'coffee';

INSERT INTO menu_options (menu_id, code, name, type, additional_price, active)
SELECT id, 'NO_SYRUP', '시럽 제외', 'EXCLUSION', 0, TRUE FROM menus WHERE category = 'coffee';
