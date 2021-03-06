package com.silptech.kdf.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/*
 This class executes the url through httpGet and returns the contents.
 DomElement is used to parse the xml feeds.
 */
public class XMLParser {
    public static int statusCode;

    // constructor
    public XMLParser() {

    }

    /*
     * Getting XML from URL making HTTP request
     *
     * @param url string
     */
    public static String getXmlFromUrl(String url) {
        String xml = null;
        HttpParams httpParams = new BasicHttpParams();
        //setting timeout in milliseconds until connection is established
        //default value is zero. i.e. tiemout is not used
        int timeoutConnection = 6000;
        //setting the default socket timeout
        int timeoutSocket = 10000;
        DefaultHttpClient httpClient;
        StatusLine statusLine;


        try {
            HttpGet httpGet = new HttpGet(url);
            HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

            // defaultHttpClient
            httpClient = new DefaultHttpClient(httpParams);
            httpGet.setParams(httpParams);
//            HttpPost httpPost = new HttpPost(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);

            statusLine = httpResponse.getStatusLine();
            statusCode = statusLine.getStatusCode();
            Log.i("XML PARSER", " status code : " + statusCode);
            HttpEntity httpEntity = httpResponse.getEntity();

            xml = EntityUtils.toString(httpEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ConnectTimeoutException e) {
            Log.i("XML Parser", "Connection Timeout");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return XML
        return xml;
    }

    /**
     * Getting XML DOM element
     */
    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }

        return doc;
    }

    /**
     * Getting node value
     *
     * @param elem element
     */
    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    /**
     * Getting node value
     */
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
}
