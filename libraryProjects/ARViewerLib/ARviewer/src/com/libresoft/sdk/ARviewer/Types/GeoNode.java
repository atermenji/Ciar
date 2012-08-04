/*
 *
 *  Copyright (C) 2008-2011 GSyC/LibreSoft, Universidad Rey Juan Carlos
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
 *  Author : Roberto Calvo Palomino <rocapal@libresoft.es>
 *
 */

package com.libresoft.sdk.ARviewer.Types;

import java.io.Serializable;

import android.location.Location;

/**
 * This class provides the essential Node of social Network. All the 
 * contents must inherit of this class.
 * 
 * @author Roberto Calvo
 * @author <a href="mailto:rocapal@libresoft.es">rocapal@libresoft.es</a>
 * @author <a target="_blank" href="http://libregeosocial.morfeo-project.org/">http://libregeosocial.morfeo-project.org/</a>
 * @version 0.1
 */

public class GeoNode implements Serializable {

	// Serializable UID
	private static final long serialVersionUID = -9121943636715457236L;

	private Integer mId;
	private Double  mRadius;
	private String  mSince;
	
	private Double mLatitude;
	private Double mLongitude;
	private Double mAltitude;
	private String mPosition_since;
	
	private String mUsername;
	private Integer mUser_id;
	
	private ExtenalInfo mExtInfo;
	
	private GenericLayer mFatherLayer = null;
	
	/**
     * Constructor used to create this object. 
     * @param id Identifier of social network (can be 0)
     * @param latitude The latitude of the node position 
     * @param longitude The longitude of the node position
     * @param altitude The Altitude of the node posicion
     * @param radius The radius/error of the node position
     * @param since The time when the node was created
     */

	public GeoNode(Integer id, Double latitude, Double longitude, Double altitude, Double radius, String since, 
			String position_since)
	{
		super();
		mId = id;
		mRadius = radius;	
		
		
		mLatitude = latitude;
		mLongitude = longitude;
		mAltitude = altitude;
		mPosition_since = position_since;

		mSince = since;
		
		mExtInfo=null;
	}
	
	public void setFatherLayer (GenericLayer layer)
	{
		mFatherLayer = layer;
	}
	
	public GenericLayer getFatherLayer ()
	{
		return mFatherLayer;
	}
	
	/**
     * Return the Identifier asociated to social network
     */
	
	public Integer getId() {
		return mId;
	}
	
	/**
     * Return the latitude of the node
     */
	public Double getLatitude() {
		return mLatitude;
	}
	
	/**
     * Return the longitude of the node
     */
	public Double getLongitude() {
		return mLongitude;
	}
	
	/**
     * Return the altitude of the node
     */
	public Double getAltitude(){
		return mAltitude;
	}
	
	/**
     * Return the radius of the node
     */
	public Double getRadius() {
		return mRadius;
	}
	
	public String getPosition_since(){
		return mPosition_since;
	}
	
	/**
     * Set the latitude of the node
     * 
     * @param latitude The latitude of the position node
     */
	public void setLatitude(Double latitude){
		mLatitude = latitude;
		getLocation();
	}
	
	/**
     * Set the longitude of the node
     * 
     * @param longitude The latitude of the position node
     */
	public void setLongitude(Double longitude){
		mLongitude = longitude;
		getLocation();
	}
	
	
	public Location getLocation ()
	{
		Location myLocation = new Location ("gps");
		myLocation.setLatitude(mLatitude);
		myLocation.setLongitude(mLongitude);
		myLocation.setAltitude(mAltitude);
		
		return myLocation;
	}
	
	
	public void setAltitude(float altitude){
		mAltitude = (double) altitude;
		getLocation();
	}
	
	public void setSince (String since)
	{
		mSince = since;
	}
	
	public String getSince ()
	{
		return mSince;
	}
	
	public void setExternalInfo (ExtenalInfo extInfo)
	{
		mExtInfo = extInfo;
	}
	
	public ExtenalInfo getExternalInfo ()
	{
		return mExtInfo;
	}
	
	public void setId(Integer id){
		mId = id;
	}

	public void setRadius(Double radius){
		mRadius = radius;
	}
	
	public void setPosition_since(String position_since){
		mPosition_since = position_since;
	}
	
	//uploader info
	public void setUser_id(Integer user_id){
		mUser_id = user_id;
	}
	
	public void setUsername(String username){
		mUsername = username;
	}
	
	public String getUsername(){
		return mUsername;
	}
	
	public Integer getUser_id(){
		return mUser_id;
	}
}
