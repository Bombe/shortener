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

import java.io.InputStreamReader;
import java.io.StringWriter;

import net.pterodactylus.util.template.Template;
import plugin.shortener.Shortener.KeyShorteningProgress;
import plugin.shortener.Shortener.ShortenedKey;
import freenet.clients.http.PageMaker;
import freenet.clients.http.PageNode;
import freenet.clients.http.ToadletContext;
import freenet.l10n.BaseL10n;

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
		return "Index";
	}

	/**
	 * {@inheritDoc}
	 */
	public Response handleRequest(Request request) {
		ToadletContext toadletContext = request.getToadletContext();
		PageMaker pageMaker = toadletContext.getPageMaker();
		BaseL10n pluginL10n = ShortenerPlugin.l10n.getBase();
		PageNode pageNode = pageMaker.getPageNode(pluginL10n.getString("Page.Index.Title"), toadletContext);
		pageNode.addCustomStyleSheet("css/shortener.css");

		try {
			Template template = new Template(new InputStreamReader(getClass().getResourceAsStream("/plugin/shortener/html/Index.html")));
			template.addAccessor(KeyShorteningProgress.class, new Shortener.KeyShorteningProgressAccessor());
			template.addAccessor(ShortenedKey.class, new Shortener.ShortenedKeyAccessor());
			template.set("inProgressKeys", shortener.getKeyShorteningProgresses());
			template.set("shortenedKeys", shortener.getShortenedKeys());
			template.set("formPassword", formPassword);
			StringWriter stringWriter = new StringWriter();
			template.render(stringWriter);
			pageNode.content.addChild("%", stringWriter.toString());
		} catch (Throwable t1) {
			return new Response(500, "Internal Server Error", "text/plain", t1.getMessage());
		}

		return new Response(200, "OK", "text/html", pageNode.outer.generate());
	}

}
