package tests;

import gui.helpers.MyTextPane;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.html.*;

@SuppressWarnings("serial")
public class BulletTest extends JFrame{
	private JScrollPane scp;
	private MyTextPane txt;
	private JButton btnBullets; 
	private JButton btnPlainText;
	private JPanel pnlControls;
	
	public BulletTest () {
		setTitle("JComponent Testklasse");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		HTMLEditorKit kit = new HTMLEditorKit();
		HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
		txt = new MyTextPane(doc,200,150);
		txt.setContentType("text/html");
		txt.setText("<ol>");
		Action bullets = new HTMLEditorKit.InsertHTMLTextAction(
				"Bullets", "<li> </li>", HTML.Tag.OL, HTML.Tag.LI);
		btnBullets = new JButton(bullets);
		btnPlainText = new JButton ("plain text");
		pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER));
		scp = new JScrollPane(txt);
		add(scp, BorderLayout.CENTER);
		add(pnlControls, BorderLayout.NORTH);
		pnlControls.add(btnBullets);
		pnlControls.add(btnPlainText);
		setListeners();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void setListeners() {
		btnPlainText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(txt.getText());
				System.out.println("*********************************************************************");
				
			}
		});
		
		
		
	}
	
	public static void main(String[] args) {
		new BulletTest();
	}
	
}
