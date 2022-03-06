package es.uniovi.miw.foodws.controllers;

import es.uniovi.miw.foodws.models.Menu;
import es.uniovi.miw.foodws.repositories.MenuRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/menus")
public class MenusController {

    private final MenuRepository menuRepository;

    public MenusController(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * Get all menus if param is null or get menus by name if param is not null
     *
     * @param menuName It can be null. If null returns every menu, if not returns matched
     * @return List of menus
     */
    @GetMapping
    public ResponseEntity<?> getMenus
    (@RequestParam(value = "menuName", required = false) String menuName) {
        if (menuName != null)
            return ResponseEntity.
                    ok(menuRepository.findByMenuName(menuName));
        return ResponseEntity.ok(menuRepository.findAll());
    }

    /**
     * Get menu by id
     *
     * @param id of the menu
     * @return menu
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMenu(@PathVariable long id) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found.get());
    }


    /**
     * Get menu ingredients
     *
     * @param id of the menu
     * @return menu ingredients
     */
    @GetMapping("/{id}/ingredients")
    public ResponseEntity<?> getMenuIngredients(@PathVariable long id) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found.get().getIngredientSet());
    }

    //TODO
//    @GetMapping("/{id}/ingredients/nutritional")
//    public ResponseEntity<?> getMenuIngredientsNutritional(@PathVariable long id) {
//        Optional<Menu> found = menuRepository.findById(id);
//        if (found.isEmpty())
//            return ResponseEntity.notFound().build();
//
//        FatSecretFood fsf = new FatSecretFood();
//
//        for (Ingredient ing : found.get().getIngredientSet()) {
//            double grams = ing.getGrams()
//            fsf.setCalories(fsf.getCalories() + computePortion(grams, obj.calories));
//            fsf.setCarbohydrate(fsf.getCarbohydrate() + computePortion(grams * obj.carbohydrate));
//            fsf.setCarbohydratePercentage(fsf.getCarbohydratePercentage() + computePortion(grams, obj.carbohydratePercentage));
//            fsf.setFat(fsf.getFat() + computePortion(grams, obj.fat);
//            fsf.setFatPercentage(fsf.getFatPercentage() + computePortion(grams, obj.fatPercentage));
//            fsf.setProtein(fsf.getProtein() + computePortion(grams, obj.protein));
//            fsf.setProteinPercentage(fsf.getProteinPercentage() + computePortion(grams, obj.proteinPercentage));
//        }
//        return ResponseEntity.ok(fsf);
//    }

    private double computePortion(double grams, double portionValue) {
        int total = 100;
        return grams * portionValue / total;
    }

    /**
     * Add menu
     *
     * @param menu to add
     * @return menu added
     */
    @PostMapping
    public ResponseEntity<?> postMenu(@Valid @RequestBody Menu menu) {
        menuRepository.saveAndFlush(menu);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(menu.getId())
                .toUri();
        return ResponseEntity.created(location).body(menu);
    }

    /**
     * Updates menu found by id
     *
     * @param id   of the menu to update
     * @param menu to update
     * @return updated menu
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> putMenu(@PathVariable long id, @Valid
    @RequestBody Menu menu) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Menu current = found.get();
        current.setMenuName(menu.getMenuName());
        current.setMenuDescription(menu.getMenuDescription());
        current.setMenuPrice(menu.getMenuPrice());
        current.setIngredientSet(menu.getIngredientSet());
        current.setRestaurant(menu.getRestaurant());
        menuRepository.saveAndFlush(current);
        return ResponseEntity.ok(current);
    }

    /**
     * Deletes an menu found by id
     *
     * @param id of the menu to delete
     * @return deleted menu
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenu(@PathVariable long id) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Menu current = found.get();
        menuRepository.delete(current);
        return ResponseEntity.ok(current);
    }


}
