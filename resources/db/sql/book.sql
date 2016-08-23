-- :name get-books
-- :doc Gets all the books
select * from book;


-- :name create-new-book! :! :n
-- :doc Creates a new book record
insert into book (book_name, author) values (:book_name, :author);


-- :name  update-book! :! :n
-- :doc Updates an existing book record
update book
set book_name = :book_name, author = :author
where book_id = :book_id;


-- :name  delete-book! :! :n
-- :doc Deletes an existing book record
delete from book where book_id = :book_id;
