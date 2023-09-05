create table reservation(
    int id not null auto_increment,
    int book_ref not null,
    int borrower_id not null,
    date date_of_reservation not null,
);


alter table reservation
    add constraint res_borrower
    foreign key (borrower_id) references borrower(id);


alter table reservation
    add constraint res_book
    foreign key (book_ref) references copies(ref);

create trigger onAddBook 
after  create on books
for each row 
begin 
    insert into copies (isbn) values NEW.isbn ; 
end;