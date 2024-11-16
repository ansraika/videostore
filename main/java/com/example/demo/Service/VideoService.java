package com.example.demo.Service;

import com.example.demo.Entity.Video;
import com.example.demo.Repository.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    private static final long MAX_SIZE_MB = 25; // max size of video in MB
    private static final int MIN_LENGTH_SEC = 5; // min video length in seconds
    private static final int MAX_LENGTH_SEC = 15; // max video length in seconds

    @Value("${video.upload-dir}")
    private String uploadDir;

    public Video uploadVideo(MultipartFile file, String videoName, int lengthInSeconds) throws IOException {
        // Validate file size
        log.info("Uploading the video...");
        if (file.getSize() > MAX_SIZE_MB * 1024 * 1024) {
            log.info("file size too large");
            throw new IllegalArgumentException("File size exceeds the maximum allowed size of 25MB");
        }

        // Validate video length
        if (lengthInSeconds < MIN_LENGTH_SEC || lengthInSeconds > MAX_LENGTH_SEC) {
            log.info("file time is not between 5 to 15 seconds");
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
        return videoRepository.save(video);
    }
}
