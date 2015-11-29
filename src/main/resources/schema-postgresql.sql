--CREATE DATABASE test;
-- Table: tuple_row

-- DROP TABLE tuple_row;

CREATE TABLE tuple_row
(
  "left" character varying(100) NOT NULL,
  "right" character varying(100) NOT NULL,
  occurrences integer,
  id serial NOT NULL,
  CONSTRAINT tuple_row_pkey PRIMARY KEY (id)
) WITH (  OIDS=FALSE );
ALTER TABLE tuple_row
  OWNER TO postgres;

-- Index: left_index
-- DROP INDEX left_index;

CREATE INDEX left_index
  ON tuple_row
  USING btree
  ("left" COLLATE pg_catalog."default");

-- Index: occurences_index

-- DROP INDEX occurences_index;

CREATE INDEX occurences_index
  ON tuple_row
  USING btree
  (occurrences);

-- Index: right_index

-- DROP INDEX right_index;

CREATE INDEX right_index
  ON tuple_row
  USING btree
  ("right" COLLATE pg_catalog."default");

