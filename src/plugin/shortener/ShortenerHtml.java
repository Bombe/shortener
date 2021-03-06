/*
 * shortener - ShortenerHtml.java - Copyright © 2010 David Roden
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

import java.util.Set;

import plugin.shortener.Shortener.KeyShorteningProgress;
import plugin.shortener.Shortener.ShortenedKey;
import freenet.support.HTMLNode;
import freenet.support.TimeUtil;

/**
 * Contains methods that convert {@link Shortener} information into
 * {@link HTMLNode}s.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class ShortenerHtml {

	/**
	 * Creates an infobox that shows information about currently running key
	 * shortenings.
	 *
	 * @param shortener
	 *            The shortener to get the information from
	 * @return An infobox containing information about currently running key
	 *         shortenings, or an empty node
	 */
	public static HTMLNode getRunningKeyShortenings(Shortener shortener) {
		Set<KeyShorteningProgress> keyShorteningProgresses = shortener.getKeyShorteningProgresses();
		if (keyShorteningProgresses.isEmpty()) {
			return new HTMLNode("#");
		}
		HTMLNode boxNode = new HTMLNode("div", "class", "infobox");
		boxNode.addChild("div", "class", "infobox-header", "Running Key Shortenings");
		HTMLNode boxContentNode = boxNode.addChild("div", "class", "infobox-content");

		long now = System.currentTimeMillis();
		HTMLNode progressTable = boxContentNode.addChild("div", "class", "progress-table");
		HTMLNode headerRow = progressTable.addChild("div", "class", "header");
		headerRow.addChild("div", "class", "original-key", ShortenerPlugin.l10n.getBase().getString("Table.RunningKeys.Header.OriginalKey"));
		headerRow.addChild("div", "class", "current-key", ShortenerPlugin.l10n.getBase().getString("Table.RunningKeys.Header.CurrentKey"));
		headerRow.addChild("div", "class", "duration", ShortenerPlugin.l10n.getBase().getString("Table.RunningKeys.Header.Duration"));
		for (KeyShorteningProgress keyShorteningProgress : keyShorteningProgresses) {
			HTMLNode progressRow = progressTable.addChild("div", "class", "progress-row");
			progressRow.addChild("div", "class", "original-key").addChild("a", "href", "/" + keyShorteningProgress.getOriginalKey(), keyShorteningProgress.getOriginalKey().toString());
			progressRow.addChild("div", "class", "current-key", keyShorteningProgress.getCurrentKey());
			progressRow.addChild("div", "class", "duration", TimeUtil.formatTime(now - keyShorteningProgress.getStartTime(), 2, false));
		}

		return boxNode;
	}

	/**
	 * Creates an infobox that shows all keys that have already been shortened.
	 *
	 * @param shortener
	 *            The shortener to the the information from
	 * @return The infobox with information about shortened keys
	 */
	public static HTMLNode getShortenedKeys(Shortener shortener) {
		Set<ShortenedKey> shortenedKeys = shortener.getShortenedKeys();
		if (shortenedKeys.isEmpty()) {
			return new HTMLNode("#");
		}
		HTMLNode boxNode = new HTMLNode("div", "class", "infobox");
		boxNode.addChild("div", "class", "infobox-header", "Shortened Keys");
		HTMLNode boxContentNode = boxNode.addChild("div", "class", "infobox-content");

		HTMLNode progressTable = boxContentNode.addChild("div", "class", "shortened-table");
		HTMLNode headerRow = progressTable.addChild("div", "class", "header");
		headerRow.addChild("div", "class", "original-key", ShortenerPlugin.l10n.getBase().getString("Table.ShortenedKeys.Header.OriginalKey"));
		headerRow.addChild("div", "class", "shortened-key", ShortenerPlugin.l10n.getBase().getString("Table.ShortenedKeys.Header.ShortenedKey"));
		for (ShortenedKey shortenedKey : shortenedKeys) {
			HTMLNode progressRow = progressTable.addChild("div", "class", "shortened-row");
			progressRow.addChild("div", "class", "original-key").addChild("a", "href", "/" + shortenedKey.getOriginalKey(), shortenedKey.getOriginalKey().toString());
			progressRow.addChild("div", "class", "shortened-key").addChild("a", "href", "/" + shortenedKey.getShortenedKey(), shortenedKey.getShortenedKey().toString());
		}

		return boxNode;
	}

}
