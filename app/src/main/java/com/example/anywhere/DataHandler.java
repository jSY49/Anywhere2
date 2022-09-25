package com.example.anywhere;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class DataHandler extends DefaultHandler {
    private String elementValue = null;
    private Boolean elementOn = false;
    private ArrayList<DataGetterSetters> dataList = new ArrayList<DataGetterSetters>();
    private DataGetterSetters data = null;

    public ArrayList<DataGetterSetters> getData() {
        return dataList;
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        elementOn = true;
        if (localName.equals("item")) {
            data = new DataGetterSetters();
        }
    }

    /**
     * This will be called when the tags of the XML end.
     **/
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        elementOn = false;

        if (localName.equalsIgnoreCase("title")) {
            data.setTitle(elementValue);
        } else if (localName.equalsIgnoreCase("mapx")) {
            data.setLon(elementValue);
        } else if (localName.equalsIgnoreCase("mapy")) {
            data.setLat(elementValue);
        } else if (localName.equalsIgnoreCase("contentid")) {
            data.setContentId(elementValue);
        }else if(localName.equalsIgnoreCase("contenttypeid")){
            data.setContenttypeid(elementValue);
        }else if (localName.equalsIgnoreCase("item")) {
            dataList.add(data);
            data = null;
        }

    }

    /**
     * This is called to get the tags value
     **/
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (elementOn) {
            elementValue = new String(ch, start, length);
            elementOn = false;
        }

    }

}

