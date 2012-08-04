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
 * The GPXParser class provides a set of methods to parse
 * standard format GPX to the data model (GeoNode) that the
 * ARViewer manages.
 * 
 * @author Jorge Fernández González <jfernandez@libresoft.es>
 * @version 1.0
 * 
 * @see com.libresoft.sdk.ARviewer.Utils.KMLParser
 * @see com.libresoft.apps.ARviewer.ARviewer
 * @see com.libresoft.sdk.ARviewer.Types.GeoNode
 * @see com.libresoft.sdk.ARviewer.Types.Note
 * @see com.libresoft.sdk.ARviewer.Types.Photo
 * @see com.libresoft.sdk.ARviewer.Types.Audio
 * @see com.libresoft.sdk.ARviewer.Types.Video
 * 
 */
public class GPXParser{    
    /**
     * Getting a GPX file that contains a sequence of waypoints (or points of
     * interest) from a given URL, it is parsed to an ArrayList of objects 
     * type GeoNode, using a SAX parser for it.
     * 
     * @param url The URL where you want request a GPX file.
     * @return An ArrayList containing the objects GeoNode that have been parsed.
     * 
     * @throws ParserConfigurationException If there are problem with the SAX parser.
     * @throws SAXException If there are problems while parsing the GPX file (document malformed).
     * @throws IOException If there are connections problems.
     */
    public ArrayList<GeoNode> parseGPX2GeoNode(String url) throws
    		ParserConfigurationException, SAXException, IOException{
    	
    	ArrayList<GeoNode> result = null;
    	try{
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            GPXReader reader = new GPXReader();
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
    private class GPXReader extends DefaultHandler {
    	private StringBuilder contentBuffer; // This variable stores the tag's content.
    	private ArrayList<GeoNode> arrayGeoNode = new ArrayList<GeoNode>();
    	
    	private String name = null;
    	private String description = null;
    	private double latitude = -1.0;
    	private double longitude = -1.0;
    	private double altitude = -1.0;
    	private String since = null;
    	   
    	public GPXReader() {
    		clear();
    	}
    	   
    	public void clear() {
    		arrayGeoNode.clear();
    		contentBuffer = new StringBuilder();
    	}
    	
    	/**
    	 * Returns an ArrayList of GeoNode objects as result of parsing a GPX document.
    	 * 
    	 * @return An ArrayList of GeoNode objects as result of parsing a GPX document.
    	 */
    	public ArrayList<GeoNode> getArrayGeoNode(){
    		return arrayGeoNode;
    	}
    	
    	/**
    	 * DefaultHandler::startElement() fires whenever an XML start 
    	 * tag is encountered.
    	 * 
    	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    	 * @throws SAXException If there are problems while parsing the KML file (document malformed).
    	 */
    	public void startElement(String uri, String localName, 
    							String qName, Attributes attributes) 
    							throws SAXException {
    		// the <bounds> element has attributes which specify min & max latitude and longitude
    		/*if (localName.compareToIgnoreCase("bounds") == 0) {
    	         minLat = new Float(attributes.getValue("minlat")).floatValue();
    	         maxLat = new Float(attributes.getValue("maxlat")).floatValue();
    	         minLon = new Float(attributes.getValue("minlon")).floatValue();
    	         maxLon = new Float(attributes.getValue("maxlon")).floatValue();
    	      } else {
			*/
    		// the <wpt> element has attributes which specify latitude and longitude (it has child elements that specify the time and elevation)
    		if (localName.compareToIgnoreCase("wpt") == 0) {
    			latitude = Double.parseDouble(attributes.getValue("lat"));
    			longitude = Double.parseDouble(attributes.getValue("lon"));
    		}
    	      
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
    	public void characters(char[] ch, int start, int length) 
    			throws SAXException {
    		contentBuffer.append(String.copyValueOf(ch, start, length));
    	}
    	   
    	/**
    	 * The DefaultHandler::endElement() function fires for each end tag.
    	 *
    	 * @see org.xml.sax.helpers.DefaultHandler#endElement(String, String, String)
    	 * @throws SAXException If there are problems while parsing the KML file (document malformed).
    	 */
    	public void endElement(String uri, String localName, String qName) 
    							throws SAXException {
    		
    		// <WPT> WAYPOINT GPX
    		// Required Information:
    		// <lat> Latitude of the waypoint.
    		// <lon> Longitude of the waypoint.
    		
    		// Optional Position Information:
    		// <ele> Elevation of the waypoint.
    		// <time> Creation date/time of the waypoint.
    		
    		// Optional Description Information:
    		// <name> GPS waypoint name of the waypoint.
    		// <desc> Descriptive description of the waypoint.
    		
    		// More parameters: http://www.topografix.com/gpx_manual.asp#gpx_req
    		
    		if("name".equals(localName)){	
    			name = contentBuffer.toString();
    		}else if("desc".equals(localName)){
    			description = contentBuffer.toString(); 	    	  
    		}else if("ele".equals(localName)){
       			altitude = new Float(contentBuffer.toString()); 	    	  
       		}else if("time".equals(localName)){
       			since = contentBuffer.toString(); 	    	  
       		}else if ("wpt".equals(localName)){
       			arrayGeoNode.add(new Note(null, name, description,
       						latitude, longitude, altitude,
							null, since, null, null));
       		}
    	}
    }
}