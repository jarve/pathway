/**
 * Stax-parser
 * 
 * TutkaUI -portlet by CSC - IT Center for Science Ltd. is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *  Permissions beyond the scope of this license may be available at http://www.csc.fi/english/contacts.
 *  
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">TutkaUI-portlet</span> by <a xmlns:cc="http://creativecommons.org/ns#" href="http://avaa.tdata.fi/" property="cc:attributionName" rel="cc:attributionURL">CSC - IT Center for Science Ltd.</a> is licensed under a
 *  <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License</a>.<br />
 * Permissions beyond the scope of this license may be available at <a xmlns:cc="http://creativecommons.org/ns#" href="http://www.csc.fi/english/contacts" rel="cc:morePermissions">http://www.csc.fi/english/contacts</a>.
 */
package fi.csc.pathway;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <XmlRootElement>
<datetime>2013-07-01 08:10:00.0</datetime>
<polygon>
POLYGON((24.1747 67.4622,24.186 67.461,24.2113 67.478,24.2261 67.4962,24.2307 67.5157,24.2473 67.5337,24.2757 67.55,24.2473 67.5337,24.2307 67.5157,24.2038 67.4989,24.1814 67.5017,24.1747 67.4622))
</polygon>
<datetime>2013-07-01 08:15:00.0</datetime>
<polygon>
POLYGON((24.3684 67.6816,24.4031 67.6965,24.4238 67.6922,24.3928 67.6987,24.3684 67.6816))
</polygon>
...
 * @author pj
 * As standard read XML content of the URL as possible 
 * 
 *  NOTE Java 8
 */
public class XMLparser {
	private static Log log = LogFactoryUtil.getLog(XMLparser.class);
	//String URL = "http://localhost:8181/tutkaUI/query.jsp";
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

	/**
	 * The name of the method come from JAXB, but didn't get it to unmarshal 
	 * @param url String 
	 * @return ArrayList<Result> The result contains one datetime and polugon
	 */
	public ArrayList<Result> createClient(String url) {
		ArrayList<Result>cr  = new ArrayList<Result>();
		//ArrayList<ArrayList<LatLon>> polygons =  new ArrayList<ArrayList<LatLon>>();
		PolygonParser pp = new PolygonParser();
		try {
			
			URL u;
			try {
				u = new URL(url);
			} catch ( java.net.MalformedURLException e) {
				log.error("java.net.MalformedURLException" + url);
				//u = new URL(URL);
				return cr;
			}
			InputStream in = u.openStream();
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(in);
			while (true) {
			    int event = parser.nextTag();
			    if (event == XMLStreamConstants.END_ELEMENT) {
			       if (parser.getLocalName().equals("XmlRootElement")) {
			    	   parser.close();
			    	   break;
			       }
			    }
			    if (event == XMLStreamConstants.START_ELEMENT) {
			        if (parser.getLocalName().equals("datetime")) {
			        	//dates.add(parser.getElementText());
			        	LocalDateTime dt = LocalDateTime.parse(parser.getElementText().trim(), dtf);
			        	int e2 = parser.nextTag();
			        	if (e2 != XMLStreamConstants.START_ELEMENT) {
			        		System.out.println("Syntax error: "+parser.getLocalName());
			        	}
			        	if  (parser.getLocalName().equals("polygon")) {
			        		cr.add( new Result(dt, pp.parse(parser.getElementText())));			        		
			        	}
			        }
			    }
			}

		} catch (Exception e) {
			e.printStackTrace();
			//log.error(e.getMessage());
		}
		return  cr;
	}
}
