/*
 * Copyright (c) 2007-2022 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.swt.preferences;

import java.io.IOException;
import java.util.Date;

import org.eclipse.jdt.annotation.Nullable;

import com.install4j.api.launcher.ApplicationLauncher;
import com.install4j.api.update.UpdateSchedule;
import com.install4j.api.update.UpdateScheduleRegistry;

class Install4jUpdater extends Updater {

	private static final String UPDATER_APPLICATION_ID = "440";

	public Install4jUpdater() {
		// to make this class available for reflective loading
	}

	@Override
	protected boolean isAvailable() {
		return true;
	}

	@Override
	public Schedule getSchedule() {
		UpdateSchedule updateSchedule = UpdateScheduleRegistry.getUpdateSchedule();

		if (updateSchedule == null) {
			updateSchedule = UpdateSchedule.NEVER;
		}

		Schedule schedule;

		switch (updateSchedule) {
		case NEVER:
			schedule = Schedule.NEVER;
			break;
		case DAILY:
			schedule = Schedule.DAILY;
			break;
		case WEEKLY:
			schedule = Schedule.WEEKLY;
			break;
		case MONTHLY:
			schedule = Schedule.MONTHLY;
			break;
		case ON_EVERY_START:
			schedule = Schedule.ALWAYS;
			break;
		default:
			schedule = Schedule.NEVER;
		}
		return schedule;
	}

	@Override
	public void setSchedule(Schedule schedule) {
		UpdateSchedule updateSchedule;

		switch (schedule) {
		case NEVER:
			updateSchedule = UpdateSchedule.NEVER;
			break;
		case DAILY:
			updateSchedule = UpdateSchedule.DAILY;
			break;
		case WEEKLY:
			updateSchedule = UpdateSchedule.WEEKLY;
			break;
		case MONTHLY:
			updateSchedule = UpdateSchedule.MONTHLY;
			break;
		case ALWAYS:
			updateSchedule = UpdateSchedule.ON_EVERY_START;
			break;
		default:
			updateSchedule = UpdateSchedule.NEVER;
		}
		UpdateScheduleRegistry.setUpdateSchedule(updateSchedule);
	}

	@Override
	@Nullable
	public Date getLastCheckDate() {
		return UpdateScheduleRegistry.getLastUpdateCheckDate();
	}

	@Override
	public void checkNow(UpdaterListener listener) throws IOException {
		listener.onUpdaterStarted();
		try {
			ApplicationLauncher.launchApplication(UPDATER_APPLICATION_ID, null, false,
					new ApplicationLauncher.Callback() {

						@Override
						public void prepareShutdown() {
							listener.onPrepareShutdown();
						}

						@Override
						public void exited(int exitValue) {
							listener.onUpdaterFinished(exitValue);
						}

					});
			UpdateScheduleRegistry.checkedForUpdate();
		} catch (IOException | RuntimeException e) {
			listener.onUpdaterFinished(-1);
			throw e;
		}
	}

}
