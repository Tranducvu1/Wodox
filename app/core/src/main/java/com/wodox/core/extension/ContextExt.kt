package com.wodox.core.extension

import android.content.Context
import java.io.File
import android.util.Size
import android.view.WindowManager
import android.os.Build
import android.graphics.Point
import android.util.DisplayMetrics


val Context.imageDir: File
    get() = createDirectory("images")

val Context.videoDir: File
    get() = createDirectory("videos")

val Context.docsDir: File
    get() = createDirectory("documents")

val Context.audioDir: File
    get() = createDirectory("audio")

val Context.downloadsDir: File
    get() = createDirectory("downloads")

val Context.tempDir: File
    get() = createDirectory("temp")

val Context.cacheAppDir: File
    get() = createDirectory("cache")

val Context.pdfDir: File
    get() = createDirectory("pdf")

fun Context.createDirectory(dirName: String): File {
    val dir = File(filesDir, dirName)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}


fun Context.getFileInDirectory(dirName: String, fileName: String): File {
    val dir = createDirectory(dirName)
    return File(dir, fileName)
}

fun Context.clearDirectory(dirName: String): Boolean {
    return try {
        val dir = File(filesDir, dirName)
        if (dir.exists() && dir.isDirectory) {
            dir.deleteRecursively()
            createDirectory(dirName)
            true
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Context.getDirectorySize(dirName: String): Long {
    val dir = File(filesDir, dirName)
    return if (dir.exists() && dir.isDirectory) {
        dir.walkTopDown().sumOf { it.length() }
    } else {
        0L
    }
}

fun Context.deleteFile(dirName: String, fileName: String): Boolean {
    return try {
        val file = File(File(filesDir, dirName), fileName)
        file.exists() && file.delete()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Context.listFilesInDirectory(dirName: String): List<File> {
    val dir = File(filesDir, dirName)
    return if (dir.exists() && dir.isDirectory) {
        dir.listFiles()?.toList() ?: emptyList()
    } else {
        emptyList()
    }
}


fun Context.screenSize(): Size {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val wm = getSystemService(WindowManager::class.java)
        val metrics = wm.currentWindowMetrics
        val bounds = metrics.bounds
        Size(bounds.width(), bounds.height())
    } else {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getRealSize(point)
        Size(point.x, point.y)
    }
}


fun Context.getDialogWidth(): Int {
    val displayMetrics = DisplayMetrics()
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val screenWidth = displayMetrics.widthPixels
    return (screenWidth * 0.9).toInt()
}

fun Context.titleTextColor(): Int {
    return try {
        val typedArray = this.obtainStyledAttributes(intArrayOf(android.R.attr.textColorPrimary))
        val color = typedArray.getColor(0, android.graphics.Color.BLACK)
        typedArray.recycle()
        color
    } catch (e: Exception) {
        android.graphics.Color.BLACK
    }
}