package es.uniovi.miw.foodws.repositories;

import es.uniovi.miw.foodws.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // Get all menus by name
    List<Restaurant> findByRestaurantName(String restaurantName);
}
