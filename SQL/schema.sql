create table employee
(
    id         INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(20),
    last_name  VARCHAR(20) DEFAULT NULL,
    salary     INT         DEFAULT NULL,
    PRIMARY KEY (id)
);


drop table employee;
create table employee
(
    first_name VARCHAR(20),
    last_name  VARCHAR(20) DEFAULT NULL,
    salary     INT         DEFAULT NULL,
    PRIMARY KEY (first_name)
);
