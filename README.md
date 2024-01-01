# java-filmorate
Template repository for Filmorate project.


ER-диаграмма\
![Изображение](https://github.com/naykama/java-filmorate/blob/add-ER/image/DB_1.png)\
Примеры запросов для основных операций:  
Для фильмов:   
1. Вернуть все фильмы `getAllFilms():

```
SELECT f.film_id,
f.name,
f.description,
f.release_date,
f.duration,
g.name,
f.rating
FROM film AS f
LEFT JOIN genre AS g ON f.genre_id = g.genre_id;
```
2. Вернуть фильм по id `Film getFilmById(@PathVariable long id)`:

```
SELECT f.film_id,
f.name,
f.description,
f.release_date,
f.duration,
g.name,
f.rating
FROM film AS f
LEFT JOIN genre AS g ON f.genre_id = g.genre_id
WHERE f.film_id = id;
```

3. Вернуть список count(10) лучших фильмов `List<Film> getBestFilms(@RequestParam(required = false) Integer count)`:

```
SELECT f.film_id,
f.name,
f.description,
f.release_date,
f.duration,
g.name,
f.rating
FROM film AS f
LEFT JOIN genre AS g ON f.genre_id = g.genre_id
WHERE film_id IN (SELECT film_id,
		COUNT(film_id)
		FROM likes
		GROUP BY film_id
		ORDER BY COUNT(film_id) DESC
		LIMIT count);
```
Для пользователей:
1. Вернуть всех пользователей `List<User> getAllUsers()`:
```
SELECT u.user_id,
u.email,
u.login,
u.name,
u.birthday,
f.user_id
FROM user AS u
LEFT JOIN friends AS f ON user.id = f.user_id;
```
2. Вернуть пользователя по id `User getUserById(@PathVariable long id)`:
```
SELECT u.user_id,
u.email,
u.login,
u.name,
u.birthday,
f.user_id
FROM user AS u
LEFT JOIN friends AS f ON user.id = f.user_id
WHERE user_id = id;
```
3. Вернуть общих друзей у двух пользователей `List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId)`:
```
SELECT u.user_id,
u.email,
u.login,
u.name,
u.birthday,
f.user_id
FROM user AS u
LEFT JOIN friends AS f ON u.user_id = f.user_id
WHERE user_id IN (SELECT friend_id,
		FROM friends
		WHERE user_id = id AND user_id = otherId;
```
4. Вернуть список друзей у пользователя `List<User> getFriends(@PathVariable long id)`:
```
SELECT u.user_id,
u.email,
u.login,
u.name,
u.birthday,
f.user_id
FROM user AS u
LEFT JOIN friends AS f ON u.user_id = f.user_id
WHERE u.user_id IN (SELECT friend_id,
		FROM friends
		WHERE user_id = id;
```