/**
 * @author mkyong
 * Visit http://www.mkyong.com
 */
package com.mlucky.coin.web.controlers;

import com.mlucky.coin.web.model.User;
import com.mlucky.coin.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String home() {
        return "redirect:/index";
    }

    @RequestMapping("/index")
    public String listContacts(Map<String, Object> map) {

        map.put("user", new User());

        return "user";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json")
    public User login(@ModelAttribute("login") String login, @ModelAttribute("password") String password) {
        return "";
    }
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    public String addUser(@ModelAttribute("user") User user, BindingResult result) {
        userService.addUser(user);
        return "redirect:/login";
    }

    @RequestMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Integer userId) {
        userService.removeUser(userId);
        return "redirect:/login";
    }

//    @RequestMapping(value = '/logout', method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    void logout(HttpServletRequest servletRequest) {
//        String sessionId = AuthenticationProcessFilter.getSession(servletRequest)
//        sessionService.endSession(sessionId)
//    }

}
