package com.esgi.myyoutube;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_act_swipe)
    protected SwipeRefreshLayout swipe;

    @BindView(R.id.main_act_recycler_videos)
    protected RecyclerView recyclerVideos;

    protected VideosAdapter videosAdapter;

    private GitHubService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        videosAdapter = new VideosAdapter(getApplicationContext());
        videosAdapter.setClickListener(new VideosAdapter.ClickListener() {
            @Override
            public void onClick(VideosAdapter.ViewHolder viewHolder, Video video) {
                openVideoAct(viewHolder, video);
            }
        });
        recyclerVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerVideos.setAdapter(videosAdapter);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pull();
            }
        });
        swipe.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        service = new Retrofit.Builder()
                .baseUrl(GitHubService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitHubService.class);

        pull();
    }

    private void pull() {
        swipe.setRefreshing(true);
        service.getVideos().enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, final Response<List<Video>> response) {
                final List<Video> videos = response.body();
                Set<String> fav = Util.getSharedPreferences(MainActivity.this).getStringSet(Util.Pref.VIDEO_FAV, new HashSet<String>());
                if (fav != null) {
                    for (Video video : videos)
                        if (fav.contains(video.getId()))
                            video.setFav(true);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                        videosAdapter.setVideos(videos);
                    }
                });
            }

            @Override
            public void onFailure(retrofit2.Call<List<Video>> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "Oops une érreur réseau !", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openVideoAct(VideosAdapter.ViewHolder viewHolder, Video video) {
        Intent intent = new Intent(MainActivity.this, VideoActivity.class);
        intent.putExtra(Util.ExtraKey.VIDEO, video);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this,
                    Pair.create((View) viewHolder.getPicture(), getString(R.string.transition_video_picture)),
                    Pair.create((View) viewHolder.getTitle(), getString(R.string.transition_video_tile)),
                    Pair.create((View) viewHolder.getDescription(), getString(R.string.transition_video_description))
            );
            ActivityCompat.startActivityForResult(MainActivity.this, intent, Util.ActResult.VIDEO, options.toBundle());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Util.ActResult.VIDEO) {
            Video videoResult = data.getParcelableExtra(Util.ExtraKey.VIDEO);
            int currentVideoPos = videosAdapter.getVideos().indexOf(videoResult);
            if (videosAdapter.getVideos().get(currentVideoPos).isFav() != videoResult.isFav()) {
                videosAdapter.getVideos().set(currentVideoPos, videoResult);
                videosAdapter.notifyDataSetChanged();
            }
        }
    }
}
