create table book (
  book_id   int primary key auto_increment not null,
  book_name varchar(200)                   not null,
  author    varchar(200)                   not null,
);

insert into book (book_name, author) values ('To Kill a Mockingbird', 'Harper Lee');
insert into book (book_name, author) values ('Pride and Prejudice', 'Jane Austen');
insert into book (book_name, author) values ('Animal Farm', 'George Orwell');
insert into book (book_name, author) values ('The Book Thief', 'Markus Zusak');
