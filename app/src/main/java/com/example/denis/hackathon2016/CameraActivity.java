package com.example.denis.hackathon2016;

import java.io.IOException;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class CameraActivity extends AppCompatActivity implements SensorEventListener, PlaceListService.Listener {
    private SensorManager mSensorManager;
    private Sensor mRotationVector;
    private float[] mRotationVectorValue;

    SurfaceView sv;
    SurfaceHolder holder;
    HolderCallback holderCallback;
    Camera camera;
    PlaceListService MPlaceListService;

    final int CAMERA_ID = 0;
    final boolean FULL_SCREEN = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        sv = (SurfaceView) findViewById(R.id.surfaceView);
        holder = sv.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holderCallback = new HolderCallback();
        holder.addCallback(holderCallback);

        CameraSurfaceView sv = (CameraSurfaceView)findViewById(R.id.surfaceView);
        CameraSurfaceView.Circle2D[] a = new CameraSurfaceView.Circle2D[2];
        a[0] = new CameraSurfaceView.Circle2D(0, 0, 1, "test1");
        a[1] = new CameraSurfaceView.Circle2D((float)0.5, (float)0.5, (float)1.5, "test2");
        sv.setCricles(a);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        try {
            MPlaceListService = new PlaceListService(this, this);
        } catch (Exception e) {
        }
    }

    PlaceListService.Place[] mPlaces = new PlaceListService.Place[0];

    public void onPlacesGet(PlaceListService.Place[] places) {
        mPlaces = places;
    }

    private void renderMatrix() {
        if (mRotationVectorValue != null) {
            float[] rotationMatrix = new float[16];
            float[] perspectiveMatrix = new float[16];
            float[] mVPmatrix = new float[16];

            float[] points = new float[mPlaces.length * 4];
            Location loc = MPlaceListService.getLocation();
            float max = 0;
            for (int i = 0; i < mPlaces.length; i++) {
                max = max > Math.abs(mPlaces[i].lat - (float)loc.getLatitude()) ? max : Math.abs(mPlaces[i].lat - (float)loc.getLatitude());
                max = max > Math.abs(mPlaces[i].lon - (float)loc.getLongitude()) ? max : Math.abs(mPlaces[i].lon - (float)loc.getLongitude());
            }
            for (int i = 0; i < mPlaces.length; i++) {
                points[i*4+0] = (mPlaces[i].lat - (float)loc.getLatitude()) / max;
                points[i*4+1] = (mPlaces[i].lon - (float)loc.getLongitude()) / max;
                points[i*4+2] = 0;
                points[i*4+3] = 0;
            }

            CameraSurfaceView sv = (CameraSurfaceView)findViewById(R.id.surfaceView);
            SensorManager.getRotationMatrixFromVector(rotationMatrix, mRotationVectorValue);
            android.opengl.Matrix.perspectiveM(perspectiveMatrix, 0, 90, sv.getAspectRatio(), (float)0.1, 1);
            android.opengl.Matrix.multiplyMM(mVPmatrix, 0, perspectiveMatrix, 0, rotationMatrix, 0);

            CameraSurfaceView.Circle2D[] a = new CameraSurfaceView.Circle2D[points.length / 4];
            for (int i = 0; i < points.length / 4; i++) {
                float[] res = new float[4];
                android.opengl.Matrix.multiplyMV(res, 0, mVPmatrix, 0, points, i * 4);
                a[i] = new CameraSurfaceView.Circle2D(res[0] + 0.5f, -res[1] + 0.5f, (res[2] > 0) ? 1 : 0, mPlaces[i].name);
            }

            sv.setCricles(a);
            sv.invalidate();
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
            mRotationVectorValue = event.values;
        renderMatrix();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_GAME);
        camera = Camera.open(CAMERA_ID);
        setPreviewSize(FULL_SCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if (camera != null)
            camera.release();
        camera = null;
    }

    class HolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                sv.setWillNotDraw(false);
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            camera.stopPreview();
            setCameraDisplayOrientation(CAMERA_ID);
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

    }

    void setPreviewSize(boolean fullScreen) {

        // получаем размеры экрана
        Display display = getWindowManager().getDefaultDisplay();
        boolean widthIsMax = display.getWidth() > display.getHeight();

        // определяем размеры превью камеры
        Size size = camera.getParameters().getPreviewSize();

        RectF rectDisplay = new RectF();
        RectF rectPreview = new RectF();

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0, 0, display.getWidth(), display.getHeight());

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0, 0, size.width, size.height);
        } else {
            // превью в вертикальной ориентации
            rectPreview.set(0, 0, size.height, size.width);
        }

        Matrix matrix = new Matrix();
        // подготовка матрицы преобразования
        if (!fullScreen) {
            // если превью будет "втиснут" в экран (второй вариант из урока)
            matrix.setRectToRect(rectPreview, rectDisplay,
                    Matrix.ScaleToFit.START);
        } else {
            // если экран будет "втиснут" в превью (третий вариант из урока)
            matrix.setRectToRect(rectDisplay, rectPreview,
                    Matrix.ScaleToFit.START);
            matrix.invert(matrix);
        }
        // преобразование
        matrix.mapRect(rectPreview);

        // установка размеров surface из получившегося преобразования
        sv.getLayoutParams().height = (int) (rectPreview.bottom);
        sv.getLayoutParams().width = (int) (rectPreview.right);
    }

    void setCameraDisplayOrientation(int cameraId) {
        // определяем насколько повернут экран от нормального положения
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;

        // получаем инфо по камере cameraId
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        // задняя камера
        if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
            result = ((360 - degrees) + info.orientation);
        } else
            // передняя камера
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                result = ((360 - degrees) - info.orientation);
                result += 360;
            }
        result = result % 360;
        camera.setDisplayOrientation(result);
    }
}
