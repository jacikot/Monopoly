package rs.ac.bg.etf.monopoly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BoardView extends View {
    public BoardView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {

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

        public MyImage(Drawable d) {
            this.d = d;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size=(canvas.getHeight()>canvas.getWidth())?canvas.getWidth():canvas.getHeight();
        int partition=size/59;
        int offset=(size%59)/2;
        super.onDraw(canvas);
    }
}
