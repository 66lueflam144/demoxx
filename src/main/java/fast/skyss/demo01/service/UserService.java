package fast.skyss.demo01.service;

import fast.skyss.demo01.entity.User;
import fast.skyss.demo01.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Value("${upload.path}")
    private String uploadPath;

    @Value("classpath:static/avatar.jpg")
    private Resource defaultAvatar;

    public User registerUser(String username, String password, String email, MultipartFile avatar) {

        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email already exists");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setAdmin(false);

        if (avatar != null && !avatar.isEmpty()) {
            String fileName = username + "_" + avatar.getOriginalFilename();
            String filePath = uploadPath + "/avatars/" + fileName;
            try {
                // Create avatars directory if it doesn't exist
                new File(uploadPath + "/avatars").mkdirs();
                avatar.transferTo(new File(filePath));
                newUser.setAvatarPath("/avatars/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Just set the path to the static resource
            newUser.setAvatarPath("/static/avatar.jpg");
        }

        return userRepository.save(newUser);
    }


    public User login(String username, String password) {
        return userRepository.findByUsername(username);
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name);
    }

    public Object getAllUsers() {
        return userRepository.findAll();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


    @Transactional(readOnly = true)
    public User getUserWithPosts(Long userId) {
        return userRepository.findUserWithPosts(userId);
    }

}
