package com.example.demo.Configuration;

import java.util.List;

public class MergeRequest {
    private List<String> videoNames;  // List of video names to merge
    private String outputVideoName;   // Name of the merged video

    public List<String> getVideoNames() {
        return videoNames;
    }

    public void setVideoNames(List<String> videoNames) {
        this.videoNames = videoNames;
    }

    public String getOutputVideoName() {
        return outputVideoName;
    }

    public void setOutputVideoName(String outputVideoName) {
        this.outputVideoName = outputVideoName;
    }




}
