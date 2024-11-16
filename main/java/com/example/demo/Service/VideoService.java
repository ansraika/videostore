package com.example.demo.Service;

import com.example.demo.Entity.Video;
import com.example.demo.Repository.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    private static final long MAX_SIZE_MB = 25; // max size of video in MB
    private static final int MIN_LENGTH_SEC = 1; // min video length in seconds
    private static final int MAX_LENGTH_SEC = 25; // max video length in seconds

    @Value("${video.upload-dir}")
    private String uploadDir;

    public void uploadVideo(MultipartFile file, String videoName, int lengthInSeconds) throws IOException {
        // Validate file size
        log.info("Uploading the video...");
        if (file.getSize() > MAX_SIZE_MB * 1024 * 1024) {
            log.info("file size too large");
            throw new IllegalArgumentException("File size exceeds the maximum allowed size of 25MB");
        }

        // Validate video length
        if (lengthInSeconds < MIN_LENGTH_SEC || lengthInSeconds > MAX_LENGTH_SEC) {
            log.info("file time is not between 1 to 25 seconds");
            throw new IllegalArgumentException("Video length must be between 5 and 15 seconds");
        }

        // Create the directory if it doesn't exist
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            if (!dirCreated) {
                throw new IOException("Failed to create directory: " + uploadDir);
            }
        }

        // Save the video file in the directory
        String videoPath = uploadDir + "/" + videoName;
        File videoFile = new File(videoPath);
        file.transferTo(videoFile);

        // Create a new Video entity
        Video video = new Video();
        video.setVideoName(videoName);
        video.setVideoPath(videoPath);
        video.setLengthInSeconds(lengthInSeconds);
        video.setSizeInMB(file.getSize() / (1024 * 1024)); // convert size to MB

        // Save the video entity to the database
        videoRepository.save(video);
    }



    // Method for trimming the video
    public void trimVideo(String videoName, int startTrimSec, int endTrimSec) throws IOException {
        Video video = videoRepository.findByVideoName(videoName)
                .orElseThrow(() -> new IOException("Video not found with name: " + videoName));

        String originalVideoPath = video.getVideoPath();
        File originalVideo = new File(originalVideoPath);

        if (!originalVideo.exists()) {
            throw new IOException("Original video file not found at: " + originalVideoPath);
        }

        // Calculate the output file path for the trimmed video
        String trimmedVideoPath = uploadDir + "/trimmed_" + videoName;
        File trimmedVideo = new File(trimmedVideoPath);

        // Execute FFmpeg command to trim the video
        String command = String.format("ffmpeg -ss %d -i %s -to %d -c:v libx264 -c:a aac -strict experimental %s",
                startTrimSec, originalVideoPath, endTrimSec, trimmedVideoPath);

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();  // Wait for the process to finish
        } catch (InterruptedException | IOException e) {
            log.error("Error occurred while trimming the video", e);
            throw new IOException("Error occurred while trimming the video", e);
        }

        // Update the video entity with the new trimmed video
        video.setVideoPath(trimmedVideoPath);
        video.setLengthInSeconds(endTrimSec - startTrimSec); // Update the video length
        video.setSizeInMB(trimmedVideo.length() / (1024 * 1024)); // Update the size in MB

        // Save the updated video entity
        videoRepository.save(video);
    }


    public void mergeVideos(List<String> videoNames, String outputVideoName) throws IOException {
        // Validate the video list: Check if all videos exist
        List<String> videoPaths = new ArrayList<>();
        for (String videoName : videoNames) {
            Video video = videoRepository.findByVideoName(videoName)
                    .orElseThrow(() -> new IOException("Video not found with name: " + videoName));
            String videoPath = video.getVideoPath();
            File videoFile = new File(videoPath);

            if (!videoFile.exists()) {
                throw new IOException("Video file not found at: " + videoPath);
            }
            videoPaths.add(videoPath);
        }

        // Create the FFmpeg command for merging
        StringBuilder inputFiles = new StringBuilder();
        for (int i = 0; i < videoPaths.size(); i++) {
            inputFiles.append("file '").append(videoPaths.get(i)).append("'\n");
        }

        // Create a temporary text file that lists the videos to be merged
        File tempFile = File.createTempFile("videos_list", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(inputFiles.toString());
        }

        // Output path for the merged video
        String mergedVideoPath = uploadDir + "/" + outputVideoName;
        File mergedVideoFile = new File(mergedVideoPath);

        // FFmpeg command to merge videos
        String command = String.format("ffmpeg -f concat -safe 0 -i %s -c copy %s", tempFile.getAbsolutePath(), mergedVideoPath);

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();  // Wait for the process to finish
        } catch (InterruptedException | IOException e) {
            log.error("Error occurred while merging the videos", e);
            throw new IOException("Error occurred while merging the videos", e);
        } finally {
            // Delete the temporary file after use
            tempFile.delete();
        }

        // Update the database with the new merged video information
        Video mergedVideo = new Video();
        mergedVideo.setVideoName(outputVideoName);
        mergedVideo.setVideoPath(mergedVideoPath);

        // Assuming the merged video has a length of the sum of the individual videos' lengths
        int totalLength = videoNames.stream()
                .mapToInt(name -> {
                    Video video = null;
                    try {
                        video = videoRepository.findByVideoName(name)
                                .orElseThrow(() -> new IOException("Video not found with name: " + name));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return video.getLengthInSeconds();
                })
                .sum();
        mergedVideo.setLengthInSeconds(totalLength);

        // Set the size of the merged video (in MB)
        mergedVideo.setSizeInMB(mergedVideoFile.length() / (1024 * 1024)); // Size in MB

        // Save the merged video in the database
        videoRepository.save(mergedVideo);
    }

}
