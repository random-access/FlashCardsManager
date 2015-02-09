package gui;

import exc.CustomErrorHandling;
import gui.helpers.MyTextPane;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import storage.PicType;
import utils.PicUtils;

@SuppressWarnings("serial")
public class PicAndTextPanel extends JPanel {

	private JLabel lblPic, lblTitle;
	private JPanel pnlTitle;
	private MyTextPane txtPane;
	private HTMLDocument doc;
	private String pathToPic;
	private String txt;
	private PicType type;
	private boolean editable;

	private int customWidth;

	private static final int STEPSIZE = 50;
	private static final int DEFAULT_CARD_WIDTH = 450;
	private static final int DEFAULT_CARD_HEIGHT_TEXT_ONLY = 250;
	private static final int DEFAULT_CARD_HEIGHT_PIC_AND_TEXT = 50;
	private static final int MAX_PIC_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.75);
	private static final int MAX_PIC_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height);

	public PicAndTextPanel(String pathToPic, String txt, PicType type, boolean editable, int customWidth) throws IOException {
		super(new BorderLayout(10, 10));
		this.pathToPic = pathToPic;
		this.txt = txt;
		this.type = type;
		this.editable = editable;
		this.customWidth = customWidth;
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
		if (customWidth == 0) {
			txtPane = (pathToPic == null) ? new MyTextPane(DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT_TEXT_ONLY) : new MyTextPane(
					DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT_PIC_AND_TEXT);
		} else {
			txtPane = (pathToPic == null) ? new MyTextPane(customWidth, DEFAULT_CARD_HEIGHT_TEXT_ONLY) : new MyTextPane(
					customWidth, DEFAULT_CARD_HEIGHT_PIC_AND_TEXT);
		}
		txtPane.setContentType("text/html");
		// correct input non-html text / preformatted html
		if (txt == null || txt.contains("<html>")) {
			txtPane.setDocument(doc);
			txtPane.setText(txt);
		} else {
			try {
				doc.insertString(0, txt, null);
				txtPane.setDocument(doc);
			} catch (BadLocationException e) {
				CustomErrorHandling.showInternalError(null, e);
			}
		}

		txtPane.setEditable(editable);
		txtPane.setBackground(Color.WHITE);
	}

	private void addWidgets() throws IOException {
		this.add(pnlTitle, BorderLayout.NORTH);
		this.add(txtPane, BorderLayout.CENTER);
		pnlTitle.add(lblTitle, BorderLayout.NORTH);
		if (pathToPic != null) {
			addPicture(pathToPic);
		}
	}

	private void setTitle() {
		if (type == null) {
			lblTitle = txt.equals("") ? new JLabel() : new JLabel("Fertig!");
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
		double width = img.getWidth();
		double scaleFactor = 1;
		if (height > MAX_PIC_HEIGHT || width > MAX_PIC_WIDTH) {
			scaleFactor = 1 / (Math.max(height / MAX_PIC_HEIGHT, width / MAX_PIC_WIDTH));
		}
		if (scaleFactor != 1) {
			BufferedImage bmg = PicUtils.scale(img, BufferedImage.TYPE_INT_ARGB, (int) (width * scaleFactor),
					(int) (height * scaleFactor), scaleFactor, scaleFactor);
			return bmg;
		}
		return img;
	}

	public void removePicture() {
		if (pathToPic != null) {
			pathToPic = null;
			pnlTitle.remove(lblPic);
			txtPane.setMinimalHeight(DEFAULT_CARD_HEIGHT_TEXT_ONLY);
			this.revalidate();
		}
	}

	public void addPicture(String pathToPic) throws IOException {
		if (pnlTitle.isAncestorOf(lblPic)) {
			removePicture();
		}
		this.pathToPic = pathToPic;
		BufferedImage img = ImageIO.read(new File(pathToPic));
		lblPic = new JLabel(new ImageIcon(fitPicInCard(img)));
		pnlTitle.add(lblPic, BorderLayout.CENTER);
		txtPane.setMinimalHeight(DEFAULT_CARD_HEIGHT_PIC_AND_TEXT);
		this.revalidate();

	}

	public String getText() {
		return txtPane.getText();
	}

	public void makeLarger() {
		txtPane.setMinimalWidth(txtPane.getWidth() + STEPSIZE);
		txtPane.revalidate();
		txtPane.repaint();
	}

	public void makeSmaller() {
		txtPane.setMinimalWidth(txtPane.getWidth() - STEPSIZE);
		txtPane.revalidate();
		txtPane.repaint();
	}

	public int getCustomWidth() {
		return txtPane.getMinimalWidth();
	}

	public String getPathToPic() {
		return pathToPic;
	}
}
