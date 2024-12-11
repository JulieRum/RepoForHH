package com.example.peopleListJulie.dao;

import com.example.peopleListJulie.models.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonDaoTest {

    Connection connection = mock(Connection.class);

    PersonDao personDao  = new PersonDao(connection);

    @Test
    @DisplayName("возвращает список всех людей из базы данных")
    void shouldFindAllPersonsFromDB() throws SQLException {
        //given
        Statement statement = mock(Statement.class);
        ResultSet resultSet = mock(ResultSet.class);
        doReturn(statement).when(connection).createStatement();
        doReturn(resultSet).when(statement).executeQuery("SELECT * FROM first_db_schema.person");

        when(resultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        Person person1 = new Person(1, "julRum", "julRumEmail", "Iuliia", "Rumiantseva", "000");
        Person person2 = new Person(2, "julSvetl", "julSvetlEmail", "Iuliia", "Svetlichnaia", "111");
        prepareResultSet(resultSet, person1, person2);

        //when
        List<Person> actualResult = personDao.findAll();

        //then
        assertEquals(2, actualResult.size());
        personWillBeCorrect(person1, actualResult.get(0));
        personWillBeCorrect(person2, actualResult.get(1));
    }

    private static void prepareResultSet(ResultSet resultSet, Person person1, Person person2) throws SQLException {
        when(resultSet.getInt("id"))
                .thenReturn(person1.getId())
                .thenReturn(person2.getId());

        when(resultSet.getString("email"))
                .thenReturn(person1.getEmail())
                .thenReturn(person2.getEmail());

        when(resultSet.getString("firstname"))
                .thenReturn(person1.getFirstName())
                .thenReturn(person2.getFirstName());

        when(resultSet.getString("lastname"))
                .thenReturn(person1.getLastName())
                .thenReturn(person2.getLastName());

        when(resultSet.getString("username"))
                .thenReturn(person1.getUserName())
                .thenReturn(person2.getUserName());

        when(resultSet.getString("phonenumber"))
                .thenReturn(person1.getPhoneNumber())
                .thenReturn(person2.getPhoneNumber());
    }

    private static void personWillBeCorrect(Person person1, Person actualPerson1) {
        assertEquals(person1.getId(), actualPerson1.getId());
        assertEquals(person1.getEmail(), actualPerson1.getEmail());
        assertEquals(person1.getFirstName(), actualPerson1.getFirstName());
        assertEquals(person1.getLastName(), actualPerson1.getLastName());
        assertEquals(person1.getUserName(), actualPerson1.getUserName());
        assertEquals(person1.getPhoneNumber(), actualPerson1.getPhoneNumber());
    }

    @Test
    @DisplayName("возвращает человека из базы данных по id")
    void shouldFindAPersonByIdFromDB() throws SQLException {
        //given
        int id = 1;
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        doReturn(preparedStatement).when(connection).prepareStatement("SELECT * FROM first_db_schema.person WHERE id=?");
        doReturn(resultSet).when(preparedStatement).executeQuery();
        when(resultSet.next())
                .thenReturn(true);
        Person person1 = new Person(id, "julRum", "julRumEmail", "Iuliia", "Rumiantseva", "000");

        when(resultSet.getInt("id"))
                .thenReturn(id);
        when(resultSet.getString("email"))
                .thenReturn(person1.getEmail());
        when(resultSet.getString("lastname"))
                .thenReturn(person1.getLastName());
        when(resultSet.getString("firstname"))
                .thenReturn(person1.getFirstName());
        when(resultSet.getString("username"))
                .thenReturn(person1.getUserName());
        when(resultSet.getString("phonenumber"))
                .thenReturn(person1.getPhoneNumber());


        doNothing().when(preparedStatement).setInt(1, id);

        //when
        Person actualperson = personDao.findById(id);

        //then
        personWillBeCorrect(person1, actualperson);
        verify(preparedStatement, times(1)).setInt(1, id);

    }

    @Test
    @DisplayName("сохраняет человека в базу данных")
    void shouldSavePersonInDB() throws SQLException {
        //given
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(
                "INSERT INTO first_db_schema.person (username, email, firstname, lastname, phonenumber) VALUES (?, ?, ?, ?, ?)");

        Person person1 = new Person(1, "julRum", "julRumEmail", "Iuliia", "Rumiantseva", "000");

        // when
        personDao.save(person1);

        //then
        verify(connection).prepareStatement("INSERT INTO first_db_schema.person (username, email, firstname, lastname, phonenumber) VALUES (?, ?, ?, ?, ?)");

        verify(preparedStatement).setString(1, "julRum");
        verify(preparedStatement).setString(2, "julRumEmail");
        verify(preparedStatement).setString(3, "Iuliia");
        verify(preparedStatement).setString(4, "Rumiantseva");
        verify(preparedStatement).setString(5, "000");

        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("обновляет человека в бaзе данных")
    void updatePersonInDB() throws SQLException {
        //given
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(
                "UPDATE first_db_schema.person SET email=?, firstname =?, lastname=?, phonenumber=? WHERE id=?");
        int id = 1;
        Person updatedPerson = new Person(id, "Arkadii", "Ar4iEmail", "Arkadii", "Kriv4ikov", "111");

        // when
        personDao.update(id, updatedPerson);

        //then
        verify(connection).prepareStatement("UPDATE first_db_schema.person SET email=?, firstname =?, lastname=?, phonenumber=? WHERE id=?");

        verify(preparedStatement).setString(1, "Ar4iEmail");
        verify(preparedStatement).setString(2, "Arkadii");
        verify(preparedStatement).setString(3, "Kriv4ikov");
        verify(preparedStatement).setString(4, "111");
        verify(preparedStatement).setInt(5, id);

        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("удаляет человека из базы данных")
    void deletePersonFromDB() throws SQLException {
        //given
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(
                "DELETE FROM first_db_schema.person WHERE id=?");
        int id = 1;

        // when
        personDao.delete(id);

        //then
        verify(connection).prepareStatement("DELETE FROM first_db_schema.person WHERE id=?");

        verify(preparedStatement).setInt(1, id);

        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("возвращает список всех людей из базы данных по ключевому слову")
    void shouldFindAllPersonsFromDbByKeyword() throws SQLException {
        //given
        String keyword = "Key";
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        doReturn(preparedStatement).when(connection).prepareStatement("SELECT * FROM first_db_schema.person WHERE firstname=?");
        doReturn(resultSet).when(preparedStatement).executeQuery();

        when(resultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        Person person1 = new Person(1, "julRum", "julRumEmail", keyword, "Rumiantseva", "000");
        Person person2 = new Person(2, "julSvetl", "julSvetlEmail", keyword, "Svetlichnaia", "111");

        prepareResultSet(resultSet, person1, person2);

        //when
        List<Person> actualResult = personDao.findByKeyword(keyword);

        //then
        assertEquals(2, actualResult.size());
        personWillBeCorrect(person1, actualResult.get(0));
        personWillBeCorrect(person2, actualResult.get(1));
    }
}