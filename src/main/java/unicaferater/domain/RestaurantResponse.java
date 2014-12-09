package unicaferater.domain;

import java.util.List;
import unicaferater.domain.Restaurant;

public class RestaurantResponse {
    /**
     * Pyynnön status
     */
	private String status;

    /**
     * Ravintolan ruokalistat
     */
	private List<MenuOfTheDay> data;

    /**
     * Ravintolan info (mm. aukioloajat)
     */
    private RestaurantInformation information;


    /**
     * Hakee ravintolan ruokalistat
     * @return Palauttaa ravintolan ruokalistat
     */
	public List<MenuOfTheDay> getData() {
		return data;
	}

    /**
     * Asettaa ravintolan ruokalistat
     * @param data
     */
	public void setData(List<MenuOfTheDay> data) {
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

    /**
     * Hakee ravintolaan liittyvän informaation
     * OLIO PUUTTUU
     * @return Palauttaa ravintolaan littyvän informaation
     */
    public RestaurantInformation getInformation() {
//        return information;
        return null;
    }

    /**
     * Asettaa ravintolaan liittyvän informaation
     * @param information
     */
    public void setInformation(RestaurantInformation information) {
        this.information = null;
    }


    @Override
	public String toString() {
		return "Pyyntö, jonka status on " + this.getStatus() + " ja siihen kuuluu " + this.getData().size() + " ruokalistaa.";
	}
}
