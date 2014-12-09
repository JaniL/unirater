package unicaferater.domain;

import java.util.List;
import unicaferater.domain.Restaurant;

public class RestaurantResponse {
	private String status;
	private List<MenuOfTheDay> data;

    private RestaurantInformation information;
	
	public List<MenuOfTheDay> getData() {
		return data;
	}
	
	public void setData(List<MenuOfTheDay> data) {
		this.data = data;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

    public RestaurantInformation getInformation() {
//        return information;
        return null;
    }

    public void setInformation(RestaurantInformation information) {
        this.information = null;
    }

    @Override
	public String toString() {
		// TODO Auto-generated method stub
		return data.toString();
	}
}
