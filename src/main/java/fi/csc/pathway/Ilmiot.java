/**
 * 
 */
package fi.csc.pathway;

import java.io.Serializable;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Component;
//import com.vaadin.ui.ListSelect;
import com.vaadin.ui.OptionGroup;

/**
 * @author pj
 *
 */
public class Ilmiot implements Serializable {
	
	private static final long serialVersionUID = 8120377098934783114L;
	private static final String SADE = "Rain";
	static final String RAE = "Hail";
	OptionGroup tyyppi;
	OptionGroup tutka;

	/**
	 * Alustaa sateen tyyppi kentän.
	 * 
	 * @param tyyppi OptionGroup Vesi- vai raesade valinta.
	 * @param tutka OptionGroup Tarvitaan toistaiseksi, koska kaikilla tutkilla ei ole raesade dataa.
	 */
	Ilmiot(OptionGroup tyyppi, OptionGroup tutka) {
		this.tutka	= tutka;
		this.tyyppi = tyyppi;
		this.tyyppi.addItem(SADE);
		this.tyyppi.addItem(RAE);
		this.tyyppi.setValue(SADE);
		this.tyyppi.addValueChangeListener(changeValueListener_ilmiö);
	}
	
	  final ValueChangeListener changeValueListener_ilmiö = new ValueChangeListener() {
	    	private static final long serialVersionUID = -5974051423624746113L;
	        /**
	         * Perustuu tietokannan sisältöön ja voi poistaa, kun joka tutkalle on raesade taulu
	         */	 
	        @Override
       public void valueChange(ValueChangeEvent event) {
	        	if (event.getProperty().getValue().toString().equals(RAE)) {
	        		tutka.setItemEnabled(Tutkat.LUOSTO, false);
	        		tutka.setItemEnabled(Tutkat.VIMPELI, false);
	        		//tutka.removeItem(Tutkat.LUOSTO);
	        		//tutka.removeItem(Tutkat.VIMPELI);
	        	} else {
	        		tutka.setItemEnabled(Tutkat.LUOSTO, true);
	        		tutka.setItemEnabled(Tutkat.VIMPELI, true);
	        	}
	        }
	    };
	



	public Component getTyyppi() {
		return tyyppi;
	}
	}
