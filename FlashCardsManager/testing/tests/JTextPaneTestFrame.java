package tests;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

@SuppressWarnings("serial")
public class JTextPaneTestFrame extends JFrame {
	private JScrollPane scp;
	private JPanel pnlBottom, pnlTop;
	private JButton btnBold, btnItalic, btnUnderlined, btnLeft, btnRight, btnCenter, btnSave, btnOpen, btnOutput;
	private JTextPane txt;
	private HTMLDocument doc;

	private Action boldAction = new StyledEditorKit.BoldAction();
	private Action italicAction = new StyledEditorKit.ItalicAction();
	private Action underlinedAction = new StyledEditorKit.UnderlineAction();
	private Action leftAction = new StyledEditorKit.AlignmentAction("Left Align", StyleConstants.ALIGN_LEFT);
	private Action rightAction = new StyledEditorKit.AlignmentAction("Right Align", StyleConstants.ALIGN_RIGHT);
	private Action centerAction = new StyledEditorKit.AlignmentAction("Center Align", StyleConstants.ALIGN_CENTER);

	public JTextPaneTestFrame() {
		setTitle("JComponent Testklasse");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		HTMLEditorKit editorKit = new HTMLEditorKit();
		doc = (HTMLDocument) editorKit.createDefaultDocument();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		createWidgets();
		addWidgets();
		setListeners();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void createWidgets() {
		txt = new JTextPane(doc);
		txt.setContentType("text/html");

		scp = new JScrollPane(txt);
		pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlTop = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnBold = new JButton(boldAction);
		btnBold.setText("bold");
		btnItalic = new JButton(italicAction);
		btnItalic.setText("italic");
		btnUnderlined = new JButton(underlinedAction);
		btnUnderlined.setText("underlined");
		btnLeft = new JButton(leftAction);
		btnRight = new JButton(rightAction);
		btnCenter = new JButton(centerAction);
		btnOutput = new JButton("Print plain text");
		btnSave = new JButton("Speichern");
		btnOpen = new JButton("Oeffnen");
	}

	private void addWidgets() {
		add(scp, BorderLayout.CENTER);
		add(pnlTop, BorderLayout.NORTH);
		add(pnlBottom, BorderLayout.SOUTH);
		pnlTop.add(btnBold);
		pnlTop.add(btnItalic);
		pnlTop.add(btnUnderlined);
		pnlTop.add(btnLeft);
		pnlTop.add(btnCenter);
		pnlTop.add(btnRight);
		pnlBottom.add(btnSave);
		pnlBottom.add(btnOpen);
		pnlBottom.add(btnOutput);
	}

	private void setListeners() {
		btnOutput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(txt.getText());
			}
		});

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String pathToSave;
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = jfc.showOpenDialog(JTextPaneTestFrame.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					pathToSave = jfc.getSelectedFile().getAbsolutePath();
					try (FileWriter fw = new FileWriter(pathToSave)) {
						fw.write(txt.getText());
						fw.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					System.out.println("just closing");
				}
				System.out.println("done saving");
			}
		});

		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String pathToOpen;
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnValue = jfc.showOpenDialog(JTextPaneTestFrame.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					pathToOpen = jfc.getSelectedFile().getAbsolutePath();
					try {
						FileReader fr = new FileReader(pathToOpen);
						String str = new String(Files.readAllBytes(Paths.get(pathToOpen)));
						HTMLEditorKit editorKit = new HTMLEditorKit();
						doc = (HTMLDocument) editorKit.createDefaultDocument();
						if (str.contains("<html>")) {
							editorKit.read(fr, doc, 0);
							txt.setDocument(doc);
						} else {
							txt.setDocument(doc);
							doc.insertString(0, str, null);
						}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				} else {
					System.out.println("just closing");
				}
				System.out.println("done saving");
			}
		});

	}

	public static void main(String[] args) {
		// new JTextPaneTestFrame();
		     GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		     // ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("A.ttf")));
		     Font[] fonts = ge.getAllFonts();
		     for (Font f : fonts) {
		    	 System.out.println(f.getFontName());
		     }
		
	}
}
