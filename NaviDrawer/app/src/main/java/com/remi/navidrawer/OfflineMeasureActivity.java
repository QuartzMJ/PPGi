package com.remi.navidrawer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.remi.navidrawer.databinding.ActivityOfflineMeasureBinding;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OfflineMeasureActivity extends AppCompatActivity {

    private String TAG = "Mika punch";
    private Mat imageMat;
    private ActivityOfflineMeasureBinding binding;
    private File mCascadeFile;
    private int facedFrame = 0;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private CascadeClassifier mFaceDetector;
    private int mOrientation;
    private RawPPGIValue mLastValue;
    private long mCurrentMiliseconds = 0;
    private int mDropFrames = 0;
    private int mFramesCount = 0;
    private int defaultPreset = 9;
    private int defaultOffset = 9;
    private int adaptedPreset;
    private Bitmap bitmap;
    private String filePath;
    private int adaptedOffset;
    private int analyzedFrameCount = 0;
    private boolean isAdapted = false;
    private int mLastBpm = 0;
    private ArrayList<Integer> BpmList = new ArrayList<Integer>();
    private ArrayList<RawPPGIValue> mRawPPGIVals;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    imageMat = new Mat();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    static {
        System.loadLibrary("opencv_java4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfflineMeasureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent mIntent = getIntent();
        filePath = mIntent.getStringExtra("Filepath");


        mOrientation = mIntent.getIntExtra("Orientation", 0);
        mFaceDetector = loadDetector(R.raw.lbpcascade_frontalface, "lbpcascade_frontalface.xml");

        mRawPPGIVals = new ArrayList<RawPPGIValue>();

        File in = new File(filePath);
        if (!in.exists()) {
            Log.d("Mika punch", "File not found");
            return;
        } else {
            Log.d("Mika punch", "File found");
        }

        Button infoBtn = binding.infoBtn;
        Button startBtn = binding.startBtn;

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread() {
                    public void run() {
                        VideoCapture vc = new VideoCapture(filePath);
                        analyseVideo(vc);
                    }
                };
                thread.start();
            }
        });


        ImageView thumbnail = binding.videoThumbnail;
        Bitmap bitmap;
        bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        thumbnail.setImageBitmap(bitmap);
    }

    public int getFrameCount(String filepath) {

        VideoCapture videoCapture = new VideoCapture(filepath);
        Mat frame = new Mat();
        int index = 0;
        while (videoCapture.read(frame)) {
            index += 1;
        }
        return index;
    }

    public void analyseVideo(VideoCapture videoCapture) {
        Mat frame = new Mat();

        while (videoCapture.read(frame)) {
            analyseFrame(frame);
        }
    }

    public void analyseFrame(Mat inputFrame) {
        Mat frame = inputFrame;
        mCurrentMiliseconds = analyzedFrameCount * 33;
        analyzedFrameCount++;

        if (mAbsoluteFaceSize == 0) {    // only called at the beginning of the activity
            int height = frame.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }


        if (mOrientation == 2) {             // in case of landscape
            frame = inputFrame;


            MatOfRect faces = new MatOfRect();
            if (mFaceDetector != null) {
                mFaceDetector.detectMultiScale(frame, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
            }

            Rect[] facesArray = faces.toArray();
            if (facesArray.length > 0) {
                if (mDropFrames > 0) {
                    mDropFrames = 0;
                }
                for (int i = 0; i < facesArray.length; i++) {

                    Mat faceROI = frame.submat(facesArray[i]);  // take out the value from the array one by one, in this case only one iteration
                    startMeasure(faceROI, frame, facesArray[i].tl());

                }
            } else {
                if (mDropFrames >= 30) {
                    mRawPPGIVals.removeAll(mRawPPGIVals);
                    mFramesCount = 0;
                    mDropFrames = 0;
                }
                mDropFrames++;
            }
        } else {             // in case of portrait mode

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mFaceDetector = loadDetector(R.raw.lbpcascade_frontalface, "lbpcascade_frontalface.xml");
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
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

            classifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());

            if (classifier.empty()) {
                classifier = null;
            } else
                cascadeDir.delete();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
        return classifier;
    }

    public void startMeasure(Mat face, Mat frame, Point startingPoint) {

        int foreheadHeight = face.rows() / 6;
        int foreheadWidth = 3 * face.cols() / 5;

        int startingX = (int) startingPoint.x + (int) face.cols() / 5;
        int startingY = (int) startingPoint.y + face.rows() / 12;

        Rect foreheadROI = new Rect(startingX, startingY, foreheadWidth, foreheadHeight);
        Imgproc.rectangle(frame, foreheadROI.tl(), foreheadROI.br(), FACE_RECT_COLOR, 3);
        Mat croppedFrame = new Mat(frame, foreheadROI);

        if (analyzedFrameCount % 15 == 0) {

            String path = Environment.getExternalStoragePublicDirectory("ppgi").getAbsolutePath() + "/" + filePath.substring(25, filePath.length() - 4);
            File file = new File(path);
            if (!file.exists()) {
                if (file.mkdir())
                    Log.d("Asuna gogo", "created: " + path);
                else
                    Log.d("Asuna gogo", "not created: " + path);
            }

            String framePath = path + "/frames";
            File frameDir = new File(framePath);
            if (!frameDir.exists()) {
                if (frameDir.mkdir())
                    Log.d("Asuna gogo2", "created: " + path);
                else
                    Log.d("Asuna gogo2", "not created: " + path);
            }

            Imgcodecs.imwrite(frameDir.getAbsolutePath() + "/" + Integer.toString(analyzedFrameCount) + ".png", frame);
            Log.d("Akane run", file.getAbsolutePath());

            bitmap = Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(frame, bitmap);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    binding.videoThumbnail.setImageBitmap(bitmap);
                }
            });

        }

        Log.d("Akane run", Integer.toString(analyzedFrameCount));
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
        Log.d("Kuroko riding", Float.toString(tmp));

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                int raw = Math.round(tmp);
                String msg = "Raw Value: " + Integer.toString(raw);
                TextView tv = binding.rawValue;
                tv.setText(msg);
            }

            ;
        });
        addAndDetermine(tmp);
        // calculate Raw values here
    }

    public void addAndDetermine(Float rawValue) {
        mLastValue = new RawPPGIValue(rawValue, mCurrentMiliseconds);
        // printRawValueAsText(mRearValue);
        updateRawValues(mLastValue, true);
    }

    public void updateRawValues(RawPPGIValue rawValue, boolean validity) {
        if (mRawPPGIVals.size() > 1) {
            if (rawValue.getValue() >= 1.8 * mRawPPGIVals.get(mRawPPGIVals.size() - 1).getValue()) {
                return;
            }
        }
        if (mRawPPGIVals.size() == 180) {
            ArrayList<RawPPGIValue> tmp = new ArrayList<RawPPGIValue>(mRawPPGIVals.subList(1, 180));
            mRawPPGIVals = tmp;
            mRawPPGIVals.add(rawValue);
        } else {
            mRawPPGIVals.add(rawValue);
        }

        if (mFramesCount < 180) {
            mFramesCount++;
        } else {
            calculateHeartRate();
            mFramesCount = 60;
        }
    }

    public void calculateHeartRate() {

        int corruptionCount = 0;
        String msg = "";
        int peakCounts = 0;
        int startIndex = 0;
        int endIndex = 0;
        int mPreset;
        int mOffset;

        if (isAdapted) {
            mPreset = adaptedPreset;
            mOffset = adaptedOffset;
        } else {
            mPreset = defaultPreset;
            mOffset = defaultOffset;
        }

        Out:
        for (int i = 0; i < mRawPPGIVals.size(); i++) {
            RawPPGIValue candidate = mRawPPGIVals.get(i);
            if (i < 60 || i > (180 - mPreset - mOffset - 2)) {   // not into consideration for too few samples in surroundings;
                continue;
            }

            for (int j = 1; j < mPreset; j++) {
                if (candidate.getValue() < mRawPPGIVals.get(i - j).getValue() || candidate.getValue() < mRawPPGIVals.get(i + j).getValue()) {
                    continue Out;
                }
            }

            int valid = 0;
            for (int j = mPreset; j < mPreset + mOffset; j++) {
                if (candidate.getValue() > mRawPPGIVals.get(i - j).getValue())
                    valid += 1;

                if (candidate.getValue() > mRawPPGIVals.get(i + j).getValue())
                    valid += 1;
            }

            if (valid < 2 * mOffset - 2) {
                continue Out;
            }

            if (peakCounts == 0) {
                startIndex = i;
            }
            peakCounts++;
            endIndex = i;
        }

        long timeDistance = mRawPPGIVals.get(endIndex).getCurrentTime() - mRawPPGIVals.get(startIndex).getCurrentTime();
        int BPM = Math.round(990 * 60 * (peakCounts - 1) / timeDistance);
        Log.d("Initial BPM", Integer.toString(BPM));

        if (!isAdapted) {
            resetPresets(BPM);
            isAdapted = true;
            mLastBpm = BPM;
        } else {
            int mCandidateBpm;
            mCandidateBpm = (BPM + mLastBpm) / 2;
            mLastBpm = mCandidateBpm;
            resetPresets(mCandidateBpm);
            BpmList.add(mCandidateBpm);
            int outputBPM = calcaulateAverages();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String msg = "Heart Rate: " + Integer.toString(outputBPM);
                    TextView tv = binding.bpm;
                    tv.setText(msg);
                }
            });
        }

    }

    public int calcaulateAverages() {
        int listSize = BpmList.size();
        int sum = 0;
        if (listSize < 5) {
            for (int value : BpmList) {
                sum += value;
            }
            return sum / listSize;
        } else {
            for (int index = listSize - 5; index < listSize; index++) {
                sum += BpmList.get(index);
            }
            return sum / 5;
        }
    }

    public void resetPresets(int bpm) {
        if (bpm < 50) {
            adaptedPreset = 20;
            adaptedOffset = 12;
        } else if (bpm > 50 && bpm < 120) {
            adaptedPreset = 9;
            adaptedOffset = 9;
        } else if (bpm > 120 && bpm < 150) {
            adaptedPreset = 8;
            adaptedOffset = 7;
        } else {
            adaptedPreset = 7;
            adaptedOffset = 4;
        }
    }
}