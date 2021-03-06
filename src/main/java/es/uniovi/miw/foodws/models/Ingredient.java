package es.uniovi.miw.foodws.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "INGREDIENTS")
@JsonIgnoreProperties("menuSet")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long fatSecretId;
    @NotBlank
    @NotNull
    private String ingredientName;
    @NotNull
    private int grams;
    @ManyToMany(targetEntity = Menu.class, mappedBy = "ingredientSet")
    private Set<Menu> menuSet;


    public Ingredient() {
    }

    public Ingredient(Long fatSecretId, String ingredientName, int grams) {
        this.fatSecretId = fatSecretId;
        this.ingredientName = ingredientName;
        this.grams = grams;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFatSecretId() {
        return fatSecretId;
    }

    public void setFatSecretId(Long fatSecretId) {
        this.fatSecretId = fatSecretId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public int getGrams() {
        return grams;
    }

    public void setGrams(int grams) {
        this.grams = grams;
    }

    public Set<Menu> getMenuSet() {
        return menuSet;
    }

    public void setMenuSet(Set<Menu> menuSet) {
        this.menuSet = menuSet;
    }
}

