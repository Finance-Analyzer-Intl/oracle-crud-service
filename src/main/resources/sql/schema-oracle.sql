-- Reference DDL for the PRODUCTS table, matching entity.Product.
-- Not run automatically. With spring.jpa.hibernate.ddl-auto=update,
-- Hibernate creates this for you in dev. For any shared/production
-- environment, run this (or an equivalent Flyway/Liquibase migration)
-- yourself and set ddl-auto=validate (or none).

CREATE TABLE PRODUCTS (
    ID          NUMBER(19)      NOT NULL,
    NAME        VARCHAR2(150)   NOT NULL,
    DESCRIPTION VARCHAR2(1000),
    PRICE       NUMBER(12,2)    NOT NULL,
    QUANTITY    NUMBER(10)      NOT NULL,
    CREATED_AT  TIMESTAMP       NOT NULL,
    UPDATED_AT  TIMESTAMP,
    CONSTRAINT PK_PRODUCTS PRIMARY KEY (ID)
);

CREATE SEQUENCE PRODUCT_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE;
