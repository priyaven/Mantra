package com.example.mantra;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.security.SecureRandom;
import java.math.BigInteger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by priya on 9/10/2016.
 */
public class MantraObject {
    private String title;
    private String text;
    private int count;

    public MantraObject(){
        this.title = "";
        this.text = "";
        this.count = 0;
    }

    public MantraObject(Node node){
        Element eElement = (Element) node;
        String title = eElement.getElementsByTagName("title").item(0).getTextContent();
        String text = eElement.getElementsByTagName("text").item(0).getTextContent();
        String cnt = eElement.getElementsByTagName("count").item(0).getTextContent();
        this.title = title;
        this.text = text;
        this.count = Integer.parseInt(cnt.trim());
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() { return count; }

    public void setCount(int count) { this.count = count; }

    public Node toNodeObject() {
        try {
            String nodestring = "<item><title>" + title + "</title><text>" + text + "</text><count>" + new Integer(count).toString() + "</count></item>";

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(nodestring)));
            return doc.getFirstChild();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
