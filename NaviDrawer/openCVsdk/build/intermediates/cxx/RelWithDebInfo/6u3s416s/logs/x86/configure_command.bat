@echo off
"C:\\Users\\Remi\\AppData\\Local\\Android\\Sdk\\cmake\\3.18.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\Remi\\AndroidStudioProjects\\PPGi\\NaviDrawer\\openCVsdk\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\Remi\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Remi\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Remi\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Remi\\AppData\\Local\\Android\\Sdk\\cmake\\3.18.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\Remi\\AndroidStudioProjects\\PPGi\\NaviDrawer\\openCVsdk\\build\\intermediates\\cxx\\RelWithDebInfo\\6u3s416s\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\Remi\\AndroidStudioProjects\\PPGi\\NaviDrawer\\openCVsdk\\build\\intermediates\\cxx\\RelWithDebInfo\\6u3s416s\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=RelWithDebInfo" ^
  "-BC:\\Users\\Remi\\AndroidStudioProjects\\PPGi\\NaviDrawer\\openCVsdk\\.cxx\\RelWithDebInfo\\6u3s416s\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
