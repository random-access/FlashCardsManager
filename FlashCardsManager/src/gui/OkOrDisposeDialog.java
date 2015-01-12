package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import exc.CustomErrorHandling;

@SuppressWarnings("serial")
public class OkOrDisposeDialog extends JDialog {
	
	private JLabel lblText;
	private JPanel pnlControls,pnlCenter;
	private JButton btnOk, btnDiscard;

	public OkOrDisposeDialog(JFrame owner, int width, int height) {
		super(owner, true);
		setLayout(new BorderLayout());
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		 } catch (ClassNotFoundException | InstantiationException
	            | IllegalAccessException | UnsupportedLookAndFeelException exc) {
	         CustomErrorHandling.showInternalError(null, exc);
	      }
		
		createWidgets();
		addWidgets();
		
		btnDiscard.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				OkOrDisposeDialog.this.dispose();
			}
		});
		
		pack();
		setSize (width, height );
		setLocationRelativeTo(owner);
	}

	private void createWidgets() {
		lblText = new JLabel ();
		lblText.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		btnOk = new JButton ("Ok");
		btnDiscard = new JButton ("Abbrechen");
		pnlControls = new JPanel (new FlowLayout(FlowLayout.CENTER));
		pnlCenter = new JPanel (new FlowLayout(FlowLayout.CENTER));
	}

	private void addWidgets() {
		this.add(pnlControls, BorderLayout.SOUTH);
		pnlControls.add(btnDiscard);
		pnlControls.add(btnOk);
		this.add(pnlCenter, BorderLayout.CENTER);
		pnlCenter.add(lblText);
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public void setText(String text) {
		lblText.setText(text);
	}
	
	public void addOkAction(ActionListener l) {
		btnOk.addActionListener(l);
	}

}
