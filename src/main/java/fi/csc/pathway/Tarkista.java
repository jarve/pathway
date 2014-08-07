/**
 * 
 */
package fi.csc.pathway;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;

/**
 * Tämän min- ja maxValue riippuvat tietokannan sisällöstä. 
 * 
 * @author pj
 * Secondary functionality
 */
public class Tarkista implements Serializable {
	
	private static final long serialVersionUID = 3953325456264644581L;
	private PopupDateField enddate;
	private PopupDateField startdate;
	Calendar calendar = new GregorianCalendar();
	final Date minValue = new GregorianCalendar(2013,4,1).getTime();
	final Date maxValue = new GregorianCalendar(2013, 11, 30).getTime();
	static final String FORMAT = "yyyy-MM-dd";
	
	Tarkista(PopupDateField enddate, PopupDateField startdate) {
		this.enddate = enddate;
		this.startdate = startdate;
		this.startdate.addValueChangeListener(changeValueListener_from);
		this.enddate.addValueChangeListener(changeValueListener_to);
		this.startdate.setImmediate(true);
		this.enddate.setImmediate(true);	
		this.startdate.setValidationVisible(true);
		this.enddate.setValidationVisible(true);
		this.enddate.setDateFormat(FORMAT);
		this.startdate.setDateFormat(FORMAT);
		this.startdate.setValue(new GregorianCalendar(2013,6,1).getTime());
		this.enddate.setValue(new GregorianCalendar(2013,6,10).getTime());
	}
	
	 private static Date getModifiedDate(Date currentdate, int modifier) {
	    	Calendar calendar = new GregorianCalendar();
	    	calendar.setTime(currentdate);
	    	if (modifier == 30){
	    		calendar.add(Calendar.MONTH, 1);
	    	}
	    	if (modifier == -30) {
	    		calendar.add(Calendar.MONTH, -1);
	    	}
	    	if (modifier != 30 && modifier != -30){
	    		calendar.add(Calendar.DAY_OF_MONTH, modifier);
	    	}
	    	return calendar.getTime();
	    }
	
    final ValueChangeListener changeValueListener_to = new ValueChangeListener() {
 
		private static final long serialVersionUID = -5974051423624746111L;

		@Override
        public void valueChange(ValueChangeEvent event) {
            try {
            	enddate.removeAllValidators();
            	enddate.addValidator(new DateRangeValidator("To-date must be set between 2013-05-01 and 2013-11-30",minValue,getModifiedDate(calendar.getTime(),1), Resolution.DAY));
                enddate.validate();
                enddate.setComponentError(null);
            } catch (Exception e) {
            	new Notification("Invalid To-date",
            		    "<br/>To-date should be set between 2013-05-01 and 2013-11-30",
            		    Notification.Type.WARNING_MESSAGE, true)
            		    .show(Page.getCurrent());
            	startdate.setValue(getModifiedDate(calendar.getTime(), -1));
            	enddate.setValue(maxValue);    
                enddate.markAsDirty();
            }            
        }
    };
	
    final ValueChangeListener changeValueListener_from = new ValueChangeListener() {
    	
		private static final long serialVersionUID = -5974051423624746112L;
        @Override
        public void valueChange(ValueChangeEvent event) {
            try {
				startdate.removeAllValidators();
				startdate.addValidator(new DateRangeValidator("From-date must be set between 2013-05-01 and 2013-11-30", minValue,maxValue,Resolution.DAY));
                startdate.validate();
                startdate.setComponentError(null);
            } catch (Exception e) {
            	new Notification("Invalid From-date",
            		    "<br/>From-date should be set between 2013-05-01 and 2013-11-30",
            		    Notification.Type.WARNING_MESSAGE, true)
            		    .show(Page.getCurrent());
            	startdate.setValue(getModifiedDate(calendar.getTime(), -1));
            	enddate.setValue(calendar.getTime());
                startdate.markAsDirty();
            }
        }
    };
    
	public Component getstartdate() {
		return startdate;
	}

	public Component getenddate() {
		return enddate;
	}
    
  
	
}
