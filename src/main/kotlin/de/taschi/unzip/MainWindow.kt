package de.taschi.unzip

import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.zip.ZipInputStream
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

        encodingField.addActionListener { _ ->
            updatePreview()
        }

        extractButton.addActionListener { _ ->
            startExtraction()
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


    private fun startExtraction() {
        val encoding = encodingField.selectedItem as Charset?
        if (encoding == null) {
            JOptionPane.showMessageDialog(this, "No encoding selected")
            return
        }

        val inputFile = File(inputFileField.text)
        val outputDir = File(outputDirField.text)

        val progress = UnzipProgress()
        val progressWindow = ProgressDialog(this)

        var finished = false

        val updateThread = Thread() {
            while(!finished) {
                progressWindow.progressBar.maximum = progress.getMaxCount()
                progressWindow.progressBar.value = progress.getProcessedCount()
                Thread.sleep(100)
            }
        }

        val successCallback = {
            progressWindow.isVisible = false
            JOptionPane.showMessageDialog(this, "Extraction finished successfully")
            finished = true
        }

        val errorCallback = { e: Exception ->
            log.error("Exception during unzip", e)
            progressWindow.isVisible = false
            JOptionPane.showMessageDialog(this, "Error while unzipping, see log for details")
            finished = false
        }

        val workerThread = UnzipWorkerThread(encoding, inputFile, outputDir, progress, successCallback, errorCallback)

        workerThread.start()
        updateThread.start()
        progressWindow.isVisible = true
    }

    private fun updatePreview() {
        val encoding = encodingField.selectedItem as Charset?
        val inputFile = File(inputFileField.text)

        if (encoding == null) {
            log.info("No encoding selected")
            fileNamePreview.text = ""
            return
        }

        if (!inputFile.exists()) {
            log.info("Input file ${inputFile.absolutePath} does not exist")
            fileNamePreview.text = ""
            return
        }

        updatePreview(encoding, inputFile)
    }

    private fun updatePreview(encoding: Charset, inputFile: File) {
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
            fileNamePreview.text = out.toString()
        } catch (e: Exception) {
            log.error("Exception while creating preview: ", e)
            fileNamePreview.text = "Error while creating preview. Selected encoding might be wrong."
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(MainWindow::class.java)
    }
}