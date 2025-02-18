package com.example.peopleListJulie;

import com.example.peopleListJulie.dao.PersonDao;
import com.example.peopleListJulie.models.Person;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PersonDao personDao;

    private static final Logger logger = LoggerFactory.getLogger(PeopleController.class);

    private PeopleController(PersonDao personDao) {
        this.personDao = personDao;
    }

    @GetMapping()
    public String findAll(Model model) {
        logger.info("Getting а list of people");
        List<Person> allPeople = personDao.findAll();
        model.addAttribute("people", allPeople);
        logger.debug("Successfully found {} people", allPeople.size());
        return "people/index";

    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        logger.info("Get a person with id: {}", id);
        Person person = personDao.findById(id);
        model.addAttribute("person", person);
        logger.debug("Successfully found the person with id {}", person);
        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        logger.info("Opening the form for creating a new person");
        return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Unknown error");

            logger.warn("An error(s) while creating a person: {}", errors);
            return "people/new";
        }
        personDao.save(person);
        logger.info("Successfully created a new person: {}", person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        logger.info("Opening edit form for the person with id: {}", id);
        Person person = personDao.findById(id);
        model.addAttribute("person", person);
        logger.debug("Loaded the person for editing: {}", person);
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult, @PathVariable("id") int id) {

        if (bindingResult.hasFieldErrors("email")) {
            logger.warn("Email error {}", bindingResult.getFieldError());
            return "people/edit";
        }
        personDao.update(id, person);
        logger.info("The person with id {} have been updated", id);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        logger.info("Deleting the person with id {}", id);
        personDao.delete(id);
        logger.debug("Successfully deleted the person with id {}", id);
        return "redirect:/people";
    }

    @GetMapping("/found")
    public String find(Model model, @RequestParam("keyword") String keyword) {
        logger.info("Searching the person with the First name {}", keyword);
        model.addAttribute("people", personDao.findByKeyword(keyword));
        logger.debug("Found the person with the First name {}", keyword);
        return "people/found";
    }

}
