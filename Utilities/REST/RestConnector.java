package com.skeds.android.phone.business.Utilities.REST;

import android.os.Environment;

import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.Logger;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.core.SkedsApplication;
import com.skeds.android.phone.business.core.async.SimpleMultipartEntity;
import com.skeds.android.phone.business.core.util.IOUtils;
import com.skeds.android.phone.business.data.xml.api.XmlStreamHandler;

import net.iharder.utils.Base64;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * HTTPS server communication utilities
 */
public final class RestConnector {

    private static final String TAG = RestConnector.class.getName();

    private static final String HEADER_AUTHORIZATION_NAME = "Authorization";
    private static final String HEADER_AUTHORIZATION_VALUE = "Basic %s";

    private static RestConnector connector = null;
    private final String urlBase;
    private final Header credits;

    /**
     * Create new connector. Set base part of URL depends on <code>isBetaServerMode()</code> option. Preset credentials
     * header.
     */
    private RestConnector() {
        urlBase = SkedsApplication.getBaseUrl();
        credits = new BasicHeader(HEADER_AUTHORIZATION_NAME, String.format(HEADER_AUTHORIZATION_VALUE,
                Base64.encodeBytes(
                        (UserUtilitiesSingleton.getInstance().getUsername() + ':' + UserUtilitiesSingleton.getInstance().getPassword())
                                .getBytes())));
    }

    /**
     * Lazy connector singleton constructor.
     *
     * @return global connector instance, never null
     */
    public static RestConnector getInstance() {
        if (connector == null) {
            connector = new RestConnector();
        }
        return connector;
    }

    /**
     * Drop created singleton (used then beta mode or username/password changed)
     */
    public static void resetInstance() {
        connector = null;
    }

    /**
     * Read XML from server with HTTP GET request
     *
     * @param queryPath server path part of URL
     * @return read XML document, never null
     * @throws com.skeds.android.phone.business.Utilities.NonfatalException on any error and exception cause contains original exception
     */
    public Document httpGet(String queryPath) throws NonfatalException {
        Document document = httpGet(queryPath, true);

        saveXmlToFile(document, queryPath);
        return document;
    }

    /**
     * Performs remote call to server with the given request and process response manually using Xml stream parsing API.
     * Use this method if you're not sure that response size of the given request call is pretty small and you can't use
     * automatic xml parser that loads full response to the memory before parsing. If you know that response is rather
     * small to be processed automatically you can use {@link #httpGet(String queryPath)} instead.
     * <p/>
     * Since this method must be used to avoid big memory consumption it should persist everything comes in response and
     * then fetch this data from the database by small chunks.
     * <p/>
     * By default it will substitute user credentials in teh request by default.
     *
     * @param queryPath       HTTP request being sent to the server
     * @param responseHandler stream handler that is responsible for parsing Xml from stream and serializing every bunch to database to
     *                        avoid memory heap overusing
     * @throws java.io.IOException in case of a problem or the connection was aborted /** Performs remote call to server with the given
     *                             request and process response automatically using Xml parser.
     * @see #httpGet(String queryPath)
     */
    public void httpGet(String queryPath, XmlStreamHandler responseHandler) throws IOException, XmlPullParserException {
        httpGet(queryPath, responseHandler, true);
    }

