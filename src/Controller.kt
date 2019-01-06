import java.awt.event.*
import java.io.File
import java.nio.file.Paths
import javax.swing.*
import kotlin.concurrent.thread

class Controller {

    private val view = View()
    private val basePathFile = Paths.get("watery-db").toAbsolutePath().toFile()
    private val resultFile = Paths.get("watery-db/result.html").toAbsolutePath().toFile()

    private val htmlHeader = "<html><head><style type='text/css'>${File("res/style.css").readText()}</style></head><body>"
    private val htmlTail = "</body></html>"

    init {

        EngineJNI.initialize()

        updateFileTree()
        view.runButton.addActionListener { executeCommand() }
        view.editorPane
                .getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK, true), "run")

        view.editorPane.actionMap.put("run", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                executeCommand()
            }
        })

        view.resultPane.text = "$htmlHeader<h2>Welcome to WaterySQL Front!</h2>$htmlTail"

        JFrame("WaterySQL").run {
            contentPane = view.contentPane
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            pack()
            isVisible = true
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    EngineJNI.finish()
                }
            })
        }
    }

    private fun executeCommand() {
        view.runButton.isEnabled = false
        view.editorPane.isEnabled = false
        view.resultPane.text = "Working..."
        resultFile.delete()

        thread {
            EngineJNI.execute(view.editorPane.text)
            view.resultPane.text = "Rendering..."
            view.resultPane.text = if (resultFile.exists())
                "$htmlHeader${resultFile.bufferedReader().readText()}$htmlTail" else
                "Done."
            view.editorPane.isEnabled = true
            view.runButton.isEnabled = true
            EngineJNI.getCurrentDatabaseName().let { curr ->
                view.currentDatabaseLabel.text = if (curr.isNotEmpty())
                    "CURRENT DATABASE: $curr" else
                    "NO DATABASE CURRENTLY IN USE"
            }
            SwingUtilities.invokeAndWait {
                updateFileTree()
            }
        }
    }

    private fun updateFileTree() {
        view.databaseRoot.refresh(basePathFile)
        view.fileTree.run {
            var i = 0
            while (i < rowCount) {
                expandRow(i++)
                updateUI()
            }
        }
    }

}
