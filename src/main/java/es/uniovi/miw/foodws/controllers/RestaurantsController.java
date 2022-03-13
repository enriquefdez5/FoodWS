package es.uniovi.miw.foodws.controllers;

import es.uniovi.miw.foodws.models.Menu;
import es.uniovi.miw.foodws.models.Restaurant;
import es.uniovi.miw.foodws.repositories.RestaurantRepository;
import es.uniovi.miw.foodws.services.FatSecretFoodService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantsController {


    @Autowired
    private final FatSecretFoodService fatSecretFoodService;

    private final RestaurantRepository restaurantRepository;

    public RestaurantsController(FatSecretFoodService fatSecretFoodService, RestaurantRepository restaurantRepository) {
        this.fatSecretFoodService = fatSecretFoodService;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Get all restaurants if param is null or get restaurants by name if param is not null
     *
     * @param restaurantName It can be null. If null returns every menu, if not returns matched
     * @return List of menus
     */
    @GetMapping
    @ApiOperation(value = "Get restaurants",
            notes = "Get restaurants filtered by restaurant name in path")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "restaurant name", value = "Restaurant name to find",
                    required = false, paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = Menu.class)
    })
    public ResponseEntity<?> getRestaurants
    (@RequestParam(value = "restaurantName", required = false) String restaurantName) {
        if (restaurantName != null) {
            List<Restaurant> restaurants = restaurantRepository.findByRestaurantName(restaurantName);
            for (Restaurant restaurant : restaurants) {
                Set<Menu> menus = restaurant.getMenuSet();
                for (Menu menu : menus) {
                    menu.setFsf(fatSecretFoodService.getFoodInfo(menu.getIngredientSet()));
                }
            }
            return ResponseEntity.ok(restaurants);
        }
        List<Restaurant> restaurants = restaurantRepository.findAll();
        for (Restaurant restaurant : restaurants) {
            Set<Menu> menus = restaurant.getMenuSet();
            for (Menu menu : menus) {
                menu.setFsf(fatSecretFoodService.getFoodInfo(menu.getIngredientSet()));
            }
        }
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Get restaurant by id
     *
     * @param id of the restaurant
     * @return restaurant
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get restaurant by id",
            notes = "Get restaurant find by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id of the restaurant",
                    required = true, paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Restaurant not found"),
            @ApiResponse(code = 200, message = "Ok", response = Menu.class)
    })
    public ResponseEntity<?> getRestaurant(@PathVariable long id) {
        Optional<Restaurant> found = restaurantRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();

        Restaurant restaurant = found.get();
        Set<Menu> menus = restaurant.getMenuSet();
        for (Menu menu : menus) {
            menu.setFsf(fatSecretFoodService.getFoodInfo(menu.getIngredientSet()));
        }
        return ResponseEntity.ok(restaurant);
    }

    /**
     * Get menus from restaurant
     *
     * @param id of the restaurant
     * @return restaurants menus
     */
    @GetMapping("/{id}/menus")
    @ApiOperation(value = "Get menus of a restaurant",
            notes = "Get all menus of a restaurant find by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id of the restaurant",
                    required = true, paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Restaurant not found"),
            @ApiResponse(code = 200, message = "Ok", response = Menu.class)
    })
    public ResponseEntity<?> getRestaurantsMenus(@PathVariable long id) {
        Optional<Restaurant> found = restaurantRepository.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Set<Menu> menus = found.get().getMenuSet();
        for (Menu menu : menus) {
            menu.setFsf(fatSecretFoodService.getFoodInfo(menu.getIngredientSet()));
        }
        return ResponseEntity.ok(menus);
    }


    /**
     * Add restaurant
     *
     * @param restaurant to add
     * @return restaurant added
     */
    @PostMapping
    @ApiOperation(value = "Post restaurant",
            notes = "Add a new restaurant")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "restaurant", value = "Restaurant object to persist",
                    required = true, paramType = "body", dataType = "Restaurant")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = Menu.class)
    })
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
    @ApiOperation(value = "Update restaurant",
            notes = "Update restaurant find by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id of the restaurant",
                    required = true, paramType = "path"),
            @ApiImplicitParam(name = "restaurant", value = "Restaurant object to persist",
                    required = true, paramType = "body")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Restaurant not found"),
            @ApiResponse(code = 200, message = "Ok", response = Menu.class)
    })
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
    @ApiOperation(value = "Delete restaurant",
            notes = "Delete restaurant find by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id of the restaurant",
                    required = true, paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Restaurant not found"),
            @ApiResponse(code = 200, message = "Ok", response = Menu.class)
    })
    public ResponseEntity<?> deleteRestaurant(@PathVariable long id) {
        Optional<Restaurant> found = restaurantRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Restaurant current = found.get();
        restaurantRepository.delete(current);
        return ResponseEntity.ok(current);
    }


}
