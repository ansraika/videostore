package com.example.demo.Service;

import com.example.demo.Controller.VideoController;
import com.example.demo.Entity.Video;
import com.example.demo.Repository.VideoRepository;
import com.example.demo.Response.ShareableLinkResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VideoServiceTest {



    @Mock
    private VideoRepository videoRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private VideoService videoService;

    @Value("${video.upload-dir}")
    private String uploadDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        uploadDir = "src/test/resources";  // Set a test directory for file uploads
    }

    @Test
    public void testUploadVideo_invalidFileSize() throws IOException {
        long largeFileSize = 1024 * 1024 * 30; // 30 MB (exceeds max size)

        when(file.getSize()).thenReturn(largeFileSize);

        // Test for exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            videoService.uploadVideo(file, "large_video.mp4", 10);
        });

        assertEquals("File size exceeds the maximum allowed size of 25MB", exception.getMessage());
    }

    @Test
    public void testUploadVideo_invalidLength() throws IOException {
        when(file.getSize()).thenReturn((long) (1024 * 1024 * 10)); // 10 MB
        when(file.getOriginalFilename()).thenReturn("test_video.mp4");

        // Test for invalid video length
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            videoService.uploadVideo(file, "short_video.mp4", 0);
        });

        assertEquals("Video length must be between 5 and 15 seconds", exception.getMessage());
    }

    @Test
    public void testGenerateShareableLink() {
        String videoName = "test_video.mp4";
        Video video = new Video();
        video.setVideoName(videoName);
        video.setVideoPath("/path/to/video");
        when(videoRepository.findByVideoName(videoName)).thenReturn(Optional.of(video));

        ShareableLinkResponse response = videoService.generateShareableLink(videoName);

        assertNotNull(response.getShareableLink());
        assertTrue(response.getShareableLink().contains("http://localhost:8080/video/access?token="));
    }

    @Test
    public void testValidateAccessToken_validToken() {
        String token = UUID.randomUUID().toString();
        Instant expiryTime = Instant.now().plus(1, ChronoUnit.MINUTES); // Setting expiry time for 1 minute

        // Mock the tokenStore
        videoService.tokenStore.put(token, expiryTime);

        String result = videoService.validateAccessToken(token);

        assertEquals("The video is accessible via this temporary link.", result);
    }

    @Test
    public void testValidateAccessToken_expiredToken() {
        String token = UUID.randomUUID().toString();
        Instant expiryTime = Instant.now().minus(1, ChronoUnit.MINUTES); // Setting expired time

        // Mock the tokenStore
        videoService.tokenStore.put(token, expiryTime);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            videoService.validateAccessToken(token);
        });

        assertEquals("The link has expired or is invalid!", exception.getMessage());
    }


    @Test
    public void testTrimVideo_videoNotFound() throws IOException {
        String videoName = "non_existent_video.mp4";
        when(videoRepository.findByVideoName(videoName)).thenReturn(Optional.empty());

        IOException exception = assertThrows(IOException.class, () -> {
            videoService.trimVideo(videoName, 5, 10);
        });

        assertEquals("Video not found with name: " + videoName, exception.getMessage());
    }


    @Test
    public void testMergeVideos() throws IOException {
        List<String> videoNames = List.of("samplevideo.mp4", "samplevideo2.mp4");

        Video video1 = new Video();
        video1.setVideoName("samplevideo.mp4");
        video1.setVideoPath("/Users/anshitraika/Desktop/videoStreaming/src/main/resources/videostore/samplevideo.mp4");
        video1.setLengthInSeconds(10);
        video1.setSizeInMB(10);

        Video video2 = new Video();
        video2.setVideoName("samplevideo2.mp4");
        video2.setVideoPath("/Users/anshitraika/Desktop/videoStreaming/src/main/resources/videostore/samplevideo2.mp4");
        video2.setLengthInSeconds(20);
        video2.setSizeInMB(15);

        when(videoRepository.findByVideoName("samplevideo.mp4")).thenReturn(Optional.of(video1));
        when(videoRepository.findByVideoName("samplevideo2.mp4")).thenReturn(Optional.of(video2));

        // Mock merging process
        String outputVideoName = "merged_video.mp4";

        videoService.mergeVideos(videoNames, outputVideoName);

        // Verify that videoRepository.save was called for the merged video
        verify(videoRepository, times(1)).save(any(Video.class));
    }
}
