package rs.ac.bg.etf.monopoly;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BoardView extends View {
    public BoardView(Context context) {
        super(context);
        init(context);
    }

    List<MyImage> imgs=new ArrayList<>();
    Drawable center;
    private void init(Context context) {
        TypedArray images=context.getResources().obtainTypedArray(R.array.images);
        center=context.getResources().obtainTypedArray(R.array.monopolyCover).getDrawable(0);
        for(int i=0;i<images.length();i++){
            imgs.add(new MyImage(images.getDrawable(i)));
            if(i%10==0){
                imgs.get(i).h=7;
                imgs.get(i).w=7;
            }
            else if(i>0&&i<10||i>20&&i<30){
                imgs.get(i).h=7;
                imgs.get(i).w=5;
            }
            else{
                imgs.get(i).h=5;
                imgs.get(i).w=7;
            }
        }
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    class MyImage{
        Drawable d;
        int x,y,h,w;

        public MyImage(Drawable d, int x, int y, int h, int w) {
            this.d = d;
            this.x = x;
            this.y = y;
            this.h = h;
            this.w = w;
        }

        public void setCoord(int x,int y){
            this.x = x;
            this.y = y;
        }

        public MyImage(Drawable d) {
            this.d = d;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size=(canvas.getHeight()>canvas.getWidth())?canvas.getWidth():canvas.getHeight();
        int partition=size/59;
        int x=(size%59+canvas.getWidth()-size)/2;
        int y=(size%59+canvas.getHeight()-size)/2;
        center.setBounds(x+partition*7,y+partition*7,x+partition*52,y+partition*52);
        center.draw(canvas);
        for(int i=20;i<30;i++){
            imgs.get(i).setCoord(x,y);
            imgs.get(i).d.
            imgs.get(i).d.setBounds(x,y,x+imgs.get(i).w*partition,y+imgs.get(i).h*partition);
            imgs.get(i).d.draw(canvas);
            x+=imgs.get(i).w*partition;
        }
        for(int i=30;i<40;i++){
            imgs.get(i).setCoord(x,y);
            imgs.get(i).d.setBounds(x,y,x+imgs.get(i).w*partition,y+imgs.get(i).h*partition);
            imgs.get(i).d.draw(canvas);
            y+=imgs.get(i).h*partition;
        }
        for(int i=0;i<10;i++){
            imgs.get(i).setCoord(x,y);
            imgs.get(i).d.setBounds(x,y,x+imgs.get(i).w*partition,y+imgs.get(i).h*partition);
            imgs.get(i).d.draw(canvas);
            x-=imgs.get(i+1).w*partition;
        }
        for(int i=10;i<20;i++){
            imgs.get(i).setCoord(x,y);
            imgs.get(i).d.setBounds(x,y,x+imgs.get(i).w*partition,y+imgs.get(i).h*partition);
            imgs.get(i).d.draw(canvas);
            y-=imgs.get(i+1).h*partition;
        }



        super.onDraw(canvas);
    }
}
