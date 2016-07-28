package com.esgi.myyoutube;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GitHubService {
    public static final String BASE_URL = "https://raw.githubusercontent.com";

    @GET("/florent37/MyYoutube/master/myyoutube.json")
    Call<List<Video>> getVideos();
}
