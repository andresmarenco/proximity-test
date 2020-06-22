CREATE TABLE IF NOT EXISTS vending_machine (
    id uuid primary key,
    remote_address varchar(255),
    port int,
    balance decimal
);


CREATE TABLE IF NOT EXISTS alert (
    id uuid primary key,
    alert_timestamp timestamp,
    machine_id uuid,
    solved boolean,
    CONSTRAINT alert_machine_id FOREIGN KEY (machine_id) REFERENCES vending_machine(id)
);