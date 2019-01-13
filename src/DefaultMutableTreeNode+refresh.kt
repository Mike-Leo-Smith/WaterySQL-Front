import java.io.File
import javax.swing.tree.DefaultMutableTreeNode

fun DefaultMutableTreeNode.refresh(basePath: File) {
    removeAllChildren()
    basePath.listFiles { file ->
        file.isDirectory && file.extension == "db"
    }?.map { databaseFile ->
        Pair(DefaultMutableTreeNode("Tables"), DefaultMutableTreeNode("Indexes")).apply {
            databaseFile.listFiles { file ->
                file.isFile && (file.extension == "tab")
            }?.map { tableFile ->
                DefaultMutableTreeNode(tableFile.nameWithoutExtension)
            }?.forEach { tableNode -> first.add(tableNode) }
            databaseFile.listFiles { file ->
                file.isFile && (file.extension == "idx")
            }?.map { tableFile ->
                DefaultMutableTreeNode(tableFile.nameWithoutExtension)
            }?.forEach { tableNode ->
                second.add(tableNode)
            }
        }.run {
            DefaultMutableTreeNode(databaseFile.nameWithoutExtension).apply {
                add(first)
                add(second)
            }
        }
    }?.forEach { databaseNode ->
        add(databaseNode)
    }
}
