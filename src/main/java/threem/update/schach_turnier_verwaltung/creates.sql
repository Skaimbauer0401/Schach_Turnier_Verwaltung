
CREATE TABLE persons (personId INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, username varchar(20) Unique, password varchar(20), admin boolean, wins numeric(4), losses numeric(4), draws numeric(4));

Create Table tournaments (tournamentId INT GENERATED ALWAYS AS IDENTITY primary key, name varchar(30), start_time timestamp, end_time timestamp);

Create Table persons_tournaments (personId INT REFERENCES persons(PERSONID), tournamentId INT REFERENCES tournaments(TOURNAMENTID));

DROP TABLE persons_tournaments;
DROP TABLE persons;
DROP TABLE tournaments;

Insert Into persons (username, password, admin, wins, losses, draws) Values ('Skaimbauer2','test',false,10,10,10);
INSERT INTO tournaments (name,start_time,end_time) Values ('tournament2',CURRENT_TIMESTAMP,'2025-05-21 14:30:00');
INSERT INTO persons_tournaments (personId, tournamentId) VALUES (1,1);


SELECT t.*
    fROM PERSONS_TOURNAMENTS pt JOIN TOURNAMENTS t ON pt.TOURNAMENTID = t.TOURNAMENTID
    WHERE pt.personId = 1;

SELECT * FROM persons;
SELECT * FROM tournaments;
SELECT * FROM persons_tournaments;

SELECT COUNT(personId) FROM persons WHERE username = 'Skaimbauer';