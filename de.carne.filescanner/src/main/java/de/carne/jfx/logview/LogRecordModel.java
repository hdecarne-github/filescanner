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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import de.carne.jfx.beans.TransformationReadOnlyObjectProperty;
import de.carne.util.logging.Log;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * Model class for log record display.
 */
public class LogRecordModel {

	private static final Formatter MESSAGE_FORMATTER = new Formatter() {

		@Override
		public String format(LogRecord record) {
			return formatMessage(record);
		}

	};

	/**
	 * The default {@linkplain DateTimeFormatter} for timestamp formatting.
	 */
	public static final DateTimeFormatter DEFAULT_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.S")
			.withZone(ZoneId.systemDefault());

	/**
	 * The default level image space.
	 */
	public static final double DEFAULT_LEVEL_IMAGE_SPACE = 16.0 * 16.0;

	private final DateTimeFormatter timestampFormatter;

	private final double levelImageSpace;

	private final SimpleObjectProperty<Level> levelProperty = new SimpleObjectProperty<>();

	private TransformationReadOnlyObjectProperty<Level, String> levelNameProperty = null;

	private TransformationReadOnlyObjectProperty<Level, Image> levelImageProperty = null;

	private final SimpleObjectProperty<Instant> timestampProperty = new SimpleObjectProperty<>();

	private TransformationReadOnlyObjectProperty<Instant, String> timestampStringProperty = null;

	private final SimpleStringProperty messageProperty = new SimpleStringProperty();

	/**
	 * Construct {@code LogRecord}.
	 *
	 * @param record The log record.
	 */
	public LogRecordModel(LogRecord record) {
		this(record, DEFAULT_TIMESTAMP_FORMATTER, DEFAULT_LEVEL_IMAGE_SPACE);
	}

	/**
	 * Construct {@code LogRecord}.
	 *
	 * @param record The log record.
	 * @param timestampFormatter The {@linkplain DateTimeFormatter} to use for
	 *        timestamp formatting.
	 * @param levelImageSpace The required level image space.
	 */
	public LogRecordModel(LogRecord record, DateTimeFormatter timestampFormatter, double levelImageSpace) {
		assert record != null;
		assert timestampFormatter != null;

		this.timestampFormatter = timestampFormatter;
		this.levelImageSpace = levelImageSpace;
		this.levelProperty.set(record.getLevel());
		this.timestampProperty.set(Instant.ofEpochMilli(record.getMillis()));
		this.messageProperty.set(MESSAGE_FORMATTER.format(record));
	}

	/**
	 * Get the {@linkplain DateTimeFormatter} used for timestamp formatting.
	 *
	 * @return The {@linkplain DateTimeFormatter} used for timestamp formatting.
	 */
	public DateTimeFormatter getTimestampFormatter() {
		return this.timestampFormatter;
	}

	/**
	 * Get the required level image space.
	 *
	 * @return The required level image space.
	 */
	public double getLevelImageSpace() {
		return this.levelImageSpace;
	}

	/**
	 * Get the log record's level.
	 *
	 * @return The log record's level.
	 */
	public Level getLevel() {
		return this.levelProperty.get();
	}

	/**
	 * Set the log record's level.
	 *
	 * @param level The level to set.
	 */
	public void setLevel(Level level) {
		this.levelProperty.set(level);
	}

	/**
	 * Get the log record's level property.
	 *
	 * @return The log record's level property.
	 */
	public ObjectProperty<Level> levelProperty() {
		return this.levelProperty;
	}

	/**
	 * Get the log record's level string.
	 *
	 * @return The log record's level string.
	 */
	public String getLevelString() {
		return levelStringProperty().get();
	}

	/**
	 * Get the log record's level string property.
	 *
	 * @return The log record's level string property.
	 */
	public ReadOnlyObjectProperty<String> levelStringProperty() {
		if (this.levelNameProperty == null) {
			this.levelNameProperty = new TransformationReadOnlyObjectProperty<>(this.levelProperty,
					l -> Log.levelToString(l));
		}
		return this.levelNameProperty;
	}

	/**
	 * Get the log record's level image.
	 *
	 * @return The log record's level image.
	 */
	public Image getLevelImage() {
		return levelImageProperty().get();
	}

	/**
	 * Get the log record's level image property.
	 *
	 * @return The log record's level image property.
	 */
	public ReadOnlyObjectProperty<Image> levelImageProperty() {
		if (this.levelImageProperty == null) {
			this.levelImageProperty = new TransformationReadOnlyObjectProperty<>(this.levelProperty,
					l -> levelToImage(l));
		}
		return this.levelImageProperty;
	}

	private Image levelToImage(Level level) {
		return LogImages.getImage(Log.snapLevel(level), this.levelImageSpace);
	}

	/**
	 * Get the log record's timestamp.
	 *
	 * @return The log record's timestamp.
	 */
	public Instant getTimestamp() {
		return this.timestampProperty.get();
	}

	/**
	 * Set the log record's timestamp.
	 *
	 * @param timestamp The timestamp to set.
	 */
	public void setTimestamp(Instant timestamp) {
		this.timestampProperty.set(timestamp);
	}

	/**
	 * Get the log record's timestamp property.
	 *
	 * @return The log record's timestamp property.
	 */
	public ObjectProperty<Instant> timestampProperty() {
		return this.timestampProperty;
	}

	/**
	 * Get the log record's timestamp string.
	 *
	 * @return The log record's timestamp string.
	 */
	public String getTimestampString() {
		return this.timestampStringProperty().get();
	}

	/**
	 * Get the log record's timestamp string property.
	 *
	 * @return The log record's timestamp string property.
	 */
	public ReadOnlyObjectProperty<String> timestampStringProperty() {
		if (this.timestampStringProperty == null) {
			this.timestampStringProperty = new TransformationReadOnlyObjectProperty<>(this.timestampProperty,
					ts -> timestampToString(ts));
		}
		return this.timestampStringProperty;
	}

	private String timestampToString(Instant timestamp) {
		return this.timestampFormatter.format(timestamp);
	}

	/**
	 * Get the log record's message.
	 *
	 * @return The log record's message.
	 */
	public String getMessage() {
		return this.messageProperty.get();
	}

	/**
	 * Set the log record's message.
	 *
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.messageProperty.set(message);
	}

	/**
	 * Get the log record's message property.
	 *
	 * @return The log record's message property.
	 */
	public StringProperty messageProperty() {
		return this.messageProperty;
	}

}
