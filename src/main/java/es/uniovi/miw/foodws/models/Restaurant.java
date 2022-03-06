package es.uniovi.miw.foodws.models;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @NotNull
    private String restaurantName;
    @NotBlank
    @NotNull
    private String restaurantAddress;
    @OneToMany(targetEntity = Menu.class, mappedBy = "restaurant")
    private Set menuSet;

    public Restaurant() {
    }

    public Restaurant(String restaurantName, String restaurantAddress, Set menuSet) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.menuSet = menuSet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public Set getMenuSet() {
        return menuSet;
    }

    public void setMenuSet(Set menuSet) {
        this.menuSet = menuSet;
    }
}
