package com.example.demo.Controller;

import com.example.demo.Configuration.MergeRequest;
import com.example.demo.Service.VideoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
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

    @PostMapping("/trim")
    public ResponseEntity<String> trimVideo(@RequestParam String videoName,
                                            @RequestParam int startTrimSec,
                                            @RequestParam int endTrimSec) {
        try {
            //startTrimSec: the start time in seconds from where to trim the video.
            //endTrimSec: the end time in seconds where the video should stop
            // Ensure start and end times are valid
            if (startTrimSec < 0 || endTrimSec <= startTrimSec) {
                return new ResponseEntity<>("Invalid time range", HttpStatus.BAD_REQUEST);
            }

            // Trim the video
            videoService.trimVideo(videoName, startTrimSec, endTrimSec);
            return new ResponseEntity<>("Video trimmed successfully!", HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>("Error trimming video: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/merge")
    public ResponseEntity<String> mergeVideos(@RequestBody MergeRequest mergeRequest) {
        try {
            videoService.mergeVideos(mergeRequest.getVideoNames(), mergeRequest.getOutputVideoName());
            return ResponseEntity.ok("Videos merged successfully.");
        } catch (IOException e) {
            log.error("Error merging videos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error merging videos: " + e.getMessage());
        }
    }

}
