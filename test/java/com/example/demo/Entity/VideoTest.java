package com.example.demo.Entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

public class VideoTest {

    // Create a Validator instance to validate entities manually
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    @Test
    public void testVideoEntity() {
        Video video = new Video();

        // Test setters and getters for id
        video.setId(1L);
        assertEquals(1L, video.getId());

        // Test setters and getters for videoName
        String videoName = "test_video.mp4";
        video.setVideoName(videoName);
        assertEquals(videoName, video.getVideoName());

        // Test setters and getters for videoPath
        String videoPath = "/videos/test_video.mp4";
        video.setVideoPath(videoPath);
        assertEquals(videoPath, video.getVideoPath());

        // Test setters and getters for lengthInSeconds
        int lengthInSeconds = 10;
        video.setLengthInSeconds(lengthInSeconds);
        assertEquals(lengthInSeconds, video.getLengthInSeconds());

        // Test setters and getters for sizeInMB
        long sizeInMB = 100;
        video.setSizeInMB(sizeInMB);
        assertEquals(sizeInMB, video.getSizeInMB());
    }

    @Test
    public void testVideoValidation() {
        Video video = new Video();

        // Test validation constraints for lengthInSeconds
        video.setLengthInSeconds(-1);  // Invalid value for lengthInSeconds
        Set<ConstraintViolation<Video>> violations = validator.validate(video);
        assertFalse(violations.isEmpty(), "Expected validation failure for lengthInSeconds");

        // Test validation constraints for sizeInMB
        video.setSizeInMB(30);  // Invalid value for sizeInMB
        violations = validator.validate(video);
        assertFalse(violations.isEmpty(), "Expected validation failure for sizeInMB");

        // Check if specific validation messages are present
        for (ConstraintViolation<Video> violation : violations) {
            System.out.println(violation.getMessage());
        }
    }
}