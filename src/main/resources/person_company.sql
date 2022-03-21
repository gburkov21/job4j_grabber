create database person_company_db;

CREATE TABLE company
(
    id   integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id         integer NOT NULL,
    name       character varying,
    company_id integer references company (id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

insert into company (id, name)
values (1, 'IBM'),
       (2, 'Google'),
       (3, 'Amazon'),
       (4, 'Twitter'),
       (5, 'Apple');

insert into person (id, name, company_id)
values (1, 'Катя', 1),
       (2, 'Маша', 1),
       (3, 'Вера', 1),
       (4, 'Даша', 2),
       (5, 'Саша', 2),
       (6, 'Наташа', 2),
       (7, 'Карина', 2),
       (8, 'Марина', 3),
       (9, 'Люда', 3),
       (10, 'Таня', 3),
       (11, 'Гоша', 3),
       (12, 'Леша', 3),
       (13, 'Игорь', 4),
       (14, 'Артем', 4),
       (15, 'Сергей', 4),
       (16, 'Рома', 5),
       (17, 'Петя', 5),
       (18, 'Олег', 5),
       (19, 'Гриша', 5),
       (20, 'Юрий', 5);

select p.name, c.name
from person p
         join company c on c.id = p.company_id
where c.id != 5;

select c.name, count(*)
from company c
         join person p on c.id = p.company_id
group by c.name
having count(*) = (select max(myCount)
                   from (select count(*) as myCount
                         from company c
                                  join person p on c.id = p.company_id
                         group by c.name) as innerCount);