package rems.brewtaste.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rems.brewtaste.domain.Tasting;
import rems.brewtaste.repository.TastingRepository;
import rems.brewtaste.repository.UserRepository;
import rems.brewtaste.repository.search.TastingSearchRepository;
import rems.brewtaste.service.TastingService;

import javax.inject.Inject;
import java.time.ZonedDateTime;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing Tasting.
 */
@Service
@Transactional
public class TastingServiceImpl implements TastingService{

    private final Logger log = LoggerFactory.getLogger(TastingServiceImpl.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private TastingRepository tastingRepository;

    @Inject
    private TastingSearchRepository tastingSearchRepository;

    /**
     * Save a tasting.
     *
     * @param tasting the entity to save
     * @return the persisted entity
     */
    public Tasting save(Tasting tasting) {
        log.debug("Request to save Tasting : {}", tasting);
        if (tasting.getDate() == null) {
            tasting.setDate(ZonedDateTime.now());
        }
        Tasting result = tastingRepository.save(tasting);
        tastingSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the tastings.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Tasting> findAll(Pageable pageable) {
        log.debug("Request to get all Tastings");
        Page<Tasting> result = tastingRepository.findByUserIsCurrentUser(pageable);
        return result;
    }

    /**
     *  Get one tasting by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Tasting findOne(Long id) {
        log.debug("Request to get Tasting : {}", id);
        Tasting tasting = tastingRepository.findOne(id);
        return tasting;
    }

    /**
     *  Delete the  tasting by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Tasting : {}", id);
        tastingRepository.delete(id);
        tastingSearchRepository.delete(id);
    }

    /**
     * Search for the tasting corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Tasting> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tastings for query {}", query);
        return tastingSearchRepository.search(queryStringQuery(query), pageable);
    }
}
