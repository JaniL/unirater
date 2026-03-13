package unicaferater.domain.lounastyokalu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantFeedItem {

    private Long id;
    private String title;
    private String slug;
    private RestaurantResponse menuData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public RestaurantResponse getMenuData() {
        return menuData;
    }

    public void setMenuData(RestaurantResponse menuData) {
        this.menuData = menuData;
    }
}
