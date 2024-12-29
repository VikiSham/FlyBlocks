package com.example.flyblocks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;

public class Ped {
    private int x, dx;
    private int y, dy;
    private Bitmap image;
    private Bitmap currentImage;
    private int screenWidth;
    private int screenHeight;
    private Rect rect;//הגדרת מחלקת מלבן לעטוף תמונה של פד כדי לבדוק התנגשויות
    int wImg, hImg;

    public Ped(Bitmap bitmap, int screenWidth, int screenHeight) {
        this.image = bitmap;
        this.image = Bitmap.createScaledBitmap(bitmap,200,70,false);
        currentImage = this.image;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        dx = 10;
        dy = 0;
        x = 0;
        y = this.screenHeight/3*2;

        wImg=x+currentImage.getWidth(); // רוחב המלבן
        hImg=y+currentImage.getHeight(); // אורך המלבן
        rect=new Rect(x,y,wImg,hImg); // הפעלת המלבן על הפד

    }

    public void setX(int x)
    {
        this.x=x;
    }
    public int getX()
    {
        return this.x;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(currentImage,x,y,null);
    }

    public void move()
    {
        x+= dx;
        y+= dy;
        if(x > screenWidth && dx >0)
            x = 0;
        if(y> screenHeight && dy>0)
            y=0;
        if(x < 0 && dx < 0)
            x = screenWidth;
        if(y<0 && dy<0)
            y = screenHeight;

        wImg=x+currentImage.getWidth();
        hImg=y+currentImage.getHeight();
        //עידכון מקום המלבן של התמונה
        rect.set(x,y,wImg,hImg);
    }

    public void changeDir(String direction) {
        switch (direction)
        {
            case "Left":
                dx = -50;
                dy = 0;
                currentImage = rotateBitmap(180);
                break;
            case "Right":
                dx = 50;
                dy = 0;
                currentImage = rotateBitmap(0);
                break;

        }
        move();
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
