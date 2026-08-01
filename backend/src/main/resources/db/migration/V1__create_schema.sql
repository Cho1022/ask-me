CREATE TABLE menus (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    group_name VARCHAR(100) NOT NULL,
    category VARCHAR(30) NOT NULL,
    description VARCHAR(500) NOT NULL,
    base_price INT NOT NULL,
    image_url VARCHAR(1000) NOT NULL,
    temperature VARCHAR(10) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE menu_aliases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_id BIGINT NOT NULL,
    alias VARCHAR(100) NOT NULL,
    CONSTRAINT fk_menu_alias_menu FOREIGN KEY (menu_id) REFERENCES menus (id),
    CONSTRAINT uk_menu_alias UNIQUE (menu_id, alias)
);

CREATE TABLE menu_options (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_id BIGINT NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    additional_price INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_menu_option_menu FOREIGN KEY (menu_id) REFERENCES menus (id),
    CONSTRAINT uk_menu_option_code UNIQUE (menu_id, code)
);

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(30) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    total_price INT NOT NULL,
    original_transcript VARCHAR(2000),
    order_channel VARCHAR(20) NOT NULL,
    service_mode VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    confirmed_at DATETIME NOT NULL
);

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    menu_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    size VARCHAR(20) NOT NULL,
    unit_price INT NOT NULL,
    total_price INT NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE TABLE order_item_options (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_item_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    option_code VARCHAR(50) NOT NULL,
    option_name VARCHAR(100) NOT NULL,
    additional_price INT NOT NULL,
    CONSTRAINT fk_order_item_option_item FOREIGN KEY (order_item_id) REFERENCES order_items (id)
);
