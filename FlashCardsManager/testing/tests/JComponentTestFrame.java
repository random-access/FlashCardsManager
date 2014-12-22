package tests;

import java.awt.BorderLayout;

import javax.swing.*;

@SuppressWarnings("serial")
public class JComponentTestFrame extends JFrame{
	private JScrollPane scp;
	
	public JComponentTestFrame (JComponent c) {
		setTitle("JComponent Testklasse");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		scp = new JScrollPane(c);
		add(scp, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
