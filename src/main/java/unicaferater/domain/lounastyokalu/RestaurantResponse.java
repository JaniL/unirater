package unicaferater.domain.lounastyokalu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantResponse {
    /**
     * Pyynnön status
     */
	private String status;

    private Long id;

    private String name;

    private Integer areacode;

    private Map<String, Object> visitingHours;

    /**
     * Ravintolan ruokalistat
     */
	private List<MenuOfTheDay> data;

    private List<MenuOfTheDay> menus;

    /**
     * Ravintolan info (mm. aukioloajat)
     */
    private RestaurantInformation information;


    /**
     * Hakee ravintolan ruokalistat
     * @return Palauttaa ravintolan ruokalistat
     */
	public List<MenuOfTheDay> getData() {
		if (data != null) {
            return data;
        }
        return menus;
	}

    /**
     * Asettaa ravintolan ruokalistat
     * @param data
     */
	public void setData(List<MenuOfTheDay> data) {
		this.data = data;
        this.menus = data;
	}

    public List<MenuOfTheDay> getMenus() {
        if (menus != null) {
            return menus;
        }
        return data;
    }

    public void setMenus(List<MenuOfTheDay> menus) {
        this.menus = menus;
        this.data = menus;
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
        return information;
    }

    /**
     * Asettaa ravintolaan liittyvän informaation
     * @param information
     */
    public void setInformation(RestaurantInformation information) {
        this.information = information;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAreacode() {
        return areacode;
    }

    public void setAreacode(Integer areacode) {
        this.areacode = areacode;
    }

    public Map<String, Object> getVisitingHours() {
        return visitingHours;
    }

    public void setVisitingHours(Map<String, Object> visitingHours) {
        this.visitingHours = visitingHours;
    }


    @Override
	public String toString() {
        if ("OK".equals(this.getStatus()) && this.getData() != null) {
            return "Rajapinnan vastaus, jonka status on " + this.getStatus() + " ja se sisältää " + this.getData().size() + " ruokalistaa.";
        }
		return "Rajapinnan vastaus, jonka status on " + this.getStatus() + ".";
	}
}
