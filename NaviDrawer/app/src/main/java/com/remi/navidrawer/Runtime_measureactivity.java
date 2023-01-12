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
import org.opencv.core.RotatedRect;
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
    private int cameraId = 0; // 0 for back camera, 1 for front camera
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
    private Float mLastRawValue = 0f;
    private TimeWindowContainer mContainer;


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
        if (getResources().getConfiguration().orientation == 1) {
            setContentView(R.layout.activity_measureactivity);
        } else {
            setContentView(R.layout.activity_measure_landscape);
        }
        if (allPermissionsGranted()) {
            Log.d("Test", "Magic works");
        } else {
            ActivityCompat.requestPermissions(this, Configuration.REQUIRED_PERMISSIONS,
                    Configuration.REQUEST_CODE_PERMISSIONS);
        }
        if (savedInstanceState == null) {
            mRawPPGIVals = new ArrayList<RawPPGIValue>();
            mPeakPPGIVals = new ArrayList<RawPPGIValue>();
            mRearValue = null;
        } else {
            mRawPPGIVals = savedInstanceState.getParcelableArrayList("RawValue List");
            mPeakPPGIVals = savedInstanceState.getParcelableArrayList("PeakValue List");
            mRearValue = savedInstanceState.getParcelable("Rear");
        }

        javaCameraView = findViewById(R.id.javaCameraView);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.setMaxFrameSize(854, 480);
        javaCameraView.setCameraIndex(cameraId);
        javaCameraView.enableFpsMeter();

        ImageButton switchCameraBtn = findViewById(R.id.switchCameraButton2);
        switchCameraBtn.setBackgroundResource(R.drawable.ic_switch_front_foreground);
        switchCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraId = cameraId ^ 1;
                javaCameraView.disableView();
                javaCameraView.setCameraIndex(cameraId);
                if (cameraId == 0)
                    switchCameraBtn.setBackgroundResource(R.drawable.ic_switch_front_foreground);
                else
                    switchCameraBtn.setBackgroundResource(R.drawable.ic_switch_back_foreground);
                javaCameraView.enableView();
            }
        });

        setBoundingState(1);
        ImageButton boundingBoxSwitcher =  findViewById(R.id.boundingBoxSwitcher);
        boundingBoxSwitcher.setBackgroundResource(R.drawable.ic_boundingbox_off_foreground);
        boundingBoxSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getBoundingState() == 1){
                    setBoundingState(0);
                    boundingBoxSwitcher.setBackgroundResource(R.drawable.ic_boundingbox_on_foreground);
                }
                else{
                    setBoundingState(1);
                    boundingBoxSwitcher.setBackgroundResource(R.drawable.ic_boundingbox_off_foreground);
                }
            }
        });

        mBpmCandidates = new ArrayList<Integer>();
        // 0 for no plots, 1 for plots
        setPlotState(1);
        TextView plotText = findViewById(R.id.tv_heartrate);
        plotText.setVisibility(View.VISIBLE);
        ImageButton plotSwitcher = findViewById(R.id.plotRuntimeHeartRate);
        plotSwitcher.setBackgroundResource(R.drawable.ic_plot_foreground);
        plotSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPlotState() == 1) {
                    setPlotState(0);
                    plotText.setVisibility(View.INVISIBLE);
                    plotSwitcher.setBackgroundResource(R.drawable.ic_plot_off_foreground);
                } else {
                    setPlotState(1);
                    plotText.setVisibility(View.VISIBLE);
                    plotSwitcher.setBackgroundResource(R.drawable.ic_plot_foreground);
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
        mContainer = new TimeWindowContainer();
        javaCameraView.setScreenOrientation(getResources().getConfiguration().orientation); // 1 for portrait, 2 for landscape
        Log.d("Loop Check Set: ", Integer.toString(getResources().getConfiguration().orientation));
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
        savedInstanceState.putParcelable("Rear", mRearValue);
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = new Mat();
        mCurrentMiliseconds = Calendar.getInstance().getTimeInMillis();
        // in case of portrait, set the frame into correct rotation
        // but it uses landscape orientation as default, nothing to modify here
        if (getResources().getConfiguration().orientation == 2) {
            frame = inputFrame.rgba();
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
                if (mDropFrames > 0) {
                    mRecoverTime = mCurrentMiliseconds;
                    mContainer.add(mRecoverTime);
                    mDropFrames = 0;
                }
                for (int i = 0; i < facesArray.length; i++) {

                    Mat faceROI = frame.submat(facesArray[i]);  // take out the value from the array one by one, in this case only one iteration
                    if (getBoundingState() == 1) {
                        Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
                    }
                    startMeasure(faceROI, frame, facesArray[i].tl());
                }
            } else {
                if (mDropFrames == 0) {
                    mLastDropTime = mCurrentMiliseconds;
                    mContainer.add(mLastDropTime);
                }
                mDropFrames += 1;
                // Placeholder for non faces images
                updateRawValues(new RawPPGIValue(mLastRawValue, mCurrentMiliseconds));
            }
            return frame;
        } else {    // in the mode of potrait, where the image is in the wrong direction

            frame = inputFrame.rgba();
            //  absolute face size initialization
            if (mAbsoluteFaceSize == 0) {    // only called at the beginning of the activity
                int height = frame.rows();
                if (Math.round(height * mRelativeFaceSize) > 0) {
                    mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
                }
            }
            // container for multiple detected faces, in our case only one for heart rate measurement
            // convert into array for indexing

            Mat rotatedFrame = new Mat();
            if (cameraId == 0) {      // in case of back camera
                Mat rotatedMat = Imgproc.getRotationMatrix2D(new Point(frame.cols() / 2, frame.rows() / 2), 270, 1);

                Rect bbox = new RotatedRect(new Point(frame.cols() / 2, frame.rows() / 2), frame.size(), 270).boundingRect();
                rotatedMat.put(0, 2, rotatedMat.get(0, 2)[0] + bbox.width / 2.0 - frame.cols() / 2);
                rotatedMat.put(1, 2, rotatedMat.get(1, 2)[0] + bbox.height / 2.0 - frame.rows() / 2);

                Imgproc.warpAffine(frame, rotatedFrame, rotatedMat, new Size(new Point(frame.rows(), frame.cols())));
            } else {
                Mat rotatedMat = Imgproc.getRotationMatrix2D(new Point(frame.cols() / 2, frame.rows() / 2), 90, 1);

                Rect bbox = new RotatedRect(new Point(frame.cols() / 2, frame.rows() / 2), frame.size(), 90).boundingRect();
                rotatedMat.put(0, 2, rotatedMat.get(0, 2)[0] + bbox.width / 2.0 - frame.cols() / 2);
                rotatedMat.put(1, 2, rotatedMat.get(1, 2)[0] + bbox.height / 2.0 - frame.rows() / 2);

                Imgproc.warpAffine(frame, rotatedFrame, rotatedMat, new Size(new Point(frame.rows(), frame.cols())));
            }

            MatOfRect faces = new MatOfRect();
            if (mFaceDetector != null) {
                mFaceDetector.detectMultiScale(rotatedFrame, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
            }
            Rect[] facesArray = faces.toArray();
            if (facesArray.length > 0) {
                if (mDropFrames > 0) {
                    mRecoverTime = mCurrentMiliseconds;
                    mContainer.add(mRecoverTime);
                    mDropFrames = 0;
                }
                for (int i = 0; i < facesArray.length; i++) {

                    Mat faceROI = rotatedFrame.submat(facesArray[i]);  // take out the value from the array one by one, in this case only one iteration
                    if (getBoundingState() == 1) {
                        Imgproc.rectangle(rotatedFrame, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
                    }
                    startMeasure(faceROI, rotatedFrame, facesArray[i].tl());
                }
            } else {
                if (mDropFrames == 0) {
                    mLastDropTime = mCurrentMiliseconds;
                    mContainer.add(mLastDropTime);
                }
                mDropFrames += 1;
                // Placeholder for non faces images
                updateRawValues(new RawPPGIValue(mLastRawValue, mCurrentMiliseconds));
            }


            Mat returnFrame = new Mat();
            if (cameraId == 0) {
                Mat rotatedMat = Imgproc.getRotationMatrix2D(new Point(rotatedFrame.cols() / 2, rotatedFrame.rows() / 2), 90, 1);

                Rect bbox = new RotatedRect(new Point(rotatedFrame.cols() / 2, rotatedFrame.rows() / 2), rotatedFrame.size(), 90).boundingRect();
                rotatedMat.put(0, 2, rotatedMat.get(0, 2)[0] + bbox.width / 2.0 - rotatedFrame.cols() / 2);
                rotatedMat.put(1, 2, rotatedMat.get(1, 2)[0] + bbox.height / 2.0 - rotatedFrame.rows() / 2);

                Imgproc.warpAffine(rotatedFrame, returnFrame, rotatedMat, frame.size());
            } else {
                Mat rotatedMat = Imgproc.getRotationMatrix2D(new Point(rotatedFrame.cols() / 2, rotatedFrame.rows() / 2), 270, 1);

                Rect bbox = new RotatedRect(new Point(rotatedFrame.cols() / 2, rotatedFrame.rows() / 2), rotatedFrame.size(), 270).boundingRect();
                rotatedMat.put(0, 2, rotatedMat.get(0, 2)[0] + bbox.width / 2.0 - rotatedFrame.cols() / 2);
                rotatedMat.put(1, 2, rotatedMat.get(1, 2)[0] + bbox.height / 2.0 - rotatedFrame.rows() / 2);

                Imgproc.warpAffine(rotatedFrame, returnFrame, rotatedMat, frame.size());
            }
            Log.d("Frame Size Check", "Frame: " + frame.size().toString());
            Log.d("Frame Size Check", "RoatedFrame: " + rotatedFrame.size().toString());
            Log.d("Frame Size Check", "ReturnFrame: " + returnFrame.size().toString());
            return returnFrame;
        }
    }

    public void startMeasure(Mat face, Mat frame, Point startingPoint) {

        int foreheadHeight = face.rows() / 6;
        int foreheadWidth = 3 * face.cols() / 5;

        int startingX = (int) startingPoint.x + (int) face.cols() / 5;
        int startingY = (int) startingPoint.y + face.rows() / 12;

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
        addAndDetermine(tmp);
        // calculate Raw values here
    }

    public void addAndDetermine(Float rawValue) {
        mRearValue = new RawPPGIValue(rawValue, mCurrentMiliseconds);
        //printRawValueAsText(mRearValue);
        updateRawValues(mRearValue);
        findPeakValues();
    }


    public void updateRawValues(RawPPGIValue rawValue) {
        if (mRawPPGIVals.size() == 80) {
            ArrayList<RawPPGIValue> tmp = new ArrayList<RawPPGIValue>(mRawPPGIVals.subList(1, 80));
            mRawPPGIVals = tmp;
            mRawPPGIVals.add(rawValue);
        } else {
            mRawPPGIVals.add(rawValue);
        }
        printRawPeakValueAsText(rawValue);
    }

    public void findPeakValues() {
        int mCount = mRawPPGIVals.size();

        if (mCount <= 20) {
            return;
        }

        if (mCount > 20) {
            RawPPGIValue mCandidate = mRawPPGIVals.get(mCount - 11);

            for (int index = 1; index < 8; index++) {
                if (mCandidate.getValue() < mRawPPGIVals.get(mCount - 11 - index).getValue() || mCandidate.getValue() < mRawPPGIVals.get(mCount - 11 + index).getValue()) {
                    return;
                }
            }
            updatePeakValues(mCandidate);
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
    }

    public void compareAndCalculate() {
        long interval = calculateTimeInterval();
        if (interval > 0 && !mContainer.isInTimeWindowContainer(mLastPeakValue.getCurrentTime())) {
            calculateHeartbeat(interval);
        }
    }

    public long calculateTimeInterval() {
        long mTimeInterval = mCurrentPeakValue.getCurrentTime() - mLastPeakValue.getCurrentTime();
        String msg = "Current peak: " + Long.toString(mCurrentPeakValue.getCurrentTime()) + " Last peak: "
                + Long.toString(mLastPeakValue.getCurrentTime()) + " Time interval: " +
                Long.toString(mTimeInterval) + "\n";
        Log.d("Check! ", msg);
        return mTimeInterval;
    }

    public void calculateHeartbeat(long interval) {
        long mLongCandidateBpm = (1 * 1000 * 60 / interval);
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
