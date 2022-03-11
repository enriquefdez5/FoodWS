package es.uniovi.miw.foodws.controllers;

import es.uniovi.miw.foodws.models.FatSecretFood;
import es.uniovi.miw.foodws.models.Menu;
import es.uniovi.miw.foodws.repositories.MenuRepository;
import es.uniovi.miw.foodws.services.FatSecretFoodService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final FatSecretFoodService fatSecretFoodService;

    private final MenuRepository menuRepository;

    public MenusController(FatSecretFoodService fatSecretFoodService, MenuRepository menuRepository) {
        this.fatSecretFoodService = fatSecretFoodService;
        this.menuRepository = menuRepository;
    }

    /**
     * Get all menus if param is null or get menus by name if param is not null
     *
     * @param menuName It can be null. If null returns every menu, if not returns matched
     * @return List of menus
     */
    @GetMapping
    @ApiOperation(value = "List menus",
            notes = "Returns a list of menus")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuName", value = "Name of the searched menu",
                    paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 200, message = "Ok",
                    response = Menu.class,
                    responseContainer = "List")
    })
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
    @ApiOperation(value = "Get menu",
            notes = "Get menu by id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Id of the menu",
                    required = true, paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 404, message = "Menu not found"),
            @ApiResponse(code = 200, message = "Ok",
                    response = Menu.class)
    })
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
    @GetMapping("/{id}/ingredients/nutritional")
    public ResponseEntity<?> getMenuIngredientsNutritional(@PathVariable long id) {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();

        Menu menu = found.get();
        FatSecretFood fsf = fatSecretFoodService.getFoodInfo(menu.getIngredientSet());
        if (fsf == null) {
            return ResponseEntity.internalServerError().body("Ups! Something went wrong...");
        }
        menu.setFsf(fsf);
        return ResponseEntity.ok(menu);
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
