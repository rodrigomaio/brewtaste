package rems.brewtaste.service;

import rems.brewtaste.domain.Beer;

import java.util.Optional;

/**
 * Service Interface for managing RateBeer external info.
 */
public interface RateBeerService {
	/**
     * Fetch beer information.
     *
     * @param beer the beer entity
     * @return the entity plus obtained info
     */
    Beer fetch(Beer beer);
}
