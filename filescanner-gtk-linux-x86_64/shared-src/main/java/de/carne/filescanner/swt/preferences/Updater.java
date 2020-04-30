/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
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

import de.carne.util.logging.Log;

abstract class Updater {

	private static final Log LOG = new Log();

	public enum Type {

		INSTALL4J("Install4j");

		private final String className;

		private Type(String prefix) {
			Class<Updater> baseClass = Updater.class;
			String oldSimpleName = baseClass.getSimpleName();
			String newSimpleName = prefix + oldSimpleName;

			this.className = baseClass.getName().replace(oldSimpleName, newSimpleName);
		}

		Class<? extends Updater> getUpdaterClass() throws ClassNotFoundException {
			return Class.forName(this.className).asSubclass(Updater.class);
		}

	}

	public enum Schedule {

		NEVER,

		DAILY,

		WEEKLY,

		MONTHLY,

		ALWAYS;

	}

	@Nullable
	private static Updater updaterInstance = null;
	private static boolean sealed = false;

	/**
	 * Gets the default {@linkplain Updater} instance for the current platform.
	 *
	 * @return the default {@linkplain Updater} instance for the current platform or {@code null} if none is available.
	 */
	@Nullable
	public static synchronized Updater getInstance() {
		if (!sealed) {
			for (Type type : Type.values()) {
				updaterInstance = getInstance(type);
				if (updaterInstance != null) {
					break;
				}
			}
			sealed = true;
		}
		return updaterInstance;
	}

	@Nullable
	private static Updater getInstance(Type type) {
		LOG.info("Considering updater {0}...", type);

		Updater updater = null;

		try {
			Updater loaded = type.getUpdaterClass().getConstructor().newInstance();

			if (loaded.isAvailable()) {
				LOG.info("Using updater {0}", type);

				updater = loaded;
			} else {
				LOG.info("Ignoring unavailable updater {0}", type);
			}
		} catch (ReflectiveOperationException | LinkageError e) {
			LOG.info(e, "Ignoring unloadable updater {0}", type);
		}
		return updater;
	}

	protected abstract boolean isAvailable();

	public abstract Schedule getSchedule();

	public abstract void setSchedule(Schedule schedule);

	@Nullable
	public abstract Date getLastCheckDate();

	public abstract void checkNow(UpdaterListener listener) throws IOException;

}
