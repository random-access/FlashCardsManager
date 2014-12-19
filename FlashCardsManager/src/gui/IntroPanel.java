package gui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import utils.Logger;

@SuppressWarnings("serial")
public class IntroPanel extends JFrame {
	static BufferedImage imgIntro;

	static {
		try {
			imgIntro = ImageIO.read(ProjectPanel.class.getClassLoader()
					.getResourceAsStream("img/Intro_LearningCards_500x372.png"));
		} catch (IOException e) {
		   JOptionPane.showMessageDialog(null,
               "Ein interner Fehler ist aufgetreten", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(e);
		}
	}
	
	JLabel lblIntroLabel;
	
	IntroPanel() {
		this.setLayout(new BorderLayout());
		lblIntroLabel = new JLabel(new ImageIcon(imgIntro));
		this.add(lblIntroLabel, BorderLayout.CENTER);
		this.setSize(500, 402);
		setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
