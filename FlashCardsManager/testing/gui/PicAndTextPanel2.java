package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import utils.PicUtils;
import db.PicType;

@SuppressWarnings("serial")
public class PicAndTextPanel2 extends JPanel {

	private JLabel lblPic, lblTitle;
	private JPanel pnlTitle;
	private MyTextPane txtPane;
	private HTMLDocument doc;
	private BufferedImage img;
	private String txt;
	private PicType type;
	private boolean editable;

	private static final int MAX_PIC_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.75);
	private static final int MAX_PIC_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.85);

	PicAndTextPanel2(BufferedImage img, String txt, PicType type, boolean editable) {
		super(new BorderLayout(10, 10));
		this.img = img;
		this.txt = txt;
		this.type = type;
		this.editable = editable;
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		this.setBackground(Color.WHITE);
		createWidgets();
		addWidgets();

	}

	private void createWidgets() {
		pnlTitle = new JPanel(new BorderLayout());
		pnlTitle.setBackground(Color.WHITE);
		setTitle();
		lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

		HTMLEditorKit editorKit = new HTMLEditorKit();
		doc = (HTMLDocument) editorKit.createDefaultDocument();
		txtPane = new MyTextPane(450, 250);
		txtPane.setContentType("text/html");
		// correctly input non-html text
		if (txt == null || txt.contains("<html>")) {
			txtPane.setDocument(doc);
			txtPane.setText(txt);
		} else {
			try {
				doc.insertString(0, txt, null);
				txtPane.setDocument(doc);				
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		txtPane.setEditable(editable);
		txtPane.setBackground(Color.WHITE);
	}

	private void addWidgets() {
		this.add(pnlTitle, BorderLayout.NORTH);
		this.add(txtPane, BorderLayout.CENTER);
		pnlTitle.add(lblTitle, BorderLayout.NORTH);
		if (img != null) {
			addPicture(img);
		}
	}

	private void setTitle() {
		if (type == null) {
			if (txt.equals("")) {
				lblTitle = new JLabel();
			} else {
				lblTitle = new JLabel("Fertig!");
			}
		} else {
			switch (type) {
			case QUESTION:
				lblTitle = new JLabel("Frage");
				break;
			case ANSWER:
				lblTitle = new JLabel("Antwort");
				break;
			}
		}
	}

	private BufferedImage fitPicInCard(BufferedImage img) {
		double height = img.getHeight();
		System.out.println("original height: " + height);
		double width = img.getWidth();
		System.out.println("original width: " + width);
		double scaleFactor = 1;
		if (height > MAX_PIC_HEIGHT || width > MAX_PIC_WIDTH) {
			scaleFactor = 1 / (Math.max(height / MAX_PIC_HEIGHT, width / MAX_PIC_WIDTH));
		}
		System.out.println("scale factor: " + scaleFactor);
		if (scaleFactor != 1) {
			BufferedImage bmg = PicUtils.scale(img, BufferedImage.TYPE_INT_ARGB, (int) (width * scaleFactor),
					(int) (height * scaleFactor), scaleFactor, scaleFactor);
			System.out.println("new height: " + bmg.getHeight());
			System.out.println("new width: " + bmg.getWidth());
			return bmg;
		}
		return img;
	}

	public void addPicture(BufferedImage img) {
		if (pnlTitle.isAncestorOf(lblPic)) {
			removePicture();
		}
		this.img = img;
		lblPic = new JLabel(new ImageIcon(fitPicInCard(img)));
		pnlTitle.add(lblPic, BorderLayout.CENTER);
		txtPane.setMinimalHeight(50);
		this.revalidate();
	}

	public void removePicture() {
		if (img != null) {
			img = null;
			pnlTitle.remove(lblPic);
			txtPane.setMinimalHeight(250);
			this.revalidate();
		}
	}

	public void addPicture(String picUrl) throws IOException {
		BufferedImage img;
		img = ImageIO.read(new File(picUrl));
		addPicture(img);
	}

	public String getText() {
		return txtPane.getText();
	}

}
