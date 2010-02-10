/*
 * shortener - IndexPage.java - Copyright © 2010 David Roden
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

import freenet.clients.http.PageMaker;
import freenet.clients.http.PageNode;
import freenet.clients.http.ToadletContext;
import freenet.l10n.BaseL10n;
import freenet.support.HTMLNode;

/**
 * The index page of the shortener plugin.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class IndexPage implements Page {

	/** The key shortener. */
	private final Shortener shortener;

	/** The form password. */
	private final String formPassword;

	/**
	 * Creates a new index page.
	 *
	 * @param shortener
	 *            The key shortener
	 * @param formPassword
	 *            The form password
	 */
	public IndexPage(Shortener shortener, String formPassword) {
		this.shortener = shortener;
		this.formPassword = formPassword;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPath() {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public Response handleRequest(Request request) {
		ToadletContext toadletContext = request.getToadletContext();
		PageMaker pageMaker = toadletContext.getPageMaker();
		BaseL10n pluginL10n = ShortenerPlugin.l10n.getBase();
		PageNode pageNode = pageMaker.getPageNode(pluginL10n.getString("Page.Index.Title"), toadletContext);

		pageNode.content.addChild(ShortenerHtml.getRunningKeyShortenings(shortener));

		HTMLNode inputBox = pageNode.content.addChild("div", "class", "infobox");
		inputBox.addChild("div", "class", "infobox-header", "Shorten Key");
		HTMLNode inputForm = inputBox.addChild("div", "class", "infobox-content").addChild("form", new String[] { "action", "method" }, new String[] { "Shorten", "post" });
		inputForm.addChild("input", new String[] { "type", "name" }, new String[] { "text", "key" });
		inputForm.addChild("input", new String[] { "type", "value" }, new String[] { "submit", "Shorten" });

		return new Response(200, "OK", "text/html", pageNode.outer.generate());
	}

}
