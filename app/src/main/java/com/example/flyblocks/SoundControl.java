package com.example.flyblocks;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundControl {
    private SoundPool soundPool;
    private int bombID; // מזהה לצליל בשם bomb
    private int overID;
    private int sliceID;

    // עמ לגשת לתיקיה raw אפשרי רק דרך activity לכן פונים דרך הבנאי לחלון באמצעות context
    public SoundControl(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(20).build();//כמה צלילים במקסימום ניתן להזרים/לנגן - כרגע המקסימום 20
        }
        else
            soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC,1);

        // טעינת צליל
        bombID=soundPool.load(context,R.raw.bomb,1);
        overID=soundPool.load(context,R.raw.over,1);
        sliceID=soundPool.load(context,R.raw.slice,1);
    }

    public void playOver()
    {
        // ניגון צליל(מזהה, רמקול שמאלי - 1 זה עוצמה גבוהה, רמקול ימני, סדר עדיפויות, האם יהיה צליל בלופ, באיזה מהירות לנגן צליל)
        soundPool.play(overID,1f,1f,1,0,1f);
    }

    public void playBomb()
    {
        soundPool.play(bombID,1f,1f,1,0,1f);
    }
    public void playSlice()
    {
        soundPool.play(sliceID,1f,1f,1,0,1f);
    }
}
