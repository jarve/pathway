/**
 * perustuu ilmatieteenlaitos.fi/suomen-tutkaverkko sivuun
 */
package fi.csc.pathway;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.OptionGroup;

/**
 * @author pj
 * Nämä varmaan voisi tallentaa tietokantaan ja lukea sieltä
 */
public class Tutkat implements Serializable {
	public static final String LOW = "Low";
	static final int IKAALINEN = 0;
	static final int KORPPOO = 1;
	static final int KOUVOLA = 2;
	static final int KUOPIO = 3;
	static final int LUOSTO = 4; 
	static final int UTAJÄRVI = 5;
	static final int VANTAA = 6;
	static final int VIMPELI = 7;
	static final String[] NIMET = { "Ikaalinen", "Korppoo", "Kouvola", "Kuopio", "Luosto", "Utajärvi",
		"Vantaa", "Vimpeli" };
	static final String[] LYHENTEET = { "ika", "kor", "anj", "kuo", "luo", "uta", "van", "vim" };
	static final String[] KULMAT = { "0_3", "0_5", "0_5", "0_3", "0_7", "0_3", "0_5", "0_3" };
	static final LatLon[] SIJAINNIT = {
		new LatLon(61.7673, 23.0764), new LatLon(60.1285, 21.6434), new LatLon(60.9039, 27.1081),
		new LatLon(62.8624, 27.3815), new LatLon(67.1391, 26.8969), new LatLon(64.7749, 26.3189),
		new LatLon(60.2706, 24.869), new LatLon(63.1048, 23.8209)
	};
	static Map<String, Integer> resolve = new HashMap<String, Integer>();
	private static final long serialVersionUID = 3953325456264644583L;
	
	/**
	 * Luo tutkavalinnan ja käyttäjän valittua tutkan zoomaa kartan oikeaan kohtaan
	 * Luo valinnan minimimäärälle monikulmion kärkiä, jolla aineistosta voidaan karsia pois laivat ym. häiriöt.
	 * @param t ListSelect vaadin komponentti
	 * @param gm GoogleMap
	 */
	void alusta(OptionGroup t, OptionGroup kärjet, final GoogleMap gm) { 
		for (int i = 0; i <= VIMPELI; i++) {
			t.addItem(i);
			t.setItemCaption(i, NIMET[i]);
		}
		t.select(VANTAA);
		t.setNullSelectionAllowed(false);
		//t.setRows(VIMPELI+1);
		//t.setHeight("300px");
		//t.setImmediate(true);
		//t.setWidth("100px");
		t.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1587681534765672693L;

			public void valueChange(ValueChangeEvent event) {
				gm.setCenter(SIJAINNIT[(int)event.getProperty().getValue()]);
				gm.setZoom(Googlekartta.CLICKZOOM);
			}
		});
		kärjet.addItem(5);
		kärjet.addItem(10);
		kärjet.addItem(15);
		kärjet.addItem(20);
		kärjet.addItem(25);
		kärjet.addItem(30);
		kärjet.addItem(35);
		kärjet.setValue(15);		
	}
	
	/**
	 * Tutkien sijainnit kartalle
	 * @param gm GoogleMap
	 */
	void addMarkers(GoogleMap gm) {
		for (int i = 0; i <= VIMPELI; i++) {
			GoogleMapMarker gmarker = new GoogleMapMarker(NIMET[i], SIJAINNIT[i],true, null);
			gmarker.setDraggable(false);
			gm.addMarker(gmarker);
		}
		//this.gm = gm;
	}
	
	static {
		for (int i = 0; i <= VIMPELI; i++) {
			resolve.put(NIMET[i], i);
		}
	}
}
