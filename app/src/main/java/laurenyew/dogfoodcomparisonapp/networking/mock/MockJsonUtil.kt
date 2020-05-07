package laurenyew.dogfoodcomparisonapp.networking.mock

import android.content.Context
import androidx.annotation.VisibleForTesting
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Helper Util class to read in the Json file
 */
object MockJsonUtil {
    @Throws(Exception::class)
    fun getJsonStringFromFile(context: Context, filePath: String): String {
        val stream = context.resources.assets.open(filePath)

        val jsonFile = convertInputStreamToString(stream)
        //Make sure you close all streams.
        stream.close()
        return jsonFile
    }

    @Throws(Exception::class)
    @VisibleForTesting
    fun convertInputStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val buffer = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) {
            buffer.append(line).append("\n")
            line = reader.readLine()
        }
        reader.close()
        return buffer.toString()
    }
}