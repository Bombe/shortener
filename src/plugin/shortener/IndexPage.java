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

import net.pterodactylus.util.template.Template;

/**
 * The index page of the shortener plugin.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class IndexPage extends TemplatePage {

	/** The key shortener. */
	private final Shortener shortener;

	/**
	 * Creates a new index page.
	 *
	 * @param shortener
	 *            The key shortener
	 * @param template
	 *            The template to render
	 */
	public IndexPage(Shortener shortener, Template template) {
		super("Index", template);
		this.shortener = shortener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processTemplate(Template template) {
		template.set("inProgressKeys", shortener.getKeyShorteningProgresses());
		template.set("shortenedKeys", shortener.getShortenedKeys());
	}

}
