package com.example.demo.Response;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ShareableLinkResponse {
    // Getters and setters
    private String shareableLink;
    private Instant expiryTime;

    public ShareableLinkResponse(String shareableLink, Instant expiryTime) {
        this.shareableLink = shareableLink;
        this.expiryTime = expiryTime;
    }

    public void setShareableLink(String shareableLink) {
        this.shareableLink = shareableLink;
    }

    public void setExpiryTime(Instant expiryTime) {
        this.expiryTime = expiryTime;
    }
}
