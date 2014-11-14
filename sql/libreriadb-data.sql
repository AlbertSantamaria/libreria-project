source libreriadb-schema.sql;

SET foreign_key_checks = 0;

insert into users values('test', MD5('test'), 'test', 'test@acme.com');
insert into user_roles values ('test', 'test');

insert into users values('alicia', MD5('alicia'), 'Alicia', 'alicia@acme.com');
insert into user_roles values ('alicia', 'registered');

insert into users values('blas', MD5('blas'), 'Blas', 'blas@acme.com');
insert into user_roles values ('blas', 'registered');

insert into users values('pedro', MD5('pedro'), 'Pedro', 'pedro@acme.com');
insert into user_roles values ('pedro', 'admin');

insert into users values('admin', MD5('admin'), 'admin', 'admin@acme.com');
insert into user_roles values ('admin', 'admin');

insert into authors(name) values ('Dante Alighieri');
insert into authors(name) values ('Sommo Poeta');
insert into authors(name) values ('Il Volgare');
insert into authors(name) values ('Chillida');
insert into authors(name) values ('Juan');

select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia','Dante Alighieri', 'italian', '1', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 2','Dante Alighieri', 'italian', '1', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 3', 'Dante Alighieri','italian', '2', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 4', 'Dante Alighieri','italian', '2', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 5', 'Sommo Poeta','italian', '4', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 6', 'Sommo Poeta','italian', '4', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 7', 'Sommo Poeta','italian', '20', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 8', 'Sommo Poeta','italian', '2', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 9', 'Sommo Poeta','italian', '3', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 10','Il Volgare', 'italian', '10', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 11', 'Il Volgare','italian', '1', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Divina Comedia 12','Il Volgare', 'italian', '2', '1996-01-20','2000-09-14','Proa');
select sleep(1);insert into books (title, author, language, edition, editionDate, printingDate, publisher) values ('Poemario','Chillida', 'Euskera', '1', '1996-01-20','2000-09-14','BayonaEd');

insert into reviews (bookid,content,username,name) values ('1', 'Reseña 1','alicia','Alicia');
select sleep(1);insert into reviews (bookid,content,username,name) values ('1', 'Reseña 2','blas','Blas');
select sleep(1);insert into reviews (bookid,content,username,name) values ('2', 'Reseña 3','blas','Blas');
select sleep(1);insert into reviews (bookid,content,username,name) values ('3', 'Reseña 4','alicia','Alicia');
select sleep(1);insert into reviews (bookid,content,username,name) values ('4', 'Reseña 5','blas','Blas');
select sleep(1);insert into reviews (bookid,content,username,name) values ('5', 'Reseña 6','blas','Blas');
select sleep(1);insert into reviews (bookid,content,username,name) values ('6', 'Reseña 7','blas','Blas');
select sleep(1);insert into reviews (bookid,content,username,name) values ('7', 'Reseña 8','alicia','Alicia');
select sleep(1);insert into reviews (bookid,content,username,name) values ('8', 'Reseña 9','blas','Blas');
select sleep(1);insert into reviews (bookid,content,username,name) values ('9', 'Reseña 10','blas','Blas');
select sleep(1);insert into reviews (bookid,content,username,name) values ('10', 'Reseña 11','alicia','Alicia');
select sleep(1);insert into reviews (bookid,content,username,name) values ('11', 'Reseña 12','alicia','Alicia');
select sleep(1);insert into reviews (bookid,content,username,name) values ('12', 'Reseña 13','pedro','Pedro');
