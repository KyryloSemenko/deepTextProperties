-- hsqldb

-- DROP TABLE tuple;

CREATE TABLE TUPLE
(
  id INTEGER IDENTITY PRIMARY KEY,
  prev character varying(100) NOT NULL,
  fol character varying(100) NOT NULL,
  num integer
);

-- Index: prev_index
-- DROP INDEX prev_index;
CREATE INDEX PUBLIC.PREV_INDEX ON PUBLIC.TUPLE (prev);
CREATE INDEX PUBLIC.FOL_INDEX ON PUBLIC.TUPLE (fol);
CREATE INDEX PUBLIC.NUM_INDEX ON PUBLIC.TUPLE (num);

