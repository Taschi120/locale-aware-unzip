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
        c1.anchor = GridBagConstraints.CENTER
        c1.insets = Insets(5, 5, 5, 5)
        add(JLabel("Unzipping in progress."), c1)

        val c2 = GridBagConstraints()
        c2.gridx = 0
        c2.gridy = 1
        c2.anchor = GridBagConstraints.CENTER
        c2.insets = Insets(5, 5, 5, 5)
        c2.fill = GridBagConstraints.HORIZONTAL
        c2.weightx = 1.toDouble()
        add(progressBar, c2)

        minimumSize = Dimension(400, 160)
        defaultCloseOperation = JDialog.DO_NOTHING_ON_CLOSE
        isModal = true
    }
}