package com.paige.login.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.paige.login.models.User;
import com.paige.login.services.UserService;

@Controller
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registration.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "login.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
 // if result has errors, return the registration page (don't worry about 	validations just now)
    		if(result.hasErrors()) {
    			return "registration.jsp";
    		}
// else, save the user in the database, save the user id in session, and redirect them to the /home route
    		else {
    			userService.registerUser(user);
    			session.setAttribute("userInSessionId", user.getId());
    			return "redirect:/home";
    		}
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        // if the user is authenticated, save their user id in session
    		if (userService.authenticateUser(email, password)) {
    			User userInSession = userService.findByEmail(email);
    			session.setAttribute("userInSessionId", userInSession.getId());
    			return "redirect:/home";
    		}
        // else, add error messages and return the login page
    		else {
    			model.addAttribute("error", "Invalid credentials please try again");
    			return "login.jsp";
    		}
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        // get user from session, save them in the model and return the home page
    		Long userId = (Long) session.getAttribute("userInSessionId");
    		model.addAttribute("user", userService.findUserById(userId));
    		return "main.jsp";
    }
    
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session
        // redirect to login page
    		session.invalidate();
    		return "redirect:/login";
    }
}
