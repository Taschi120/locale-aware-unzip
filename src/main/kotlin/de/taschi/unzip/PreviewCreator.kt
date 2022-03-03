package de.taschi.unzip

import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.zip.ZipInputStream

class PreviewCreator(
    private val encoding: Charset,
    private val inputFile: File,
    val onResult: (String) -> Unit,
    val onError: (Exception) -> Unit): Thread() {

    override fun run() {
        try {
            var out = StringBuilder()
            FileInputStream(inputFile).use { fis ->
                ZipInputStream(fis, encoding).use { zis ->
                    while (true) {
                        val entry = zis.nextEntry
                        if (entry == null) {
                            break
                        } else {
                            out.append(entry.name)
                            out.append(System.lineSeparator())
                        }
                    }
                }
            }
            onResult(out.toString())
        } catch (e: Exception) {
            log.error("Exception while creating preview: ", e)
            onError(e)
        }
    }

}