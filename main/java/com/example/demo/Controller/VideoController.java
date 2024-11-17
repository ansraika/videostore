package com.example.demo.Controller;

import com.example.demo.Configuration.MergeRequest;
import com.example.demo.Response.ShareableLinkResponse;
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
import java.util.NoSuchElementException;

/**
 * REST controller for handling video-related operations such as uploading, trimming,
 * merging, generating shareable links, and validating access tokens.
 */
@Slf4j
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * Uploads a video file to the server.
     *
     * @param file            the video file to upload
     * @param videoName       the name of the video
     * @param lengthInSeconds the length of the video in seconds, constrained to be between 5 and 15 seconds
     * @return a ResponseEntity indicating the success or failure of the operation
     */
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while uploading the video:" + e.getMessage());
        }
    }

    /**
     * Trims a video to a specific time range.
     *
     * @param videoName   the name of the video to trim
     * @param startTrimSec the start time in seconds from which to trim the video
     * @param endTrimSec   the end time in seconds where the video should stop trimming
     * @return a ResponseEntity indicating the success or failure of the trimming operation
     */
    @PostMapping("/trim")
    public ResponseEntity<String> trimVideo(@RequestParam String videoName,
                                            @RequestParam int startTrimSec,
                                            @RequestParam int endTrimSec) {
        try {
            // Validate time range
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

    /**
     * Merges multiple videos into a single output video.
     *
     * @param mergeRequest the request body containing the video names and the output video name
     * @return a ResponseEntity indicating the success or failure of the merge operation
     */
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

    /**
     * Generates a shareable link for a video.
     *
     * @param videoName the name of the video for which the shareable link should be generated
     * @return a ResponseEntity containing the generated link or an error message
     */
    @GetMapping("/generate-link")
    public ResponseEntity<?> generateShareableLink(@RequestParam String videoName) {
        try {
            log.info("Generating shareable link...");
            ShareableLinkResponse response = videoService.generateShareableLink(videoName);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Validates an access token for a video.
     *
     * @param token the access token to validate
     * @return a ResponseEntity indicating whether the token is valid and access is granted
     */
    @GetMapping("/access-link")
    public ResponseEntity<?> accessVideo(@RequestParam String token) {
        try {
            String result = videoService.validateAccessToken(token);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
        }
    }
}
