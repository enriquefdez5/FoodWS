package es.uniovi.miw.foodws.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.uniovi.miw.foodws.models.Ingredient;
import es.uniovi.miw.foodws.models.Menu;
import es.uniovi.miw.foodws.repositories.MenuRepository;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/menus")
public class MenusController {

    private static final ObjectMapper mapper = new ObjectMapper();


    private final String CLIENT_ID = "1bdea3a471974265bc15440817c2bd2a";
    private final String CLIENT_SECRET = "51ca4d89329e4b64890023d1c88fe003";
    private final String OAUTH_FATSECRET_TOKEN_URL = "https://oauth.fatsecret.com/connect/token";
    private final String FATSECRET_API_URL = "https://platform.fatsecret.com/rest/server.api";
    private final String FATSECRET_API_FOOD_METHOD = "method=food.get.v2";
    private final String FATSECRET_API_FOOD_METHOD_FORMAT = "format=json";


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
    @ApiOperation(value = "List menus",
            notes = "Returns a list of menus")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuName", value = "Name of the searched menu",
                    required = false, paramType = "path")
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
    public ResponseEntity<?> getMenuIngredientsNutritional(@PathVariable long id) throws JsonProcessingException {
        Optional<Menu> found = menuRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();

        // Post petition for token
        String authHeader = CLIENT_ID + ":" + CLIENT_SECRET;
        byte[] b = authHeader.getBytes(StandardCharsets.US_ASCII);
        Response postResponse = ClientBuilder.newClient().
                target(OAUTH_FATSECRET_TOKEN_URL).
                request(MediaType.APPLICATION_JSON).
                header(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.getEncoder().encode(b))).
                post(Entity.entity("grant_type=client_credentials&scope=basic", MediaType.APPLICATION_FORM_URLENCODED));

        // If status != 200
        if (!postResponse.getStatusInfo().toEnum().equals(Response.Status.OK)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("3rd service does " +
                    "not answer");
        }

        // Status == 200
        String response = postResponse.readEntity(String.class);
        String access_token = "";
        try {
            Token token = new ObjectMapper().readValue(response, Token.class);
            access_token = token.getAccess_token();
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error mapping 3rd service info");
        }

        // Get ing info
        for (Ingredient ing : found.get().getIngredientSet()) {
            System.out.println("Access token: " + access_token);
            Response postIngResponse = ClientBuilder.newClient().
                    target(FATSECRET_API_URL).
                    request(MediaType.APPLICATION_JSON).
                    header(HttpHeaders.AUTHORIZATION, "Bearer " + access_token).
                    post(Entity.entity(FATSECRET_API_FOOD_METHOD + "&food_id=" + ing.getFatSecretId() +
                            "&" + FATSECRET_API_FOOD_METHOD_FORMAT, MediaType.APPLICATION_FORM_URLENCODED));
            String ingresponse = postIngResponse.readEntity(String.class);
            System.out.println(ingresponse);
        }

        return ResponseEntity.ok("ey!");


//        for (Ingredient ing : found.get().getIngredientSet()) {
//            double grams = ing.getGrams();
//            fsf.setCalories(fsf.getCalories() + computePortion(grams, obj.calories));
//            fsf.setCarbohydrate(fsf.getCarbohydrate() + computePortion(grams * obj.carbohydrate));
//            fsf.setCarbohydratePercentage(fsf.getCarbohydratePercentage() + computePortion(grams, obj.carbohydratePercentage));
//            fsf.setFat(fsf.getFat() + computePortion(grams, obj.fat);
//            fsf.setFatPercentage(fsf.getFatPercentage() + computePortion(grams, obj.fatPercentage));
//            fsf.setProtein(fsf.getProtein() + computePortion(grams, obj.protein));
//            fsf.setProteinPercentage(fsf.getProteinPercentage() + computePortion(grams, obj.proteinPercentage));
//        }

    }

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
