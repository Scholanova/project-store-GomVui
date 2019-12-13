--liquibase formatted sql

--changeset scholanova:2
CREATE TABLE IF NOT EXISTS STOCK (
  ID                  SERIAL          NOT NULL,
  NAME                VARCHAR(255)    NOT NULL,
  TYPE                VARCHAR(255)    NOT NULL,
  VALUE               INTEGER,
  STOREID             INTEGER,
  PRIMARY KEY (ID),
  FOREIGN KEY (STOREID) REFERENCES STORES(ID)
);
