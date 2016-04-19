package rems.brewtaste.web.rest;

import rems.brewtaste.BrewtasteApp;
import rems.brewtaste.domain.Beer;
import rems.brewtaste.repository.BeerRepository;
import rems.brewtaste.service.BeerService;
import rems.brewtaste.repository.search.BeerSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the BeerResource REST controller.
 *
 * @see BeerResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BrewtasteApp.class)
@WebAppConfiguration
@IntegrationTest
public class BeerResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private BeerRepository beerRepository;

    @Inject
    private BeerService beerService;

    @Inject
    private BeerSearchRepository beerSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBeerMockMvc;

    private Beer beer;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BeerResource beerResource = new BeerResource();
        ReflectionTestUtils.setField(beerResource, "beerService", beerService);
        this.restBeerMockMvc = MockMvcBuilders.standaloneSetup(beerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        beerSearchRepository.deleteAll();
        beer = new Beer();
        beer.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createBeer() throws Exception {
        int databaseSizeBeforeCreate = beerRepository.findAll().size();

        // Create the Beer

        restBeerMockMvc.perform(post("/api/beers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(beer)))
                .andExpect(status().isCreated());

        // Validate the Beer in the database
        List<Beer> beers = beerRepository.findAll();
        assertThat(beers).hasSize(databaseSizeBeforeCreate + 1);
        Beer testBeer = beers.get(beers.size() - 1);
        assertThat(testBeer.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Beer in ElasticSearch
        Beer beerEs = beerSearchRepository.findOne(testBeer.getId());
        assertThat(beerEs).isEqualToComparingFieldByField(testBeer);
    }

    @Test
    @Transactional
    public void getAllBeers() throws Exception {
        // Initialize the database
        beerRepository.saveAndFlush(beer);

        // Get all the beers
        restBeerMockMvc.perform(get("/api/beers?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(beer.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getBeer() throws Exception {
        // Initialize the database
        beerRepository.saveAndFlush(beer);

        // Get the beer
        restBeerMockMvc.perform(get("/api/beers/{id}", beer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(beer.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBeer() throws Exception {
        // Get the beer
        restBeerMockMvc.perform(get("/api/beers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBeer() throws Exception {
        // Initialize the database
        beerService.save(beer);

        int databaseSizeBeforeUpdate = beerRepository.findAll().size();

        // Update the beer
        Beer updatedBeer = new Beer();
        updatedBeer.setId(beer.getId());
        updatedBeer.setName(UPDATED_NAME);

        restBeerMockMvc.perform(put("/api/beers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBeer)))
                .andExpect(status().isOk());

        // Validate the Beer in the database
        List<Beer> beers = beerRepository.findAll();
        assertThat(beers).hasSize(databaseSizeBeforeUpdate);
        Beer testBeer = beers.get(beers.size() - 1);
        assertThat(testBeer.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Beer in ElasticSearch
        Beer beerEs = beerSearchRepository.findOne(testBeer.getId());
        assertThat(beerEs).isEqualToComparingFieldByField(testBeer);
    }

    @Test
    @Transactional
    public void deleteBeer() throws Exception {
        // Initialize the database
        beerService.save(beer);

        int databaseSizeBeforeDelete = beerRepository.findAll().size();

        // Get the beer
        restBeerMockMvc.perform(delete("/api/beers/{id}", beer.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean beerExistsInEs = beerSearchRepository.exists(beer.getId());
        assertThat(beerExistsInEs).isFalse();

        // Validate the database is empty
        List<Beer> beers = beerRepository.findAll();
        assertThat(beers).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBeer() throws Exception {
        // Initialize the database
        beerService.save(beer);

        // Search the beer
        restBeerMockMvc.perform(get("/api/_search/beers?query=id:" + beer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(beer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
