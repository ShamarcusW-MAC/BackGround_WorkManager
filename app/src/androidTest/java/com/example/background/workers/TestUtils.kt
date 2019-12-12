

package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.background.OUTPUT_PATH
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.UUID


@Throws(Exception::class)
fun copyFileFromTestToTargetCtx(testCtx: Context, targetCtx: Context, filename: String): Uri {

    val destinationFilename = String.format("blur-test-%s.png", UUID.randomUUID().toString())
    val outputDir = File(targetCtx.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val outputFile = File(outputDir, destinationFilename)

    val bis = BufferedInputStream(testCtx.assets.open(filename))
    val bos = BufferedOutputStream(FileOutputStream(outputFile))
    val buf = ByteArray(1024)
    bis.read(buf)
    do {
        bos.write(buf)
    } while (bis.read(buf) != -1)
    bis.close()
    bos.close()

    return Uri.fromFile(outputFile)
}


fun uriFileExists(targetCtx: Context, uri: String?): Boolean {
    if (uri.isNullOrEmpty()) {
        return false
    }

    val resolver = targetCtx.contentResolver

    try {
        BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(uri)))
    } catch (e: FileNotFoundException) {
        return false
    }
    return true
}