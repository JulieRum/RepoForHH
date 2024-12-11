package com.example.peopleListJulie;

import com.example.peopleListJulie.dao.PersonDao;
import com.example.peopleListJulie.models.Person;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PersonDao personDao;

    private PeopleController(PersonDao personDao) {
        this.personDao = personDao;
    }

    @GetMapping()
    public String findAll(Model model) {
        model.addAttribute("people", personDao.findAll());
        return "people/index";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", personDao.findById(id));
        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "people/new";

        personDao.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        Person person = personDao.findById(id);
        model.addAttribute("person", person);
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult, @PathVariable("id") int id) {

        if (bindingResult.hasFieldErrors("email"))
            return "people/edit";
        personDao.update(id, person);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        personDao.delete(id);
        return "redirect:/people";
    }

    @GetMapping("/found")
    public String find(Model model, @RequestParam("keyword") String keyword) {
        model.addAttribute("people", personDao.findByKeyword(keyword));
        return "people/found";
    }

}
