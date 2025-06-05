
CREATE TABLE persons (personId INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, username varchar(20) Unique, password varchar(32672), admin boolean, wins numeric(4), losses numeric(4), draws numeric(4));

Create Table tournaments (tournamentId INT GENERATED ALWAYS AS IDENTITY primary key, name varchar(30), start_time timestamp, end_time timestamp);

Create Table persons_tournaments (personId INT REFERENCES persons(PERSONID), tournamentId INT REFERENCES tournaments(TOURNAMENTID));

CREATE TABLE matches (
    matchId INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tournamentId INT REFERENCES tournaments(tournamentId),
    player1Id INT REFERENCES persons(personId),
    player2Id INT REFERENCES persons(personId),
    result CHAR(1),  -- 'W' (Sieg für player1), 'L' (Niederlage für player1), 'D' (Unentschieden)
    CONSTRAINT unique_match UNIQUE (tournamentId, player1Id, player2Id)
);



Insert Into persons (username, password, admin, wins, losses, draws) Values ('Skaimbauer2','test',false,10,10,10);
INSERT INTO tournaments (name,start_time,end_time) Values ('tournament2',CURRENT_TIMESTAMP,'2025-05-21 14:30:00');
INSERT INTO persons_tournaments (personId, tournamentId) VALUES (1,1);
INSERT INTO matches (tournamentId, player1Id, player2Id, result) VALUES (1, 2, 3, 'W');


SELECT t.*
    fROM PERSONS_TOURNAMENTS pt JOIN TOURNAMENTS t ON pt.TOURNAMENTID = t.TOURNAMENTID
    WHERE pt.personId = 1;

SELECT * FROM persons;
SELECT * FROM tournaments;
SELECT * FROM persons_tournaments;
SELECT * FROM matches;

-- Get all players in a tournament
SELECT p.personId, p.username
    FROM persons_tournaments pt JOIN persons p on pt.personId = p.personId
    WHERE pt.tournamentId = 1;

-- Get all matches in a tournament with player names
SELECT m.matchId, p1.username AS player1, p2.username AS player2, m.result
    FROM matches m
    JOIN persons p1 ON m.player1Id = p1.personId
    JOIN persons p2 ON m.player2Id = p2.personId
    WHERE m.tournamentId = 1;

SELECT COUNT(personId) FROM persons WHERE username = 'Skaimbauer';

-- Uncomment these statements if you want to reset the database
-- Make sure to drop tables in the correct order (respecting foreign key constraints)
DROP TABLE matches;
DROP TABLE persons_tournaments;
DROP TABLE persons;
DROP TABLE tournaments;
