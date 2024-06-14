package org.sourceovh.demoex.controller;

import org.sourceovh.demoex.model.Role;
import org.sourceovh.demoex.model.User;
import org.sourceovh.demoex.model.dto.UserDto;
import org.sourceovh.demoex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;

@Controller
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user")User user, UserDto userDto, Model model) {
        try {
            if (userService.existsByEmailOrNumberPhone(userDto.getEmail(), userDto.getNumberPhone())) {
                throw new Exception("Пользователь с таким email или номером телефона уже существует!");
            }
            userService.createUser(user);

            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/auth/register";
        }
    }


    @GetMapping("/login")
    public String login(Principal principal, Model model) {
            User user = userService.getUserByPrincipal(principal);
            model.addAttribute("user", user);
            return "/auth/login";
    }


    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "account/profile";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String showAdminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        return "account/admin";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/account/admin";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/users/role/{id}")
    public String updateUserRole(@PathVariable("id") Long userId, @RequestParam("role") String role) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.getRoles().clear();
        user.getRoles().add(Role.valueOf(role));

        userService.save(user);

        return "redirect:/users/admin";
    }


    @GetMapping("/")
    public String mainPage() {
        return "main";
    }
}


