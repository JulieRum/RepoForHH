package com.example.peopleListJulie.dao;

import com.example.peopleListJulie.models.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class PersonDao {
    private static int PEOPLE_COUNT;

    private static final String URL = "jdbc:postgresql://localhost:5432/first_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

    private static Connection connection;

    private static final Logger logger = LoggerFactory.getLogger(PersonDao.class);

    public PersonDao() {
    }

    public PersonDao(Connection connectionParam) {
        connection = connectionParam;
        logger.info("Connection in constructor: {}", connectionParam);
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("Postgresql loaded");
        } catch (ClassNotFoundException e) {
            logger.error("Postgresql failed", e);
        }

        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                logger.info("Successful connection to Database");
            } catch (SQLException e) {
                logger.error("Connection to Database failed", e);
            }
        }
    }

    public List<Person> findAll() {
        logger.info("Executing 'findAll'");
        List<Person> people = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM first_db_schema.person";
            ResultSet resultset = statement.executeQuery(SQL);


            while (resultset.next()) {
                Person person = new Person();
                person.setId(resultset.getInt("id"));
                person.setEmail(resultset.getString("email"));
                person.setFirstName(resultset.getString("firstname"));
                person.setLastName(resultset.getString("lastname"));
                person.setUserName(resultset.getString("username"));
                person.setPhoneNumber(resultset.getString("phonenumber"));

                people.add(person);
            }
            logger.info("Found {} people in the Database", people.size());

        } catch (SQLException e) {
            logger.error("Error while executing 'findAll'", e);
        }
        return people;
    }

    public Person findById(int id) {
        logger.info("Executing 'findById' {}", id);
        Person person = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " +
                    "first_db_schema.person WHERE id=?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            person = new Person();

            person.setId(resultSet.getInt("id"));
            person.setUserName(resultSet.getString("username"));
            person.setFirstName(resultSet.getString("firstname"));
            person.setLastName(resultSet.getString("lastname"));
            person.setEmail(resultSet.getString("email"));
            person.setPhoneNumber(resultSet.getString("phonenumber"));

            logger.debug("Found the Person in the Database with id {}", id);
        } catch (SQLException e) {
            logger.error("The Person with id {} have not been found", id, e);
        }

        return person;
    }

    public void save(Person person) {
        logger.info("Executing 'save' person {}", person);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO first_db_schema.person (username, email, firstname, lastname, phonenumber) VALUES (?, ?, ?, ?, ?)");

            preparedStatement.setString(1, person.getUserName());
            preparedStatement.setString(2, person.getEmail());
            preparedStatement.setString(3, person.getFirstName());
            preparedStatement.setString(4, person.getLastName());
            preparedStatement.setString(5, person.getPhoneNumber());

            preparedStatement.executeUpdate();
            logger.info("A new person have been saved in the Database: {}", person);

        } catch (SQLException e) {
            logger.error("A new person have not been saved in the Database", e);
        }
    }

    public void update(int id, Person updatedPerson) {
        logger.info("Executing 'update' person {}", updatedPerson);
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE first_db_schema.person SET email=?, firstname =?, lastname=?, phonenumber=? WHERE id=?");
            preparedStatement.setString(1, updatedPerson.getEmail());
            preparedStatement.setString(2, updatedPerson.getFirstName());
            preparedStatement.setString(3, updatedPerson.getLastName());
            preparedStatement.setString(4, updatedPerson.getPhoneNumber());
            preparedStatement.setInt(5, id);
            preparedStatement.executeUpdate();
            logger.info("The person {} have been updated in the Database", updatedPerson);
        } catch (SQLException e) {
            logger.error("The person {} have not been updated in the Database", updatedPerson, e);
        }
    }

    public void delete(int id) {
        logger.info("Executing 'delete' person with id {}", id);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM first_db_schema.person WHERE id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            logger.info("The person with id {} have been deleted from the Database", id);
        } catch (SQLException e) {
            logger.error("The person with id {} have not been deleted from the Database", id, e);
        }

    }

    public List<Person> findByKeyword(String keyword) {
        logger.info("Executing 'findByKeyword' the person with keyword {}", keyword);
        List<Person> people = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM first_db_schema.person WHERE firstname ILIKE ?");
            preparedStatement.setString(1, keyword);

            ResultSet resultset = preparedStatement.executeQuery();

            while (resultset.next()) {
                Person person = new Person();
                person.setId(resultset.getInt("id"));
                person.setEmail(resultset.getString("email"));
                person.setFirstName(resultset.getString("firstname"));
                person.setLastName(resultset.getString("lastname"));
                person.setUserName(resultset.getString("username"));
                person.setPhoneNumber(resultset.getString("phonenumber"));

                people.add(person);
            }
            logger.info("Found {} people with a keyword {}", people.size(), keyword);
        } catch (SQLException e) {
            logger.error("People with a keyword {} have not been found", keyword, e);
        }
        return people;

    }
}
