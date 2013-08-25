#!/bin/bash

export srcpath="/Volumes/spare room/workspace/ClockFace/src/com/jilgen/clockface/";
export destpath="src/com/jilgen/clockface/";

echo "SRC: $srcpath";
echo "DEST: $destpath";

for each in `ls "$srcpath"`
do
    echo "$each";
    sed 's/clockface/yourface/g' "$srcpath/$each" | sed 's/ClockFace/YourFace/g' > "$destpath/$each"
done
