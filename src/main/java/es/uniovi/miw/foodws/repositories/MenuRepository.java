package es.uniovi.miw.foodws.repositories;

import es.uniovi.miw.foodws.models.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    // Get all menus by name
    List<Menu> findByMenuName(String menuName);
}
