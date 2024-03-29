package rs.ac.bg.etf.monopoly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

public class BoardView extends View {
    public BoardView(Context context) {
        super(context);
        init(context);
    }

    List<MyImage> imgs=new ArrayList<>();
    Drawable center;
    String[]colors;
    private void init(Context context) {
        linePaint = new Paint();
        linePaint.setStrokeWidth(5);
        linePaint.setColor(ResourcesCompat.getColor(
                getResources(),
                R.color.black,
                null));
        colors=getResources().getStringArray(R.array.colors);
        TypedArray images=context.getResources().obtainTypedArray(R.array.images);
        center=context.getResources().obtainTypedArray(R.array.monopolyCover).getDrawable(0);
        for(int i=0;i<images.length();i++){
            images.getDrawable(i).clearColorFilter();
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

    private Paint linePaint = null;

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    int partition;

    private void drawField(Canvas canvas,MyImage img){
        canvas.drawRect(img.x,img.y,img.x+img.w*partition,img.y+img.h*partition,linePaint);
        img.d.setBounds(img.x+1,img.y+1,img.x+img.w*partition-1,img.y+img.h*partition-1);
        img.d.draw(canvas);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int size=(canvas.getHeight()>canvas.getWidth())?canvas.getWidth():canvas.getHeight();
        partition=size/59;
        int x=(size%59+canvas.getWidth()-size)/2;
        int y=(size%59+canvas.getHeight()-size)/2;
        center.setBounds(x+partition*7,y+partition*7,x+partition*52,y+partition*52);
        center.draw(canvas);
        for(int i=20;i<30;i++){
            imgs.get(i).setCoord(x,y);
            drawField(canvas,imgs.get(i));
            x+=imgs.get(i).w*partition;
        }
        for(int i=30;i<40;i++){
            imgs.get(i).setCoord(x,y);
            drawField(canvas,imgs.get(i));
            y+=imgs.get(i).h*partition;
        }
        for(int i=0;i<10;i++){
            imgs.get(i).setCoord(x,y);
            drawField(canvas,imgs.get(i));
            x-=imgs.get(i+1).w*partition;
        }
        for(int i=10;i<20;i++){
            imgs.get(i).setCoord(x,y);
            drawField(canvas,imgs.get(i));
            y-=imgs.get(i+1).h*partition;
        }



        super.onDraw(canvas);
    }

    public interface Callback{
        void call(int index);
    }
    Callback callback;
    public void setCallback(Callback callback){
        this.callback=callback;
    }

    public OnTouchListener listener= new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getActionMasked()==MotionEvent.ACTION_DOWN){
                float x=event.getX(event.getActionIndex());
                float y=event.getY(event.getActionIndex());
                int index=0;
                for(MyImage i:imgs){
                    if(i.x<=x && (i.x+i.w*partition)>x && i.y<=y && (i.y+i.h*partition)>y){
                        if(callback!=null) callback.call(index);
                        break;
                    }
                    index++;
                }
                return true;
            }
            return false;
        }
    };

    public void useFilter(int field, int color){
        imgs.get(field).d.setColorFilter(Color.parseColor(colors[color]), PorterDuff.Mode.MULTIPLY);
    }

    public void clearFilter(int field){
        imgs.get(field).d.clearColorFilter();
    }
}
