package com.example.demo.Response;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class ShareableLinkResponseTest {

    @Test
    public void testConstructorAndGetters() {
        String expectedLink = "http://example.com/shareable-link";
        Instant expectedExpiryTime = Instant.now().plusSeconds(60);  // 1 minute from now

        // Create an instance using the constructor
        ShareableLinkResponse response = new ShareableLinkResponse(expectedLink, expectedExpiryTime);

        // Verify that the constructor sets the values correctly
        assertEquals(expectedLink, response.getShareableLink());
        assertEquals(expectedExpiryTime, response.getExpiryTime());
    }

    @Test
    public void testSetters() {
        String newLink = "http://example.com/new-shareable-link";
        Instant newExpiryTime = Instant.now().plusSeconds(120);  // 2 minutes from now

        // Create an instance using the constructor
        ShareableLinkResponse response = new ShareableLinkResponse("initialLink", Instant.now());

        // Use setters to update values
        response.setShareableLink(newLink);
        response.setExpiryTime(newExpiryTime);

        // Verify that the setters update the values correctly
        assertEquals(newLink, response.getShareableLink());
        assertEquals(newExpiryTime, response.getExpiryTime());
    }
}
