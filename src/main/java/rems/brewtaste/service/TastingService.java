package rems.brewtaste.service;

import rems.brewtaste.domain.Tasting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Tasting.
 */
public interface TastingService {

    /**
     * Save a tasting.
     * 
     * @param tasting the entity to save
     * @return the persisted entity
     */
    Tasting save(Tasting tasting);

    /**
     *  Get all the tastings.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Tasting> findAll(Pageable pageable);

    /**
     *  Get the "id" tasting.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Tasting findOne(Long id);

    /**
     *  Delete the "id" tasting.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the tasting corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Tasting> search(String query, Pageable pageable);
}
