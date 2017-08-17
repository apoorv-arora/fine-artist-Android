package in.fine.artist.home.utils.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by nik on 27/12/16.
 */

public class CircularTextView extends TextView {
    private float strokeWidth;
    private int strokeColor;
    private int solidColor;

    private Context context;

    public CircularTextView(Context context) {
        super(context);
        this.context = context;
    }

    public CircularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CircularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }


    @Override
    public void draw(Canvas canvas) {

        Paint circlePaint = new Paint();
        circlePaint.setColor(solidColor);
        circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        Paint strokePaint = new Paint();
        strokePaint.setColor(strokeColor);
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        int  h = this.getHeight();
        int  w = this.getWidth();
        int diameter = ((h > w) ? h : w);
        int radius = diameter/2;
        this.setHeight(diameter);
        this.setWidth(diameter);

        canvas.drawCircle(diameter / 2 , diameter / 2, radius, strokePaint);
        canvas.drawCircle(diameter / 2, diameter / 2, radius-strokeWidth, circlePaint);
        super.draw(canvas);
    }

    public void setStrokeWidth(int dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        strokeWidth = dp*scale;
    }

    public void setStrokeColor(int color) {
        strokeColor = color;
    }

    public void setSolidColor(int color) {
        solidColor = color;
    }
}
