#!/bin/bash

VID=$(realpath $1)
WIDTH=$2
HEIGHT=$3

set -e -x

mkdir workdir
cd workdir

ffmpeg -i $VID -vf fps=20 image_%d.png
../rotten.py $VID.txt $WIDTH $HEIGHT

cd ..
rm -r workdir
