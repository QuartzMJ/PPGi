package com.remi.navidrawer.ui.capture;

import static android.os.Environment.getExternalStorageDirectory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FileOutputOptions;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;
import com.remi.navidrawer.MainActivity;
import com.remi.navidrawer.R;
import com.remi.navidrawer.databinding.FragmentCaptureBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class CaptureFragment extends Fragment {

    private @NonNull FragmentCaptureBinding viewBinding;
    private ImageCapture imageCapture = null;
    private VideoCapture videoCapture = null;
    private Recording recording = null;
    private CameraSelector lensFacing = CameraSelector.DEFAULT_BACK_CAMERA;
    private ExecutorService cameraExecutor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /* ((MainActivity) getActivity()).getFloatingActionButton().hide();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);*/
        CaptureViewModel captureViewModel =
                new ViewModelProvider(this).get(CaptureViewModel.class);

        viewBinding = FragmentCaptureBinding.inflate(inflater, container, false);
        View root = viewBinding.getRoot();
        viewBinding.videoCaptureButton.setOnClickListener(v->captureVideo());
        viewBinding.switchCameraButton.setOnClickListener(v->switchCamera());
        startCamera();
        return root;
    }

    private void switchCamera() {
        if (lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA) {
            lensFacing = CameraSelector.DEFAULT_BACK_CAMERA;
            viewBinding.switchCameraButton.setBackgroundResource(R.drawable.ic_switch_front);
        } else if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA) {
            lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA;
            viewBinding.switchCameraButton.setBackgroundResource(R.drawable.ic_switch_back);
        }
        startCamera();
    }

    private void startCamera() {


        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(() -> {
            try {

                ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();


                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(viewBinding.viewFinder.getSurfaceProvider());

                Recorder recorder = new Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build();
                videoCapture = VideoCapture.withOutput(recorder);
                CameraSelector cameraSelector = lensFacing;
                imageCapture = new ImageCapture.Builder().build();


                processCameraProvider.unbindAll();
                processCameraProvider.bindToLifecycle(this, cameraSelector,
                        videoCapture,
                        preview);

            } catch (Exception e) {
                Log.e(Configuration.TAG, "Binding magic  failed ！" + e);
            }
        }, ContextCompat.getMainExecutor(getContext()));

    }

    @SuppressLint("CheckResult")
    private void captureVideo() {

        if (videoCapture != null) {
            viewBinding.videoCaptureButton.setEnabled(false);
            Recording curRecording = recording;
            if (curRecording != null) {
                curRecording.stop();
                recording = null;
                return;
            }

            String name = new SimpleDateFormat(Configuration.FILENAME_FORMAT, Locale.ENGLISH)
                    .format(System.currentTimeMillis());


            String filename =  Environment.getExternalStoragePublicDirectory("ppgi")+"/" + name +".mp4";
            File file = new File(filename);
            FileOutputOptions fileOutputOptions = new FileOutputOptions.Builder(file).build();

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        Configuration.REQUEST_CODE_PERMISSIONS);
            }
            Recorder recorder = (Recorder) videoCapture.getOutput();
            recording = recorder.prepareRecording(getContext(), fileOutputOptions)
                    .withAudioEnabled()
                    .start(ContextCompat.getMainExecutor(getContext()), videoRecordEvent -> {
                        if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                            viewBinding.videoCaptureButton.setBackgroundResource(R.drawable.ic_capture_stop);
                            viewBinding.videoCaptureButton.setEnabled(true);
                        } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                            if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
                                String msg = "Video capture succeeded: " +
                                        ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults()
                                                .getOutputUri();
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                Log.d(Configuration.TAG, msg);
                            } else {
                                if (recording != null) {
                                    recording.close();
                                    recording = null;
                                    Log.e(Configuration.TAG, "Video capture end with error: " +
                                            ((VideoRecordEvent.Finalize) videoRecordEvent).getError());
                                }
                            }
                            viewBinding.videoCaptureButton.setBackgroundResource(R.drawable.ic_capture_start);
                            viewBinding.videoCaptureButton.setEnabled(true);
                        }
                    });
        }
    }

    static class Configuration {
        public static final String TAG = "CameraxBasic";
        public static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
        public static final int REQUEST_CODE_PERMISSIONS = 10;
        public static final int REQUEST_AUDIO_CODE_PERMISSIONS = 12;
        public static final String[] REQUIRED_PERMISSIONS =
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.P ?
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE} :
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.MANAGE_EXTERNAL_STORAGE};
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinding = null;
    }

    @Override
    public void  onDetach(){
        super.onDetach();
       /* requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);*/
    }

    @Override
    public void onResume() {
        super.onResume();
       /* ((MainActivity) getActivity()).getSupportActionBar().hide();*/
    }
}
