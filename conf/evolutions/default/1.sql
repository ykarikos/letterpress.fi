# Game schema
 
# --- !Ups

CREATE TABLE game (
    id varchar(50) NOT NULL DEFAULT '',
    tiles varchar(50) not null default '',
    playerOne varchar(50) not null default '',
    playerTwo varchar(50) not null default '',
    playerOneScore int not null default 0,
    playerTwoScore int not null default 0
);
 
# --- !Downs
 
DROP TABLE game;
