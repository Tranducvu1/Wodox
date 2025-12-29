/*
 * Copyright (C) 2015-2023 Emanuel Moecklin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onegravity.rteditor.spans;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.bumptech.glide.Glide;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * An ImageSpan representing an embedded image.
 */
public class ImageEmojiSpan extends android.text.style.ImageSpan {
    private final int size;
    private final String fileName;

    public ImageEmojiSpan(Context context, String baseUrl, String fileName, int size) {
        super(context, Objects.requireNonNull(ImageEmojiSpan.getBitmap(context, baseUrl + "/" + fileName, size)), Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? ALIGN_CENTER : ALIGN_BASELINE);

        this.size = size;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSize() {
        return size;
    }

    static Bitmap getBitmap(Context context, String path, int size) {
        try {
            Bitmap bitmap = Glide.with(context).asBitmap().load(path).submit().get();

            return Bitmap.createScaledBitmap(bitmap, size, size, true);
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }


}