package com.esgi.myyoutube;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoActivity extends AppCompatActivity {

    @BindView(R.id.video_act_image_picture)
    ImageView imagePicture;

    @BindView(R.id.video_act_text_title)
    TextView textTitle;

    @BindView(R.id.video_act_text_description)
    TextView textDescription;

    @BindView(R.id.video_act_image_favorite)
    ImageView imageFav;


    private Video video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        video = getIntent().getParcelableExtra(Util.ExtraKey.VIDEO);

        getSupportActionBar().setTitle(video.getName());
        textTitle.setText(video.getName());
        textDescription.setText(video.getDescription());
        notifyFav();

        Glide
                .with(this)
                .load(video.getImageUrl())
                .crossFade()
                .into(imagePicture);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Util.ExtraKey.VIDEO, video);
        setResult(Activity.RESULT_OK, returnIntent);
        supportFinishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void notifyFav() {
        imageFav.setSelected(video.isFav());
    }


    @OnClick(R.id.video_act_frame_favorite)
    public void onClickFav() {
        video.setFav(!video.isFav());
        SharedPreferences sharedPreferences = Util.getSharedPreferences(this);
        Set<String> fav = sharedPreferences.getStringSet(Util.Pref.VIDEO_FAV, new HashSet<String>(1));
        if (video.isFav()) {
            fav.add(video.getId());
        } else {
            fav.remove(video.getId());
        }
        sharedPreferences.edit().remove(Util.Pref.VIDEO_FAV).putStringSet(Util.Pref.VIDEO_FAV, fav).apply();
        notifyFav();
    }


    @OnClick(R.id.video_act_frame_play)
    public void onClickPlay() {
        try {
            System.out.println("vnd.youtube:" + video.getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getVideoUrl().replace("https://www.youtube.com/watch?v=", "")));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getVideoUrl()));
            startActivity(intent);
        }
    }
}
