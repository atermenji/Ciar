/*
 *
 *  Copyright (C) 2011 GSyC/LibreSoft, Universidad Rey Juan Carlos.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Jorge Fernández González <jfernandez@libresoft.es>
 *
 */

package com.libresoft.sdk.ARviewer.Utils;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.libresoft.sdk.ARviewer.Types.GeoNode;
import com.libresoft.sdk.ARviewer.Types.Note;

/**
 * The KMLParser class provides a set of methods to parse
 * standard format KML to the data model (GeoNode) that the 
 * ARViewer manages.
 * 
 * @author Jorge Fernández González <jfernandez@libresoft.es>
 * @version 1.0
 * 
 * @see com.libresoft.sdk.ARviewer.Utils.GPXParser
 * @see com.libresoft.apps.ARviewer.ARviewer
 * @see com.libresoft.sdk.ARviewer.Types.GeoNode
 * @see com.libresoft.sdk.ARviewer.Types.Note
 * @see com.libresoft.sdk.ARviewer.Types.Photo
 * @see com.libresoft.sdk.ARviewer.Types.Audio
 * @see com.libresoft.sdk.ARviewer.Types.Video
 *
 */
public class KMLParser{
    /**
     * Getting a KML file that contains a sequence of waypoints (or points of
     * interest) from a given URL, it is parsed to an ArrayList of objects 
     * type GeoNode, using a SAX parser for it.
     * 
     * @param url The URL where you want request a KML file.
     * @return An ArrayList containing the objects GeoNode that have been parsed.
     * 
     * @exception ParserConfigurationException If there are problem with the SAX parser.
     * @exception SAXException If there are problems while parsing the KML file (document malformed).
     * @exception IOException If there are connections problems.
     */
    public ArrayList<GeoNode> parseKML2GeoNode(String url) throws
    		ParserConfigurationException, SAXException, IOException{
    	
    	ArrayList<GeoNode> result = null;
    	try{
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            KMLReader reader = new KMLReader();
           	sp.parse(url, reader);
           	result = reader.getArrayGeoNode();
        }catch(ParserConfigurationException pcex){
        	//Log.w(TAG, pcex.toString());
        	throw pcex;
        }catch(SAXException saxex){
        	//Log.w(TAG, saxex.toString());
        	throw saxex;
        } catch (IOException ioex) {
        	//Log.w(TAG, ioex.toString());
        	throw ioex;
        }
        return result;
    }
    
    /**
     * GPXReader extends the DefaultHandler for a SAX parser, implementing
     * the specific parsing of labels for a GPX document.  
     */
    private class KMLReader extends DefaultHandler {
    	private StringBuilder contentBuffer; // This variable stores the tag's content.
    	private ArrayList<GeoNode> arrayGeoNode = new ArrayList<GeoNode>();
    	
    	private String name = null;
    	private String description = null;
    	private double latitude = -1.0;
    	private double longitude = -1.0;
    	private double altitude = -1.0;
    	
    	public KMLReader() {
    		clear();
    	}
    	   
    	public void clear() {
    		arrayGeoNode.clear();
    		contentBuffer = new StringBuilder();
    	}
    	
    	/**
    	 * Returns an ArrayList of GeoNode objects as result of parsing a KML document.
    	 * 
    	 * @return An ArrayList of GeoNode objects as result of parsing a KML document.
    	 */
    	public ArrayList<GeoNode> getArrayGeoNode(){
    		return arrayGeoNode;
    	}

    	public void startDocument() throws SAXException{
    	}
    	
    	public void endDocument()throws SAXException{
    	}

    	/**
    	 * DefaultHandler::startElement() fires whenever an XML start 
    	 * tag is encountered.
    	 * 
    	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    	 */
    	public void startElement(String uri, String localName, 
    							String qName, Attributes attributes){
    		// Clear content buffer
    		contentBuffer.delete(0, contentBuffer.length());
    	}

    	/**
    	 * The DefaultHandler::characters() function fires one or more times 
    	 * for each text node encountered.
    	 * 
    	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
    	 * @throws SAXException If there are problems while parsing the KML file (document malformed).
    	 */
    	public void characters(char buf[], int offset, int len) throws SAXException{
    		contentBuffer.append(String.copyValueOf(buf, offset, len));
    	}
        
    	/**
    	 * The DefaultHandler::endElement() function fires for each end tag.
    	 *
    	 * @see org.xml.sax.helpers.DefaultHandler#endElement(String, String, String)
    	 */
    	public void endElement(String uri, String localName, String qName) {    		
    		if("name".equals(localName)){	
    			name = contentBuffer.toString();
    		}else if("description".equals(localName)) {
    			description = contentBuffer.toString(); 	    	  
    		}else if("coordinates".equals(localName)) {
    			System.out.println(contentBuffer.toString().substring(0, contentBuffer.toString().indexOf(",")));
    			System.out.println(contentBuffer.toString().substring(contentBuffer.toString().indexOf(",")+1));
    			System.out.println(contentBuffer.toString().substring(contentBuffer.toString().lastIndexOf(",")+1));
    			latitude = new Double(
    						contentBuffer.toString().substring(0, 
    						contentBuffer.toString().indexOf(","))).
    						doubleValue(); 
    			longitude = new Double(
    						contentBuffer.toString().substring(
    						contentBuffer.toString().indexOf(",")+1, 
    						contentBuffer.toString().lastIndexOf(","))).
    						doubleValue();
    			altitude = new Double(
    						contentBuffer.toString().substring(
    						contentBuffer.toString().lastIndexOf(",")+1)).
    						doubleValue();
    		}else if("Placemark".equals(localName)) {
    			arrayGeoNode.add(new Note(null, name, description,
    								latitude, longitude, altitude,
    									null, null,	null, null));
    		}
    	}
    }
}