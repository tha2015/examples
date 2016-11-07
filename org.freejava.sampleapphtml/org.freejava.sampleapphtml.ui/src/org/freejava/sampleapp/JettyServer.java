package org.freejava.sampleapp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
//import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class JettyServer {
	private static String host;
	private static int port = 0;
	private static final int AUTO_SELECT_JETTY_PORT = 0;

	public static int start(String webappName) throws Exception {
		//Dictionary d = new Hashtable();
		//d.put("http.port", new Integer(getPortParameter())); //$NON-NLS-1$
		// set the base URL
		//d.put("context.path", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		//d.put("other.info", "freejava"); //$NON-NLS-1$ //$NON-NLS-2$
		//Logger.getLogger("org.mortbay").setLevel(Level.WARNING);
		//JettyConfigurator.startServer(webappName, d);
		checkBundle();
		return port;
	}

	private static void selectPort() {
		if (port <= 0) {
			try {
			// Create a new server socket
			ServerSocketChannel _acceptChannel = ServerSocketChannel.open();
			// Set to blocking mode
			_acceptChannel.configureBlocking(true);

			// Bind the server socket to the local host and port
			_acceptChannel.socket().setReuseAddress(true);
			InetSocketAddress addr = getHost() == null ? new InetSocketAddress(
					getPort()) : new InetSocketAddress(getHost(), getPort());
			_acceptChannel.socket().bind(addr, 1);

			port = _acceptChannel.socket().getLocalPort();
			if (port <= 0)
				throw new IOException("Server channel not bound");
			// Set to non blocking mode
			_acceptChannel.configureBlocking(false);
			_acceptChannel.socket().close();
			_acceptChannel.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Ensures that the bundle with the specified name and the highest available
	 * version is started and reads the port number
	 */
	private static void checkBundle() throws InvalidSyntaxException, BundleException  {




		//Bundle bundle = Platform.getBundle("org.eclipse.equinox.http.registry"); //$NON-NLS-1$if (bundle != null) {
		//if (bundle.getState() == Bundle.RESOLVED) {
		//	bundle.start(Bundle.START_TRANSIENT);
		//}
		Bundle bundle3 = Platform.getBundle("org.freejava.sampleapphtml.webapp"); //$NON-NLS-1$if (bundle != null) {
		if (bundle3.getState() == Bundle.RESOLVED) {
			bundle3.start(Bundle.START_TRANSIENT);
		}
		Bundle bundle2 = Platform.getBundle("org.eclipse.jetty.osgi.boot"); //$NON-NLS-1$if (bundle != null) {
		if (bundle2.getState() == Bundle.RESOLVED) {
			selectPort();
			System.setProperty("jetty.home.bundle", "org.eclipse.jetty.osgi.boot");
			System.setProperty("jetty.port", "" + port);

			bundle2.start(Bundle.START_TRANSIENT);
		}
		if (port == -1) {
			// Jetty selected a port number for us
			//ServiceReference[] reference = bundle.getBundleContext().getServiceReferences("org.osgi.service.http.HttpService", "(other.info=freejava)"); //$NON-NLS-1$ //$NON-NLS-2$
			//Object assignedPort = reference[0].getProperty("http.port"); //$NON-NLS-1$
			//port = Integer.parseInt((String)assignedPort);
		}
	}

	public static void stop(String webappName)  {
		try {
			//JettyConfigurator.stopServer(webappName);
		}
		catch (Exception e) {
			//HelpBasePlugin.logError("An error occured while stopping the help server", e); //$NON-NLS-1$
		}
	}

	public static int getPort() {
		return port;
	}

	/*
	 * Get the port number which will be passed to Jetty
	 */
	private static int getPortParameter() {
		if (port == -1) {
			return AUTO_SELECT_JETTY_PORT;
		}
		return port;
	}

	public static String getHost() {
		if (host == null) {
				host = "127.0.0.1"; //$NON-NLS-1$
		}
		return host;
	}

}
