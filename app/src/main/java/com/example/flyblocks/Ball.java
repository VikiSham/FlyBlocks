package com.example.flyblocks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;

import java.util.Random;

public class Ball {
    private int x, dx;
    private int y, dy;
    private Bitmap image;
    private Bitmap currentImage;
    private int screenWidth;
    private int screenHeight;
    private Rect rect;//הגדרת מחלקת מלבן לעטוף תמונה של כדור כדי לבדוק התנגשויות
    private int wImg, hImg;
    private Random rnd;

    public Ball(Bitmap bitmap, int screenWidth, int screenHeight) {
        this.image = bitmap;
        this.image = Bitmap.createScaledBitmap(bitmap,100,100,false);
        currentImage = this.image;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        rnd = new Random();
        x = 0;
        y=0;
        new_game();

        wImg=x+currentImage.getWidth();
        hImg=y+currentImage.getHeight();
        rect=new Rect(x,y,wImg,hImg);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(currentImage,x,y,null);
    }

    public void new_game()
    {
        x=0;
        y=0;

        y = rnd.nextInt(screenHeight);
        dx = 10 + rnd.nextInt(30);
        dy = 10 + rnd.nextInt(30);
    }
    public boolean move()
    {
        boolean res=true;
        x+=dx;
        y+=dy;
        if(x<0 && dx<0) // move left
            dx =-dx;
        else if(x + image.getWidth()> screenWidth && dx >0) // move right
            dx =-dx;
        if(y<0 && dy<0) // move up
        {
            dy = -dy;
            //res = false;
        }
        else if(y+image.getHeight()>screenHeight && dy>0) // move down
        {
            dy = -dy;
            res = false;
        }
        wImg=x+currentImage.getWidth();
        hImg=y+currentImage.getHeight();
        //עידכון מקום המלבן של התמונה
        rect.set(x,y,wImg,hImg);
        return res;
    }

    public void changeDir()
    {
        if(dy>0)
            dy=-dy;
        if(x<0 && dx<0)
            dx =-dx;
        else if(x + image.getWidth()> screenWidth && dx >0)
            dx =-dx;
    }

    public Bitmap rotateBitmap(float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }

    public Rect getRect() {
        return rect;
    }

    // פעולה אשר מחזירה פיקסל של התמונה כאשר יש נקודה על המסך(נירמול התמונה ביחס למסך) ובודקת אם הפיקסל הוא שקוף
    public boolean isFilled(int a, int b)
    {
        return currentImage.getPixel(a-x,b-y)!= Color.TRANSPARENT;
    }
}
