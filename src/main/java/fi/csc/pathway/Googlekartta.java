/**
 *  Pathway project funded by Academy of Finland. 
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
import java.util.Date;
import java.util.ListIterator;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.text.Format;
import java.time.LocalDateTime;
import java.net.URLEncoder;

import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.GoogleMapControl;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.server.Sizeable;

/**
 * Karttatoiminnot on toteutettu com.vaadin.tapio.googlemaps.GoogleMap vaatimen lisäpalikalle
 * Muodostetaan kysely query.jsp palvelulle, joka palauttaa tiedot tietokannasta XML:nä.
 * 
 * @author pj
 *
 */
public class Googlekartta implements Serializable {
	
	private static final long serialVersionUID = 3953325456264644582L;
	static final String apikey = "AIzaSyDl-9f9zH8Yjzi6iK8VjS_xPwG7L8cBksY";
	static final String HOST = "http://localhost:";
	static final String QUERY = "/query.jsp?startdate=";
	static final String TUTKA = "&tutka=";
	static final String KULMA = "&kulma=";
	static final String TYYPPI = "&tyyppi=";
	static final String ALUE = "&alue=";
	static final String KÄRJET = "&vertices=";
	static final String END = "&enddate=";
	static final String SPACE = " ";
	static final String COMMA = ",";
	static final float MAPLEVEYS = 720; //px
	static final float MAPKORKEUS = 600; //px
	public static final int CLICKZOOM = 8;
	private static final int MINZOOM = 5;

	final Tutkat tutkat = new Tutkat();
	private GoogleMap googleMap;
	private Label time;
	private Label lkm;
	private ArrayList<Result> jcr ;
	private OptionGroup t;
	private int i=0;
	Format formatter = new SimpleDateFormat("yyyy-MM-dd");
	private String pname;
	private OptionGroup kulma;
	private OptionGroup tyyppi;
	private OptionGroup vertices;
	private ArrayList<GoogleMapPolygon> arealist = new ArrayList<GoogleMapPolygon>();
	
	/**
	 * Constructori kopioi käyttöliittymäkomponentit olion käyttöön
	 * 
	 * @param aika Label 	aikaleima tietokannasta kartalle piirrettäville sateille
	 * @param tutkat OptionGroup	Tutkan valinta
	 * @param kulma OptionGroup		Tutkan käyttämän kulman valinta
	 * @param tyyppi OptionGroup	Sateen tyyppi, vesi tai rae
	 * @param kärjet OptionGroup	Monikulmion kärkien lukumäätä
	 * @param name String			Portti, jossa ohjelmaa ajava tomcat pyörii + tämän portletin nimi
	 */
	Googlekartta(Label aika, OptionGroup tutkat, OptionGroup kulma, OptionGroup tyyppi, OptionGroup kärjet, String name, Label lkm) {	
		this.time = aika;
		this.pname = name;
		this.t = tutkat;
		this.kulma = kulma;
		this.tyyppi = tyyppi;
		this.vertices = kärjet;
		this.lkm = lkm;
	}
	
	/**
	 * Muodostetaan kysely palvelulle, mitä varten luetaan kaikki käyttöliimäkomponentit.
	 * 
	 * @param startd Date
	 * @param endd Date
	 * @param tutka int (tai Integer )tutkan numero
	 * @return boolean onnistuiko kysely, false jos dataa ei löytynyt 
	 */
	public boolean getMap(Date startd, Date endd, Object tutka) {
		String qalue = alue(googleMap.getCenter(), 360.0/Math.pow(2,googleMap.getZoom())); //kartalla näkyvä
		int t = (int)tutka;
		//googleMap = new GoogleMap(Tutkat.SIJAINNIT[t], 10, apikey);		
		asetukset();
		XMLparser client = new XMLparser();
		String starts = formatter.format(startd);
		String ends = formatter.format(endd);
		this.jcr = client.createClient(HOST+pname+QUERY+starts+
				TUTKA+Tutkat.LYHENTEET[t]+KULMA+kulmaparsinta(kulma, t)+
				TYYPPI+tyyppiparsinta(tyyppi)+KÄRJET+vertices.getValue()+
				ALUE+qalue+END+ends);
		jcr.sort(Result.ResultComparator);
		i = 0;
		if (!jcr.isEmpty()) {
			addOverlay( jcr.get(i).polygon );
			lkm.setValue(Integer.toString(jcr.size()));
		} else {
			Notification.show("Query return empty result.", "Please try other query", Notification.Type.WARNING_MESSAGE);
			return false;
		}
		return true;	
	}
	
	

	/**	
	 * Käyttäjä painoi next-painiketta: poistetaan vanhat piirrokset ja piiretään uudet
	 * 
	 * @return  boolean true, jos data loppui
	 */
	public boolean updateMap() {
		ListIterator<GoogleMapPolygon> li = arealist.listIterator();
		while (li.hasNext()) {
			googleMap.removePolygonOverlay(li.next());
			// arealist tyhjennys ei kannattane
		}
		arealist = new ArrayList<GoogleMapPolygon>(); // luotetetaan roskienkerääjään
		i++; 
		try {
			if ( addOverlay( jcr.get(i).polygon) ) {
				return true;
			}
		} catch ( java.lang.IndexOutOfBoundsException e) {
			return true;
		}
		lkm.setValue(Integer.toString(jcr.size()-i));
		return (i == jcr.size()-1);
	}
	
