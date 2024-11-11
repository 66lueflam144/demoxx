package fast.skyss.demo01.controller;

import fast.skyss.demo01.entity.User;
import fast.skyss.demo01.repository.PostRepository;
import fast.skyss.demo01.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final PostRepository postService;

    @Autowired
    public UserController(UserService userService, PostRepository postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        // 重新从数据库加载用户信息，包括posts
        user = userService.getUserWithPosts(user.getId());
        model.addAttribute("user", user);
        System.out.println(user + "in HOME");
        return "user/home";
    }


    @PostMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        // Vulnerable to XSS - directly passing user input to view
        model.addAttribute("keyword", keyword);

        model.addAttribute("posts", postService.searchPosts(keyword));

        System.out.println("Now searching for: " + keyword);
        return "post/public/search";
    }
}