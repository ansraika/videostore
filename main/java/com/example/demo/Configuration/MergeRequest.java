package com.example.demo.Configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a request to merge multiple videos into a single output video.
 * <p>
 * This class contains the list of video names that should be merged and the
 * name of the output video. It is typically used when merging videos
 * through the video service.
 * </p>
 */
@Getter
@Setter
public class MergeRequest {

    private List<String> videoNames;
    private String outputVideoName;
}
