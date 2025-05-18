#!/bin/bash

BAURL='https://youtube.com/watch?v=FtutLA63Cp8'
FILENAME=badapple.mp4
BLOCKS=45

set -e -x

mkdir workdir
cd workdir

python3 -m youtube_dl -o $FILENAME $BAURL
ffmpeg -i $FILENAME -vf fps=20 image_%d.png
../rotten.py ../badapple.txt $BLOCKS $BLOCKS

cd ..
rm -r workdir
