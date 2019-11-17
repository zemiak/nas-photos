#!/bin/sh

BASE=~/Pictures/

main() {
    for folder in $(find . -name "????" -type d | xargs)
    do
    done
}

yearFolder() {

}

pictureFolder() {

}

createThumbnail() {
    mkdir -p video-thumbnails
    mtn -i -c 2 -r 2 -w 256 -h 96 -O video-thumbnails -o .JPG P1120576.MOV
}
