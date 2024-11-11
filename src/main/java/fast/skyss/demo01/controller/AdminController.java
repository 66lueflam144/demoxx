package fast.skyss.demo01.controller;

import fast.skyss.demo01.entity.Post;
import fast.skyss.demo01.entity.User;
import fast.skyss.demo01.service.PostService;
import fast.skyss.demo01.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    private final PostService postService;

    @Autowired
    public AdminController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/dashboard";
    }

    // Vulnerable: No proper authorization check
    @GetMapping("/user/{userId}")
    public String viewUserProfile(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        // Vulnerable: Exposing all posts including private ones
        model.addAttribute("posts", postService.findByUser(user));
        System.out.println("Now viewing user: " + user.getUsername());
        return "admin/user-profile";
    }

    // Vulnerable: No CSRF protection, no proper validation
    @PostMapping("/user/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        System.out.println("NoW deleted userId: " + userId);
        return "redirect:/admin/dashboard";
    }
    @PostMapping("/post/{postId}/delete")
    public String deletePost(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        Long userId = post.getUser().getId();
        postService.deletePost(postId);
        System.out.println("NoW deleted postId: " + postId);
        return "redirect:/admin/user/" + userId;
    }

    @PostMapping("/post/{postId}/visibility")
    public String togglePostVisibility(@PathVariable Long postId,
                                       @RequestParam Boolean publicFlag) {
        Post post = postService.getPostById(postId);
        Long userId = post.getUser().getId();
        postService.updatePostVisibility(postId, publicFlag);
        System.out.println("postId: " + postId + ", publicFlag: " + publicFlag);
        return "redirect:/admin/user/" + userId;
    }
}