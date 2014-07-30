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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedPortletSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.annotations.VaadinServletConfiguration;


//import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;

@Theme("valo")
@SuppressWarnings("serial")
@Widgetset("fi.csc.pathway.tutkaUIWidgetSet")
public class TutkaUI extends UI {

 	private static final long serialVersionUID = 2872281444404886611L;
	private static Log log = LogFactoryUtil.getLog(TutkaUI.class);

			
    @WebServlet(asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TutkaUI.class, widgetset = "fi.csc.pathway.tutkaUIWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
    
    /**
     * Pääohjelma, joka luo käyttöliittymän.
     */
    @Override
    protected void init(VaadinRequest request) {
        final String portletContextName = getPortletContextName(request);
        final VerticalLayout vasenpalkki = new VerticalLayout();        
        final HorizontalLayout layout  = new HorizontalLayout(); //main
        final HorizontalLayout karttapalkki = new HorizontalLayout();
        final Label hr = new Label("  ____________________", ContentMode.HTML);
        final Label aika = new Label("Time", ContentMode.HTML);
        final Button next = new Button("Next");
    	final OptionGroup tyyppi = new OptionGroup("Type");	
    	final OptionGroup tutka = new OptionGroup("Radar");
		final OptionGroup kulma = new OptionGroup("Angle");	
		final OptionGroup kärjet =  new OptionGroup("Min vertices");	
        final Googlekartta gma = new Googlekartta(aika, tutka, kulma, tyyppi, kärjet, portletContextName);
        final Tutkat tutkat = new Tutkat();
    	final PopupDateField enddate = new PopupDateField("To:");
		final PopupDateField startdate = new PopupDateField("From:");		
		final GoogleMap oldComponent = gma.getOrigMap();
		final GridLayout parametrit = new GridLayout(2,2);     
	    tutkat.alusta(tutka, kärjet, oldComponent);
	    parametrit.addComponent(tutka);
	    parametrit.addComponent(kärjet);
	    Ilmiot ilmio = new Ilmiot(tyyppi, tutka);	
		kulma.addItem("1.5");
		kulma.addItem("0.7");
		kulma.addItem(Tutkat.LOW); //luostolla ei ole matalampaa kuin 0.7
		kulma.setValue("1.5");
		parametrit.addComponent(kulma);	
		parametrit.addComponent(ilmio.getTyyppi());
		vasenpalkki.addComponent(parametrit);
		Tarkista tarkista = new Tarkista(enddate, startdate);
		vasenpalkki.addComponent(tarkista.getstartdate());
		vasenpalkki.addComponent(tarkista.getenddate());
		next.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = -8146345475859196611L;

			public void buttonClick(ClickEvent event) {            	
            	if (gma.updateMap()) {
            		next.setEnabled(false);
            	}
            }
         });
		final Button button = new Button("Query");
		button.addClickListener(new Button.ClickListener() {
			//Component oldC = oldComponent;
			private static final long serialVersionUID = -8146345475859196612L;

			public void buttonClick(ClickEvent event) {
				//Component newComponent = 
				if (gma.getMap(startdate.getValue(),enddate.getValue(), tutka.getValue())) { 
					//karttapalkki.replaceComponent(oldC, newComponent);
					//oldC = newComponent;
					vasenpalkki.addComponent(hr);
					vasenpalkki.addComponent(aika);
					vasenpalkki.addComponent(next);
				}
			}
		});
        vasenpalkki.addComponent(button);
        layout.addComponent(vasenpalkki);        
        karttapalkki.setHeight(600, Sizeable.Unit.PIXELS);  
        karttapalkki.addComponent(oldComponent);
        layout.addComponent(karttapalkki);
	    layout.setMargin(true);
	    setContent(layout);
    }

    private String getPortletContextName(VaadinRequest request) {
        WrappedPortletSession wrappedPortletSession = (WrappedPortletSession) request
                .getWrappedSession();
        PortletSession portletSession = wrappedPortletSession
                .getPortletSession();

        final PortletContext context = portletSession.getPortletContext();   
        final String portletContextName = context.getPortletContextName();
        File f = new File("/opt/avaa/liferay-portal/tomcat/shared/radar.properties");
        Properties prop = new Properties(); // voisi parsia myös suoraan tomcatin configista
        FileInputStream in = null;
		try {
			in = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
        String portti = prop.getProperty("portti");  
        if (null == portti) {
        	portti = "8181";      // tomcat
        	log.error("Konfiguraation lukuvirhe: portiksi asetettu 8181");  
        } //palvelu on käytössä ulkopuolle myös portissa 80 (apache)
        String parvo = portti +"/"+  portletContextName;
        return parvo;
    }
    
}
