package rems.brewtaste.service;

import rems.brewtaste.domain.Beer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Beer.
 */
public interface BeerService {

    /**
     * Save a beer.
     * 
     * @param beer the entity to save
     * @return the persisted entity
     */
    Beer save(Beer beer);

    /**
     *  Get all the beers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Beer> findAll(Pageable pageable);

    /**
     *  Get the "id" beer.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Beer findOne(Long id);

    /**
     *  Delete the "id" beer.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the beer corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Beer> search(String query, Pageable pageable);
}
