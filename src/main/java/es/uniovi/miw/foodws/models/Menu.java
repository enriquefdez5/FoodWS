package es.uniovi.miw.foodws.models;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "MENUS")
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
    @NotNull
    @Min(0)
    private Double menuPrice;
    @ManyToOne(targetEntity = Restaurant.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    private Restaurant restaurant;
    @ManyToMany(targetEntity = Ingredient.class, cascade = CascadeType.ALL)
    @JoinTable(
            name = "menus_ingredients",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> ingredientSet;

    @Transient
    private FatSecretFood fsf;

    public Menu() {
    }

    public Menu(String menuName, String menuDescription, Double menuPrice, Restaurant restaurant) {
        this.menuName = menuName;
        this.menuDescription = menuDescription;
        this.menuPrice = menuPrice;
        this.restaurant = restaurant;
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

    public FatSecretFood getFsf() {
        return fsf;
    }

    public void setFsf(FatSecretFood fsf) {
        this.fsf = fsf;
    }
}
