package rems.brewtaste.web.rest;

import com.codahale.metrics.annotation.Timed;
import rems.brewtaste.domain.Beer;
import rems.brewtaste.service.BeerService;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Beer.
 */
@RestController
@RequestMapping("/api")
public class BeerResource {

    private final Logger log = LoggerFactory.getLogger(BeerResource.class);
        
    @Inject
    private BeerService beerService;
    
    /**
     * POST  /beers : Create a new beer.
     *
     * @param beer the beer to create
     * @return the ResponseEntity with status 201 (Created) and with body the new beer, or with status 400 (Bad Request) if the beer has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/beers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Beer> createBeer(@RequestBody Beer beer) throws URISyntaxException {
        log.debug("REST request to save Beer : {}", beer);
        if (beer.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("beer", "idexists", "A new beer cannot already have an ID")).body(null);
        }
        Beer result = beerService.save(beer);
        return ResponseEntity.created(new URI("/api/beers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("beer", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /beers : Updates an existing beer.
     *
     * @param beer the beer to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated beer,
     * or with status 400 (Bad Request) if the beer is not valid,
     * or with status 500 (Internal Server Error) if the beer couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/beers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Beer> updateBeer(@RequestBody Beer beer) throws URISyntaxException {
        log.debug("REST request to update Beer : {}", beer);
        if (beer.getId() == null) {
            return createBeer(beer);
        }
        Beer result = beerService.save(beer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("beer", beer.getId().toString()))
            .body(result);
    }

    /**
     * GET  /beers : get all the beers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of beers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/beers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Beer>> getAllBeers(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Beers");
        Page<Beer> page = beerService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/beers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /beers/:id : get the "id" beer.
     *
     * @param id the id of the beer to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the beer, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/beers/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Beer> getBeer(@PathVariable Long id) {
        log.debug("REST request to get Beer : {}", id);
        Beer beer = beerService.findOne(id);
        return Optional.ofNullable(beer)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /beers/:id : delete the "id" beer.
     *
     * @param id the id of the beer to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/beers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBeer(@PathVariable Long id) {
        log.debug("REST request to delete Beer : {}", id);
        beerService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("beer", id.toString())).build();
    }

    /**
     * SEARCH  /_search/beers?query=:query : search for the beer corresponding
     * to the query.
     *
     * @param query the query of the beer search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/beers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Beer>> searchBeers(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Beers for query {}", query);
        Page<Beer> page = beerService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/beers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
