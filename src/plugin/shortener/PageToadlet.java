/*
 * shortener - PageToadlet.java - Copyright © 2010 David Roden
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

import java.io.IOException;
import java.net.URI;
import java.util.Map.Entry;

import plugin.shortener.Page.Request;
import plugin.shortener.Page.Response;
import freenet.client.HighLevelSimpleClient;
import freenet.clients.http.Toadlet;
import freenet.clients.http.ToadletContext;
import freenet.clients.http.ToadletContextClosedException;
import freenet.support.MultiValueTable;
import freenet.support.api.Bucket;
import freenet.support.api.HTTPRequest;
import freenet.support.io.BucketTools;

/**
 * {@link Toadlet} implementation that is wrapped around a {@link Page}.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class PageToadlet extends Toadlet {

	/** The name of the menu item. */
	private final String menuName;

	/** The page that handles processing. */
	private final Page page;

	/**
	 * Creates a new toadlet that hands off processing to a {@link Page}.
	 *
	 * @param highLevelSimpleClient
	 * @param menuName
	 *            The name of the menu item
	 * @param page
	 *            The page to handle processing
	 */
	protected PageToadlet(HighLevelSimpleClient highLevelSimpleClient, String menuName, Page page) {
		super(highLevelSimpleClient);
		this.menuName = menuName;
		this.page = page;
	}

	/**
	 * Returns the name to display in the menu.
	 *
	 * @return The name in the menu
	 */
	public String getMenuName() {
		return menuName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String path() {
		return page.getPath();
	}

	/**
	 * Handles a HTTP GET request.
	 *
	 * @param uri
	 *            The URI of the request
	 * @param httpRequest
	 *            The HTTP request
	 * @param toadletContext
	 *            The toadlet context
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws ToadletContextClosedException
	 *             if the toadlet context is closed
	 */
	public void handleMethodGET(URI uri, HTTPRequest httpRequest, ToadletContext toadletContext) throws IOException, ToadletContextClosedException {
		handleRequest(new Request(uri, "GET", toadletContext));
	}

	/**
	 * Handles a HTTP POST request.
	 *
	 * @param uri
	 *            The URI of the request
	 * @param httpRequest
	 *            The HTTP request
	 * @param toadletContext
	 *            The toadlet context
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws ToadletContextClosedException
	 *             if the toadlet context is closed
	 */
	public void handleMethodPOST(URI uri, HTTPRequest httpRequest, ToadletContext toadletContext) throws IOException, ToadletContextClosedException {
		handleRequest(new Request(uri, "POST", toadletContext));
	}

	/**
	 * Handles a HTTP request.
	 *
	 * @param pageRequest
	 *            The request to handle
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws ToadletContextClosedException
	 *             if the toadlet context is closed
	 */
	private void handleRequest(Request pageRequest) throws IOException, ToadletContextClosedException {
		Response pageResponse = page.handleRequest(pageRequest);
		MultiValueTable<String, String> headers = new MultiValueTable<String, String>();
		if (pageResponse.getHeaders() != null) {
			for (Entry<String, String> headerEntry : pageResponse.getHeaders().entrySet()) {
				headers.put(headerEntry.getKey(), headerEntry.getValue());
			}
		}
		Bucket data = pageRequest.getToadletContext().getBucketFactory().makeBucket(-1);
		if (pageResponse.getContent() != null) {
			BucketTools.copyFrom(data, pageResponse.getContent(), -1);
		}
		writeReply(pageRequest.getToadletContext(), pageResponse.getStatusCode(), pageResponse.getContentType(), pageResponse.getStatusText(), headers, data);
	}

}
