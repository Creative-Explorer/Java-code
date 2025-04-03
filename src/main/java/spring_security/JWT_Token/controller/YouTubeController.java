package spring_security.JWT_Token.controller;


import org.springframework.web.bind.annotation.*;
import spring_security.JWT_Token.dto.*;
import spring_security.JWT_Token.service.VideoApiResponse;
import spring_security.JWT_Token.service.YouTubeService;

import javax.xml.stream.events.Comment;
import java.util.List;

@RestController
@RequestMapping("/youtube")
public class YouTubeController {
    private final YouTubeService youTubeService;

    public YouTubeController(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    // 1. Get videos by category
    @GetMapping("/videos/category")
//    @CrossOrigin(origins = "http://localhost:4200")
    public List<Video> getVideosByCategory(@RequestParam String categoryId) {
        return youTubeService.getVideosByCategory(categoryId);
    }


    // 2. Search for videos by keyword
    @GetMapping("/videos/search")
    public List<VideoApiResponse> searchVideos(@RequestParam String query) {
        return youTubeService.searchVideos(query);
    }

    // 3. Get video details by video ID
//    @GetMapping("/videos/{videoId}")
//    public VideoDetails getVideoDetails(@PathVariable String videoId) {
//        return youTubeService.getVideoDetails(videoId);
//    }

    // 4. Get trending videos
//    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/videos/trending")
    public List<Video> getTrendingVideos() {
        return youTubeService.getTrendingVideos();
    }

    // 5. Get playlists by channel ID
    @GetMapping("/playlists/channel")
    public List<Playlist> getPlaylistsByChannel(@RequestParam String channelId) {
        return youTubeService.getPlaylistsByChannel(channelId);
    }

    // 6. Get videos from a specific playlist
    @GetMapping("/playlists/{playlistId}/videos")
    public List<VideoApiResponse> getVideosFromPlaylist(@PathVariable String playlistId) {
        return youTubeService.getVideosFromPlaylist(playlistId);
    }

    // 7. Get channel details by channel ID
    @GetMapping("/channels/{channelId}")
    public ChannelDetails getChannelDetails(@PathVariable String channelId) {
        return youTubeService.getChannelDetails(channelId);
    }

    // 8. Get comments for a video
    @GetMapping("/videos/{videoId}/comments")
    public List<Comment> getVideoComments(@PathVariable String videoId) {
        return youTubeService.getVideoComments(videoId);
    }

    // 9. Get related videos by video ID
    @GetMapping("/videos/{videoId}/related")
    public List<VideoApiResponse> getRelatedVideos(@PathVariable String videoId) {
        return youTubeService.getRelatedVideos(videoId);
    }

    // 10. Get video statistics by video ID
    @GetMapping("/videos/{videoId}/stats")
    public VideoStats.Statistics getVideoStats(@PathVariable String videoId) {
        return youTubeService.getVideoStats(videoId);
    }

    @GetMapping("/videos/all")
//    @CrossOrigin(origins = "http://localhost:4200")
    public List<VideoApiResponse> getAllVideos() {
        return youTubeService.getAllVideos();
    }
}
