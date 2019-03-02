package com.workxplay.faveslist.fragment.story;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.workxplay.faveslist.R;
import com.workxplay.faveslist.base.BaseAppTabFragment;
import com.workxplay.faveslist.fragment.video.JzvdExt1;

import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZDataSource;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZMediaSystem;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;
import cn.jzvd.JzvdStd;

/**
 * Created by Billy on 11/3/16.
 */

public class StoryVideoViewFragment extends BaseAppTabFragment {
    public static final String TAG = StoryVideoViewFragment.class.getSimpleName();

    @BindView(R.id.video_player)
    public JzvdExt1 video;

    @BindView(R.id.storyVideoPostTextContainer)
    RelativeLayout storyVideoPostTextContainer;
    ProgressBar bar;
    List<BaseAppTabFragment> items;
    public StoryViewFragment storyview;
    public CountDownTimer timer;

    String mTitle ="",mVideo;
    int top = 0, left = 0, color = 0;
    long duration = 4000;

    @Override
    public int getLayout() {
        return R.layout.fragment_story_video_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideo!=null && !mVideo.equals("")) {
            video.progressbar = bar;
            video.frag = this;
            //Jzvd.releaseAllVideos();
            //JZMediaManager.instance().releaseMediaPlayer();
            JZDataSource jzDataSource = new JZDataSource(mVideo);
            video.setUp(jzDataSource, Jzvd.SCREEN_WINDOW_LIST);
            video.progressBar.setVisibility(View.GONE);
            video.bottomProgressBar.setVisibility(View.GONE);
            video.startVideo();
        }else{
            if (timer!=null){
                timer.cancel();
            }
            final StoryVideoViewFragment story = this;
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
            //Jzvd.releaseAllVideos();
            //video.release();
            JZDataSource jzDataSource = new JZDataSource(mVideo);
            video.setUp(jzDataSource, Jzvd.SCREEN_WINDOW_LIST);
            video.replayTextView.setText("");
            video.startVideo();
            video.progressBar.setVisibility(View.GONE);
            video.bottomProgressBar.setVisibility(View.GONE);
            video.findViewById(R.id.surface_container).setOnTouchListener(new StoryViewFragment.OnSwipeTouchListener(getActivity()){
                public void onSwipeRight() {
                    if (timer!=null){
                        timer.cancel();
                    }
                    //video.release();
                    bar.setProgress(0);
                    previousStoryItem();
                }

                public void onSwipeLeft() {
                    if (timer!=null){
                        timer.cancel();
                    }
                    //video.release();
                    bar.setProgress(100);
                    nextStoryItem();
                }
            });
        }

    }
}
