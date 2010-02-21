/*
 * shortener - TemplatePage.java - Copyright © 2010 David Roden
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

import java.io.StringWriter;

import net.pterodactylus.util.template.Template;
import freenet.clients.http.PageMaker;
import freenet.clients.http.PageNode;
import freenet.clients.http.ToadletContext;
import freenet.l10n.BaseL10n;

/**
 * Base class for all {@link Page}s that are rendered with {@link Template}s.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class TemplatePage implements Page {

	/** The path of the page. */
	private final String path;

	/** The template to render. */
	private final Template template;

	/**
	 * Creates a new template page.
	 *
	 * @param path
	 *            The path of the page
	 * @param template
	 *            The template to render
	 */
	public TemplatePage(String path, Template template) {
		this.path = path;
		this.template = template;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response handleRequest(Request request) {
		ToadletContext toadletContext = request.getToadletContext();
		PageMaker pageMaker = toadletContext.getPageMaker();
		BaseL10n pluginL10n = ShortenerPlugin.l10n.getBase();
		PageNode pageNode = pageMaker.getPageNode(pluginL10n.getString("Page.Index.Title"), toadletContext);
		pageNode.addCustomStyleSheet("css/shortener.css");

		processTemplate(template);
		StringWriter stringWriter = new StringWriter();
		template.render(stringWriter);
		pageNode.content.addChild("%", stringWriter.toString());

		return new Response(200, "OK", "text/html", pageNode.outer.generate());
	}

	/**
	 * Can be overridden when extending classes need to set variables in the
	 * template before it is rendered.
	 *
	 * @param template
	 *            The template to set variables in
	 */
	protected void processTemplate(Template template) {
		/* do nothing. */
	}

}
