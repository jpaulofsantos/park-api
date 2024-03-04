insert into USERS (id, username, password, role) values (99, 'admin@gmail.com', '$2a$12$wSGtL7BO2CgYB5zmTBqlaOSPy4R1iDF0vlskKt7ZJzGDmD8mtEtKm', 'ROLE_ADMIN');
insert into USERS (id, username, password, role) values (100, 'teste20@gmail.com', '123456', 'ROLE_ADMIN');
insert into USERS (id, username, password, role) values (101, 'teste21@gmail.com', '$2a$12$wSGtL7BO2CgYB5zmTBqlaOSPy4R1iDF0vlskKt7ZJzGDmD8mtEtKm', 'ROLE_CLIENT');
insert into USERS (id, username, password, role) values (102, 'teste22@gmail.com', '123456', 'ROLE_ADMIN');
insert into USERS (id, username, password, role) values (103, 'joaopaulo@gmail.com', '$2a$12$wSGtL7BO2CgYB5zmTBqlaOSPy4R1iDF0vlskKt7ZJzGDmD8mtEtKm', 'ROLE_CLIENT');
insert into USERS (id, username, password, role) values (104, 'marcio@gmail.com', '$2a$12$wSGtL7BO2CgYB5zmTBqlaOSPy4R1iDF0vlskKt7ZJzGDmD8mtEtKm', 'ROLE_CLIENT');
insert into USERS (id, username, password, role) values (105, 'carlos@gmail.com', '$2a$12$wSGtL7BO2CgYB5zmTBqlaOSPy4R1iDF0vlskKt7ZJzGDmD8mtEtKm', 'ROLE_CLIENT');
insert into USERS (id, username, password, role) values (106, 'teste26@gmail.com', '123456', 'ROLE_CLIENT');
insert into USERS (id, username, password, role) values (107, 'teste27@gmail.com', '123456', 'ROLE_ADMIN');
insert into USERS (id, username, password, role) values (108, 'teste28@gmail.com', '123456', 'ROLE_CLIENT');
insert into USERS (id, username, password, role) values (109, 'teste29@gmail.com', '123456', 'ROLE_CLIENT');

insert into CLIENTS (id, name, cpf, id_user) values (2, 'Marcio Rodrigues', '23764699027', 104);
insert into CLIENTS (id, name, cpf, id_user) values (3, 'Carlos Menezes', '30603384005', 105);

insert into spaces (id, code, status) values (100, 'AD10', 'OCCUPIED');
insert into spaces (id, code, status) values (200, 'AE11', 'OCCUPIED');
insert into spaces (id, code, status) values (300, 'AF12', 'OCCUPIED');
insert into spaces (id, code, status) values (400, 'AJ13', 'FREE');
insert into spaces (id, code, status) values (500, 'AK14', 'FREE');

insert into clients_have_spaces (receipt_number, plate, manufacturer, model, color, entry_date, id_client, id_parking_space) values ('20240303-114646', 'OUG-2020', 'VW', 'T-CROSS', 'BRANCO', '2024-03-03 11:46:46', 3, 100);
insert into clients_have_spaces (receipt_number, plate, manufacturer, model, color, entry_date, id_client, id_parking_space) values ('20240304-124249', 'OUG-2021', 'VW', 'GOL', 'PRETO', '2024-03-04 12:42:49', 2, 200);
insert into clients_have_spaces (receipt_number, plate, manufacturer, model, color, entry_date, id_client, id_parking_space) values ('20240305-134041', 'OUG-2022', 'VW', 'AMAROK', 'CINZA', '2024-03-05 13:40:41', 3, 300);