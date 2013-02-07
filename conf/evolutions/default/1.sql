# Game schema
 
# --- !Ups

CREATE SEQUENCE game_id_seq;
CREATE TABLE game (
    id integer NOT NULL DEFAULT nextval('game_id_seq'),
    tiles varchar(50),
    playerOne varchar(50),
    playerTwo varchar(50)
);
 
# --- !Downs
 
DROP TABLE game;
DROP SEQUENCE game_id_seq;