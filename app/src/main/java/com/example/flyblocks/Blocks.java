package com.example.flyblocks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

public class Blocks {
    private ArrayList<Bitmap> bitmaps;
    private int x,y,dx,dy;
    private int index;
    private int screen_width;
    private int screen_heigth;
    private Random rnd;
    private Rect rect;

    public Blocks(ArrayList<Bitmap> bitmaps, int screen_width, int screen_heigth) {
        this.bitmaps = bitmaps;
        this.screen_width = screen_width;
        this.screen_heigth = screen_heigth;
        index=0;
        rnd=new Random();
        rect=new Rect();//בלי ערכים התחלתיים כי נקודות x y מוגדרות ב init
        init();
    }

    public void init() {

        x=rnd.nextInt(1000);
        y=-rnd.nextInt(10);
        dx= rnd.nextInt(5);//מהירות ב-x
        dy=5+rnd.nextInt(5);//מהירות ב-y
        index=rnd.nextInt(bitmaps.size());

        //עידכון מסגרת התמונה לפי רוחב התמונה הנוכחית מתוך רשימת התמונות
        rect.set(x,y,x+bitmaps.get(index).getWidth(),y+bitmaps.get(index).getHeight());
    }

    public  void draw(Canvas canvas)
    {
        canvas.drawBitmap(bitmaps.get(index),x,y,null);//draw picture per index

    }

    public boolean move()
    {
        y+=dy;
        if(y>1250) {     //move down stop the game; this is the heght of ped
           init();
           return false;
        }

        //לאחר תזוזת הבלוק מעדכנים את המלבן לאותו המקום של הבלוק שוב
        rect.set(x,y,x+bitmaps.get(index).getWidth(),y+bitmaps.get(index).getHeight());//עידכון מסגרת התמונה לפי רוחב התמונה הנוכחית מתוך רשימת התמונות
        return true;
    }

    public Rect getRect() {
        return rect;
    }

    // פעולה אשר מחזירה פיקסל של התמונה כאשר יש נקודה על המסך (נירמול התמונה ביחס למסך) ובודקת אם הפיקסל הוא שקוף
    public boolean isFilled(int a, int b)
    {
        return bitmaps.get(index).getPixel(a-x,b-y)!= Color.TRANSPARENT;

    }
}
