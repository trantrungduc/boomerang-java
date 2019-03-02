package com.workxplay.faveslist.fragment.story;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.workxplay.faveslist.R;
import com.workxplay.faveslist.base.BaseAppTabFragment;

import org.bytedeco.javacpp.RealSense;

import java.util.List;

import butterknife.BindView;
import cn.jzvd.Jzvd;

/**
 * Created by Billy on 11/3/16.
 */

public class StoryVideoViewFragment extends BaseAppTabFragment {
    public static final String TAG = StoryVideoViewFragment.class.getSimpleName();

    @BindView(R.id.video_player)
    public PlayerView simpleExoPlayerView;

    @BindView(R.id.storyVideoPostTextContainer)
    RelativeLayout storyVideoPostTextContainer;
    ProgressBar bar;
    List<BaseAppTabFragment> items;
    public StoryViewFragment storyview;
    public CountDownTimer timer;

    String mTitle ="",mVideo;
    int top = 0, left = 0, color = 0;
    long duration = 4000;
    private MediaController ctlr;
    boolean setDuration = false;

    //private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;

    @Override
    public int getLayout() {
        return R.layout.fragment_story_video_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideo!=null && !mVideo.equals("")) {
            refresh();
        }
    }

    public void nextStoryItem(){
        final StoryVideoViewFragment story = this;
        if (items.indexOf(story)+1==items.size()){
            storyview.nextDay();
        }else {
            if (getActivity()!=null && getActivity().findViewById(R.id.flipcontainer)!=null){
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()/*.setCustomAnimations(
                            R.anim.sliding_in_left,
                            R.anim.sliding_in_left)*/
                            .replace(R.id.flipcontainer, items.get(items.indexOf(this) + 1), "a").commit();
                    storyview.currentView = items.get(items.indexOf(this) + 1);
                }catch (Exception e){

                }
            }
        }
    }

    public void previousStoryItem(){
        final StoryVideoViewFragment story = this;
        if (items.indexOf(story)==0){
            storyview.previousDay();
        }else {
            if (getActivity()!=null && getActivity().findViewById(R.id.flipcontainer)!=null){
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()/*.setCustomAnimations(
                            R.anim.sliding_in_left,
                            R.anim.sliding_in_left)*/
                            .replace(R.id.flipcontainer, items.get(items.indexOf(this) - 1), "a").commit();
                    storyview.currentView = items.get(items.indexOf(this) - 1);
                }catch (Exception e){

                }
            }
        }
    }

    @Override
    public void onInitView() {
        storyVideoPostTextContainer.setTop(top);
        storyVideoPostTextContainer.setLeft(left);

        Jzvd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP);
        if (mVideo!=null && !mVideo.equals("")) {
            intVideo();
        }
        /*DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) video.getLayoutParams();
        params.width =  metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        video.setLayoutParams(params);*/


    }
    private void intVideo(){
        /*ctlr = new MediaController(getContext());
        ctlr.setVisibility(View.GONE);
        ctlr.setMediaPlayer(video);
        video.setMediaController(ctlr);
        video.setVideoURI(Uri.parse(mVideo));

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                bar.setProgress(100);
                nextStoryItem();
            }
        });
        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                timer = new CountDownTimer(duration, 200) {
                    public boolean isFinish = false;
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long val = (duration-millisUntilFinished)*100/duration;
                        if (bar!=null) {
                            bar.setProgress((int) val);
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (bar!=null) {
                            bar.setProgress(100);
                        }
                        nextStoryItem();

                    }
                }.start();
                return true;
            }
        });
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                duration = mediaPlayer.getDuration();
                video.start();
                timer = new CountDownTimer(duration, 200) {
                    public boolean isFinish = false;
                    @Override
                    public void onTick(long millisUntilFinished) {
                        bar.setProgress((int)(video.getCurrentPosition()*100/duration));
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }
        });
        getView()*/
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        int h = simpleExoPlayerView.getResources().getConfiguration().screenHeightDp;
        int w = simpleExoPlayerView.getResources().getConfiguration().screenWidthDp;
        ////Set media controller
        simpleExoPlayerView.setUseController(false);//set to true or false to see controllers
        simpleExoPlayerView.requestFocus();
        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "exoplayer2example"), bandwidthMeter);

        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(mVideo),dataSourceFactory, new DefaultExtractorsFactory(),null, null);
        //new HlsMediaSource(Uri.parse(mVideo), dataSourceFactory, 1, null, null);
        //final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
        // Prepare the player with the source.
        player.prepare(videoSource);

        player.addListener(new ExoPlayer.EventListener() {


            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                //bar.setProgress((double)timeline.getcu);
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                //Log.v(TAG, "Listener-onTracksChanged... ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                //Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState+"|||isDrawingCacheEnabled():"+simpleExoPlayerView.isDrawingCacheEnabled());
                if (playbackState == ExoPlayer.STATE_READY && !setDuration) {
                    duration = player.getDuration();
                    timer = new CountDownTimer(duration*2, 200) {
                        public boolean isFinish = false;
                        @Override
                        public void onTick(long millisUntilFinished) {
                            bar.setProgress((int)((double)player.getCurrentPosition()*100/(double)duration));
                        }

                        @Override
                        public void onFinish() { }
                    }.start();
                    setDuration=true;
                }
                if (playbackState==ExoPlayer.STATE_ENDED){
                    bar.setProgress(100);
                    if (timer!=null){
                        timer.cancel();
                    }
                    nextStoryItem();
                }
            }
        });
        player.setPlayWhenReady(true);
        simpleExoPlayerView.setOnTouchListener(new StoryViewFragment.OnSwipeTouchListener(getActivity()){
            public void onSwipeRight() {
                if (timer!=null){
                    timer.cancel();
                }
                //video.stopPlayback();
                bar.setProgress(0);
                previousStoryItem();
            }

            public void onSwipeLeft() {
                if (timer!=null){
                    timer.cancel();
                }
                //video.stopPlayback();
                bar.setProgress(100);
                nextStoryItem();
            }
        });
    }

    private void refresh(){
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "exoplayer2example"), bandwidthMeter);

        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(mVideo),dataSourceFactory, new DefaultExtractorsFactory(),null, null);
        player.prepare(videoSource);
        player.addListener(new ExoPlayer.EventListener() {


            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                //bar.setProgress((double)timeline.getcu);
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                //Log.v(TAG, "Listener-onTracksChanged... ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                //Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState+"|||isDrawingCacheEnabled():"+simpleExoPlayerView.isDrawingCacheEnabled());
                if (playbackState == ExoPlayer.STATE_READY && !setDuration) {
                    duration = player.getDuration();
                    timer = new CountDownTimer(duration*2, 200) {
                        public boolean isFinish = false;
                        @Override
                        public void onTick(long millisUntilFinished) {
                            bar.setProgress((int)((double)player.getCurrentPosition()*100/(double)duration));
                        }

                        @Override
                        public void onFinish() { }
                    }.start();
                    setDuration=true;
                }
                if (playbackState==ExoPlayer.STATE_ENDED){
                    bar.setProgress(100);
                    if (timer!=null){
                        timer.cancel();
                    }
                    nextStoryItem();
                }
            }
        });
        player.setPlayWhenReady(true);
        simpleExoPlayerView.setOnTouchListener(new StoryViewFragment.OnSwipeTouchListener(getActivity()){
            public void onSwipeRight() {
                if (timer!=null){
                    timer.cancel();
                }
                //video.stopPlayback();
                bar.setProgress(0);
                previousStoryItem();
            }

            public void onSwipeLeft() {
                if (timer!=null){
                    timer.cancel();
                }
                //video.stopPlayback();
                bar.setProgress(100);
                nextStoryItem();
            }
        });
    }
}
