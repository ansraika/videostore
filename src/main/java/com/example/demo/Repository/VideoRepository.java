package com.example.demo.Repository;

import com.example.demo.Entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    // Custom query method to find a video by its name
    Optional<Video> findByVideoName(String videoName);

}
