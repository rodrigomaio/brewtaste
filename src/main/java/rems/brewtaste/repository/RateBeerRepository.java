package rems.brewtaste.repository;

import rems.brewtaste.domain.Beer;

import java.util.Optional;

/**
 * Repository for the Beer entity from RateBeer.
 */
public interface RateBeerRepository {

    Optional<Beer> findOneById(long id);

}
