package fast.skyss.demo01.repository;

import fast.skyss.demo01.entity.Post;
import fast.skyss.demo01.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
//    List<Post> findByUserAndIsPublic(User user, boolean isPublic);

    // Vulnerable search method (SQL injection possible)
    @Query(value = "SELECT * FROM posts WHERE title LIKE %?1% OR content LIKE %?1%", nativeQuery = true)
    List<Post> searchPosts(String keyword);

    // Changed to parameterized query to prevent SQL injection
    //@Query("SELECT p FROM Post p WHERE p.content LIKE %:keyword% OR p.title LIKE %:keyword%")

    List<Post> findByUser(User user);

    List<Post> findByPublicFLag(boolean publicFlag);
}

