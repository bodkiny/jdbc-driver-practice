DROP TABLE IF EXISTS customers;

CREATE TABLE customers
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    sex          VARCHAR(10)  NOT NULL,
    email        VARCHAR(70)  NOT NULL,
    phone_number VARCHAR(13),
    address      VARCHAR(100),
    CONSTRAINT customers_sex CHECK (sex IN ('male', 'female'))
);

INSERT INTO customers (name, sex, email, phone_number, address)
VALUES ('John Doe', 'male', 'john.doe@example.com', '1234567890', '123 Main St'),
       ('Jane Doe', 'female', 'jane.doe@example.com', '0987654321', '456 Main St'),
       ('Alice Smith', 'female', 'alice.smith@example.com', '1112223333', '789 Main St'),
       ('Bob Johnson', 'male', 'bob.johnson@example.com', '4445556666', '321 Main St'),
       ('Charlie Brown', 'male', 'charlie.brown@example.com', '7778889999', '654 Main St'),
       ('Diana Prince', 'female', 'diana.prince@example.com', '0001112222', '987 Main St'),
       ('Edward Cullen', 'male', 'edward.cullen@example.com', '3334445555', '123 Secondary St'),
       ('Fiona Apple', 'female', 'fiona.apple@example.com', '6667778888', '456 Secondary St'),
       ('George Washington', 'male', 'george.washington@example.com', '9990001111', '789 Secondary St'),
       ('Hannah Montana', 'female', 'hannah.montana@example.com', '2223334444', '321 Secondary St'),
       ('Igor Stravinsky', 'male', 'igor.stravinsky@example.com', '5556667777', '654 Secondary St'),
       ('Julia Roberts', 'female', 'julia.roberts@example.com', '8889990001', '987 Secondary St'),
       ('Kevin Hart', 'male', 'kevin.hart@example.com', '1112223334', '123 Tertiary St'),
       ('Linda McCartney', 'female', 'linda.mccartney@example.com', '4445556667', '456 Tertiary St'),
       ('Michael Jordan', 'male', 'michael.jordan@example.com', '7778889992', '789 Tertiary St');