	/**
	 * Oikeasti piirretään sadealueita kartalle. Piirretään kaikki samanaikaiset kerralla
	 * @param points ArrayList<LatLon> Sade/Rae/alueen ääriviivat (peräisin tietokannasta)
	 */	
	private boolean addOverlay(ArrayList<LatLon> points) {
		GoogleMapPolygon overlay = new GoogleMapPolygon(points,
				"#ae1f1f", 0.8, "#194915", 0.5, 3);
		LocalDateTime tmptime = jcr.get(i).datet;
		this.time.setValue(tmptime.toString());		
		googleMap.addPolygonOverlay(overlay);
		arealist.add(overlay);
		try {
			while (tmptime.equals(jcr.get(i+1).datet)) {
				overlay = new GoogleMapPolygon(jcr.get(++i).polygon,
						"#ae1f1f", 0.8, "#194915", 0.5, 3);
				googleMap.addPolygonOverlay(overlay);
				arealist.add(overlay);
			}
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			return true;
		}
		return false;
	}
	
	/**
	 * GoogleMap asetukset ja toiminnot
	 */
	private void asetukset() {
		googleMap.setWidth(MAPLEVEYS, Sizeable.Unit.PIXELS);
		googleMap.setHeight(MAPKORKEUS, Sizeable.Unit.PIXELS);
		//googleMap.setSizeFull(); ei toimi :-(
		googleMap.removeControl(GoogleMapControl.StreetView);
		googleMap.setMinZoom(MINZOOM);
		googleMap.addStyleName("gmapoverflow");	
		googleMap.addMapClickListener(new MapClickListener() { 
			private static final long serialVersionUID = 2759997992621914028L;
			@Override
            public void mapClicked(LatLon position) {
            	googleMap.setCenter(position);
                        //+ position.getLat() , position.getLon()
            	googleMap.setZoom(CLICKZOOM);
              
            }
        });
		googleMap.addMarkerClickListener(new MarkerClickListener() {
			private static final long serialVersionUID = 5415142881854153766L;
			@Override
            public void markerClicked(GoogleMapMarker clickedMarker) {
            	googleMap.setCenter(clickedMarker.getPosition());
            	googleMap.setZoom(CLICKZOOM);
            	String m = clickedMarker.getCaption();
            	t.select(Tutkat.resolve.get(m));
            }
		 });
	}

	/**
	 * Sovelluksen käynnistyessä näytettävä Suomen kartta, jolla tutkasemien sijainnit
	 * @return Component GoogleMap
	 */
	public GoogleMap getOrigMap() {
		googleMap = new GoogleMap(new LatLon(64.963, 24.7), MINZOOM, apikey);
		asetukset();
		tutkat.addMarkers(googleMap);
		return googleMap;
	}
	
	/**
	 * 
	 * static final int GLOBE_WIDTH = 256; // a constant in Google's map projection
	 * Tästä seuraa että kaava 360/2**zoom antaa 256pikseliä asteina. Huomaa että 256
	 * on tosiaan pikseleitä: kuvan kokoa muutettaessa (MAPLEVEYS ja -KORKEUS yllä) on kaavaa korjattava.
	 *
	 * 
	 * @param kp LatLon keskipiste
	 * @param p256 double reunojen etäisyys asteina kekispisteetä 360/2**zoom kaavasta
	 * @return postgis LINESTRING, josta query.jsp yritää parsia geometrian
	 * Tässä tapauksessa piirrämme neliön keskipisteen ympärille
	 */
	private String alue(LatLon kp, double p256) {
		StringBuffer sb = new StringBuffer();
		sb.append(kp.getLon()-p256);
		sb.append(SPACE);
		sb.append(kp.getLat()-p256);
		sb.append(COMMA);
		sb.append(kp.getLon()-p256);
		sb.append(SPACE);
		sb.append(kp.getLat()+p256);
		sb.append(COMMA);
		sb.append(kp.getLon()+p256);
		sb.append(SPACE);
		sb.append(kp.getLat()+p256);
		sb.append(COMMA);
		sb.append(kp.getLon()+p256);
		sb.append(SPACE);
		sb.append(kp.getLat()-p256);
		sb.append(COMMA);
		sb.append(kp.getLon()-p256);
		sb.append(SPACE);
		sb.append(kp.getLat()-p256);
		try {
			return URLEncoder.encode(sb.toString(),"UTF-8");
		} catch (UnsupportedEncodingException e) {	
			System.err.println("UTF-8 koodaus hukassa: pahoja ongelmia");
			return sb.toString();
		}
	}
	
	/**
	 * Käsitelee kulmanvalinnan arvon. Eri tutkilla on eri matala kulma!
	 * @param kulma2 OptionGroup kulman valintaan
	 * @return String tietokantataulun nimen osa
	 */
	private String kulmaparsinta(OptionGroup kulma2, int t) {
		String kulma = kulma2.getValue().toString();
		if (kulma.equals(Tutkat.LOW)) {
			return Tutkat.KULMAT[t];
		} 
		return kulma.replaceFirst("\\.", "_"); //tietokantataulun nimessä on alaviiva
	}
	
	/**
	 * Muuntaa tutkan havainnoista lasketun ilmiön - rae- tai vesisateen - tietokantataulun nimeksi. 
	 *
	 * @param tyyppi2 OptionGroup Ilmiön valinta: Sade tai rae.
	 * @return String Taulun nimen alkuosa tietokannassa. Toistaiseksi z tai hclass.
	 */
	private String tyyppiparsinta(OptionGroup tyyppi2) {
		String taulu = "z"; //sade
		if (tyyppi.getValue().toString().equals(Ilmiot.RAE)) {
			taulu = "hclass";
		}		
		return taulu;
	}
}
