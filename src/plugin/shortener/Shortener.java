/*
 * shortener - Shortener.java - Copyright © 2010 David Roden
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

package plugin.shortener;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.pterodactylus.util.template.Accessor;
import freenet.client.HighLevelSimpleClient;
import freenet.client.InsertException;
import freenet.keys.FreenetURI;
import freenet.support.Base64;
import freenet.support.Executor;
import freenet.support.TimeUtil;

/**
 * This class bundles the real functionality, i.e. shortening keys and telling
 * the rest of the plugin about it.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class Shortener {

	/** The already shortened keys. */
	private final Map<String, ShortenedKey> shortenedKeys = new LinkedHashMap<String, ShortenedKey>();

	/** The keys that are currently shortened. */
	private final Set<KeyShorteningProgress> keyShorteningProgresses = new HashSet<KeyShorteningProgress>();

	/** The node’s executor. */
	private final Executor nodeExecutor;

	/** The node’s high-level simple client. */
	private final HighLevelSimpleClient highLevelSimpleClient;

	/**
	 * Creates a new key shortener.
	 *
	 * @param nodeExecutor
	 *            The node’s executor
	 * @param highLevelSimpleClient
	 *            The node’s high-level simple client
	 */
	public Shortener(Executor nodeExecutor, HighLevelSimpleClient highLevelSimpleClient) {
		this.nodeExecutor = nodeExecutor;
		this.highLevelSimpleClient = highLevelSimpleClient;
	}

	//
	// ACCESSORS
	//

	/**
	 * Returns progress information about all currently running key shortenings.
	 *
	 * @return Progress information about the current key shortenings
	 */
	public Set<KeyShorteningProgress> getKeyShorteningProgresses() {
		return new HashSet<KeyShorteningProgress>(keyShorteningProgresses);
	}

	/**
	 * Returns all shortened keys.
	 *
	 * @return The shortened keys
	 */
	public Set<ShortenedKey> getShortenedKeys() {
		return new HashSet<ShortenedKey>(shortenedKeys.values());
	}

	//
	// ACTIONS
	//

	/**
	 * Shortens the given key.
	 *
	 * @param key
	 *            The key to shorten
	 * @throws MalformedURLException
	 *             if the key is not a valid Freenet URI
	 */
	public void shortenKey(final String key) throws MalformedURLException {
		final KeyShorteningProgress keyShorteningProgress = new KeyShorteningProgress(key);
		final FreenetURI originalKey = new FreenetURI(key);
		keyShorteningProgresses.add(keyShorteningProgress);
		nodeExecutor.execute(new Runnable() {

			@SuppressWarnings("synthetic-access")
			public void run() {
				boolean inserted = false;
				int length = 0;
				FreenetURI shortenedKey = null;
				do {
					if (++length == 43) {
						break;
					}
					try {
						String shortenedKeyString = "KSK@" + Base64.encode(originalKey.getRoutingKey()).substring(0, length);
						keyShorteningProgress.setCurrentKey(shortenedKeyString);
						shortenedKey = new FreenetURI(shortenedKeyString);
						highLevelSimpleClient.insertRedirect(shortenedKey, originalKey);
						inserted = true;
					} catch (MalformedURLException mue1) {
						/* TODO - now this should not be happening. */
					} catch (InsertException ie1) {
						/* TODO - check for collisions */
					}
				} while (!inserted);
				if (inserted) {
					keyShorteningProgresses.remove(keyShorteningProgress);
					shortenedKeys.put(key, new ShortenedKey(originalKey, shortenedKey));
				}
			}
		}, "Shortening Key: " + originalKey);
	}

	/**
	 * Container for key shortenings progress information.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
	public class KeyShorteningProgress {

		/** The start time of the key shortening. */
		private final long startTime = System.currentTimeMillis();

		/** The original key. */
		private final String originalKey;

		/** The key that is currently being tried. */
		private String currentKey;

		/**
		 * Creates a new progress information container.
		 *
		 * @param originalKey
		 *            The original key
		 */
		public KeyShorteningProgress(String originalKey) {
			this.originalKey = originalKey;
		}

		/**
		 * Returns the time the key shortening process was started.
		 *
		 * @return The start time of the key shortening
		 */
		public long getStartTime() {
			return startTime;
		}

		/**
		 * The original key that is being shortened.
		 *
		 * @return The original key
		 */
		public String getOriginalKey() {
			return originalKey;
		}

		/**
		 * Returns the shortened key that is currently being tried.
		 *
		 * @return The key currently being tried
		 */
		public String getCurrentKey() {
			return currentKey;
		}

		/**
		 * Sets the key currently being tried.
		 *
		 * @param currentKey
		 *            The key currently being tried
		 */
		void setCurrentKey(String currentKey) {
			this.currentKey = currentKey;
		}

	}

	/**
	 * Container for the original and the shortened key.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
	public class ShortenedKey {

		/** The original key. */
		private final FreenetURI originalKey;

		/** The shortened key. */
		private final FreenetURI shortenedKey;

		/**
		 * Creates a new shortened key container.
		 *
		 * @param originalKey
		 *            The original key
		 * @param shortenedKey
		 *            The shortened key
		 */
		public ShortenedKey(FreenetURI originalKey, FreenetURI shortenedKey) {
			this.originalKey = originalKey;
			this.shortenedKey = shortenedKey;
		}

		/**
		 * Returns the original key.
		 *
		 * @return The original key
		 */
		public FreenetURI getOriginalKey() {
			return originalKey;
		}

		/**
		 * Returns the shortened key.
		 *
		 * @return The shortened key
		 */
		public FreenetURI getShortenedKey() {
			return shortenedKey;
		}

	}

	/**
	 * {@link Accessor} implementation that returns all fields from a
	 * {@link KeyShorteningProgress}.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
	public static class KeyShorteningProgressAccessor implements Accessor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object get(Object object, String member) {
			KeyShorteningProgress keyShorteningProgress = (KeyShorteningProgress) object;
			if ("originalKey".equals(member)) {
				return keyShorteningProgress.getOriginalKey();
			} else if ("currentKey".equals(member)) {
				return keyShorteningProgress.getCurrentKey();
			} else if ("duration".equals(member)) {
				return TimeUtil.formatTime(System.currentTimeMillis() - keyShorteningProgress.getStartTime(), 2, false);
			}
			return null;
		}

	}

	/**
	 * {@link Accessor} implementation that returns all needed files from a
	 * {@link ShortenedKey}.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
	public static class ShortenedKeyAccessor implements Accessor {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object get(Object object, String member) {
			ShortenedKey shortenedKey = (ShortenedKey) object;
			if ("originalKey".equals(member)) {
				return shortenedKey.getOriginalKey();
			} else if ("shortenedKey".equals(member)) {
				return shortenedKey.getShortenedKey();
			}
			return null;
		}

	}

}
