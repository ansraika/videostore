package com.example.demo.Controller;

import com.example.demo.Service.VideoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("videoName") String videoName,
            @RequestParam("lengthInSeconds") @Min(5) @Max(15) int lengthInSeconds) {
        try {
            videoService.uploadVideo(file, videoName, lengthInSeconds);
            return ResponseEntity.status(HttpStatus.CREATED).body("Video uploaded successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while uploading the video:"+e.getMessage());
        }
    }
}
