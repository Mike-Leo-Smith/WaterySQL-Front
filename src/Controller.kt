import java.awt.event.*
import java.io.File
import java.nio.file.Paths
import javax.swing.*
import kotlin.concurrent.thread

class Controller {

    val view = View()
    val basePath = Paths.get("watery-db").toAbsolutePath().toFile()

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
        thread {
            EngineJNI.execute(view.editorPane.text)
            view.resultPane.text = "Done."
            File("result.html").takeIf { file ->
                file.exists()
            }?.run {
                view.resultPane.text = bufferedReader().readText()
                delete()
            }
            view.editorPane.isEnabled = true
            view.runButton.isEnabled = true

            updateFileTree()
        }
    }

    private fun updateFileTree() {
        view.databaseRoot.refresh(basePath)
        var i = 0
        while (i < view.fileTree.rowCount) {
            view.fileTree.expandRow(i++)
        }
    }

}
