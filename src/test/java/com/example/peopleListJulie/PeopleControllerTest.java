package com.example.peopleListJulie;

import com.example.peopleListJulie.dao.PersonDao;
import com.example.peopleListJulie.models.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled//fixme
class PeopleControllerTest {

    @Mock
    PersonDao personDao;

    @InjectMocks
    PeopleController controller;

    @Test
    @DisplayName("create создаст нового человека и перенаправит на страницу People")
    void createPerson_ReqestIsValid_ReturnsRedirectionOnPeoplePage() {
        //given
        Person person = mock(Person.class);
        BindingResult bindingResult = mock(BindingResult.class);
        doNothing().when(personDao).save(person);
        doReturn(false).when(bindingResult).hasErrors();

        //when
        String actualResult = controller.create(person, bindingResult);

        //then
        assertEquals("redirect:/people", actualResult);
        verify(personDao, times(1)).save(person);
    }

    @Test
    @DisplayName("create при наличии ошибок перенаправит на страницу People New")
    void createPerson_ReqestIsNotValid_ReturnsRedirectionOnPeopleNewPage() {
        //given
        Person person = mock(Person.class);
        BindingResult bindingResult = mock(BindingResult.class);
        doReturn(true).when(bindingResult).hasErrors();

        //when
        String actualResult = controller.create(person, bindingResult);

        //then
        assertEquals("people/new", actualResult);
        verifyNoInteractions(personDao);
    }

    @Test
    @DisplayName("возврщает страницу со всеми пользователями")
    void findAll_shouldReturnPageIndex() {
        //given
        Model model = new ConcurrentModel();
        List<Person> people = mock(List.class);
        doReturn(people).when(personDao).findAll();

        //when
        String actualResult = controller.findAll(model);

        //then
        assertEquals("people/index", actualResult);
        assertEquals(people, model.getAttribute("people"));
    }
    @Test
    @DisplayName("возвращает страницу с пользователем по id")
    void findById_shouldReturnPageWithPersonId() {
        //given
        Person person = mock(Person.class);
        Model model = new ConcurrentModel();
        int id = 1;
        doReturn(person).when(personDao).findById(id);
        //when
        String actualResult = controller.findById(id, model);

        //then
        assertEquals("people/show", actualResult);
        assertEquals(person, model.getAttribute("person"));
    }

    @Test
    @DisplayName("Возвращает страницу new")
    void newPerson_ShouldReturnPageNew() {
        //given
        Person person = mock(Person.class);
        //when
        String actualResult = controller.newPerson(person);

        //then
        assertEquals("people/new", actualResult);
    }

    @Test
    @DisplayName("Возвращает страницу people/edit")
    void edit_ShouldReturnPageEdit() {
        //given
        int id = 1;
        Model model = new ConcurrentModel();
        Person person = mock(Person.class);
        doReturn(person).when(personDao).findById(id);

        //when
        String actualResult = controller.edit(id, model);

        //then
        assertEquals("people/edit", actualResult);
        assertEquals(person, model.getAttribute("person"));
    }

    @Test
    @DisplayName("update обновит человека и перенаправит на страницу People")
    void update_ReqestIsValid_ReturnsRedirectionOnPeoplePage() {
        //given
        int id = 1;
        Person person = mock(Person.class);
        BindingResult bindingResult = mock(BindingResult.class);
        doNothing().when(personDao).update(id, person); // Что будет, если эту строку не писать?
        doReturn(false).when(bindingResult).hasFieldErrors("email");

        //when
        String actualResult = controller.update(person, bindingResult, id);

        //then
        assertEquals("redirect:/people", actualResult);
        verify(personDao, times(1)).update(id, person);
    }

    @Test
    @DisplayName("update при наличии ошибок перенаправит на страницу People/edit")
    void update_ReqestIsNotValid_ReturnsRedirectionOnPeopleNewPage() {
        //given
        int id = 1;
        Person person = mock(Person.class);
        BindingResult bindingResult = mock(BindingResult.class);
        doReturn(true).when(bindingResult).hasFieldErrors("email");

        //when
        String actualResult = controller.update(person, bindingResult, id);

        //then
        assertEquals("people/edit", actualResult);
        verifyNoInteractions(personDao);
    }

    @Test
    @DisplayName("delete удаляет человека и перенаправляет на страницу people")
    void delete_ShouldReturnOnThePagePeople() {
        //given
        int id = 1;
        doNothing().when(personDao).delete(id);

        //when
        String actualResult = controller.delete(id);

        //then
        assertEquals("redirect:/people", actualResult);
    }

    @Test
    @DisplayName("find находит человека по ключи и перенаправляет на страницу people/found")
    void find_ShouldReturnOnThePagePeopleFound() {
        //given
        String keyword = "Something";
        Model model = new ConcurrentModel();
        List<Person> people = mock(List.class);
        doReturn(people).when(personDao).findByKeyword(keyword);

        //when
        String actualResult = controller.find(model, keyword);

        //then
        assertEquals("people/found", actualResult);
    }
}