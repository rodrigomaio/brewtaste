package rems.brewtaste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rems.brewtaste.domain.Tasting;

/**
 * Spring Data JPA repository for the Tasting entity.
 */
public interface TastingRepository extends JpaRepository<Tasting,Long> {

    @Query("select tasting from Tasting tasting where tasting.user.login = ?#{principal.username}")
    Page<Tasting> findByUserIsCurrentUser(Pageable pageable);

}
