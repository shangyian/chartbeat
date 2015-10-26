CREATE KEYSPACE IF NOT EXISTS chartbeat WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

CREATE TABLE IF NOT EXISTS chartbeat.raw_page_visits (
    id UUID,
    timestamp timestamp,
    page_title text,
    page_url text,
    visits bigint,
    links bigint,
    read bigint,
    num_refs bigint,
    search bigint,
    social bigint,
    PRIMARY KEY (id, timestamp)
);

CREATE INDEX IF NOT EXISTS url ON chartbeat.raw_page_visits ( page_url );
CREATE INDEX IF NOT EXISTS timestamp_idx ON chartbeat.raw_page_visits ( timestamp );
