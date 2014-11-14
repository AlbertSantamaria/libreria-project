drop user 'librero'@'localhost';
create user 'librero'@'localhost' identified by 'librero';
grant all privileges on libreriadb.* to 'librero'@'localhost';
flush privileges;
