DROP TABLE IF EXISTS likes, film_genre, friends, films, users, genres, mpa;


CREATE TABLE IF NOT EXISTS users (
    user_id int AUTO_INCREMENT PRIMARY KEY,
    email varchar,
    login varchar,
    user_name varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id int PRIMARY KEY,
    genre_name varchar
);

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id int PRIMARY KEY,
    mpa_name varchar(6)
);

CREATE TABLE IF NOT EXISTS films (
    film_id int AUTO_INCREMENT PRIMARY KEY,
    film_name varchar(200),
    description varchar(200),
    release_date date,
    duration int,
    mpa_id int,
    FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id int,
    user_id int,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id int,
    genre_id int,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id),
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id int,
    friend_id int,
    status varchar,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (friend_id) REFERENCES users (user_id)
);


