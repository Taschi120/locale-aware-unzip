package de.taschi.unzip

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class UnzipWorkerThread(val encoding: Charset,
                        val inputFile: File,
                        val outputDir: File,
                        val progress: UnzipProgress,
                        val onSuccess: () -> Unit,
                        val onError: (Exception) -> Unit): Thread() {

    private fun extract() {
        log.info("Starting extraction")

        if (!inputFile.exists()) {
            throw UnzipException("Input file ${inputFile.absolutePath} does not exist.")
        }

        if (!outputDir.exists()) {
            throw UnzipException("Output directory ${outputDir.absolutePath} does not exist.")
        }

        if (!outputDir.isDirectory) {
            throw UnzipException("Output directory ${outputDir.absolutePath} is not a directory.")
        }

        val innerOutputDir = outputDir.resolve(inputFile.nameWithoutExtension)
        if (innerOutputDir.exists()) {
            // TODO: Offer user to delete previously existing input, or cancel.
            throw UnzipException("Output directory ${outputDir.absolutePath} already exists")
        }

        log.info("Creating output dir")
        if (!innerOutputDir.mkdirs()) {
            throw UnzipException("Could not create directory ${innerOutputDir.absolutePath}")
        }

        FileInputStream(inputFile).use { fis ->
            ZipInputStream(fis, encoding).use { zis ->
                log.info("Zip file opened.")
                while (true) {
                    val entry = zis.nextEntry
                    if (entry == null) {
                        break
                    } else {
                        if (entry.isDirectory) {
                            createDirectory(innerOutputDir, entry)
                        } else {
                            createFile(innerOutputDir, entry, zis)
                        }
                    }
                }
            }
        }
    }

    private fun createFile(outputDir: File, entry: ZipEntry, zis: ZipInputStream) {
        log.info("Extracting file ${entry.name}")
        val outfile = newFile(outputDir, entry)
        val parent = outfile.parentFile
        mkdirp(parent)

        FileOutputStream(outfile).use { fos ->
            var buffer = ByteArray(1024)
            var len: Int
            while(true) {
                val length = zis.read(buffer)
                if (length <= 0) break
                fos.write(buffer, 0, length)
            }
        }
        log.info("Extraction of ${entry.name} complete")
    }

    private fun createDirectory(outputDir: File, entry: ZipEntry) {
        log.info("Extracting dir ${entry.name}")
        val outfile = newFile(outputDir, entry)
        mkdirp(outputDir)
    }

    private fun mkdirp(dir: File) {
        log.info("Trying to create ${dir.canonicalPath}")
        if (dir.isFile) {
            throw UnzipException("Could not create ${dir.absolutePath}: A file exists at this location already")
        }

        if (!dir.isDirectory && !dir.mkdirs()) {
            throw UnzipException("Could not create ${dir.absolutePath}: mkdirs() returned false")
        }
    }

    private fun newFile(outputDir: File, entry: ZipEntry): File {
        val destFile = File(outputDir, entry.getName())

        val destDirPath: String = outputDir.getCanonicalPath()
        val destFilePath: String = destFile.canonicalPath

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw IOException("Entry is outside of the target dir: " + entry.getName())
        }

        return destFile
    }

    override fun run() {
        try {
            extract()
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UnzipWorkerThread::class.java)
    }

}