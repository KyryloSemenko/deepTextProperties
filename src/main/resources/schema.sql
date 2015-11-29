-- hsqldb

-- DROP TABLE tuple_row;

CREATE TABLE TUPLE_ROW
(
  id INTEGER IDENTITY PRIMARY KEY,
  "left" character varying(100) NOT NULL,
  "right" character varying(100) NOT NULL,
  occurrences integer
);

-- Index: left_index
-- DROP INDEX left_index;
CREATE INDEX PUBLIC.LEFT_INDEX ON PUBLIC.TUPLE_ROW ("left");
CREATE INDEX PUBLIC.RIGHT_INDEX ON PUBLIC.TUPLE_ROW ("right");
CREATE INDEX PUBLIC.OCCURENCES_INDEX ON PUBLIC.TUPLE_ROW (occurrences);

