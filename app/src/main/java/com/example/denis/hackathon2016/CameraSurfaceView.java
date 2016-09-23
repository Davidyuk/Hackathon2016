package com.example.denis.hackathon2016;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView {

    protected final Paint rectanglePaint = new Paint();
    private float aspectRatio = 1;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        rectanglePaint.setARGB(255, 200, 0, 0);
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setStrokeWidth(2);
    }

    static class Circle2D {
        Circle2D(float _x, float _y, float _r) {
            x = _x; y = _y; r = _r;
        }
        float x, y, r;
    }

    private Circle2D[] mCircles = new Circle2D[0];

    public void setCricles(Circle2D[] circles) {
        mCircles = circles;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    @Override
    protected void onDraw(Canvas canvas){
        aspectRatio = canvas.getWidth() / canvas.getHeight();
        for (Circle2D c : mCircles)
            canvas.drawCircle(c.x * canvas.getWidth(), c.y * canvas.getHeight(), c.r * 30, rectanglePaint);
        Log.w(this.getClass().getName(), "On Draw Called");
    }
}
