package com.gmail.at.zhuikov.aleksandr.rssreader.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Wrapper for {@link URL#openStream()} that isolates network interaction in
 * tests.
 */
public class ConnectionOpener {

	public InputStream getInputStream(URL url) throws IOException {
		return url.openStream();
	}
}