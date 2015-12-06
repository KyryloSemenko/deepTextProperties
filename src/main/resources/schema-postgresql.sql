-- Table: tuple

-- DROP TABLE tuple;

CREATE TABLE tuple
(
  prev character varying(100) NOT NULL,
  fol character varying(100) NOT NULL,
  num integer,
  id serial NOT NULL,
  CONSTRAINT tuple_pkey PRIMARY KEY (id),
  CONSTRAINT tuple_prev_fol_key UNIQUE (prev, fol)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tuple
  OWNER TO postgres;

-- Index: fol_index

-- DROP INDEX fol_index;

CREATE INDEX fol_index
  ON tuple
  USING btree
  (fol COLLATE pg_catalog."default");

-- Index: num_index

-- DROP INDEX num_index;

CREATE INDEX num_index
  ON tuple
  USING btree
  (num);

-- Index: prev_index

-- DROP INDEX prev_index;

CREATE INDEX prev_index
  ON tuple
  USING btree
  (prev COLLATE pg_catalog."default");

