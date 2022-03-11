package es.uniovi.miw.foodws.controllers;

import es.uniovi.miw.foodws.models.Restaurant;
import es.uniovi.miw.foodws.repositories.RestaurantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantsController {

    private final RestaurantRepository restaurantRepository;

    public RestaurantsController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Get all restaurants if param is null or get restaurants by name if param is not null
     *
     * @param restaurantName It can be null. If null returns every menu, if not returns matched
     * @return List of menus
     */
    @GetMapping
    public ResponseEntity<?> getRestaurants
    (@RequestParam(value = "restaurantName", required = false) String restaurantName) {
        if (restaurantName != null)
            return ResponseEntity.
                    ok(restaurantRepository.findByRestaurantName(restaurantName));
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    /**
     * Get restaurant by id
     *
     * @param id of the restaurant
     * @return restaurant
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurant(@PathVariable long id) {
        Optional<Restaurant> found = restaurantRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found.get());
    }

    /**
     * Get menus from restaurant
     *
     * @param id of the restaurant
     * @return restaurants menus
     */
    @GetMapping("/{id}/menus")
    public ResponseEntity<?> getRestaurantsMenus(@PathVariable long id) {
        Optional<Restaurant> found = restaurantRepository.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found.get().getMenuSet());
    }


    /**
     * Add restaurant
     *
     * @param restaurant to add
     * @return restaurant added
     */
    @PostMapping
    public ResponseEntity<?> postRestaurant(@Valid @RequestBody Restaurant restaurant) {
        System.out.println("restaurant: " + restaurant);
        restaurantRepository.saveAndFlush(restaurant);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(restaurant.getId())
                .toUri();
        return ResponseEntity.created(location).body(restaurant);
    }

    /**
     * Updates restaurant found by id
     *
     * @param id         of the restaurant to update
     * @param restaurant to update
     * @return updated menu
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> putRestaurant(@PathVariable long id, @Valid
    @RequestBody Restaurant restaurant) {
        Optional<Restaurant> found = restaurantRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Restaurant current = found.get();
        current.setRestaurantName(restaurant.getRestaurantName());
        current.setRestaurantAddress(restaurant.getRestaurantAddress());
        current.setMenuSet(restaurant.getMenuSet());
        restaurantRepository.saveAndFlush(current);
        return ResponseEntity.ok(current);
    }

    /**
     * Deletes an restaurant found by id
     *
     * @param id of the restaurant to delete
     * @return deleted restaurant
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable long id) {
        Optional<Restaurant> found = restaurantRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Restaurant current = found.get();
        restaurantRepository.delete(current);
        return ResponseEntity.ok(current);
    }


}
