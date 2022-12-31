package com.remi.navidrawer;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Runtime_measureactivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private JavaCameraView javaCameraView;
    private int cameraId = 0;
    private CascadeClassifier mFaceDetector, mNoseDetector;
    private Mat mGray;
    private File mCascadeFile;
    private static final String TAG = "OCVSample::Activity";
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private int mBoundingState;  // 0 for no bounds, 1 for bounds
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);


    private BaseLoaderCallback mBaseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    javaCameraView.enableView();
                }
                break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        List<CameraBridgeViewBase> list = new ArrayList<>();
        list.add(javaCameraView);
        return list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measureactivity);
        if (allPermissionsGranted()) {
            Log.d("Test", "Magic works");
        } else {
            ActivityCompat.requestPermissions(this, Configuration.REQUIRED_PERMISSIONS,
                    Configuration.REQUEST_CODE_PERMISSIONS);
        }
        javaCameraView = findViewById(R.id.javaCameraView);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.setMaxFrameSize(960, 720);
        javaCameraView.setCameraIndex(cameraId);
        javaCameraView.enableFpsMeter();
        javaCameraView.setScreenOrientation(getResources().getConfiguration().orientation);


        ImageButton switchCameraBtn = findViewById(R.id.switchCameraButton2);
        switchCameraBtn.setBackgroundResource(R.drawable.ic_switch_front);

        findViewById(R.id.switchCameraButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraId = cameraId ^ 1;
                javaCameraView.disableView();
                javaCameraView.setCameraIndex(cameraId);
                if (cameraId == 0)
                    switchCameraBtn.setBackgroundResource(R.drawable.ic_switch_front);
                else
                    switchCameraBtn.setBackgroundResource(R.drawable.ic_switch_back);
                javaCameraView.enableView();
            }
        });

        setBoundingState(1);
        findViewById(R.id.boundingBoxSwitcher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getBoundingState() == 1)
                    setBoundingState(0);
                else
                    setBoundingState(1);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mBaseLoaderCallback);
        } else {
            mBaseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        mFaceDetector = loadDetector(R.raw.lbpcascade_frontalface, "lbpcascade_frontalface.xml");
        mNoseDetector = loadDetector(R.raw.haarcascade_mcs_nose, "haarcascade_mcs_nose.xml");
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = new Mat();
        if (getResources().getConfiguration().orientation == 1) {
            Mat reversedFrame = inputFrame.rgba();
            if (cameraId == 0) {
                Mat rotateMat = Imgproc.getRotationMatrix2D(new Point(reversedFrame.rows() / 2, reversedFrame.cols() / 2), 270, 1);
                Imgproc.warpAffine(reversedFrame, frame, rotateMat, frame.size());
            } else {
                Mat rotateMat = Imgproc.getRotationMatrix2D(new Point(reversedFrame.rows() / 2, reversedFrame.cols() / 2), 90, 1);
                Imgproc.warpAffine(reversedFrame, frame, rotateMat, frame.size());
            }
        } else {
            frame = inputFrame.rgba();
        }

        Mat mGray = new Mat();
        Imgproc.cvtColor(frame, mGray, Imgproc.COLOR_BGR2GRAY);

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        // code runs well til mGray but has problems in the next if condition statement
        MatOfRect faces = new MatOfRect();

        if (mFaceDetector != null) {
            mFaceDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }


        Rect[] facesArray = faces.toArray();
        if (getBoundingState() == 1) {
            for (int i = 0; i < facesArray.length; i++) {

                Mat faceROI = mGray.submat(facesArray[i]);
                MatOfRect noses = new MatOfRect();
                mNoseDetector.detectMultiScale(faceROI, noses, 1.1, 2, 2,
                        new Size(30, 30));

                Rect[] nosesArray = noses.toArray();
                try {

                    if (nosesArray != null) {
                        Imgproc.rectangle(frame,
                                new Point(facesArray[i].tl().x + nosesArray[0].tl().x, facesArray[i].tl().y + nosesArray[0].tl().y),
                                new Point(facesArray[i].tl().x + nosesArray[0].br().x, facesArray[i].tl().y + nosesArray[0].br().y),
                                FACE_RECT_COLOR, 3);

                        Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    return frame;
                }
            }

        } else {
            for (int i = 0; i < facesArray.length; i++) {
                Mat faceROI = mGray.submat(facesArray[i]);
                MatOfRect noses = new MatOfRect();
                mNoseDetector.detectMultiScale(faceROI, noses, 1.1, 2, 2,
                        new Size(30, 30));
            }
        }
        return frame;


    }

    @Override
    public void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    private CascadeClassifier loadDetector(int rawID, String fileName) {
        CascadeClassifier classifier = null;
        try {

            // load cascade file from application resources
            InputStream is = getResources().openRawResource(rawID);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, fileName);
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            Log.e(TAG, "start to load file:  " + mCascadeFile.getAbsolutePath());
            classifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());

            if (classifier.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                classifier = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

            cascadeDir.delete();


        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
        return classifier;
    }

    private boolean allPermissionsGranted() {
        for (String permission : Configuration.REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    static class Configuration {
        public static final String TAG = "CameraxBasic";
        public static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
        public static final int REQUEST_CODE_PERMISSIONS = 50;
        public static final int REQUEST_AUDIO_CODE_PERMISSIONS = 12;
        public static final String[] REQUIRED_PERMISSIONS =
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.P ?
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE} :
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO};
    }

    public void setBoundingState(int value) {
        mBoundingState = value;
    }

    public int getBoundingState() {
        return mBoundingState;
    }
}
