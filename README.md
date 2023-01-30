#PPGI Heartrate measurement app

###There are two different modes for different type of devices, runtime mode for high performance devices and offline mode for low end devices

##Runtime mode 
###Click the floating action button in the home fragment, a camera preview will show up to detect your own face and forehead in bounding boxes with OpenCV. It is set to use the front camera by default, but switchable by clicking the icon at the right corner. 

The boudingboxes and the displayed text are also dismissable by this logic. An alert window will come out after a few seconds of none face detection.

The runtime mode works under the assumption of when 30fps frame rate is secured. It is easy achievable for modern devices. It also offer an offline mode for legacy low end devices.

##Offline mode
###The offline mode is designed for legacy low end Android devices which do not have that much computational power to apply a runtime face detection smoothly enough. It separates measurement into two different parts: Video capturing and frame analysis.

###Video capturing: Swipe from left to right to open navigation drawer and click the capture icon to open a camera preview. Take a video with the face of the testee in it for enough long,longer than 30 seconds for better accuracy. The result at the very beginning might be not accurate if you take a few seconds to adjust your position after the capture is began. It is suggested to sit still during the video capturing.

###Frame analysis: After the video is saved onto the device, you can open the gallery in the navigation drawer and pick a video for analysis. A dialog will pop up to ask you the orientation of the video you would like to measure with, it is neccessary argument for OpenCV to set up the right parameter. 

##Performance
###The measured result from this app has a huge difference from the value´with medicial equipments for some known reason: Muscle shaking, ambient light influence and misdetection.

Usually it has around 10 to 15 BPM difference in heart rate range of 60-120 and could be even larger as your heart rate goes up.

Some possible improvements are on the way after my exams period.

##Bugs and Issues
###It has some compatiable issues with devices over android 9 because of permission control from android 10, I am tring to fix it in the future after my exams are over. And it would crash to home when you rotate your device at the capture fragment.

The layout also does not look so good on phone, because I was developing it on an android 7.0 tablet with 4:3 ratio screen, a possible solution is to write different layout files and determine the usage depends on its resolution and screensize.

##Todo in the future.
###Fixed the known Bugs.
###Improved the layout.
###Implement the database class to keep track of historical heart rate changes.
###Implement a custom layout to plot the measured heart rate with a electrocardiogram-like graph.
