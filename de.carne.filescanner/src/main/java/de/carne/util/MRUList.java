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
package de.carne.util;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Utility class providing MRU (most recently used) list.
 *
 * @param <K> The list's key type.
 * @param <V> The list's value type.
 */
public class MRUList<K, V> extends LinkedList<MRUList.Entry<K, V>> {

	private static final long serialVersionUID = 1L;

	/**
	 * MRU list entry object.
	 *
	 * @param <K> The list's key type.
	 * @param <V> The list's value type.
	 */
	public static final class Entry<K, V> {

		private final K key;

		private final V value;

		Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * Get the entry key.
		 *
		 * @return The entry key.
		 */
		public K getKey() {
			return this.key;
		}

		/**
		 * Get the entry value.
		 *
		 * @return The entry value.
		 */
		public V getValue() {
			return this.value;
		}

	}

	private final int maxSize;

	/**
	 * Construct {@code MRUList}.
	 *
	 * @param maxSize The maximum size of the MRU list.
	 */
	public MRUList(int maxSize) {
		assert maxSize > 0;

		this.maxSize = maxSize;
	}

	/**
	 * Use MRU list entry.
	 * <p>
	 * If the requested entry is in the list, it is moved to the front of the
	 * list.
	 * </p>
	 *
	 * @param key The key identifying the value to use.
	 * @return The found value or {@code null} if the key is not in the list.
	 */
	public synchronized V use(K key) {
		assert key != null;

		Entry<K, V> usedEntry = null;
		Iterator<Entry<K, V>> entryIterator = iterator();

		while (entryIterator.hasNext()) {
			Entry<K, V> entry = entryIterator.next();

			if (key.equals(entry.getKey())) {
				entryIterator.remove();
				addFirst(entry);
				usedEntry = entry;
				break;
			}
		}
		return (usedEntry != null ? usedEntry.getValue() : null);
	}

	/**
	 * Use MRU list entry.
	 * <p>
	 * Add a new entry to the list. If the extended list exceeds the maximum
	 * size limit, the least recently used entry is removed from the list.
	 * </p>
	 *
	 * @param key The key to add.
	 * @param value The value to add.
	 */
	public synchronized void use(K key, V value) {
		addFirst(new Entry<>(key, value));
		if (size() > this.maxSize) {
			removeLast();
		}
	}

}
