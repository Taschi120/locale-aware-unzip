package de.taschi.unzip

//    Locale Aware Unzipper
//    Copyright (C) 2022 S. Hillebrand <dev@hit-to-key.net>
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
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

        ZipFile(inputFile, encoding).use { zf ->
            progress.setMaxCount(zf.size())
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
                        progress.incrementProcessedCount()
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
        mkdirp(outfile)
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
        val destFile = File(outputDir, entry.name)

        val destDirPath: String = outputDir.canonicalPath
        val destFilePath: String = destFile.canonicalPath

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw IOException("Entry is outside of the target dir: " + entry.name)
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