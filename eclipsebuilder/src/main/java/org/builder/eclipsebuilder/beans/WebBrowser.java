package org.builder.eclipsebuilder.beans;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.PlainSocketFactory;
import org.apache.http.conn.Scheme;
import org.apache.http.conn.SchemeRegistry;
import org.apache.http.conn.params.HttpConnectionManagerParams;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;

public class WebBrowser {

    private ClientConnectionManager clientConnectionManager;
    private HttpClient httpClient;

    public WebBrowser() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionManagerParams.setMaxTotalConnections(params, 100);
        HttpConnectionManagerParams.setDefaultMaxConnectionsPerHost(params, 20);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        this.clientConnectionManager = new ThreadSafeClientConnManager(params,
                schemeRegistry);
        this.httpClient = new DefaultHttpClient(this.clientConnectionManager,
                params);
    }

    @Override
    protected void finalize() throws Throwable {
        this.clientConnectionManager.shutdown();
        super.finalize();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void getLinksAndContenType(String urlStr, List<String> hyperlinks, List<String> contentType) throws Exception {
        InputStream is = null;
        HttpGet httpget = null;
        try {
            httpget = new HttpGet(urlStr);
            HttpResponse response = this.httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            Header header = entity.getContentType();
            if (header != null && header.getValue() != null && contentType != null) {
                contentType.add(header.getValue());
            }
            is = new BufferedInputStream(entity.getContent());
            if (hyperlinks != null && header != null && header.getValue() != null
                    && header.getValue().startsWith("text/html")) {
                InputSource isrc = new InputSource(is);
                DOMParser parser = new DOMParser();
                parser.setFeature("http://xml.org/sax/features/namespaces", false);
                parser.parse(isrc);
                URL documentURL = new URL(urlStr);
                HTMLDocument document = (HTMLDocument) parser.getDocument();
                HTMLCollection links = document.getLinks();
                for (int i = 0; i < links.getLength(); i++) {
                    Node link = links.item(i);
                    NamedNodeMap attrs = link.getAttributes();
                    String href = null;
                    for (int j = 0; j < attrs.getLength(); j++) {
                        if ("href".equalsIgnoreCase(attrs.item(j).getNodeName())) {
                            href = attrs.item(j).getNodeValue();
                        }
                    }
                    if (!href.startsWith("javascript:") && !href.startsWith("news:")) {
                        String absHref = new URL(documentURL, href).toString();
                        int lastSep = absHref.lastIndexOf('/');
                        int lastSharp = absHref.lastIndexOf('#');
                        if (lastSep >= 0 && lastSharp >= 0 && lastSharp > lastSep) {
                            absHref = absHref.substring(0, lastSharp);
                        }
                        hyperlinks.add(absHref);
                    }
                }
                NodeList options = document.getElementsByTagName("option");
                for (int i  = 0; i < options.getLength(); i ++) {
                    Element option = (Element) options.item(i);
                    String value = option.getAttribute("value");
                    if (value.startsWith("http://")) hyperlinks.add(value);
                }
            }
        } finally {
            if (httpget != null) {
                httpget.abort();
            }
        }
    }

    public String getContentType(String urlStr) throws Exception {
        String contentType = null;
        HttpGet httpget = new HttpGet(urlStr);
        try {
            HttpResponse response = this.httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            contentType = entity.getContentType().getValue();
        } finally {
            httpget.abort();
        }
        return contentType;
    }

    public Object[] getFileNameAndSize(URL url) throws Exception {
        String fileName = null;
        Long fileSize = null;

        HttpParams params = new BasicHttpParams();
        HttpConnectionManagerParams.setMaxTotalConnections(params, 100);
        HttpConnectionManagerParams.setDefaultMaxConnectionsPerHost(params, 20);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        DefaultHttpClient client = new DefaultHttpClient(
                this.clientConnectionManager, params);
        MyRedirectHandler handler = new MyRedirectHandler();
        client.setRedirectHandler(handler);
        HttpGet httpget = new HttpGet(url.toURI());
        try {
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();
            long length = entity.getContentLength();
            if (length >= 0)
                fileSize = Long.valueOf(length);
            String name = getContentDispositionName(response);
            if (name != null) {
                fileName = name;
            } else {
                // guess it from last (redirected) URL
                URL location = handler.getLocation() != null ? handler
                        .getLocation().toURL() : url;
                fileName = guessFileNameFromUrl(location);
            }
        } finally {
            httpget.abort();
        }
        return new Object[] { fileName, fileSize };
    }

    private String guessFileNameFromUrl(URL location) {
        String url = location.toString();
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        if (!isValidFileName(fileName)) {
            url = location.getPath();
            fileName = url.substring(url.lastIndexOf('/') + 1);
            if (!isValidFileName(fileName)) {
                fileName = "unknown";
            }
        }
        return fileName;
    }

    private boolean isValidFileName(String fileName) {
        boolean valid = false;
        if (fileName != null && fileName.length() > 0) {
            String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789$%'`-@{}~!#()&_^ +,.=[]";
            valid = StringUtils.containsOnly(fileName, validChars);
            valid = valid && !fileName.endsWith(" ") && !fileName.endsWith(".");
        }
        return valid;
    }

    private static class MyRedirectHandler extends DefaultRedirectHandler {
        private URI location;

        public URI getLocation() {
            return location;
        }

        @Override
        public URI getLocationURI(HttpResponse response, HttpContext context)
                throws ProtocolException {
            location = super.getLocationURI(response, context);
            return location;
        }
    }

    private String getContentDispositionName(HttpResponse response) {
        String name = null;
        Header header = response.getFirstHeader("Content-Disposition");
        if (header != null && header.getValue() != null) {
            String headerValue = header.getValue();
            String[] params = headerValue.split(";");
            for (String param : params) {
                String[] nameVal = param.split("=");
                if (nameVal != null && nameVal[0] != null
                        && nameVal[0].trim().equalsIgnoreCase("filename")) {
                    name = nameVal[1].trim();
                    name = StringUtils.replace(name, "\"", "");
                }
            }
        }
        return name;
    }

    public String getUrlContentAsText(String url) throws Exception {
        String content;
        HttpGet httpget = null;
        InputStream is = null;
        try {
            httpget = new HttpGet(url);
            HttpResponse response = this.httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            content = IOUtils.toString(is, "UTF-8");
        } catch (Exception e) {
            if (httpget != null) {
                httpget.abort();
            }
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return content;
    }

}
