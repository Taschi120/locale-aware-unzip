package de.taschi.unzip

import org.slf4j.LoggerFactory
import javax.swing.UIManager

val log = LoggerFactory.getLogger("main")

fun main(args: Array<String>) {
    try {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } catch (e: Exception) {
        log.error("Error while setting system look and feel", e)
    }
    MainWindow()
}