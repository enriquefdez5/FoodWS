package es.uniovi.miw.foodws.models;


import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @NotNull
    private String menuName;
    @NotBlank
    @NotNull
    private String menuDescription;
    @NotBlank
    @NotNull
    @Min(0)
    private Double menuPrice;
    @ManyToOne(targetEntity = Restaurant.class)
    private Restaurant restaurant;
    @ManyToMany(targetEntity = Ingredient.class)
    private Set<Ingredient> ingredientSet;


    public Menu() {
    }

    public Menu(String menuName, String menuDescription, Double menuPrice, Restaurant restaurant,
                Set<Ingredient> ingredientSet) {
        this.menuName = menuName;
        this.menuDescription = menuDescription;
        this.menuPrice = menuPrice;
        this.restaurant = restaurant;
        this.ingredientSet = ingredientSet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuDescription() {
        return menuDescription;
    }

    public void setMenuDescription(String menuDescription) {
        this.menuDescription = menuDescription;
    }

    public Double getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(Double menuPrice) {
        this.menuPrice = menuPrice;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Set<Ingredient> getIngredientSet() {
        return ingredientSet;
    }

    public void setIngredientSet(Set<Ingredient> ingredientSet) {
        this.ingredientSet = ingredientSet;
    }
}
