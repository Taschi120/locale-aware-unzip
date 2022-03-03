package de.taschi.unzip

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JProgressBar

class ProgressDialog(parent: MainWindow): JDialog(parent) {
    val progressBar = JProgressBar()

    init {
        layout = GridBagLayout()

        val c1 = GridBagConstraints()
        c1.gridx = 0
        c1.gridy = 0
        c1.insets = Insets(5, 5, 5, 5)
        add(JLabel("Unzipping in progress."))
        add(progressBar)

        minimumSize = Dimension(400, 160)
        isModal = true
    }
}