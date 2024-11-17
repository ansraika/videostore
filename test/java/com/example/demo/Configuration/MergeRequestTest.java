package com.example.demo.Configuration;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MergeRequestTest {

    @Test
    public void testSetAndGetVideoNames() {
        // Create an instance of MergeRequest
        MergeRequest mergeRequest = new MergeRequest();

        // Prepare test data
        List<String> videoNames = Arrays.asList("video1.mp4", "video2.mp4", "video3.mp4");

        // Set the video names
        mergeRequest.setVideoNames(videoNames);

        // Assert that the video names were set correctly
        assertEquals(videoNames, mergeRequest.getVideoNames(), "The video names should be the same as the ones set.");
    }

    @Test
    public void testSetAndGetOutputVideoName() {
        // Create an instance of MergeRequest
        MergeRequest mergeRequest = new MergeRequest();

        // Set the output video name
        String outputVideoName = "merged_video.mp4";
        mergeRequest.setOutputVideoName(outputVideoName);

        // Assert that the output video name was set correctly
        assertEquals(outputVideoName, mergeRequest.getOutputVideoName(), "The output video name should be the same as the one set.");
    }

    @Test
    public void testEmptyVideoNames() {
        // Create an instance of MergeRequest
        MergeRequest mergeRequest = new MergeRequest();

        // Set an empty list of video names
        mergeRequest.setVideoNames(Arrays.asList());

        // Assert that the video names are empty
        assertTrue(mergeRequest.getVideoNames().isEmpty(), "The video names list should be empty.");
    }

    @Test
    public void testNullVideoNames() {
        // Create an instance of MergeRequest
        MergeRequest mergeRequest = new MergeRequest();

        // Set the video names to null
        mergeRequest.setVideoNames(null);

        // Assert that the video names are null
        assertNull(mergeRequest.getVideoNames(), "The video names should be null.");
    }

    @Test
    public void testNullOutputVideoName() {
        // Create an instance of MergeRequest
        MergeRequest mergeRequest = new MergeRequest();

        // Set the output video name to null
        mergeRequest.setOutputVideoName(null);

        // Assert that the output video name is null
        assertNull(mergeRequest.getOutputVideoName(), "The output video name should be null.");
    }

    @Test
    public void testSetAndGetWithDifferentValues() {
        // Create an instance of MergeRequest
        MergeRequest mergeRequest = new MergeRequest();

        // Prepare test data
        List<String> videoNames = Arrays.asList("videoA.mp4", "videoB.mp4");
        String outputVideoName = "merged_video_A_B.mp4";

        // Set the video names and output video name
        mergeRequest.setVideoNames(videoNames);
        mergeRequest.setOutputVideoName(outputVideoName);

        // Assert that the values are correctly set
        assertEquals(videoNames, mergeRequest.getVideoNames(), "The video names should be set correctly.");
        assertEquals(outputVideoName, mergeRequest.getOutputVideoName(), "The output video name should be set correctly.");
    }
}
