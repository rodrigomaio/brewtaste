package rems.brewtaste.service.impl;

import rems.brewtaste.service.BeerService;
import rems.brewtaste.domain.Beer;
import rems.brewtaste.repository.BeerRepository;
import rems.brewtaste.repository.search.BeerSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Beer.
 */
@Service
@Transactional
public class BeerServiceImpl implements BeerService{

    private final Logger log = LoggerFactory.getLogger(BeerServiceImpl.class);
    
    @Inject
    private BeerRepository beerRepository;
    
    @Inject
    private BeerSearchRepository beerSearchRepository;
    
    /**
     * Save a beer.
     * 
     * @param beer the entity to save
     * @return the persisted entity
     */
    public Beer save(Beer beer) {
        log.debug("Request to save Beer : {}", beer);
        Beer result = beerRepository.save(beer);
        beerSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the beers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Beer> findAll(Pageable pageable) {
        log.debug("Request to get all Beers");
        Page<Beer> result = beerRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one beer by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Beer findOne(Long id) {
        log.debug("Request to get Beer : {}", id);
        Beer beer = beerRepository.findOne(id);
        return beer;
    }

    /**
     *  Delete the  beer by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Beer : {}", id);
        beerRepository.delete(id);
        beerSearchRepository.delete(id);
    }

    /**
     * Search for the beer corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Beer> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Beers for query {}", query);
        return beerSearchRepository.search(queryStringQuery(query), pageable);
    }
}
