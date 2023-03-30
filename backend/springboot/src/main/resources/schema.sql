CREATE TABLE IF NOT EXISTS public.lastsequence (
    id integer NOT NULL,
    deviceid integer,
    ende integer,
    label character varying(255),
    start integer,
    PRIMARY KEY (id)
);
