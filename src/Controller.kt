import java.awt.event.*
import java.nio.file.Paths
import javax.swing.*
import kotlin.concurrent.thread

class Controller {

    private val view = View()
    private val basePathFile = Paths.get("watery-db").toAbsolutePath().toFile()
    private val resultFile = Paths.get("watery-db/result.html").toAbsolutePath().toFile()

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
            SwingUtilities.invokeAndWait {
                view.resultPane.text = "Done."
                resultFile.takeIf { file -> file.exists() }?.apply {
                    view.resultPane.text = bufferedReader().readText()
                    delete()
                }
                view.editorPane.isEnabled = true
                view.runButton.isEnabled = true
                updateFileTree()
            }
        }
    }

    private fun updateFileTree() {
        view.databaseRoot.refresh(basePathFile)
        var i = 0
        while (i < view.fileTree.rowCount) {
            view.fileTree.expandRow(i++)
            view.fileTree.updateUI()
        }
    }

}
