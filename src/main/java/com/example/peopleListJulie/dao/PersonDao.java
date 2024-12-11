package com.example.peopleListJulie.dao;

import com.example.peopleListJulie.models.Person;
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

    public PersonDao() {
    }

    public PersonDao(Connection connectionParam) {
        connection = connectionParam;
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Person> findAll() {
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }

    public Person findById(int id) {
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


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return person;
    }

    public void save(Person person) {

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO first_db_schema.person (username, email, firstname, lastname, phonenumber) VALUES (?, ?, ?, ?, ?)");

            preparedStatement.setString(1, person.getUserName());
            preparedStatement.setString(2, person.getEmail());
            preparedStatement.setString(3, person.getFirstName());
            preparedStatement.setString(4, person.getLastName());
            preparedStatement.setString(5, person.getPhoneNumber());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(int id, Person updatedPerson) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE first_db_schema.person SET email=?, firstname =?, lastname=?, phonenumber=? WHERE id=?");
            preparedStatement.setString(1, updatedPerson.getEmail());
            preparedStatement.setString(2, updatedPerson.getFirstName());
            preparedStatement.setString(3, updatedPerson.getLastName());
            preparedStatement.setString(4, updatedPerson.getPhoneNumber());
            preparedStatement.setInt(5, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM first_db_schema.person WHERE id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Person> findByKeyword(String keyword) {
        List<Person> people = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM first_db_schema.person WHERE firstname=?");
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;

    }
}
