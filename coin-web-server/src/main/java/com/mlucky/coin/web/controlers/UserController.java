/**
 * @author mkyong
 * Visit http://www.mkyong.com
 */
package com.mlucky.coin.web.controlers;

import com.mlucky.coin.web.model.User;
import com.mlucky.coin.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addUser(@ModelAttribute("user") User user, BindingResult result) {
        userService.addUser(user);
        return "redirect:/login";
    }

    @RequestMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Integer userId) {
        userService.removeUser(userId);
        return "redirect:/login";
    }

}
