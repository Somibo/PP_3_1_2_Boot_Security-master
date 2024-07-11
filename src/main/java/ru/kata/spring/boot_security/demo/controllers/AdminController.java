package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/read";
    }

    @GetMapping("/new")
    public String getUserCreateForm(@ModelAttribute("user") User user, Model model) {
        // Set default role as ROLE_USER
        user.setAdmin(false);
        model.addAttribute("roles", roleService.getRoles());
        return "admin/create";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String getUserEditForm(@PathVariable("id") Long id, Model model) {
        User userById = userService.findById(id);
        if (userById == null) {
            throw new IllegalArgumentException("User with ID " + id + " not found.");
        }
        model.addAttribute("user", userById);
        model.addAttribute("roles", roleService.getRoles());
        return "admin/update";
    }

    @PutMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        // Check if the user with this ID exists
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User with ID " + id + " not found.");
        }

        // Set the ID from path variable to ensure it's not changed
        user.setId(id);

        userService.updateUser(user);

        redirectAttributes.addFlashAttribute("message", "User updated successfully");

        return "redirect:/admin/users";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}
