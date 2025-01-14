insert into USUARIOS (id, username, password, role)
    values (100, 'ygor@email.com', '$2a$12$G0Fft5sfccGA9ZgjC4O0nOAn36K9jIP2.QrewAUIxruTUSlNKhcTi', 'ROLE_ADMIN');
-- senha 123456

insert into USUARIOS (id, username, password, role)
    values (101, 'tulio@email.com', '$2a$12$CZHXRYKYzW5XjmsX/x3t/eNvnl.TN7ertrpEmr8De9ebiQTQ92QV6', 'ROLE_CLIENTE');
-- senha 235689

insert into USUARIOS (id, username, password, role)
    values (102, 'dilma@email.com', '$2a$12$7e1c51cH0b7OiK0x3Xl.o.0ZsRGOe69AGNXzd215jA5UjSru/uaSu', 'ROLE_ADMIN');
-- senha 124578

insert into USUARIOS (id, username, password, role)
    values (103, 'maria@email.com', '$2a$12$7e1c51cH0b7OiK0x3Xl.o.0ZsRGOe69AGNXzd215jA5UjSru/uaSu', 'ROLE_CLIENTE');
-- senha 124578

insert into USUARIOS (id, username, password, role)
    values (104, 'tupac@email.com', '$2a$12$7e1c51cH0b7OiK0x3Xl.o.0ZsRGOe69AGNXzd215jA5UjSru/uaSu', 'ROLE_CLIENTE');
-- senha 124578


insert into CLIENTES (id, nome, cpf, id_usuario) values (101, 'Tulio', '39988750005', 101);
insert into CLIENTES (id, nome, cpf, id_usuario) values (103, 'Maria', '87150818005', 103);
insert into CLIENTES (id, nome, cpf, id_usuario) values (104, 'Tupac Shakur', '90287456021', 104);


insert into VAGAS (id, codigo, status) values (10, 'A-01', 'LIVRE');
insert into VAGAS (id, codigo, status) values (20, 'A-02', 'LIVRE');
insert into VAGAS (id, codigo, status) values (30, 'A-03', 'LIVRE');
insert into VAGAS (id, codigo, status) values (40, 'A-04', 'LIVRE');
insert into VAGAS (id, codigo, status) values (50, 'A-05', 'OCUPADA');
insert into VAGAS (id, codigo, status) values (60, 'A-06', 'OCUPADA');


insert into clientes_tem_vagas (numero_recibo, placa, marca, modelo, cor, data_entrada, id_cliente, id_vaga)
    values ('20230313-101300', 'FIT-1020', 'FIAT', 'PALIO', 'VERDE', '2023-03-13 10:15:00', 104, 10);
insert into clientes_tem_vagas (numero_recibo, placa, marca, modelo, cor, data_entrada, id_cliente, id_vaga)
    values ('20230313-101400', 'SIE-1020', 'FIAT', 'SIENA', 'BRANCO', '2023-03-14 10:15:00', 103, 40);
insert into clientes_tem_vagas (numero_recibo, placa, marca, modelo, cor, data_entrada, id_cliente, id_vaga)
    values ('20230315-101500', 'FIT-1020', 'FIAT', 'PALIO', 'VERDE', '2023-03-14 10:15:00', 104, 30);
insert into clientes_tem_vagas (numero_recibo, placa, marca, modelo, cor, data_entrada, id_cliente, id_vaga)
    values ('20240107-101600', 'FIT-1020', 'FIAT', 'PALIO', 'VERDE', '2024-01-07 10:16:00', 104, 60);