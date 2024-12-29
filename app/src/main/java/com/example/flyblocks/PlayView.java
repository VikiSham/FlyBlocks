package com.example.flyblocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class PlayView extends SurfaceView implements Runnable {

    Ball ball;
    int width, heght;
    //Paint bgPaint;
    Bitmap bgbitmap;//תמונת רקע
    SurfaceHolder holder;
    Canvas canvas;

    Thread thread;
    boolean flag=true;

    int interval=50;// milliseconds

    ArrayList<Blocks> blocks; //רשימת בלוקים
    ArrayList<Bitmap>bitmaps;// רשימת תמונות לכל בלוק בצבע שונה
    Ped ped;

    boolean res;
    Handler handler;
    int score=0; // ניקוד
    int life=100;// 5 חיים עד לפסילה
    SoundControl soundControl;


    public PlayView(Context context, int width, int heght, Handler handler) {
        super(context);

        soundControl=new SoundControl(context);

        this.handler=handler;

        this.width=width;
        this.heght=heght;
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ball);// load the picture of ball
        Bitmap bitmap1= BitmapFactory.decodeResource(getResources(),R.drawable.ped);// load the picture of ped

        bgbitmap= BitmapFactory.decodeResource(getResources(),R.drawable.b1);// load the picture of backgraund
        bgbitmap=Bitmap.createScaledBitmap(bgbitmap,1100,1600,false);//change the size of sprite


        ball=new Ball(bitmap,width,heght);
        ped=new Ped (bitmap1,width,heght);
        holder=getHolder();//משתנה אשר לא מאפשר לתהליך אחר לעבוד בקנבס עד שלא יתפנה לציור הבא, עמו מפתח שנועל חדר עד שלא יתפנה

        //מערך תמונות של בלוקים
        bitmaps=new ArrayList<>();

        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.block);
        bitmap = Bitmap.createScaledBitmap(bitmap,100,50,false);//הקטנת גודל בלוק
        bitmaps.add(bitmap);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.block1);
        bitmap = Bitmap.createScaledBitmap(bitmap,100,50,false);
        bitmaps.add(bitmap);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.block2);
        bitmap = Bitmap.createScaledBitmap(bitmap,100,50,false);
        bitmaps.add(bitmap);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.block3);
        bitmap = Bitmap.createScaledBitmap(bitmap,100,50,false);
        bitmaps.add(bitmap);

        blocks=new ArrayList<>();
        for (int i=0;i<30;i++)
            blocks.add(new Blocks(bitmaps,width,heght)); //יצירת 15 בלוקים כל אחד במיקום שונה

        thread=new Thread(this);// הגדרת תהליך חדש
        thread.start(); // call to run method
    }

    private void drawSurface()
    {
        if(holder.getSurface().isValid())
        {
            canvas=holder.lockCanvas();// lock canvas while picture is drawing
            //canvas.drawPaint(bgPaint);// draw backgroun
            bgbitmap=Bitmap.createScaledBitmap(bgbitmap,canvas.getWidth(),canvas.getHeight(),false);
            canvas.drawBitmap(bgbitmap,0,0,null);
            ball.draw(canvas); //draw the ball
            ped.draw(canvas); // draw the ped

            for (int i=0;i<blocks.size();i++)
                blocks.get(i).draw(canvas); // תנועת בלוק

            holder.unlockCanvasAndPost(canvas);// unlock the picture
        }
    }


    @Override
    public void run() {
        while (flag)  // תהליך שמריץ את המשחק יעבוד כל עוד הדגל הוא true וכל עוד יש עדין חיים
        {
            if (!ball.move()) // הפעלת תנועת הכדור
            {
                life=0;
                soundControl.playBomb();
                Message msg = handler.obtainMessage();// יצירת הודעה
                msg.getData().putInt("score", score); // עידכון נתונים
                msg.getData().putInt("life", life); // עידכון נתונים
                handler.sendMessage(msg); // שליחת הודעה
            }
            else {
                if(checkTouch2(ped.getRect(),ball.getRect())) // אם יש התנגשות בין כדור ובד הכדור ישנה כיוון
                    ball.changeDir();
            }
            drawSurface(); // ציור אובייקטים
            for (int i=0;i<blocks.size() && life>0;i++) {
                if (!blocks.get(i).move())// תנועת בלוקים - פעולה בתוך אובייקט תחזיר false אם היתה נגיעה בריצפה
                {
                    life--;  // אם נוגעים ברצפה יורד מהחיים
                    soundControl.playBomb();
                    Message msg = handler.obtainMessage();// יצירת הודעה
                    msg.getData().putInt("score", score); // עידכון נתונים
                    msg.getData().putInt("life", life); // עידכון נתונים
                    handler.sendMessage(msg); // שליחת הודעה

                }
                else
                {
                    if (checkTouch(blocks.get(i).getRect(), ball.getRect()))  // בדיקת התנגשות בין ציפור ומטוס
                    {
                        //לאחר בדיקה שיש התנגשות של מלבנים בודקים התנגשות ברמת פיקסלים של עצם עצמו
                        // כי יכול להיות מצב שתמונה אינה מלבנית וכן יש התנגשות של מלבנים אבל בפועל אין התנגשות בין התמונות עצמן רק של המסגרות שלהם
                        // קריאה לפעולה שתעה בדיקה ברמת הפיקסלים
                        res = checkTouch(blocks.get(i), ball);
                        if (res) {
                            blocks.get(i).init(); // הפעלת פעולה יצירת בלוק מחדש וגם מעלימה אותה בעת נגיעה
                            score++; // כרגע יגדל הניקוד במידה ויש התגשות
                            soundControl.playSlice();
                        }
                        Message msg = handler.obtainMessage();// יצירת הודעה
                        msg.getData().putInt("score", score); // עידכון נתונים
                        msg.getData().putInt("life", life); // עידכון נתונים
                        handler.sendMessage(msg); // שליחת הודעה
                    }
                }
            }

            if(life==0)
            {
                soundControl.playOver();
                flag=false;
                break;
            }
            synchronized (this) {  //  only 1 thread do wait() - no more programms to do threads
                try {
                    wait(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // העמסת פעולה  checktouch כי יש אותו שם אבל טיפוסי פרמטרים שונים
    // encapsulation
    // פעולה זו נקראת רק במידה והיתה התנגשות בין מלבני 2 התמונות
    private boolean checkTouch(Blocks block, Ball ball1) {

        // קבלת מלבן החפיפה בין 2 התמונות
        int left=Math.max(block.getRect().left, ball1.getRect().left);// דופן שמאלי
        int top= Math.max(block.getRect().top, ball1.getRect().top); // דופן עליון
        int right= Math.min(block.getRect().right, ball1.getRect().right); // דופן ימני
        int bottom= Math.min(block.getRect().bottom, ball1.getRect().bottom); // דופן תחתון

        // סריקת מלבן ההתנגשות עמ לזהות אם יש התנגשות ברמת הפיקסלים - קריאה לפעולה שבודקת פיקסלים של התמונה שאינם שקופים

        for(int m=top;m<bottom;m++)
            for(int k=left;k<right; k++)
                if(ball1.isFilled(k,m) && block.isFilled(k,m))
                    return true;
        return false;
    }

    // פעולה שמחזירה אמת או שקר אם יש התנגשות של 2 מלבנים שעוטפים 2 תמונות של ציפור ומטוס
    private boolean checkTouch(Rect block, Rect ball)
    {
        return Rect.intersects(block,ball);// פעולה מובנית במחלקת Rect לבדיקת חפיפה בין 2 מלבנים
    }

    private boolean checkTouch2(Rect ped, Rect ball)
    {
        return Rect.intersects(ped,ball); // בדיקת נגיעה בין פד וכדור
    }
    public void changeDirection(String dir)
    {
       ped.changeDir(dir);
    }

    public boolean doPause() {
        flag=!flag;
        if(flag)
        {
            thread=new Thread(this);
            thread.start();
        }
        return flag;
    }

    public void stop() {
        flag=false;
    }

    public void newGame() {
        flag=true;
        score=0;
        life=5;
        Message msg=handler.obtainMessage();// יצירת הודעה
        msg.getData().putInt("score",score); // עידכון נתונים
        msg.getData().putInt("life",life); // עידכון נתונים
        handler.sendMessage(msg); // שליחת הודעה

        for (int i=0;i<blocks.size();i++)
            blocks.get(i).init();
        ball.new_game();
        thread=new Thread(this);
        thread.start();
    }

    public void pedMove(float deltax) {
        ped.setX(ped.getX() - (int) deltax);
        /*if(ivMain.getY() + deltay<1200) // screen heigth
            ivMain.setY(ivMain.getY() + deltay);*/
    }
}
