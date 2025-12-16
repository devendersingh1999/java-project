package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

@Controller
public class UdemyController {

    @Autowired
    private JdbcTemplate template;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Show signup page
    @GetMapping("/")
    public String signupForm() {
        return "signup"; // Thymeleaf template: src/main/resources/templates/signup.html
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // Handle signup form submission
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDetails userdetails) {
        int count = template.queryForObject(
            "SELECT COUNT(*) FROM userDetails WHERE email=?",
            Integer.class, userdetails.getEmail()
        );

        if (count > 0) return "redirect:/error"; // redirect to error page

        template.update(
            "INSERT INTO userDetails(name,email,password) VALUES (?,?,?)",
            userdetails.getName(),
            userdetails.getEmail(),
            encoder.encode(userdetails.getPassword())
        );

        return "redirect:/login"; // redirect to login page
    }

    // Show login page
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Thymeleaf template: login.html
    }

    // Handle login submission
    @PostMapping("/login")
    public String login(@ModelAttribute UserDetails userdetails, HttpSession session) {
        try {
            String storedPassword = template.queryForObject(
                "SELECT password FROM userDetails WHERE email=?",
                String.class, userdetails.getEmail()
            );

            if (encoder.matches(userdetails.getPassword(), storedPassword)) {
                session.setAttribute("email", userdetails.getEmail());
                return "redirect:/home"; // redirect to home page
            } else {
                return "redirect:/error";
            }

        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login"; // redirect to login page
    }

    // Show error page
    @GetMapping("/error")
    public String errorPage() {
        return "error"; // Thymeleaf template: error.html
    }

    // Show home page
    @GetMapping("/home")
    public String homePage() {
        return "home"; // Thymeleaf template: home.html
    }
}
