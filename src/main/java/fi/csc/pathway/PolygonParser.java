/**
 * Pathway project funded by Academy of Finland. 
 *  
 *  TutkaUI -portlet by CSC - IT Center for Science Ltd. is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *  Permissions beyond the scope of this license may be available at http://www.csc.fi/english/contacts.
 *  
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License"
 *  style="border-width:0" src="http://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">TutkaUI-portlet</span> by <a xmlns:cc="http://creativecommons.org/ns#"
 *  href="http://avaa.tdata.fi/web/" property="cc:attributionName" rel="cc:attributionURL">CSC - IT Center for Science Ltd.</a>
 *  is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License</a>.<br />
 *  Permissions beyond the scope of this license may be available at <a xmlns:cc="http://creativecommons.org/ns#"
 *   href="http://www.csc.fi/english/contacts" rel="cc:morePermissions">http://www.csc.fi/english/contacts</a>.
 */
package fi.csc.pathway;


import java.util.ArrayList;

import com.vaadin.tapio.googlemaps.client.LatLon;

/**
 * @author pj
 *  POLYGON((24.7725 59.1321,24.7711 59.1141, ...
 */
public class PolygonParser {
	
	final static String POLYGON = "POLYGON((";
	
	/**
	 * Parsii postgis polygonin vaadin-kirjaston googlemaps addon:lle.
	 * @param text String POLYGON((24.7725 59.1321,24.7711 59.1141, ...
	 * @return ArrayList<LatLon> com.vaadin.tapio.googlemaps.client.LatLon
	 */
	public ArrayList<LatLon> parse(String text) {
		ArrayList<LatLon> points = new ArrayList<LatLon>();
		int p = text.indexOf(POLYGON);
		int s = text.indexOf(")");
		if (s < 0) {
			System.err.println("Loppusulku puuttuu: "+s);
			s = text.length();
		}
		try {
			String[] pisteet = text.substring(p+POLYGON.length(), s).split(",");
			for (int i=0; i < pisteet.length; i++) {
				double lat, lon;
				String[] lonlat = pisteet[i].split(" ");
				try {
					lat = Double.parseDouble(lonlat[1]); 
					lon = Double.parseDouble(lonlat[0]);
					points.add(new LatLon(lat, lon));
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.err.println(e.getMessage());
					System.err.println(text);
				}
			}
		} catch (java.lang.StringIndexOutOfBoundsException e) {
			System.err.println(e.getMessage());
			System.err.println(text);	
		}
		return points;
	}
}