    /**
     * Performs remote call to server with the given request and process response manually using Xml stream parsing API.
     * Use this method if you're not sure that response size of the given request call is pretty small and you can't use
     * automatic xml parser that loads full response to the memory before parsing. If you know that response is rather
     * small to be processed automatically you can use {@link #httpGet(String queryPath)} instead.
     * <p/>
     * Since this method must be used to avoid big memory consumption it should persist everything comes in response and
     * then fetch this data from the database by small chunks.
     *
     * @param queryPath           HTTP request being sent to the server
     * @param responseHandler     stream handler that is responsible for parsing Xml from stream and serializing every bunch to database to
     *                            avoid memory heap overusing
     * @param requiresCredentials whether to add user credentials in the request ot not
     * @throws java.io.IOException in case of a problem or the connection was aborted /** Performs remote call to server with the given
     *                             request and process response automatically using Xml parser.
     * @see #httpGet(String queryPath)
     */
    public void httpGet(String queryPath, final XmlStreamHandler responseHandler, final boolean requiresCredentials) throws
            IOException, XmlPullParserException {
        queryPath = urlBase + queryPath;
        final HttpResponse response = doCall(setupHttpClient(),
                setupHttpGetRequest(queryPath, requiresCredentials));

        final HttpEntity entity = response.getEntity();
        InputStream inStream = null;
        try {
            inStream = entity.getContent();
            final Header contentEncoding = response.getFirstHeader(HTTP.CONTENT_ENCODING);
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                inStream = new GZIPInputStream(inStream);
            }
            responseHandler.handleStream(Logger.logXml(TAG, inStream, HTTP.UTF_8));
        } finally {
            IOUtils.closeQuietly(inStream);
            entity.consumeContent();
        }
    }

    /**
     * Execute {@link #httpGet(String)} and check server response for success. Used for DELETE or PUT operations.
     * <p/>
     * <addPartOrderResponse><response status="success" id="224"/></addPartOrderResponse>
     *
     * @param queryPath the url to server
     * @return server response XML
     * @throws com.skeds.android.phone.business.Utilities.NonfatalException
     */
    public Document httpGetCheckSuccess(String queryPath) throws NonfatalException {
        Document xmlRes = httpGet(queryPath, true);
        checkXmlResponseSuccess(xmlRes);
        saveXmlToFile(xmlRes, queryPath);

        return xmlRes;
    }

    private void saveXmlToFile(Document xmlRes, String queryPatch) {

        return;
        // new XMLOutputter().output(doc, System.out);
//        XMLOutputter xmlOutput = new XMLOutputter();
//
//
//        File externalStorageDirectory = Environment.getExternalStorageDirectory();
//        File file = new File(externalStorageDirectory, xmlRes.getRootElement().getName() + "__" + queryPatch.replace("/", "_") + ".xml");
//        // display nice nice
//        xmlOutput.setFormat(Format.getPrettyFormat());
//        try {
//            xmlOutput.output(xmlRes, new FileWriter(file));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Read XML from server with HTTP GET request
     *
     * @param queryPath           server path part of URL
     * @param requiresCredentials false if no auth. required
     * @return read XML document, never null
     * @throws com.skeds.android.phone.business.Utilities.NonfatalException on any error and exception cause contain original exception
     */
    public Document httpGet(String queryPath, boolean requiresCredentials) throws NonfatalException {
        queryPath = urlBase + queryPath;
        Logger.info("HTTP.GET", queryPath);
        try {
            return readXml(setupHttpClient(), setupHttpGetRequest(queryPath, requiresCredentials));
        } catch (ConnectTimeoutException e) {
            throw new NonfatalException(TAG, "Connection timeout!", e);
        } catch (NonfatalException e) {
            throw e;
        } catch (IOException e) {
            throw new NonfatalException(TAG, "Connection problem to the server!", e);
        } catch (Exception e) {
            throw new NonfatalException(TAG, "Can't parse response using JDom library!", e);
        }
    }

    /**
     * Read XML from server with HTTP POST request
     *
     * @param xml       outgoing XML data
     * @param queryPath server path part of URL
     * @return read XML document, never null
     * @throws com.skeds.android.phone.business.Utilities.NonfatalException on any error and exception cause contain original exception
     */
    public Document httpPost(Document xml, String queryPath) throws NonfatalException {
        return httpPost(xml, queryPath, true);
    }

    /**
     * Execute {@link #httpPost(org.jdom2.Document, String)} and check server response for success
     * <p/>
     * <addPartOrderResponse><response status="success" id="224"/></addPartOrderResponse>
     *
     * @param xml       the document which contains additional info for the post request as params
     * @param queryPath the url to the server
     * @return server response XML
     * @throws com.skeds.android.phone.business.Utilities.NonfatalException
     */
    public Document httpPostCheckSuccess(Document xml, String queryPath) throws NonfatalException {
        Document xmlRes = httpPost(xml, queryPath, true);
        checkXmlResponseSuccess(xmlRes);
        return xmlRes;
    }

    /**
     * Execute {@link #httpPostCheckSuccess(org.jdom2.Document, String, java.io.InputStream)} and check server response
     * for success
     * <p/>
     * <addPartOrderResponse><response status="success" id="224"/></addPartOrderResponse>
     *
     * @param xml       the document which contains additional info for the post request as params
     * @param queryPath the url to the server
     * @param filePath  the path to the file to upload on server
     * @return server response XML
     * @throws com.skeds.android.phone.business.Utilities.NonfatalException
     */
    public Document httpPostCheckSuccess(final Document xml, final String queryPath, final String filePath) throws
            NonfatalException,
            IOException {
        return httpPostCheckSuccess(xml, queryPath, new FileInputStream(filePath));
    }

    public Document httpPostCheckSuccess(final Document xml, final String queryPath, final InputStream stream) throws
            NonfatalException {
        final Document xmlRes = httpPostMultiPart(xml, queryPath, true, stream);
        checkXmlResponseSuccess(xmlRes);
        return xmlRes;
    }

    public Document httpPostCheckSuccess(String payload, String queryPath) throws NonfatalException {
        Document xmlRes = httpPost(payload, queryPath, true);
        checkXmlResponseSuccess(xmlRes);
        saveXmlToFile(xmlRes, queryPath);

        return xmlRes;
    }

    public Document httpPostMultiPart(final Document xml, final String queryPath, final boolean requiresCredentials, final InputStream fileStream) throws
            NonfatalException {
        return httpPostMultiPart(xml, queryPath, requiresCredentials, null, fileStream);
    }

    public Document httpPostMultiPart(final Document xml, String queryPath, final boolean requiresCredentials, final String fileName, final InputStream fileStream) throws
            NonfatalException {
        queryPath = urlBase + queryPath;
        Logger.info("HTTP.POST", queryPath);

        if (fileStream==null)return null;

        final String xmlDoc = new XMLOutputter().outputString(xml);

        Logger.logXml("HTTP.POST.XML", queryPath, xmlDoc);

        final HttpPost httpPost = setupHttpPost(queryPath, requiresCredentials);

        final SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();
        multipartEntity.addPart("xml", xmlDoc);
        multipartEntity.addPart("file", fileName, fileStream, true);
        httpPost.setEntity(multipartEntity);

        saveXmlToFile(xml, queryPath);

        try {
            return readXml(setupHttpClient(), httpPost);
        } catch (ConnectTimeoutException e) {
            throw new NonfatalException("HTTP", "Connection timeout!", e);
        } catch (NonfatalException e) {
            throw e;
        } catch (IOException e) {
            throw new NonfatalException("HTTP", "Connection problem", e);
        } catch (Exception e) {
            throw new NonfatalException("HTTP", "POST request failed", e);
        } finally {
            IOUtils.closeQuietly(fileStream);
        }
    }

    /**
     * Read XML from server with HTTP POST request
     *
     * @param xml                 outgoing XML data
     * @param queryPath           server path part of URL
     * @param requiresCredentials false if no auth. required
     * @return read XML document, never null
     * @throws com.skeds.android.phone.business.Utilities.NonfatalException on any error and exception cause containt original exception
     */
    public Document httpPost(Document xml, String queryPath, boolean requiresCredentials) throws NonfatalException {
        saveXmlToFile(xml, queryPath);

        return httpPost(new XMLOutputter().outputString(xml), queryPath, requiresCredentials);
    }

    public Document httpPost(String payload, String queryPath, boolean requiresCredentials) throws NonfatalException {
        queryPath = urlBase + queryPath;
        Logger.info("HTTP.POST", queryPath);

        //it is better to create new class where we can add values ourself not in the method
        final List<NameValuePair> pairs = new ArrayList<NameValuePair>(1);
        Logger.logXml("HTTP.POST.XML", queryPath, payload);
        try {
            final HttpPost httpPost = setupHttpPost(queryPath, requiresCredentials);

            pairs.add(new BasicNameValuePair("xml", payload));
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
            return readXml(setupHttpClient(), httpPost);
        } catch (ConnectTimeoutException e) {
            throw new NonfatalException(TAG, "Connection timeout!", e);
        } catch (NonfatalException e) {
            throw e;
        } catch (IOException e) {
            throw new NonfatalException(TAG, "Connection problem during the server request!", e);
        } catch (Exception e) {
            throw new NonfatalException(TAG, "Parsing xml response failed!", e);
        }
    }

    @Deprecated
    /**
     * Should be used {@link #httpPostMultiPart(org.jdom2.Document, String, boolean, java.io.InputStream)} method instead.
     */
    public void httpPostReport(Document xml, String queryPath) throws IOException {
        queryPath = urlBase + queryPath;

        Logger.info("HTTP.POST", queryPath);
        try {
            XMLOutputter xmlout = new XMLOutputter();
            String xmlstr = xmlout.outputString(xml);
            xml = null;
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(1);
            pairs.add(new BasicNameValuePair("xml", xmlstr));
            xmlstr = null;
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
            pairs = null;

            HttpClient client = getNewHttpClient();
            final HttpParams httpParameters = client.getParams();
            httpParameters.setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            httpParameters.setParameter("http.protocol.content-charset", "UTF-8");
            HttpConnectionParams.setConnectionTimeout(httpParameters, 1800 * 1000);
            HttpConnectionParams.setSoTimeout(httpParameters, 1800 * 1000);

            HttpPost httpPost = new HttpPost(queryPath);

            httpPost.setHeader(credits);
            httpPost.addHeader("Accept-Encoding", "gzip");

            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            client = null;
            httpPost = null;
            org.apache.http.StatusLine sline = response.getStatusLine();
            int code = sline.getStatusCode();
            String slineStr = "" + code + " " + sline.getReasonPhrase();
            if (sline.getStatusCode() > 299) {
                Logger.warn("HTTP.RESP.STATUS", slineStr);
                throw new IOException(slineStr);
            } else {
                Logger.info("HTTP.RESP.STATUS", slineStr);
            }
        } catch (ConnectTimeoutException e) {
            throw new IOException("Connection timeout!", e);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("HTTP request failed", e);
        }
    }

    /**
     * Check success status against server XML response
     * <p/>
     * <addPartOrderResponse><response status="success" id="224"/></addPartOrderResponse>
     *
     * @param xmlDoc xml response which contains the result of the operation
     * @throws com.skeds.android.phone.business.Utilities.NonfatalException
     */
    private void checkXmlResponseSuccess(final Document xmlDoc) throws NonfatalException {
        if (xmlDoc ==null) return;
        final List<Element> els = xmlDoc.getRootElement().getChildren();
        if (els.isEmpty()) {
            return;
        }
        if (els.get(0).getAttributeValue("status") == null) {
            return;
        }
        if (!"success".equals(els.get(0).getAttributeValue("status"))) {
            Logger.warn("XML", "200 status code but XML has no \"success\" attribute");
            throw new NonfatalException("SERVER", els.get(0).getAttributeValue("message"));
        }
    }

    private Document readXml(final HttpClient client, final HttpUriRequest request) throws IOException, JDOMException,
            NonfatalException {
        return readXml(doCall(client, request));
    }

    /**
     * Read HTTP response, log it into error log file, parse XML and return ready XML document
     *
     * @param response HTTP response
     * @return XML doc., never null
     * @throws java.io.UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws java.io.IOException
     * @throws org.jdom2.JDOMException
     * @throws com.skeds.android.phone.business.Utilities.NonfatalException
     */
    private Document readXml(HttpResponse response) throws IllegalStateException, IOException, JDOMException,
            NonfatalException {
        final HttpEntity entity = response.getEntity();
        InputStreamReader reader = null;
        InputStream inStream = null;
        final SAXBuilder builder = new SAXBuilder();
        try {
            inStream = entity.getContent();
            Header contentEncoding = response.getFirstHeader(HTTP.CONTENT_ENCODING);
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                inStream = new GZIPInputStream(inStream);
            }
            reader = Logger.logXml("HTTP.RESP.XML", inStream, HTTP.UTF_8);
            return builder.build(reader);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(inStream);
            entity.consumeContent();
        }
    }

    private HttpClient setupHttpClient() {
        final HttpClient client = getNewHttpClient();
        final HttpParams httpParameters = client.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 180 * 1000);
        HttpConnectionParams.setSoTimeout(httpParameters, 180 * 1000);
        return client;
    }

    private HttpPost setupHttpPost(String queryPath, boolean requiresCredentials) {
        final HttpPost httpPost = new HttpPost(queryPath);

        if (requiresCredentials) {
            httpPost.setHeader(credits);
            httpPost.addHeader("Accept-Encoding", "gzip");
        } else {
            httpPost.setHeader("Accept-Encoding", "gzip");
        }
        return httpPost;
    }

    private HttpClient getNewHttpClient() {
        try {
            final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            final SSLSocketFactory sf = new SkedsSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            final HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            final SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            final ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private HttpResponse doCall(final HttpClient client, final HttpUriRequest request) throws IOException {
        final HttpResponse response = client.execute(request);
        // check the execution status
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            final String phrase = response.getStatusLine().getReasonPhrase();
            Logger.warn("HTTP.RESP.STATUS", phrase);
            if (statusCode == 401 && "Unauthorized".equals(phrase)) {
                throw new IOException("Authorization" + phrase);
            }
            throw new IOException("There were problems to get the information. [Status code]: " + statusCode);
        }
        return response;
    }

    private HttpGet setupHttpGetRequest(String queryPath, boolean requiresCredentials) {
        final HttpGet httpGet = new HttpGet(queryPath);
        if (requiresCredentials) {
            httpGet.setHeader(credits);
            httpGet.addHeader("Accept-Encoding", "gzip");
        } else {
            httpGet.setHeader("Accept-Encoding", "gzip");
        }
        return httpGet;
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            Logger.err(TAG, e.getMessage());
        } finally {
            IOUtils.closeQuietly(br);
        }

        return sb.toString();

    }
}