package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {

    private final Sql2o sql2o;
    private static final Logger LOGGER = LoggerFactory.getLogger(Sql2oUserRepository.class);

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        Optional<User> result = Optional.empty();
        try (var connection = sql2o.open()) {
            var sql = "INSERT INTO users(email, name, password) VALUES (:email, :name, :password)";
            var query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
            result = Optional.of(user);
        } catch (Sql2oException e) {
            LOGGER.error("Ошибка при сохранении пользователя: {}", e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        Optional<User> result = Optional.empty();
        try (var connection = sql2o.open()) {
            var query = connection
                    .createQuery("SELECT * FROM users WHERE email = :email AND password = :password");
            query.addParameter("email", email)
                    .addParameter("password", password);
            var user = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            result = Optional.ofNullable(user);
        } catch (Sql2oException e) {
            LOGGER.error("Ошибка при поиске пользователя по email и паролю: {}", e.getMessage(), e);
        }
        return result;
    }
}