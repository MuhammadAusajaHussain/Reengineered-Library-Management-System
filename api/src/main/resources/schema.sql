CREATE TABLE app_user (
    id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    full_name VARCHAR(80) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE borrower_profile (
    user_id INTEGER NOT NULL PRIMARY KEY,
    address VARCHAR(120) NOT NULL,
    phone_no VARCHAR(25) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE book (
    id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn VARCHAR(30),
    title VARCHAR(120) NOT NULL,
    author VARCHAR(80) NOT NULL,
    subject VARCHAR(80) NOT NULL,
    total_copies INTEGER NOT NULL,
    available_copies INTEGER NOT NULL
);

CREATE TABLE loan (
    id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id INTEGER NOT NULL,
    borrower_user_id INTEGER NOT NULL,
    issued_by_user_id INTEGER NOT NULL,
    issue_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    returned_by_user_id INTEGER,
    fine_paid BOOLEAN NOT NULL,
    FOREIGN KEY (book_id) REFERENCES book(id),
    FOREIGN KEY (borrower_user_id) REFERENCES app_user(id),
    FOREIGN KEY (issued_by_user_id) REFERENCES app_user(id),
    FOREIGN KEY (returned_by_user_id) REFERENCES app_user(id)
);

CREATE TABLE hold_request (
    id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id INTEGER NOT NULL,
    borrower_user_id INTEGER NOT NULL,
    request_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (book_id) REFERENCES book(id),
    FOREIGN KEY (borrower_user_id) REFERENCES app_user(id)
);
