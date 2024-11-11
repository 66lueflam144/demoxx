package fast.skyss.demo01.controller;

import fast.skyss.demo01.entity.User;
import fast.skyss.demo01.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String userLogin() {
        return "auth/login";
    }

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "auth/admin-login";
    }

    @PostMapping("/login/process")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session) {
        User user = userService.login(username, password);
        if (user!= null &&!user.isAdmin()) {
            // Vulnerable: storing user object directly in session
            session.setAttribute("user", user);
            System.out.println("Now Logged In AS USER IS:" + user.getUsername());
            return "redirect:/user/home";
        }
        return "redirect:/login?error";
    }

    @PostMapping("/admin/login/process")
    public String processAdminLogin(@RequestParam String username,
                                    @RequestParam String password,
                                    @RequestParam(required = false) MultipartFile avatar,
                                    HttpSession session) {
        User admin = userService.login(username, password);
        // Vulnerable: weak admin check
        if (admin!= null && admin.isAdmin()) {
            session.setAttribute("admin", admin);
            System.out.println("Now Logged In AS ADMIN IS:" + admin.getUsername());
            return "redirect:/admin/dashboard";
        }
        return "redirect:admin/login?error";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/register/process")
    public String processRegistration(@RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam String email,
                                      @RequestParam(required = false) MultipartFile avatar,
                                      HttpSession session) {
        // Vulnerable: No input validation
        User newUser = userService.registerUser(username, password, email, avatar);
        if (newUser!= null) {
            // Vulnerable: Automatic login after registration
            session.setAttribute("user", newUser);
            System.out.println("Now Registered In AS USER IS:" + newUser.getUsername());
            return "redirect:/login";
        }
        return "redirect:/register?error";
    }
}