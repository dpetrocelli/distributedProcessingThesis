#!/bin/bash
mkdir /tmp/ffmpeg
mkdir /tmp/ffmpeg/bin
mkdir /tmp/video
mkdir /tmp/video/compressedInWorker
mkdir /tmp/video/exitCentralized
mkdir /tmp/video/fileToApplyFilter
mkdir /tmp/video/resultJoined
mkdir /tmp/video/splittedVideo
mkdir /tmp/video/returnedCompressed
mkdir /tmp/video/inputQueue
chmod 777 -R /tmp/ffmpeg
chmod 777 -R /tmp/video
cp bigbuckbunny_1500.mp4 /tmp/video/inputQueue
cp -r ffmpeg-git-20181025-64bit-static/* /tmp/ffmpeg/bin
