package spring_security.JWT_Token.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spring_security.JWT_Token.dto.*;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class YouTubeService {

    private final RestTemplate restTemplate;

    @Value("${youtube.api.key}")
    private String apiKey;

    public YouTubeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1. Get videos by category
    public List<Video> getVideosByCategory(String categoryId) {
        String url = String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&videoCategoryId=%s&key=%s", categoryId, apiKey);
        VideoResponse videoResponse = restTemplate.getForObject(url, VideoResponse.class);

        List<Video> videos = new ArrayList<>();
        if (videoResponse != null && videoResponse.getItems() != null) {
            for (VideoApiResponse item : videoResponse.getItems()) {
                Video video = new Video();
                video.setId(item.getId().getVideoId()); // Use getVideoId() from the VideoId object
                video.setTitle(item.getSnippet().getTitle());
                video.setDescription(item.getSnippet().getDescription());
                videos.add(video);
            }
        }
        return videos;
    }




    // 2. Search for videos by keyword
    public List<VideoApiResponse> searchVideos(String query) {
        String url = String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&type=video&key=%s", query, apiKey);
        return restTemplate.getForObject(url, VideoResponse.class).getItems();
    }

    // 3. Get video details by video ID
//    public VideoDetails getVideoDetails(String videoId) {
//        String url = String.format("https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&id=%s&key=%s", videoId, apiKey);
//        return restTemplate.getForObject(url, com.example.youtube.model.VideoDetailsResponse.class).getItems().get(0);
//    }

    // 4. Get trending videos
    public List<Video> getTrendingVideos() {
        String url = String.format("https://www.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&key=%s", apiKey);
        VideoResponse videoResponse = restTemplate.getForObject(url, VideoResponse.class);

        List<Video> videos = new ArrayList<>();
        if (videoResponse != null && videoResponse.getItems() != null) {
            for (VideoApiResponse item : videoResponse.getItems()) {
                Video video = new Video();
                video.setId(String.valueOf(item.getId()));
                video.setTitle(item.getSnippet().getTitle());
                video.setDescription(item.getSnippet().getDescription());
                videos.add(video);
            }
        }
        return videos;
    }




    // 5. Get playlists by channel ID
    public List<Playlist> getPlaylistsByChannel(String channelId) {
        String url = String.format("https://www.googleapis.com/youtube/v3/playlists?part=snippet&channelId=%s&key=%s", channelId, apiKey);
        return restTemplate.getForObject(url, PlaylistResponse.class).getItems();
    }

    // 6. Get videos from a specific playlist
    public List<VideoApiResponse> getVideosFromPlaylist(String playlistId) {
        String url = String.format("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=%s&key=%s", playlistId, apiKey);
        return restTemplate.getForObject(url, VideoResponse.class).getItems();
    }

    // 7. Get channel details by channel ID
    public ChannelDetails getChannelDetails(String channelId) {
        String url = String.format("https://www.googleapis.com/youtube/v3/channels?part=snippet&id=%s&key=%s", channelId, apiKey);
        return restTemplate.getForObject(url, ChannelResponse.class).getItems().get(0);
    }

    // 8. Get comments for a video
    public List<Comment> getVideoComments(String videoId) {
        String url = String.format("https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&videoId=%s&key=%s", videoId, apiKey);
        return restTemplate.getForObject(url, CommentResponse.class).getItems();
    }

    // 9. Get related videos by video ID
    public List<VideoApiResponse> getRelatedVideos(String videoId) {
        String url = String.format("https://www.googleapis.com/youtube/v3/search?relatedToVideoId=%s&part=snippet&type=video&key=%s", videoId, apiKey);
        return restTemplate.getForObject(url, VideoResponse.class).getItems();
    }

    // 10. Get video statistics by video ID
    public VideoStats.Statistics getVideoStats(String videoId) {
        String url = String.format("https://www.googleapis.com/youtube/v3/videos?part=statistics&id=%s&key=%s", videoId, apiKey);
        return restTemplate.getForObject(url, VideoStatsResponse.class).getItems().get(0).getStatistics();
    }

    public List<VideoApiResponse> getAllVideos() {
        String url = String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=500&key=%s", apiKey);
        VideoResponse videoResponse = restTemplate.getForObject(url, VideoResponse.class);

        List<VideoApiResponse> videos = new ArrayList<>();
        if (videoResponse != null && videoResponse.getItems() != null) {
            for (VideoApiResponse item : videoResponse.getItems()) {
                // Process the video response
                videos.add(item);
            }
        }
        return videos;
    }

}
