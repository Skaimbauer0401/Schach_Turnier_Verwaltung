
CREATE TABLE persons (personId INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, username varchar(20), password varchar(20), admin boolean, wins numeric(4), losses numeric(4), draws numeric(4));

Create Table tournaments (tournamentId INT GENERATED ALWAYS AS IDENTITY primary key, name varchar(30), start_time timestamp, end_time timestamp);

Create Table persons_tournaments (personId INT REFERENCES persons(PERSONID), tournamentId INT REFERENCES tournaments(TOURNAMENTID));

DROP TABLE persons_tournaments;
DROP TABLE persons;
DROP TABLE tournaments;

Insert Into persons (username, password, admin, wins, losses, draws) Values ('Skaimbauer','test123',true,10,10,10);