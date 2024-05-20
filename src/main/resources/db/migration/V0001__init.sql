
CREATE TABLE IF NOT EXISTS book
(
    isbn TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    subject TEXT NOT NULL,
    overview TEXT,
    publisher TEXT NOT NULL,
    publication_date Date NOT NULL,
    lang TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS book_item
(
    barcode TEXT NOT NULL,
    tag UUID NOT NULL,
    isbn TEXT NOT NULL,
    subject TEXT NOT NULL,
    title TEXT NOT NULL,
    is_reference_only BOOLEAN NOT NULL,
    lang TEXT NOT NULL,
    number_of_pages INTEGER NOT NULL,
    format TEXT NOT NULL,
    borrowed Date NOT NULL,
    loan_period INTEGER NOT NULL,
    due_date Date NOT NULL,
    is_overdue BOOLEAN NOT NULL,
    PRIMARY KEY(barcode, tag)
);

CREATE TABLE author
(
    name TEXT PRIMARY KEY,
    biography TEXT NOT NULL,
    publication_date Date NOT NULL
);

CREATE TABLE account
(
    isbn UUID PRIMARY KEY,
    history TEXT [] NOT NULL,
    opened Date NOT NULL,
    status TEXT NOT NULL
);

CREATE TABLE library
(
    name TEXT PRIMARY KEY,
    address TEXT NOT NULL
);

CREATE TABLE patron
(
    name TEXT PRIMARY KEY,
    address TEXT NOT NULL
);

CREATE TABLE librarian
(
    name TEXT PRIMARY KEY,
    address TEXT NOT NULL,
    position TEXT NOT NULL
);

