package com.ra.com.audioflasher;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Administrator on 2015/10/1.
 */
public class MyMediaPlayer {
    private MediaPlayer mPlayer;
    private SeekBar mSeekBar;
    private TextView mFunTV;
    private static Object MediaLock = new Object();
    private Activity mAct;
    private MediaProReader mediaProReader;
    public enum Play_State{PLAYING,STOP,PAUSE};
    private Play_State state;

    public MyMediaPlayer(Activity context,Uri uri) {
        mAct = context;
        if(uri != null)
            mPlayer = MediaPlayer.create(context, uri);//Android auto call mPlayer.prepare();;
        else
            mPlayer = MediaPlayer.create(context, R.raw.uptown_funk);//Android auto call mPlayer.prepare();;
        mPlayer.setLooping(true);
        mPlayer.start();
        state = Play_State.PLAYING;

    }

    public void linkSeekBar(SeekBar seekBar,TextView textView) {
        this.mFunTV = textView;
        this.mSeekBar = seekBar;
        listenToSeekBar();
        mediaProReader = new MediaProReader();
        mediaProReader.start();
    }


    public int getAudioSessionId()
    {
        return mPlayer.getAudioSessionId();
    }

    private void listenToSeekBar() {
        mSeekBar.setMax(mPlayer.getDuration());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(mSeekBar.getProgress());
            }
        });
    }

    public boolean play()
    {
        if (mPlayer.isPlaying()) {
            return false;
        }
        mPlayer.stop();
        try {
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mAct, R.string.player_prepare_fail, Toast.LENGTH_SHORT).show();
            return false;
        }
        mPlayer.seekTo(0);
        mPlayer.start();
        state = Play_State.PLAYING;
        return true;
    }

    public boolean stop()
    {
        mPlayer.stop();
        state = Play_State.STOP;
        return true;
    }

    public boolean pause()
    {
        if(state == Play_State.STOP)
            return false;
        if(mPlayer.isPlaying())
        {
            mPlayer.pause();
            state = Play_State.PAUSE;
            return true;
        }else
        {
            return false;
        }
    }

    public boolean resume()
    {
        if(state == Play_State.STOP)
            return false;
        if(!mPlayer.isPlaying())
        {
            mPlayer.start();
            state = Play_State.PLAYING;
            return true;
        }else
        {
            return false;
        }
    }

    public Play_State getState()
    {
        return state;
    }


    public void release() {
        mPlayer.stop();
        mediaProReader.setCancel(true);
        mediaProReader = null;
        mPlayer.release();
        mPlayer = null;
    }


    class MediaProReader extends Thread{

        private boolean cancel = false;
        private boolean pause = false;
        private float fun_rotate = 0;
        private boolean fun_add = false;
        private long startTime;

        public boolean isPause() {
            return pause;
        }

        public void setPause(boolean pause) {
            this.pause = pause;
            if(pause == false)
            {
                synchronized (MediaLock)
                {
                    MediaLock.notifyAll();
                }
            }
        }

        public boolean isCancel() {
            return cancel;
        }

        public void setCancel(boolean cancel) {
            this.cancel = cancel;
        }

        private void roateTextForFun()
        {
            if(System.currentTimeMillis()<=startTime+12.290*1000) //Zhang zhenyue is preparing~~
                return;

            if(fun_rotate < -10)
                fun_add = true;
            else if(fun_rotate > 10)
                fun_add = false;

            if(fun_add == true)
                fun_rotate+=0.0668;
            else
                fun_rotate-=0.0668;

            mFunTV.setRotation(fun_rotate);
        }
        @Override
        public void run() {
            super.run();
            startTime = System.currentTimeMillis();
            while (!cancel && mFunTV!=null && mSeekBar != null)
            {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mSeekBar != null && mPlayer != null) {
                            mSeekBar.setProgress(mPlayer.getCurrentPosition());
                            roateTextForFun();
                        }
                    }
                });
                synchronized (MediaLock)
                {
                    if(pause)
                    {
                        try {
                            MediaLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };
}
