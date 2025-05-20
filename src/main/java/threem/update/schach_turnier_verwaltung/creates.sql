Create SEQUENCE person_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE tournaments_id_seq
    START WITH 1
    INCREMENT BY 1;

Create Table tournaments (tournamentId numeric(5) primary key, name varchar(30), start_time timestamp, end_time timestamp)

Create Table persons_tournaments (personId numeric(5) REFERENCES persons(PERSONID), tournamentId numeric(4) REFERENCES tournaments(TOURNAMENTID))

DROP TABLE persons_tournaments;
DROP TABLE persons;
DROP TABLE tournaments;

Insert Into persons ()