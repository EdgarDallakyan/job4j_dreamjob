package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;
import org.sql2o.Sql2o;

import java.util.Properties;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    static void initRepositories() throws Exception {

        var properties = new Properties();
        try (var inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    void clearUsers() {
        try (var query = sql2o.open()) {
            query.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    void whenSaveUserThenFindByEmailAndPasswordReturnsUser() {
        var user = new User(0, "ivan@mail.ru", "Ivan", "password123");
        var savedUser = sql2oUserRepository.save(user).get();
        var foundUser = sql2oUserRepository.findByEmailAndPassword(savedUser.getEmail(), savedUser.getPassword());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("ivan@mail.ru");
        assertThat(foundUser.get().getName()).isEqualTo("Ivan");
    }

    @Test
    void whenSaveUsersWithSameEmailThenSecondSaveFails() {
        var user1 = new User(0, "duplicate@mail.ru", "Ivan", "pass1");
        var user2 = new User(0, "duplicate@mail.ru", "Petr", "pass2");
        var savedUser1 = sql2oUserRepository.save(user1);
        var savedUser2 = sql2oUserRepository.save(user2);
        assertThat(savedUser1).isPresent();
        assertThat(savedUser2).isEmpty();

    }

    @Test
    void whenFindByEmailAndPasswordThenReturnSavedUser() {
        var user = new User(0, "anna@mail.ru", "Anna", "secret");
        sql2oUserRepository.save(user);
        var foundUser = sql2oUserRepository.findByEmailAndPassword("anna@mail.ru", "secret");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Anna");
    }

    @Test
    void whenFindByEmailAndPasswordWithNonExistingEmailThenReturnEmpty() {
        var foundUser = sql2oUserRepository.findByEmailAndPassword("ivan@mail.ru", "password");
        assertThat(foundUser).isEmpty();
    }

    @Test
    void whenFindByEmailAndPasswordWithNonExistingPasswordThenReturnEmpty() {
        var foundUser = sql2oUserRepository.findByEmailAndPassword("ivan@mail.ru", "password");
        assertThat(foundUser).isEmpty();
    }

}