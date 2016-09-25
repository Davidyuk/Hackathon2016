package com.example.denis.hackathon2016;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView {

    protected final Paint rectanglePaint = new Paint(), textPaint = new Paint();
    private float aspectRatio = 1;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        rectanglePaint.setARGB(128, 0, 0, 255);
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setStrokeWidth(2);
        textPaint.setARGB(255, 0, 0, 0);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(100);
    }

    static class Circle2D {
        Circle2D(float _x, float _y, float _r, String _name) {
            x = _x; y = _y; r = _r;
            name = _name;
        }
        float x, y, r;
        String name;
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
        aspectRatio = (float) canvas.getWidth() / canvas.getHeight();
        for (Circle2D c : mCircles) {
            canvas.drawCircle(c.x * canvas.getWidth(), c.y * canvas.getHeight(), c.r * 30, rectanglePaint);
            canvas.drawText(c.name, c.x * canvas.getWidth(), c.y * canvas.getHeight(), textPaint);
        }
    }
}
