# Heartrate measurement with PPG Imaging 

This is a repo for ppgi heart rate measuring app running on android device. The development of the app is based on Asus Zenpad 3s on Android 7 and has been tested on Android 9.
The app has two different modes for heart rate measurement, namely a runtime mode for modern high performance devices and an offline mode for legacy low end devices.

## Runtime mode 
Click the floating action button in the home fragment, a camera preview will show up to detect your own face and forehead in bounding boxes with OpenCV. It is set to use the front camera by default, but switchable by clicking the icon at the right corner. 

The boudingboxes and the displayed text are also dismissable by this logic. An alert window will come out after a few seconds of none face detection.

The runtime mode works under the assumption of when 30fps frame rate is secured. It is easy achievable for modern devices. It also offer an offline mode for legacy low end devices.

## Offline mode
The offline mode is designed for legacy low end Android devices which do not have that much computational power to apply a runtime face detection smoothly enough. It separates measurement into two different parts: Video capturing and frame analysis.

Video capturing: Swipe from left to right to open navigation drawer and click the capture icon to open a camera preview. Take a video with the face of the testee in it for enough long,longer than 30 seconds for better accuracy. The result at the very beginning might be not accurate if you take a few seconds to adjust your position after the capture is began. It is suggested to sit still during the video capturing.

Frame analysis: After the video is saved onto the device, you can open the gallery in the navigation drawer and pick a video for analysis. A dialog will pop up to ask you the orientation of the video you would like to measure with, it is neccessary argument for OpenCV to set up the right parameter. 

## File output
During the measurement, it automatically generates a plaintext file to record the time stamp  the raw ppgi value of every frame in directory: Android/data/packagename/cache/peak for future database analysis, which is currently not implemented.

The videos captured in capture fragment will be saved in path: /ppgi, and it will also output some key frames in a subfolder with the same name of the to be analyzed video, by reading the key frames in those subfolder.

It can be disabled by simply commenting the following code in OfflineMeasurementActivity in function startMeasure(Mat face, Mat frame, Point startingPoint).

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

## Performance
The measured result from this app has a huge difference from the value´with medicial equipments for some known reason: Muscle shaking, ambient light influence and misdetection.

Usually it has around 10 to 15 BPM difference in heart rate range of 60-120 and could be even larger as your heart rate goes up.

Some possible improvements are on the way after my exams period.

## Bugs and Issues
It has some compatiable issues with devices over android 9 because of permission control from android 10, I am tring to fix it in the future after my exams are over. And it would crash to home when you rotate your device at the capture fragment.

The layout also does not look so good on phone, because I was developing it on an android 7.0 tablet with 4:3 ratio screen, a possible solution is to write different layout files and determine the usage depends on its resolution and screensize.

## Todo list in the future.
Fixed the known Bugs.
Improved the layout.
Implement the database class to keep track of historical heart rate changes.
Implement a custom layout to plot the measured heart rate with a electrocardiogram-like graph. 
