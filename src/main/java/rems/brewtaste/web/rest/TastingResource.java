package rems.brewtaste.web.rest;

import com.codahale.metrics.annotation.Timed;
import rems.brewtaste.domain.Tasting;
import rems.brewtaste.service.TastingService;
import rems.brewtaste.web.rest.util.HeaderUtil;
import rems.brewtaste.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Tasting.
 */
@RestController
@RequestMapping("/api")
public class TastingResource {

    private final Logger log = LoggerFactory.getLogger(TastingResource.class);
        
    @Inject
    private TastingService tastingService;
    
    /**
     * POST  /tastings : Create a new tasting.
     *
     * @param tasting the tasting to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tasting, or with status 400 (Bad Request) if the tasting has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tastings",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tasting> createTasting(@Valid @RequestBody Tasting tasting) throws URISyntaxException {
        log.debug("REST request to save Tasting : {}", tasting);
        if (tasting.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("tasting", "idexists", "A new tasting cannot already have an ID")).body(null);
        }
        Tasting result = tastingService.save(tasting);
        return ResponseEntity.created(new URI("/api/tastings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("tasting", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tastings : Updates an existing tasting.
     *
     * @param tasting the tasting to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tasting,
     * or with status 400 (Bad Request) if the tasting is not valid,
     * or with status 500 (Internal Server Error) if the tasting couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tastings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tasting> updateTasting(@Valid @RequestBody Tasting tasting) throws URISyntaxException {
        log.debug("REST request to update Tasting : {}", tasting);
        if (tasting.getId() == null) {
            return createTasting(tasting);
        }
        Tasting result = tastingService.save(tasting);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("tasting", tasting.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tastings : get all the tastings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tastings in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/tastings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Tasting>> getAllTastings(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Tastings");
        Page<Tasting> page = tastingService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tastings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tastings/:id : get the "id" tasting.
     *
     * @param id the id of the tasting to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tasting, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/tastings/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tasting> getTasting(@PathVariable Long id) {
        log.debug("REST request to get Tasting : {}", id);
        Tasting tasting = tastingService.findOne(id);
        return Optional.ofNullable(tasting)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tastings/:id : delete the "id" tasting.
     *
     * @param id the id of the tasting to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/tastings/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTasting(@PathVariable Long id) {
        log.debug("REST request to delete Tasting : {}", id);
        tastingService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tasting", id.toString())).build();
    }

    /**
     * SEARCH  /_search/tastings?query=:query : search for the tasting corresponding
     * to the query.
     *
     * @param query the query of the tasting search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/tastings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Tasting>> searchTastings(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Tastings for query {}", query);
        Page<Tasting> page = tastingService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tastings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
