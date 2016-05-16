/*
 * Copyright (c) 2007-2016 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.jfx.logview;

import java.io.IOException;

import de.carne.jfx.StageController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Helper class for binding the showing/hidding of the log view to an user
 * controlled {@linkplain BooleanProperty} (e.g. the selected property of a
 * {@code CheckMenuItem}.
 */
public class LogViewTriggerProperty extends ObjectPropertyBase<Boolean> {

	private final StageController parent;
	private LogViewController logView = null;

	/**
	 * Construct {@code LogViewTriggerProperty}.
	 * 
	 * @param parent The stage controller to use for log view showing.
	 */
	public LogViewTriggerProperty(StageController parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.beans.property.ReadOnlyProperty#getBean()
	 */
	@Override
	public Object getBean() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.beans.property.ReadOnlyProperty#getName()
	 */
	@Override
	public String getName() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.beans.property.ObjectPropertyBase#set(java.lang.Object)
	 */
	@Override
	public void set(Boolean newValue) {
		if (Boolean.TRUE.equals(newValue) && this.logView == null) {
			try {
				this.logView = this.parent.openStage(LogViewController.class);
				this.logView.getStage().showingProperty().addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> showingObservable, Boolean oldShowingValue,
							Boolean newShowingValue) {
						if (Boolean.FALSE.equals(newShowingValue)) {
							onLogViewHidding();
						}
					}
				});
				this.logView.getStage().show();
			} catch (IOException e) {
				this.parent.reportUnexpectedException(e);
			}
		} else if (!Boolean.TRUE.equals(newValue) && this.logView != null) {
			this.logView.getStage().close();
		}
		super.set(newValue);
	}

	void onLogViewHidding() {
		this.logView = null;
		super.set(Boolean.FALSE);
	}

}
