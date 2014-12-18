package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import db.PicType;

@SuppressWarnings("serial")
public class PicAndTextPanel extends JPanel {

	private JLabel lblPic, lblTitle;
	private JTextArea txtArea;
	private static final int MAX_PIC_WIDTH = (int) (Toolkit.getDefaultToolkit()
			.getScreenSize().width * 0.75);
	private static final int MAX_PIC_HEIGHT = (int) (Toolkit
			.getDefaultToolkit().getScreenSize().height * 0.85);

	public PicAndTextPanel(BufferedImage img, String txt, PicType type) {
		super(new BorderLayout(10, 10));
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		this.setBackground(Color.WHITE);
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
		lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		if (img != null) { // text & pic or pic only
			lblPic = new JLabel(new ImageIcon(fitPicInCard(img)));
			txtArea = new JTextArea(1, 40);
			txtArea.setText(txt);
			txtArea.setEditable(false);
			txtArea.setLineWrap(true);
			txtArea.setWrapStyleWord(true);
			txtArea.setBackground(Color.WHITE);
			this.add(lblTitle, BorderLayout.NORTH);
			this.add(lblPic, BorderLayout.CENTER);
			System.out.println("Bild-Panel:  "
					+ lblPic.getPreferredSize().width + " x "
					+ lblPic.getPreferredSize().height);
			this.add(txtArea, BorderLayout.SOUTH);
		} else { // text only
			txtArea = new JTextArea(15, 40);
			txtArea.setText(txt);
			txtArea.setEditable(false);
			txtArea.setLineWrap(true);
			txtArea.setWrapStyleWord(true);
			txtArea.setBackground(Color.WHITE);
			this.add(lblTitle, BorderLayout.NORTH);
			this.add(txtArea, BorderLayout.CENTER);
		}
	}

	private BufferedImage fitPicInCard(BufferedImage img) {
		double height = img.getHeight();
		System.out.println("original height: " + height);
		double width = img.getWidth();
		System.out.println("original width: " + width);
		double scaleFactor = 1;
		if (height > MAX_PIC_HEIGHT || width > MAX_PIC_WIDTH) {
			scaleFactor = 1 / (Math.max(height / MAX_PIC_HEIGHT, width
					/ MAX_PIC_WIDTH));
		}
		System.out.println("scale factor: " + scaleFactor);
		if (scaleFactor != 1) {
			BufferedImage bmg = scale(img, BufferedImage.TYPE_INT_ARGB,
					(int) (width * scaleFactor), (int) (height * scaleFactor),
					scaleFactor, scaleFactor);
			System.out.println("new height: " + bmg.getHeight());
			System.out.println("new width: " + bmg.getWidth());
			return bmg;
		}
		return img;
	}

	public static BufferedImage scale(BufferedImage sbi, int imageType,
			int dWidth, int dHeight, double fWidth, double fHeight) {
		BufferedImage dbi = null;
		if (sbi != null) {
			dbi = new BufferedImage(dWidth, dHeight, imageType);
			Graphics2D g = dbi.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(fWidth,
					fHeight);
			g.drawRenderedImage(sbi, at);
		}
		return dbi;
	}

}
