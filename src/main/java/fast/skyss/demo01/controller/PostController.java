package fast.skyss.demo01.controller;

import fast.skyss.demo01.entity.Post;
import fast.skyss.demo01.entity.User;
import fast.skyss.demo01.service.PostService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/post")
public class PostController {
    @Value("${upload.path}")
    private String uploadPath;


    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!");
        }
    }

    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Path filePath = Paths.get(uploadPath).resolve(filename);
        Resource resource = new FileSystemResource(filePath.toFile());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/public")
    public String showPublicPosts(Model model) {
        model.addAttribute("publicPosts", postService.getAllPublicPosts());
        return "post/public/public";
    }

    @GetMapping("/public/search")
    public String showSearchPage() {
        return "post/public/search";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(defaultValue = "true") boolean publicFlag,
                             @RequestParam(required = false) MultipartFile attachment,
                             HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        postService.createPost(title, content, publicFlag, attachment, user);
        System.out.println(user + " Post created successfully!:" + title);
        return "redirect:/post/public";
    }

    @GetMapping("/post/{postId}")
    public String viewPost(@PathVariable Long postId, Model model) {
        model.addAttribute("post", postService.getPostById(postId));
        return "post/view'";
    }

    @PostMapping("/post/{postId}/delete")
    public String deletePost(@PathVariable Long postId, HttpSession session) {
        postService.deletePost(postId);
        return "redirect:/public";
    }

    @PostMapping("/post/{postId}/visibility")
    public String toggleVisibility(@PathVariable Long postId,
                                   @RequestParam boolean isPublic) {
        postService.updatePostVisibility(postId, isPublic);
        return "redirect:/public";
    }

    @GetMapping("/download/{postId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        if (post == null || post.getAttachmentPath() == null) {
            return ResponseEntity.notFound().build();
        }

        // Create full path to the file in uploads directory
        Path path = Paths.get("uploads", post.getAttachmentPath());
        Resource resource = new FileSystemResource(path.toFile());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Use original filename for download
        String originalFilename = post.getAttachmentPath();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + originalFilename + "\"")
                .body(resource);
    }



    @PostMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("posts", postService.searchPosts(keyword));
        System.out.println("Now search in postc with Keyword: " + keyword);
        return "post/public/search";
    }


}