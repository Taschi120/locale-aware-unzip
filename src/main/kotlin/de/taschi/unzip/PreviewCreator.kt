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