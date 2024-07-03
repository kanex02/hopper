package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
public interface NotificationRepository extends CrudRepository<Notification, Long> {

    Notification findById(long id);
    
    @Query("SELECT s FROM Location s ORDER BY s.city")
    List<Notification> findAll();

}
