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
import javax.swing.UIManager

val log = LoggerFactory.getLogger("main")

fun main(args: Array<String>) {
    try {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        log.error("Error while setting system look and feel", e)
    }
    MainWindow()
}