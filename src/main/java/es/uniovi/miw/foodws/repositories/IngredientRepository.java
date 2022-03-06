package es.uniovi.miw.foodws.repositories;

import es.uniovi.miw.foodws.models.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    // Get all ingredients by name
    List<Ingredient> findByIngredientName(String ingredientName);

}
