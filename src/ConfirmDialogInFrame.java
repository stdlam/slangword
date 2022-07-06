import java.awt.Color;

import javax.swing.JFrame;

public class ConfirmDialogInFrame extends JFrame {
	public ConfirmDialogInFrame() {
        getContentPane().setBackground(Color.DARK_GRAY);
        setTitle("Confirm Dialog in Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setSize(400, 300);
        getContentPane().setLayout(null);
    }
}
