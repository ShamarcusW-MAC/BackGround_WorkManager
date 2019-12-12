

package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri

import com.example.background.KEY_IMAGE_URI

import java.io.FileNotFoundException
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import timber.log.Timber

class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val appContext = applicationContext

        makeStatusNotification("Blurring image", appContext)
        sleep()

        return try {
            val outputData = createBlurredBitmap(appContext, inputData.getString(KEY_IMAGE_URI))
            Result.success(outputData)
        } catch (fileNotFoundException: FileNotFoundException) {
            Timber.e(fileNotFoundException)
            throw RuntimeException("Failed to decode input stream", fileNotFoundException)
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            Result.failure()
        }
    }

    @Throws(FileNotFoundException::class, IllegalArgumentException::class)
    private fun createBlurredBitmap(appContext: Context, resourceUri: String?): Data {
        if (resourceUri.isNullOrEmpty()) {
            Timber.e("Invalid input uri")
            throw IllegalArgumentException("Invalid input uri")
        }

        val resolver = appContext.contentResolver

        val bitmap = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri)))

        val output = blurBitmap(bitmap, appContext)

        val outputUri = writeBitmapToFile(appContext, output)

        return workDataOf(KEY_IMAGE_URI to outputUri.toString())
    }
}