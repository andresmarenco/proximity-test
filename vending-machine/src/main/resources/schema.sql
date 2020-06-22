CREATE TABLE IF NOT EXISTS item (
    id uuid primary key,
    name varchar(250),
    cost decimal
);


CREATE TABLE IF NOT EXISTS storage_location (
    code varchar(10) primary key,
    item_id uuid,
    quantity int,
    CONSTRAINT storage_location_item_id FOREIGN KEY (item_id) REFERENCES item(id)
);


CREATE TABLE IF NOT EXISTS `transaction` (
    dtype varchar(20),
    sale_timestamp timestamp,
    id uuid primary key,
    item_id uuid,
    quantity int,
    total decimal,
    CONSTRAINT transaction_item_id FOREIGN KEY (item_id) REFERENCES item(id)
);


CREATE TABLE IF NOT EXISTS open_attempt (
    id uuid primary key,
    attempt_timestamp timestamp,
    success boolean
);


CREATE TABLE IF NOT EXISTS cash (
    denomination int primary key,
    quantity int,
    type varchar(10)
);