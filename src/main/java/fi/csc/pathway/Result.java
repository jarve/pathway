/**
 * 
 */
package fi.csc.pathway;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import com.vaadin.tapio.googlemaps.client.LatLon;

/**
 * @author pj
 *
 */
public class Result implements Serializable /*Comparable<Result>*/ {
	private static final long serialVersionUID = -9028060857053534742L;
	
	public LocalDateTime datet;
	public ArrayList<LatLon> polygon;
	
	public Result(LocalDateTime dt, ArrayList<LatLon> p) {
	  this.datet = dt;
	  this.polygon = p;
	}
	
	
	public static Comparator<Result> ResultComparator 
    = new Comparator<Result>() {
	
	public int compare(Result t1, Result t2) {
		LocalDateTime dt1 = t1.getDatetime();
		LocalDateTime dt2 = t2.getDatetime();
		if (dt1.isEqual(dt2)) {
			return 0;
		} else if (dt1.isBefore(dt2)) {
			return -1;
		}
		return 1;
	}
	};

	public LocalDateTime getDatetime() {
		return this.datet;
	}

	/*@Override
	public int compareTo(Result o) {
		return o.datet.isBefore(this.datet)
		return 0;
	}*/

}
