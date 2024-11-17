

# Video Store

## Project Overview

The project is a Spring Boot-based REST API designed to handle video file uploads, trimming, merging, and generating shareable links. This project provides endpoints to interact with video data, allowing users to upload videos, trim them, merge multiple videos, and share video links securely.

### Key Features

- **Upload Video**: Allows uploading of video files with validation on file size and video length.
- **Trim Video**: Provides functionality to trim a video between two time intervals.
- **Merge Videos**: Enables merging multiple videos into one.
- **Generate Shareable Link**: Generates a shareable link for a video.
- **Validate Access Token**: Validates a token to grant or deny access to a video.

## API Endpoints

### 1. Upload Video

- **Endpoint**: `POST /api/videos/upload`
- **Description**: Uploads a video to the server.
- **Request Parameters**:
  - `file` (required): The video file to upload.
  - `videoName` (required): The name of the video.
  - `lengthInSeconds` (required): The length of the video in seconds (between 5 and 15).
  
- **Responses**:
  - `201 Created`: Video uploaded successfully.
  - `400 Bad Request`: Invalid file size or video length.
  - `500 Internal Server Error`: Error occurred while uploading the video.

### 2. Trim Video

- **Endpoint**: `POST /api/videos/trim`
- **Description**: Trims a video between the specified start and end time.
- **Request Parameters**:
  - `videoName` (required): The name of the video.
  - `startTrimSec` (required): Start time of the trim in seconds.
  - `endTrimSec` (required): End time of the trim in seconds.
  
- **Responses**:
  - `200 OK`: Video trimmed successfully.
  - `400 Bad Request`: Invalid time range.
  - `500 Internal Server Error`: Error trimming video.

### 3. Merge Videos

- **Endpoint**: `POST /api/videos/merge`
- **Description**: Merges multiple videos into a single video.
- **Request Body**:
  - `mergeRequest` (required): JSON containing a list of video names and the output video name.

- **Responses**:
  - `200 OK`: Videos merged successfully.
  - `500 Internal Server Error`: Error merging videos.

### 4. Generate Shareable Link

- **Endpoint**: `GET /api/videos/generate-link`
- **Description**: Generates a shareable link for a video.
- **Request Parameters**:
  - `videoName` (required): The name of the video.
  
- **Responses**:
  - `200 OK`: Shareable link generated successfully.
  - `404 Not Found`: Video not found.

### 5. Access Video

- **Endpoint**: `GET /api/videos/access-link`
- **Description**: Validates an access token to grant access to a video.
- **Request Parameters**:
  - `token` (required): The access token.

- **Responses**:
  - `200 OK`: Access granted.
  - `410 Gone`: Invalid or expired token.

## Setup Instructions

### 1. Clone the Repository

Clone the repository to your local machine using the following command:

```bash
git clone https://github.com/your-username/video-management-api.git
```

### 2. Prerequisites

Ensure the following are installed:

- **Java**: Version 11 or higher
- **Maven**: For dependency management and building the project
- **JDK**: Java Development Kit (version 11 or higher)

### 3. Install Dependencies

Navigate to the project directory and run the following command to install dependencies:

```bash
mvn clean install
```

This will also generate the code coverage reports and other necessary files.

### 4. Configure the Application

If necessary, update the application properties file for your environment (e.g., `src/main/resources/application.properties`) to configure file upload directories, database connections, etc. Specifically, make sure the following is set:

- `video.upload-dir`: Define the directory where videos will be uploaded and processed.

## Running the API Server

To run the API server locally, use the following command:

```bash
mvn spring-boot:run
```

This will start the API server on the default port `8080`. You can access the API at `http://localhost:8080/api/videos`.

## Running the Test Suite

To run the test suite and generate the code coverage report, execute the following commands:

```bash
mvn clean install
```

This will:
- Clean the existing build.
- Install the dependencies.
- Generate code coverage reports.

After running the tests, the generated HTML report can be found at:

```plaintext
target/site/jacoco/index.html
```

You can open this file in any browser to view the detailed test coverage report.

## Testing

The project includes unit tests for video upload, trimming, merging, and the various edge cases. The tests are written using **JUnit** and **Mockito** for mocking dependencies. You can find the test files in the `src/test` directory.

## Things to Keep in Mind to Run the Code:

1. **Relative Path for Video Upload Directory**: 
   - Ensure that the `video.upload-dir` property in `application.properties` is correctly set. The API expects this directory for video uploads and processing.

2. **Download Postman Collection**:
   - The Postman collection for testing the API is included in this repo. Download it and import it into your Postman to easily test the API endpoints.

3. **Download Sample Videos**:
   - Download the sample video files (`samplevideo.mp4` and `samplevideo2.mp4`) that are attached to this repo.

4. **Running Test Cases**:
   - To run the test cases, place the videos `samplevideo.mp4` and `samplevideo2.mp4` in the directory defined by `video.upload-dir` in `application.properties`. Also, update the `videoPath` in `videoServiceTest.java` (line 40) to reflect this directory path.

## Contributing

Feel free to fork the project, contribute improvements, and submit pull requests. For any issues or feature requests, please open an issue in the GitHub repo.
