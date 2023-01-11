package com.remi.navidrawer;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Runtime_measureactivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private JavaCameraView javaCameraView;
    private int cameraId = 0;
    private CascadeClassifier mFaceDetector, mNoseDetector;
    private File mCascadeFile;
    private static final String TAG = "OCVSample::Activity";
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private int mPlotState;      // 0 for no plots, 1 for plots
    private int mBoundingState;  // 0 for no bounds, 1 for bounds
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private ArrayList<RawPPGIValue> mRawPPGIVals;
    private ArrayList<RawPPGIValue> mPeakPPGIVals;
    private RawPPGIValue mFrontValue;
    private RawPPGIValue mMiddleValue;
    private RawPPGIValue mRearValue;
    private RawPPGIValue mLastPeakValue;
    private RawPPGIValue mCurrentPeakValue;
    private long mCurrentMiliseconds;
    private ArrayList<Integer> mBpmCandidates;
    private int mCountDowns = 0;
    private String mOutputFilename = "";
    private int mDropFrames = 0;
    private long mLastDropTime = 0;
    private long mRecoverTime = 0;
    private Float averageRaw = 0f;


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
        if (savedInstanceState == null) {
            mRawPPGIVals = new ArrayList<RawPPGIValue>();
            mPeakPPGIVals = new ArrayList<RawPPGIValue>();
            mFrontValue = null;
            mRearValue = null;
            mMiddleValue = null;
        } else {
            mRawPPGIVals = savedInstanceState.getParcelableArrayList("RawValue List");
            mPeakPPGIVals = savedInstanceState.getParcelableArrayList("PeakValue List");
            mFrontValue = savedInstanceState.getParcelable("Front");
            mMiddleValue = savedInstanceState.getParcelable("Middle");
            mRearValue = savedInstanceState.getParcelable("Rear");
        }

        javaCameraView = findViewById(R.id.javaCameraView);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.setMaxFrameSize(854, 480);
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

        mBpmCandidates = new ArrayList<Integer>();
        // 0 for no plots, 1 for plots
        setPlotState(1);
        findViewById(R.id.tv_heartrate).setVisibility(View.VISIBLE);
        findViewById(R.id.plotRuntimeHeartRate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPlotState() == 1) {
                    setPlotState(0);
                    findViewById(R.id.tv_heartrate).setVisibility(View.INVISIBLE);
                } else {
                    setPlotState(1);
                    findViewById(R.id.tv_heartrate).setVisibility(View.VISIBLE);
                }
            }
        });
        mOutputFilename = "";
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

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mBaseLoaderCallback);
        } else {
            mBaseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        mFaceDetector = loadDetector(R.raw.lbpcascade_frontalface, "lbpcascade_frontalface.xml");
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

    public void setPlotState(int value) {
        mPlotState = value;
    }

    public int getPlotState() {
        return mPlotState;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("RawValue List", mRawPPGIVals);
        savedInstanceState.putParcelableArrayList("PeakValue List", mPeakPPGIVals);
        savedInstanceState.putParcelable("Front", mFrontValue);
        savedInstanceState.putParcelable("Middle", mMiddleValue);
        savedInstanceState.putParcelable("Rear", mRearValue);
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = new Mat();
        mCurrentMiliseconds = Calendar.getInstance().getTimeInMillis();
        // in case of portrait, set the frame into correct rotation
        // but it uses landscape orientation as default, nothing to modify here
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

        //  absolute face size initialization
        if (mAbsoluteFaceSize == 0) {    // only called at the beginning of the activity
            int height = frame.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }
        // container for multiple detected faces, in our case only one for heart rate measurement
        // convert into array for indexing
        MatOfRect faces = new MatOfRect();
        if (mFaceDetector != null) {
            mFaceDetector.detectMultiScale(frame, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            for (int i = 0; i < facesArray.length; i++) {

                Mat faceROI = frame.submat(facesArray[i]);  // take out the value from the array one by one, in this case only one iteration

                if (getBoundingState() == 1) {
                    Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
                }
                Log.d("ROI:", "Starting Point: " + facesArray[i].tl().toString() + " \n" + "Ending Point: " + facesArray[i].br().toString() + " \n");

                if (mDropFrames > 0) {
                    mRecoverTime = mCurrentMiliseconds;
                }
                mDropFrames = 0;
                startMeasure(faceROI, frame, facesArray[i].tl());
            }
        }
        if (mDropFrames == 0) {
            mLastDropTime = mCurrentMiliseconds;
        }
        mDropFrames = mDropFrames + 1;
        return frame;
    }

    public void startMeasure(Mat face, Mat frame, Point startingPoint) {

        int foreheadHeight = face.rows() / 6;
        int foreheadWidth = 3 * face.cols() / 5;

        int startingX = (int) startingPoint.x + (int) face.cols() / 5;
        int startingY = (int) startingPoint.y + face.rows()/12 ;

        Rect foreheadROI = new Rect(startingX, startingY, foreheadWidth, foreheadHeight);
        Imgproc.rectangle(frame, foreheadROI.tl(), foreheadROI.br(), FACE_RECT_COLOR, 3);
        Mat croppedFrame = new Mat(frame, foreheadROI);
        calculateRaw(croppedFrame);
    }

    public void calculateRaw(Mat forehead) {
        List<Mat> channels = new ArrayList<Mat>();
        Core.split(forehead, channels);
        Mat mGreenChannel = channels.get(1);

        MatOfDouble mMeanValue = new MatOfDouble();
        MatOfDouble mStdDev = new MatOfDouble();
        Core.meanStdDev(mGreenChannel, mMeanValue, mStdDev);
        Double mean = mMeanValue.toList().get(0);
        Float tmp = mean.floatValue();
        Log.d("RawPPGI: ", Float.toString(tmp));
        addAndDetermine(tmp);
        // calculate Raw values here
    }

    public void addAndDetermine(Float rawValue) {
        mRearValue = new RawPPGIValue(rawValue, mCurrentMiliseconds);
        //printRawValueAsText(mRearValue);
        updateRawValues(mRearValue);
        findPeakValues();
    }

    public long calculateTimeInterval() {
        long mTimeInterval = mCurrentPeakValue.getCurrentTime() - mLastPeakValue.getCurrentTime();
        String msg = "Current peak: " + Long.toString(mCurrentPeakValue.getCurrentTime()) + " Last peak: "
                + Long.toString(mLastPeakValue.getCurrentTime()) + " Time interval: " +
                Long.toString(mTimeInterval) + "\n";
        Log.d("Check! ", msg);
        return mTimeInterval;
    }

    public void updateRawValues(RawPPGIValue rawValue) {
        if (mRawPPGIVals.size() == 80) {
            ArrayList<RawPPGIValue> tmp = new ArrayList<RawPPGIValue>(mRawPPGIVals.subList(1, 80));
            mRawPPGIVals = tmp;
            mRawPPGIVals.add(rawValue);
        } else {
            mRawPPGIVals.add(rawValue);
        }
    }

    public void findPeakValues() {
        if (mRawPPGIVals.size() < 40) {
            return;
        }
        int mCount = mRawPPGIVals.size();
        int mThreshold = 0;
        if (mCount >= 11) {
            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 5).getValue())
                mThreshold = mThreshold + 5;
            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 4).getValue())
                mThreshold = mThreshold + 4;
            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 3).getValue())
                mThreshold = mThreshold + 3;
            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 2).getValue())
                mThreshold = mThreshold + 2;
            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 1).getValue())
                mThreshold++;

            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 11).getValue())
                mThreshold = mThreshold + 1;
            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 10).getValue())
                mThreshold = mThreshold + 2;
            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 7).getValue())
                mThreshold = mThreshold + 5;
            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 8).getValue())
                mThreshold = mThreshold + 4;
            if (mRawPPGIVals.get(mCount - 6).getValue() > mRawPPGIVals.get(mCount - 9).getValue())
                mThreshold += 3;
            if (mThreshold >= 29) {
                updatePeakValues(mRawPPGIVals.get(mCount - 6));
            }
        }
    }

    public void updatePeakValues(RawPPGIValue rawValue) {
        int mArraySize = mPeakPPGIVals.size();
        if (mArraySize > 19) {
            ArrayList<RawPPGIValue> tmp = new ArrayList<RawPPGIValue>(mPeakPPGIVals.subList(1, 20));
            mPeakPPGIVals = tmp;
            mPeakPPGIVals.add(rawValue);
            mLastPeakValue = mPeakPPGIVals.get(18);
            mCurrentPeakValue = mPeakPPGIVals.get(19);

            long mTimeInterval = mCurrentPeakValue.getCurrentTime() - mLastPeakValue.getCurrentTime();
            int nArraySize = mPeakPPGIVals.size();
            compareAndCalculate();

        } else if (mArraySize > 0) {
            mLastPeakValue = mPeakPPGIVals.get(mPeakPPGIVals.size() - 1);
            mCurrentPeakValue = rawValue;
            mPeakPPGIVals.add(rawValue);
            long mTimeInterval = mCurrentPeakValue.getCurrentTime() - mLastPeakValue.getCurrentTime();
            int nArraySize = mPeakPPGIVals.size();
            compareAndCalculate();
        } else {
            mPeakPPGIVals.add(rawValue);
            mCurrentPeakValue = rawValue;
        }
        printRawPeakValueAsText(rawValue);
    }

    public void compareAndCalculate() {
        if (mCurrentPeakValue.getCurrentTime() > mLastPeakValue.getCurrentTime()) {
            calculateHeartbeat();
        }
    }

    public void calculateHeartbeat() {
        long mLongCandidateBpm = (1 * 1000 * 60 / calculateTimeInterval());
        int mCandidateBpm = Math.round(mLongCandidateBpm);
        String msg = "Candidate heartbeat: " + Long.toString(mLongCandidateBpm) + " Countdowns: " +
                Integer.toString(mCountDowns) + "\n";
        Log.d("Check", msg);
        if (mCountDowns < 5) {
            mBpmCandidates.add(Integer.valueOf(mCandidateBpm));
            mCountDowns++;
        } else if (mBpmCandidates.size() == 20) {
            ArrayList<Integer> tmp = new ArrayList<Integer>(mBpmCandidates.subList(1, 20));
            mBpmCandidates = tmp;
            mBpmCandidates.add(Integer.valueOf(mCandidateBpm));
            if (mCountDowns == 5) {
                int averageBpm = calculate();
                mCountDowns = 0;
                TextView tv = (TextView) (findViewById(R.id.tv_heartrate));
                tv.setText(Integer.toString(averageBpm));
            }
            mCountDowns++;

        } else {
            mBpmCandidates.add(Integer.valueOf(mCandidateBpm));
            if (mCountDowns == 5) {
                int averageBpm = calculate();
                mCountDowns = 0;
                TextView tv = (TextView) (findViewById(R.id.tv_heartrate));
                tv.setText(Integer.toString(averageBpm));

            }
            mCountDowns++;
        }
    }

    public int calculate() {
        int sum = 0;
        int dropCount = 0;
        int addCount = 0;
        if (mBpmCandidates.size() <= 10) {
            for (int value : mBpmCandidates) {
                if (value < 400 && value > 30) {
                    sum += value;
                } else {
                    dropCount++;
                }
            }

            float temp = (float) sum / (mBpmCandidates.size() - dropCount);
            int average = Math.round(temp);
            return average;
        }
        int arraySize = mBpmCandidates.size();
        int startIndex = arraySize - 10;
        for (int i = startIndex; i < arraySize; i++) {
            if (mBpmCandidates.get(i) < 350 && mBpmCandidates.get(i) > 25) {
                sum += mBpmCandidates.get(i);
            } else {
                dropCount++;
            }
        }
        float temp = (float) sum / (10 - dropCount);
        int average = Math.round(temp);
        return average;
    }

    public void printRawPeakValueAsText(RawPPGIValue value) {
        if (mOutputFilename == "") {
            String name = new SimpleDateFormat(Configuration.FILENAME_FORMAT, Locale.ENGLISH)
                    .format(System.currentTimeMillis());
            mOutputFilename = getExternalFilesDir("Peak") + "/" + name + ".txt";
        }
        String outputValue = value.printRawValue();
        Log.d("Test Output1", "Magic works");
        appendWrite(mOutputFilename, outputValue);
    }

    public static void appendWrite(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent);
            Log.d("Test Output2", "Magic works" + file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
