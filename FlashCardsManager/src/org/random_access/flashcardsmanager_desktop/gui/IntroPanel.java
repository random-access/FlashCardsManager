package org.random_access.flashcardsmanager_desktop.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;

@SuppressWarnings("serial")
public class IntroPanel extends JFrame {
	private static BufferedImage imgIntro, imgIcon36x36, imgIcon24x24, imgIcon16x16, imgIcon12x12;

	static {
		try {
			imgIntro = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
					"org.random_access.flashcardsmanager_desktop.img/Intro_LearningCards_blue_500x372.png"));
			imgIcon36x36 = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
					"org.random_access.flashcardsmanager_desktop.img/Label_LearningCards_blue_36x36.png"));
			imgIcon24x24 = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
					"org.random_access.flashcardsmanager_desktop.img/Label_LearningCards_blue_24x24.png"));
			imgIcon16x16 = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
					"org.random_access.flashcardsmanager_desktop.img/Label_LearningCards_blue_16x16.png"));
			imgIcon12x12 = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
					"org.random_access.flashcardsmanager_desktop.img/Label_LearningCards_blue_12x12.png"));
		} catch (IOException ioe) {
			CustomErrorHandling.showInternalError(null, ioe);
		}
	}

	private JLabel lblIntroLabel;
	private LinkedList<Image> icons;

	public IntroPanel() {
		icons = new LinkedList<Image>();
		icons.add(imgIcon12x12);
		icons.add(imgIcon16x16);
		icons.add(imgIcon24x24);
		icons.add(imgIcon36x36);
		setIconImages(icons);
		this.setLayout(new BorderLayout());
		this.setUndecorated(true);
		lblIntroLabel = new JLabel(new ImageIcon(imgIntro));
		this.add(lblIntroLabel, BorderLayout.CENTER);
		this.setSize(500, 372);
		setLocationRelativeTo(null);
		this.setVisible(true);
	}

}
