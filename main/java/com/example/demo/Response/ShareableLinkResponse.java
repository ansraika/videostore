package com.example.demo.Response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents the response containing a shareable link and its expiry time.
 * <p>
 * This class is used to provide a shareable link to a resource, along with the expiry time of the link.
 * The shareable link is used for granting temporary access to a resource, and the expiry time indicates
 * when the link will no longer be valid.
 * </p>
 */
@Getter
@Setter
public class ShareableLinkResponse {

    /**
     * The generated shareable link that provides access to a resource.
     */
    private String shareableLink;

    /**
     * The time when the shareable link will expire.
     */
    private Instant expiryTime;

    /**
     * Constructs a new {@link ShareableLinkResponse} with the provided shareable link and expiry time.
     *
     * @param shareableLink the generated shareable link for accessing a resource
     * @param expiryTime the expiry time of the shareable link
     */
    public ShareableLinkResponse(String shareableLink, Instant expiryTime) {
        this.shareableLink = shareableLink;
        this.expiryTime = expiryTime;
    }
}
