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
package de.carne.util.prefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import de.carne.util.logging.Log;

/**
 * Property file based {@linkplain Preferences} implementation.
 */
class PropertiesPreferences extends AbstractPreferences {

	private static final Log LOG = new Log(PropertiesPreferences.class);

	private static final String KEY_SEPARATOR = "/";

	private final Path propertiesPath;
	private Properties properties;

	PropertiesPreferences(Path propertiesPath) {
		super(null, "");
		this.propertiesPath = propertiesPath.toAbsolutePath();
		this.properties = loadProperties(this.propertiesPath);
	}

	private PropertiesPreferences(PropertiesPreferences parent, String name) {
		super(parent, name);
		this.propertiesPath = parent.propertiesPath;
		this.properties = parent.properties;
	}

	@Override
	protected void putSpi(String key, String value) {
		synchronized (this.properties) {
			this.properties.put(nodePath() + key, value);
		}
	}

	@Override
	protected String getSpi(String key) {
		String value;

		synchronized (this.properties) {
			value = this.properties.getProperty(nodePath() + key);
		}
		return value;
	}

	@Override
	protected void removeSpi(String key) {
		synchronized (this.properties) {
			this.properties.remove(nodePath() + key);
		}
	}

	@Override
	protected void removeNodeSpi() throws BackingStoreException {
		synchronized (this.properties) {
			removeNodePath(this.properties, nodePath());
		}
	}

	@Override
	protected String[] keysSpi() throws BackingStoreException {
		String[] keys;

		synchronized (this.properties) {
			keys = getNodeKeys(this.properties, nodePath());
		}
		return keys;
	}

	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException {
		String[] children;

		synchronized (this.properties) {
			children = getNodeChildren(this.properties, nodePath());
		}
		return children;
	}

	@Override
	protected AbstractPreferences childSpi(String name) {
		return new PropertiesPreferences(this, name);
	}

	@Override
	protected void syncSpi() throws BackingStoreException {
		synchronized (this.properties) {
			mergeProperties(this.properties, nodePath(), this.propertiesPath);
		}
	}

	@Override
	protected void flushSpi() throws BackingStoreException {
		// Nothing to do here
	}

	private static void mergeProperties(Properties properties, String nodePath, Path propertiesPath)
			throws BackingStoreException {
		LOG.info(null, "Merging preferences ''{0}'' to: ''{1}''", nodePath, propertiesPath);
		try {
			Files.createDirectories(propertiesPath.getParent());
		} catch (IOException e) {
			throw new BackingStoreException(e);
		}

		Properties mergedProperties = new Properties();

		if (Files.exists(propertiesPath)) {
			try (InputStream propertiesStream = Files.newInputStream(propertiesPath, StandardOpenOption.READ)) {
				mergedProperties.load(propertiesStream);
			} catch (IOException e) {
				throw new BackingStoreException(e);
			}
		}
		removeNodePath(mergedProperties, nodePath);

		Iterator<Map.Entry<Object, Object>> entryIterator = properties.entrySet().iterator();

		while (entryIterator.hasNext()) {
			Map.Entry<Object, Object> entry = entryIterator.next();
			String key = entry.getKey().toString();

			if (getNodeKey(nodePath, key) != null) {
				mergedProperties.put(key, entry.getValue());
			}
		}
		try (OutputStream propertiesStream = Files.newOutputStream(propertiesPath, StandardOpenOption.CREATE,
				StandardOpenOption.WRITE)) {
			mergedProperties.store(propertiesStream, null);
		} catch (IOException e) {
			throw new BackingStoreException(e);
		}
	}

	private static Properties loadProperties(Path propertiesPath) {
		Properties properties = new Properties();

		if (Files.exists(propertiesPath)) {
			LOG.info(null, "Loading preferences from: ''{0}''", propertiesPath);
			try (InputStream propertiesStream = Files.newInputStream(propertiesPath, StandardOpenOption.READ)) {
				properties.load(propertiesStream);
			} catch (IOException e) {
				LOG.warning(e, null, "Unable to load preferences from: ''{0}''", propertiesPath);
			}
		}
		return properties;
	}

	private static void removeNodePath(Properties properties, String nodePath) {
		Iterator<Object> keyIterator = properties.keySet().iterator();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next().toString();

			if (getNodeKey(nodePath, key) != null) {
				keyIterator.remove();
			}
		}
	}

	private static String[] getNodeChildren(Properties properties, String nodePath) {
		HashSet<String> nodeChildren = new HashSet<>(properties.size());
		Iterator<Object> keyIterator = properties.keySet().iterator();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next().toString();
			String nodeChild = getNodeChild(nodePath, key);

			if (nodeChild != null) {
				nodeChildren.add(nodeChild);
			}
		}
		return nodeChildren.toArray(new String[nodeChildren.size()]);
	}

	private static String getNodeChild(String nodePath, String key) {
		String nodeChild = null;

		if (key.startsWith(nodePath)) {
			int childEndIndex = key.indexOf(KEY_SEPARATOR, nodePath.length());

			if (childEndIndex > 0) {
				nodeChild = key.substring(nodePath.length(), childEndIndex);
			}
		}
		return nodeChild;
	}

	private static String[] getNodeKeys(Properties properties, String nodePath) {
		ArrayList<String> nodeKeys = new ArrayList<>(properties.size());
		Iterator<Object> keyIterator = properties.keySet().iterator();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next().toString();
			String nodeKey = getNodeKey(nodePath, key);

			if (nodeKey != null) {
				nodeKeys.add(nodeKey);
			}
		}
		return nodeKeys.toArray(new String[nodeKeys.size()]);
	}

	private static String getNodeKey(String nodePath, String key) {
		String nodeKey = null;

		if (key.startsWith(nodePath) && key.indexOf(KEY_SEPARATOR, nodePath.length()) < 0) {
			nodeKey = key.substring(nodePath.length());
		}
		return nodeKey;
	}

	private String nodePath() {
		return absolutePath() + KEY_SEPARATOR;
	}

}
