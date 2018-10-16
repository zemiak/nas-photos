#!/bin/sh

docker run -d \
    -e "BIN_PATH=/usr/bin" \
    -e "PHOTO_PATH=/pictures" \
    -e "TEMP_PATH=/cache" \
    -e "WATERMARK_PATH=/opt/watermarks/" \
    -e "EXTERNAL_URL=http://10.8.13.65:10081/nasphotos/" \
    -p 10081:8080 -p 10848:4848 -p 10009:9009 \
    -v /home/vasko/media/Pictures:/pictures \
    -v /home/vasko/media/Pictures/cache:/cache \
    --name=nasphotos-dev nasphotos-dev
