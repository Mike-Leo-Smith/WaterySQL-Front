import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class View {
    
    JPanel contentPane;
    JTree fileTree;
    JButton runButton;
    JTextPane editorPane;
    JTextPane resultPane;
    JLabel currentDatabaseLabel;
    DefaultMutableTreeNode databaseRoot;
    
    private void createUIComponents() {
        databaseRoot = new DefaultMutableTreeNode("Databases");
        fileTree = new JTree(databaseRoot);
    }
    
}
