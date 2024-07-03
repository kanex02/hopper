package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.blog.BlogPost;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlogPostRepository extends CrudRepository<BlogPost, Long> {

    /**
     * Finds all blog posts ordered by date descending
     * @return A list of blog posts
     */
    List<BlogPost> findAllByOrderByDateDesc();
}
