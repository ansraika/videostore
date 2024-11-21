package com.example.demo.Configuration;


import java.util.List;

/**
 * Represents a request to merge multiple videos into a single output video.
 * <p>
 * This class contains the list of video names that should be merged and the
 * name of the output video. It is typically used when merging videos
 * through the video service.
 * </p>
 */
public class MergeRequest {
    private List<String> videoNames;
    private String outputVideoName;

    public String getOutputVideoName() {
        return outputVideoName;
    }

    public void setOutputVideoName(String outputVideoName) {
        this.outputVideoName = outputVideoName;
    }

    public List<String> getVideoNames() {
        return videoNames;
    }

    public void setVideoNames(List<String> videoNames) {
        this.videoNames = videoNames;
    }


}
