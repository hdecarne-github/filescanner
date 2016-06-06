/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.jfx.export;

import java.io.IOException;

import de.carne.jfx.StageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Dialog controller for preference editing.
 */
public class ExportController extends StageController {

	@FXML
	ImageView resultIcon;

	@FXML
	Label resultDescription;

	@FXML
	ChoiceBox<?> exportTypeSelection;

	@FXML
	TextField exportDestinationInput;

	@FXML
	ProgressBar exportProgress;

	@FXML
	void onChooseDestination(ActionEvent evt) {

	}

	@FXML
	void onStart(ActionEvent evt) {
		getStage().close();
	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(I18N.formatSTR_EXPORT_TITLE());
		getStage().sizeToScene();
	}

}
