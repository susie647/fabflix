CREATE DATABASE moviedb CHARACTER SET utf8;

use moviedb;

create table movies(
id varchar(10) primary key,
title varchar(100) not null,
year integer not null,
director varchar(100) not null,
fulltext(title)
);

create table stars (
id varchar(10) primary key,
name varchar(100) not null,
birthYear integer
);

create table stars_in_movies(
starId varchar(10) not null, 
movieId varchar(10) not null,
foreign key(starId) references stars(id),
foreign key(movieId) references movies(id)
);

create table genres(
id integer primary key auto_increment,
name varchar(32) not null
);

create table genres_in_movies(
genreId integer not null,
movieId varchar(10) not null,
foreign key(genreId) references genres(id),
foreign key(movieId) references movies(id)
);

create table creditcards(
id varchar(20) primary key,
firstName varchar(50) not null,
lastName varchar(50) not null,
expiration date not null
);

create table customers(
id integer primary key auto_increment,
firstName varchar(50) not null,
lastName varchar(50) not null,
ccId varchar(20) not null,
address varchar(200) not null,
email varchar(50) not null,
password varchar(20) not null,
foreign key(ccId) references creditcards(id)
);


create table sales(
id integer primary key auto_increment,
customerId integer not null,
movieId varchar(10) not null,
saleDate date not null,
foreign key(customerId) references customers(id),
foreign key(movieId) references movies(id)
);

create table ratings(
movieId varchar(10) not null,
rating float not null,
numVotes integer not null,
foreign key(movieId) references movies(id)
);