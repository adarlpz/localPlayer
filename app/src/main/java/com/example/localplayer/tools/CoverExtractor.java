package com.example.localplayer.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.RawResourceDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
public class CoverExtractor {

    public interface CoverCallback {
        void onCoverExtracted(Bitmap bitmap);

        void onCoverNotFound();
    }
    @OptIn(markerClass = UnstableApi.class)
    public static void getFLACCover(Context context, int rawResId, final CoverCallback callback) {
        Uri uri = RawResourceDataSource.buildRawResourceUri(rawResId);
        final ExoPlayer player = new ExoPlayer.Builder(context).build();
        MediaItem mediaItem = MediaItem.fromUri(uri);
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(new DefaultDataSource.Factory(context)).createMediaSource(mediaItem);
        player.setMediaSource(mediaSource);
        player.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onMediaMetadataChanged(AnalyticsListener.EventTime eventTime, MediaMetadata metadata) {
                if (metadata.artworkData != null) {
                    Bitmap cover = BitmapFactory.decodeByteArray(metadata.artworkData, 0, metadata.artworkData.length);
                    callback.onCoverExtracted(cover); // ✅ Use callback instead
                } else {
                    callback.onCoverNotFound(); // ✅ Use callback instead
                }
                player.release();
            }
        });
        player.prepare();
        player.play();
    }
}
