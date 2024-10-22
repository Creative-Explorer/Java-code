package spring_security.JWT_Token.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class VideoUtils {

    // Path to the FFmpeg executable
    private static final String FFMPEG_PATH = "/path/to/ffmpeg"; // Adjust this path

    /**
     * Compresses a video file using FFmpeg and returns the compressed video file.
     *
     * @param tempInputFile The input video file to be compressed.
     * @return The compressed video file.
     * @throws IOException          If an I/O error occurs during compression.
     * @throws InterruptedException If the FFmpeg process is interrupted.
     */
    public static File compressVideo(File tempInputFile) throws IOException, InterruptedException {
        // Define the path for the compressed output file
        File compressedFile = File.createTempFile("compressed-", ".mp4");

        // Define the FFmpeg command for compression
        // -i specifies the input file, -vcodec libx264 specifies the codec, -crf 28 specifies the quality (0-51)
        String command = String.format("%s -i %s -vcodec libx264 -crf 28 %s", FFMPEG_PATH, tempInputFile.getAbsolutePath(), compressedFile.getAbsolutePath());

        // Execute the FFmpeg command
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        Process process = processBuilder.start();

        // Wait for the process to complete
        boolean finished = process.waitFor(1, TimeUnit.MINUTES);
        if (!finished) {
            throw new IOException("FFmpeg process timed out");
        }

        // Check for errors
        if (process.exitValue() != 0) {
            throw new IOException("FFmpeg process failed with exit code " + process.exitValue());
        }

        return compressedFile;
    }
}
