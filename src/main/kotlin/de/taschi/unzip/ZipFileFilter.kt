package de.taschi.unzip

import java.io.File
import javax.swing.filechooser.FileFilter

class ZipFileFilter: FileFilter() {

    override fun accept(f: File?): Boolean {
        return f != null && (f.extension.lowercase() == "zip" || f.isDirectory)
    }

    override fun getDescription() = "ZIP archive (*.zip)"

}