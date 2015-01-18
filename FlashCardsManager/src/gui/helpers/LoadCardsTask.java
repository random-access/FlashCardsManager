package gui.helpers;

import exc.CustomErrorHandling;
import gui.ProjectPanel;

import java.awt.Component;
import java.awt.Cursor;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import core.LearningProject;

public class LoadCardsTask extends SwingWorker<Void, Void> implements IProgressPresenter {
	ProgressDialog dialog;
	Component owner;
	ProjectPanel source;
	LearningProject proj;
	ProjectPanel.DialogType type;

	public LoadCardsTask(ProgressDialog dialog, Component owner, ProjectPanel source, LearningProject proj,
			ProjectPanel.DialogType type) {
		this.dialog = dialog;
		this.owner = owner;
		this.source = source;
		this.proj = proj;
		this.type = type;
	}

	public void changeProgress(int progress) {
		super.setProgress(progress);
	}

	@Override
	public void changeInfo(String text) {
		dialog.changeInfo(text);
	}

	@Override
	protected Void doInBackground() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
		});
		setProgress(0);
		try {
			proj.loadLabelsAndFlashcards(this);
			setProgress(100);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					dialog.dispose();
				}
			});
		} catch (SQLException sqle) {
			CustomErrorHandling.showDatabaseError(owner, sqle);
		} finally {
			owner.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		return null;
	}

	@Override
	public void done() {
		source.resume(type);
	}
}
