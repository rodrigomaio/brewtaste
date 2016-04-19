package rems.brewtaste.repository;

import rems.brewtaste.domain.Tasting;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tasting entity.
 */
public interface TastingRepository extends JpaRepository<Tasting,Long> {

}
