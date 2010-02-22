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
import java.util.Collection;
import java.util.Collections;

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

	/** The L10n handler. */
	private final BaseL10n l10n;

	/** The l10n key for the page title. */
	private final String pageTitleKey;

	/**
	 * Creates a new template page.
	 *
	 * @param path
	 *            The path of the page
	 * @param template
	 *            The template to render
	 * @param l10n
	 *            The L10n handler
	 * @param pageTitleKey
	 *            The l10n key of the title page
	 */
	public TemplatePage(String path, Template template, BaseL10n l10n, String pageTitleKey) {
		this.path = path;
		this.template = template;
		this.l10n = l10n;
		this.pageTitleKey = pageTitleKey;
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
		PageNode pageNode = pageMaker.getPageNode(l10n.getString(pageTitleKey), toadletContext);
		for (String styleSheet : getStyleSheets()) {
			pageNode.addCustomStyleSheet(styleSheet);
		}

		processTemplate(template);
		StringWriter stringWriter = new StringWriter();
		template.render(stringWriter);
		pageNode.content.addChild("%", stringWriter.toString());

		return new Response(200, "OK", "text/html", pageNode.outer.generate());
	}

	/**
	 * Can be overridden to return a custom set of style sheets that are to be
	 * included in the page’s header.
	 *
	 * @return Additional style sheets to load
	 */
	protected Collection<String> getStyleSheets() {
		return Collections.emptySet();
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
