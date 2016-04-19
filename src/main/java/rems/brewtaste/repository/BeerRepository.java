package rems.brewtaste.repository;

import rems.brewtaste.domain.Beer;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Beer entity.
 */
public interface BeerRepository extends JpaRepository<Beer,Long> {

}
