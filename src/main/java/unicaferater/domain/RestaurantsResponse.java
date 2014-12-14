package unicaferater.domain;

import java.util.List;
import unicaferater.domain.Restaurant;

public class RestaurantsResponse {
    /**
     * Pyynnön status
     */
	private String status;

    /**
     * Pyynnön ravintolat
     */
	private List<Restaurant> data;

    /**
     * Hakee pyynnön ravintolat
     * @return Palauttaa pyynnön listaamat ravintolat
     */
	public List<Restaurant> getData() {
		return data;
	}

    /**
     * Asettaa ravintolat
     * @param data
     */
	public void setData(List<Restaurant> data) {
		this.data = data;
	}

    /**
     * Hakee pyynnön statuksen
     * @return Palauttaa pyynnön statuksen
     */
	public String getStatus() {
		return status;
	}

    /**
     * Asettaa pyynnön statuksen
     * @param status
     */
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
        if (this.getStatus().equals("OK")) {
            return "Vastaus ravintoloiden listauksesta, jonka status on " + this.getStatus() + " ja johon kuuluu " + this.getData().size() + " ravintolaa.";
        }
        return "Vastaus ravintoloiden listauksesta, jonka status on " + this.getStatus() + ".";
	}
}
