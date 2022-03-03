package de.taschi.unzip

import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.nio.charset.Charset
import javax.swing.*

class MainWindow : JFrame("Locale Aware Unzip") {

    private val inputFileField = JTextField()
    private val inputFileButton = JButton("...")
    private val outputDirField = JTextField()
    private val outputDirButton = JButton("...")
    private val encodingField = JComboBox<Charset>()

    private val fileNamePreview = JTextArea()
    private val extractButton = JButton("Extract")

    init {
        val gbl = GridBagLayout()

        layout = gbl

        makeDefaultConstraints().let {
            it.gridx = 0
            it.gridy = 0
            add(JLabel("Input file"), it)
        }

        makeDefaultConstraints().let {
            it.gridx = 1
            it.gridy = 0
            it.weightx = 1.toDouble()
            it.fill = GridBagConstraints.HORIZONTAL

            add(inputFileField, it)
        }

        makeDefaultConstraints().let {
            it.gridx = 2
            it.gridy = 0
            add(inputFileButton, it)
        }

        makeDefaultConstraints().let {
            it.gridx = 0
            it.gridy = 1
            add(JLabel("Output directory"), it)
        }

        makeDefaultConstraints().let {
            it.gridx = 1
            it.gridy = 1
            it.weightx = 1.toDouble()
            it.fill = GridBagConstraints.HORIZONTAL

            add(outputDirField, it)
        }

        makeDefaultConstraints().let {
            it.gridx = 2
            it.gridy = 1
            add(outputDirButton, it)
        }

        makeDefaultConstraints().let {
            it.gridx = 0
            it.gridy = 2
            add(JLabel("Input Encoding"), it)
        }

        makeDefaultConstraints().let {
            it.gridx = 1
            it.gridy = 2
            it.weightx = 1.toDouble()
            it.fill = GridBagConstraints.HORIZONTAL

            encodingField.model = LocaleComboModel()
            add(encodingField, it)
        }

        makeDefaultConstraints().let {
            it.gridx = 0
            it.gridy = 3
            it.gridwidth = 3
            add(JLabel("Output encoding will be UTF-8 (system default)."), it)
        }

        makeDefaultConstraints().let {
            it.gridx = 0
            it.gridy = 4
            it.gridheight = 5
            add(JLabel("File name preview"), it)
        }

        makeDefaultConstraints().let {
            it.gridx = 1
            it.gridy = 4
            it.gridheight = 5
            it.weightx = 1.toDouble()
            it.weighty = 1.toDouble()

            it.fill = GridBagConstraints.BOTH

            add(fileNamePreview, it)
        }

        makeDefaultConstraints().let {
            it.gridx = 0
            it.gridy = 9
            it.gridwidth = 3
            it.anchor = GridBagConstraints.EAST

            add(extractButton, it)
        }

        inputFileButton.addActionListener { _ ->
            selectInputArchive()
        }

        outputDirButton.addActionListener { _ ->
            selectOutputDirectory()
        }

        minimumSize = Dimension(800, 400)
        pack()
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
    }

    private fun selectInputArchive() {
        val chooser = JFileChooser()
        chooser.fileFilter = ZipFileFilter()
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY

        val result = chooser.showOpenDialog(this)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile.absolutePath
            log.info("Selected input file: $file")
            inputFileField.text = file

            updatePreview()
        } else {
            log.info("File selection was cancelled.")
        }
    }

    private fun selectOutputDirectory() {
        val chooser = JFileChooser()
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY

        val result = chooser.showOpenDialog(this)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile.absolutePath
            log.info("Selected output dir: $file")
            outputDirField.text = file
        } else {
            log.info("File selection was cancelled.")
        }
    }

    private fun makeDefaultConstraints(): GridBagConstraints {
        val constraints = GridBagConstraints()
        constraints.insets = Insets(5, 5, 5, 5)
        return constraints
    }

    private fun updatePreview() {

    }

    companion object {
        private val log = LoggerFactory.getLogger(MainWindow::class.java)
    }
}