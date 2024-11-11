package fast.skyss.demo01.service;

import fast.skyss.demo01.entity.Post;
import fast.skyss.demo01.entity.User;
import fast.skyss.demo01.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public Post createPost(String title, String content, boolean isPublic,
                           MultipartFile attachment, User user) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setPublicFLag(isPublic);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());

        if (attachment != null && !attachment.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + attachment.getOriginalFilename();
            Path targetLocation = Paths.get(uploadPath).resolve(fileName);
            try {
                Files.copy(attachment.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                post.setAttachmentPath(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Could not store file " + fileName);
            }
        }

        return postRepository.save(post);
    }


    public void updateVisibility(Long postId, boolean isPublic) {
        // Vulnerable: No authorization check
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            post.setPublicFLag(isPublic);
            postRepository.save(post);
        }
    }

    public List<Post> findByUser(User user) {
        // Vulnerable: Returns all posts including private ones
        return postRepository.findByUser(user);
    }

    public void updatePostVisibility(Long postId, Boolean isPublic) {
        // Vulnerable: Direct update without validation
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            post.setPublicFLag(isPublic);
            postRepository.save(post);
        }
    }

    // Additional useful methods
    public void deletePost(Long postId) {
        // Vulnerable: No authorization check
        postRepository.deleteById(postId);
    }



    public Post getPostById(Long postId) {
        // Vulnerable: No access control
        return postRepository.findById(postId).orElse(null);
    }

    public List<Post> getAllPublicPosts() {
        return postRepository.findByPublicFLag(true);
    }

    public List<Post> searchPosts(String keyword) {
        // Modified to only return public posts in search
        List<Post> posts = postRepository.searchPosts(keyword);
        return posts;
//        return posts.stream()
//                .filter(Post::isPublic)//a filter clearly
//                .collect(Collectors.toList());
    }

    public List<Post> getUserPosts(User user) {
        // Returns all posts (public and private) for the logged-in user
        return postRepository.findByUser(user);
    }
}
