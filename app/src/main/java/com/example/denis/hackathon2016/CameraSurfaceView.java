package com.example.denis.hackathon2016;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView {

    protected final Paint rectanglePaint = new Paint();

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        rectanglePaint.setARGB(255, 200, 0, 0);
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setStrokeWidth(2);
    }

    static class Circle2D {
        Circle2D(int _x, int _y, int _r) {
            x = _x; y = _y; r = _r;
        }
        int x, y, r;
    }

    private Circle2D[] mCircles = new Circle2D[0];

    public void setCricles(Circle2D[] circles) {
        mCircles = circles;
    }

    @Override
    protected void onDraw(Canvas canvas){
        for (Circle2D c : mCircles)
            canvas.drawCircle(c.x, c.y, c.r, rectanglePaint);
        Log.w(this.getClass().getName(), "On Draw Called");
    }
}
