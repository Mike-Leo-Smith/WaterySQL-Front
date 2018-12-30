import javax.swing.*;

public class App {
    
    private JPanel contentPane;
    private JTable resultTable;
    private JTree fileTree;
    private JButton runButton;
    private JTextPane editor;
    
    
    
    public static void main(String[] args) {
        var frame = new JFrame("Hello");
        frame.setContentPane(new App().contentPane);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
}
