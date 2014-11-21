package unicaferater.domain;

import java.util.List;
import unicaferater.domain.Restaurant;

public class RestaurantsResponse {
	private String status;
	private List<Restaurant> data;
	
	public List<Restaurant> getData() {
		return data;
	}
	
	public void setData(List<Restaurant> data) {
		this.data = data;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return data.toString();
	}
}
