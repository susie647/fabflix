DELIMITER $$ 

CREATE PROCEDURE add_movie (IN title varchar(100), IN year INT, IN director varchar(100), IN star_name varchar(100), IN genre_name varchar(32))
BEGIN

	DECLARE movieId varchar(10) DEFAULT '0';
	DECLARE maxMovieId varchar(10) DEFAULT '0';
	DECLARE starId varchar(10) DEFAULT '0';
	DECLARE maxStarId varchar(10) DEFAULT '0';
	DECLARE genreId integer DEFAULT 0;
	DECLARE status varchar(30) DEFAULT '0';

	SELECT m.id into movieId from movies as m where m.title = title and m.year = year and m.director = director limit 1;
	IF (movieId = '0') THEN
		SELECT SUBSTRING(max(m.id),3,10) INTO maxMovieId from movies as m;
		SELECT CONCAT('tt', LPAD(maxMovieId+1, 7, 0)) INTO movieId;
		INSERT INTO movies VALUES (movieId, title, year, director);

		SELECT s.id INTO starId from stars as s where s.name = star_name limit 1;
		IF (starId = '0') THEN
			SELECT SUBSTRING(max(id),3,10) INTO maxStarId from stars;
			SELECT CONCAT('nm', LPAD(maxStarId+1, 7, 0)) INTO starId;
			INSERT INTO stars VALUES (starId, star_name, null);
		END IF;

		SELECT g.id INTO genreId from genres as g where g.name = genre_name limit 1;
		IF (genreId = 0) THEN
			INSERT INTO genres (name) VALUES (genre_name);
			SELECT g.id INTO genreId from genres as g where g.name = genre_name limit 1;
		END IF;

		INSERT INTO stars_in_movies VALUES (starId, movieId);
		INSERT INTO genres_in_movies VALUES (genreId, movieId);
		INSERT INTO ratings VALUES (movieId, -1.0, 0);

		SELECT CONCAT(movieId, ',', genreId, ',', starId) INTO status;


	END IF;

	SELECT status;

END
$$

DELIMITER ;