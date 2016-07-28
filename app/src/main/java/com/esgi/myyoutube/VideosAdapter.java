package com.esgi.myyoutube;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder>{
    private List<Video> videos;
    private Context context;

    private VideosAdapter.ClickListener clickListener;

    public VideosAdapter(Context context) {
        this(new ArrayList<Video>(), context);
    }

    public VideosAdapter(List<Video> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_videos, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(videos.get(position));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setVideos(List<Video> videos) {
        this.videos.clear();
        this.videos.addAll(videos);
        notifyDataSetChanged();
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setClickListener(VideosAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.videos_adapter_image_picture)
        protected ImageView picture;

        @BindView(R.id.videos_adapter_text_title)
        protected TextView title;

        @BindView(R.id.videos_adapter_image_fav)
        protected ImageView fav;

        @BindView(R.id.videos_adapter_text_description)
        protected TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Video video) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(ViewHolder.this, video);
                }
            });
            Glide
                    .with(context)
                    .load(video.getImageUrl())
                    .crossFade()
                    .into(picture);

            title.setText(video.getName());
            description.setText(video.getDescription());

            fav.setVisibility(video.isFav() ? View.VISIBLE : View.GONE);
        }

        public ImageView getPicture() {
            return picture;
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getDescription() {
            return description;
        }
    }

    public interface ClickListener {
        void onClick(VideosAdapter.ViewHolder viewHolder, Video video);
    }
}
