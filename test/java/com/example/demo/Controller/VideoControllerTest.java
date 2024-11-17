package com.example.demo.Controller;

import com.example.demo.Service.VideoService;
import com.example.demo.Configuration.MergeRequest;
import com.example.demo.Response.ShareableLinkResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class VideoControllerTest {

    private MockMvc mockMvc;
    private MergeRequest mergeRequest;

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideoController videoController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(videoController).build();
        mergeRequest = new MergeRequest();
        mergeRequest.setVideoNames(Arrays.asList("video1.mp4", "video2.mp4"));
        mergeRequest.setOutputVideoName("mergedVideo.mp4");
    }

    // Test case for handling IllegalArgumentException (catch block in uploadVideo)
    @Test
    public void testUploadVideoFailureDueToIllegalArgument() throws Exception {
        // Mock the service method call to throw an IllegalArgumentException
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", "test video content".getBytes());
        String videoName = "video1";
        int lengthInSeconds = 10;

        doThrow(new IllegalArgumentException("Invalid file size")).when(videoService).uploadVideo(file, videoName, lengthInSeconds);

        // Perform the request and assert the response
        mockMvc.perform(multipart("/api/videos/upload")
                        .file(file)
                        .param("videoName", videoName)
                        .param("lengthInSeconds", String.valueOf(lengthInSeconds)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid file size"));
    }

    @Test
    public void testMergeVideos_failureDueToIOException() throws IOException {
        // Simulate IOException during merge
        doThrow(new IOException("Error while merging videos")).when(videoService).mergeVideos(anyList(), anyString());

        // Call the mergeVideos API method
        ResponseEntity<String> response = videoController.mergeVideos(mergeRequest);

        // Assert the response status and body
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error merging videos"));
        assertTrue(response.getBody().contains("Error while merging videos"));

        // Verify that the videoService.mergeVideos method was called once
        verify(videoService, times(1)).mergeVideos(anyList(), anyString());
    }


    // Test case for handling IOException (catch block in trimVideo)
    @Test
    public void testTrimVideoFailureDueToIOException() throws Exception {
        String videoName = "video1";
        int startTrimSec = 5;
        int endTrimSec = 10;

        // Mock the videoService call to throw an IOException
        doThrow(new IOException("File read error")).when(videoService).trimVideo(videoName, startTrimSec, endTrimSec);

        // Perform the request and assert the response
        mockMvc.perform(post("/api/videos/trim")
                        .param("videoName", videoName)
                        .param("startTrimSec", String.valueOf(startTrimSec))
                        .param("endTrimSec", String.valueOf(endTrimSec)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error trimming video: File read error"));
    }

    // Test case for handling NoSuchElementException (catch block in generateShareableLink)
    @Test
    public void testGenerateShareableLinkFailureDueToNoSuchElementException() throws Exception {
        String videoName = "non_existing_video";

        // Mock the videoService call to throw a NoSuchElementException
        when(videoService.generateShareableLink(videoName)).thenThrow(new NoSuchElementException("Video not found"));

        // Perform the request and assert the response
        mockMvc.perform(get("/api/videos/generate-link")
                        .param("videoName", videoName))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Video not found"));
    }

    // Test case for handling IllegalArgumentException (catch block in accessVideo for invalid token)
    @Test
    public void testAccessVideoFailureDueToInvalidToken() throws Exception {
        String token = "invalid_token";

        // Mock the videoService call to throw an IllegalArgumentException for invalid token
        when(videoService.validateAccessToken(token)).thenThrow(new IllegalArgumentException("The link has expired or is invalid!"));

        // Perform the request and assert the response
        mockMvc.perform(get("/api/videos/access-link")
                        .param("token", token))
                .andExpect(status().isGone())
                .andExpect(content().string("The link has expired or is invalid!"));
    }

    // Test case for handling successful upload
    @Test
    public void testUploadVideoSuccess() throws Exception {
        // Mock the service method call to successfully upload the video
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", "test video content".getBytes());
        String videoName = "video1";
        int lengthInSeconds = 10;

        doNothing().when(videoService).uploadVideo(file, videoName, lengthInSeconds);

        mockMvc.perform(multipart("/api/videos/upload")
                        .file(file)
                        .param("videoName", videoName)
                        .param("lengthInSeconds", String.valueOf(lengthInSeconds)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Video uploaded successfully!"));
    }

    @Test
    public void testUploadVideo_InternalServerError() throws Exception {
        // Mock the service method call to successfully upload the video
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", "test video content".getBytes());
        String videoName = "video1";
        int lengthInSeconds = 10;

        // Mock VideoService to throw an unchecked exception (RuntimeException)
        doThrow(new RuntimeException("Generic error")).when(videoService).uploadVideo(file, videoName, lengthInSeconds);

        // Execute the controller method
        ResponseEntity<String> response = videoController.uploadVideo(file, videoName, lengthInSeconds);

        // Assert that the status is INTERNAL_SERVER_ERROR (500)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // Assert that the response body contains the error message
        assertTrue(response.getBody().contains("An error occurred while uploading the video:Generic error"));
    }


    // Test case for trimming video successfully
    @Test
    public void testTrimVideoSuccess() throws Exception {
        String videoName = "video1";
        int startTrimSec = 5;
        int endTrimSec = 10;

        doNothing().when(videoService).trimVideo(videoName, startTrimSec, endTrimSec);

        mockMvc.perform(post("/api/videos/trim")
                        .param("videoName", videoName)
                        .param("startTrimSec", String.valueOf(startTrimSec))
                        .param("endTrimSec", String.valueOf(endTrimSec)))
                .andExpect(status().isOk())
                .andExpect(content().string("Video trimmed successfully!"));
    }

    // Test case for successful merge
    @Test
    public void testMergeVideosSuccess() throws Exception {
        List<String> videoNames = Arrays.asList("video1", "video2");
        String outputVideoName = "merged_video";

        MergeRequest mergeRequest = new MergeRequest();
        mergeRequest.setVideoNames(videoNames);
        mergeRequest.setOutputVideoName(outputVideoName);

        doNothing().when(videoService).mergeVideos(videoNames, outputVideoName);

        mockMvc.perform(post("/api/videos/merge")
                        .contentType("application/json")
                        .content("{\"videoNames\": [\"video1\", \"video2\"], \"outputVideoName\": \"merged_video\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Videos merged successfully."));
    }

    // Test case for generating shareable link successfully
    @Test
    public void testGenerateShareableLinkSuccess() throws Exception {
        String videoName = "video1";
        ShareableLinkResponse shareableLinkResponse = new ShareableLinkResponse("http://localhost:8080/video/access?token=xyz", null);

        when(videoService.generateShareableLink(videoName)).thenReturn(shareableLinkResponse);

        mockMvc.perform(get("/api/videos/generate-link")
                        .param("videoName", videoName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareableLink").value("http://localhost:8080/video/access?token=xyz"));
    }

    // Test case for accessing video successfully
    @Test
    public void testAccessVideoSuccess() throws Exception {
        String token = "xyz";

        when(videoService.validateAccessToken(token)).thenReturn("The video is accessible via this temporary link.");

        mockMvc.perform(get("/api/videos/access-link")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string("The video is accessible via this temporary link."));
    }
}
