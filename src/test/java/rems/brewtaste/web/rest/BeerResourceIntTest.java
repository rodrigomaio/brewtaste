package rems.brewtaste.web.rest;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import rems.brewtaste.BrewtasteApp;
import rems.brewtaste.domain.Beer;
import rems.brewtaste.repository.BeerRepository;
import rems.brewtaste.repository.RateBeerRepository;
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
import rems.brewtaste.service.RateBeerService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
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

    private static final Long DEFAULT_RATE_BEER_ID = 1L;
    private static final Long UPDATED_RATE_BEER_ID = 2L;

    private static final Double DEFAULT_ABV = 0D;
    private static final Double UPDATED_ABV = 1D;

    private static final Integer DEFAULT_OVERALL_RATING = 0;
    private static final Integer UPDATED_OVERALL_RATING = 1;
    private static final Integer RATEBEER_OVERALL_RATING = 99;

    private static final Integer DEFAULT_STYLE_RATING = 0;
    private static final Integer UPDATED_STYLE_RATING = 1;
    private static final String DEFAULT_STYLE = "AAAAA";
    private static final String UPDATED_STYLE = "BBBBB";
    private static final String DEFAULT_COUNTRY = "AAAAA";
    private static final String UPDATED_COUNTRY = "BBBBB";
    private static final String DEFAULT_BREWERY = "AAAAA";
    private static final String UPDATED_BREWERY = "BBBBB";

    @Inject
    private BeerRepository beerRepository;

    @InjectMocks
    @Inject
    private BeerService beerService;

    @InjectMocks
    @Inject
    private RateBeerService rateBeerService;

    @Mock
    private RateBeerRepository rateBeerRepository;

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
        beer.setRateBeerId(DEFAULT_RATE_BEER_ID);
        beer.setAbv(DEFAULT_ABV);
        beer.setOverallRating(DEFAULT_OVERALL_RATING);
        beer.setStyleRating(DEFAULT_STYLE_RATING);
        beer.setStyle(DEFAULT_STYLE);
        beer.setCountry(DEFAULT_COUNTRY);
        beer.setBrewery(DEFAULT_BREWERY);
    }

    @Test
    @Transactional
    public void createBeer() throws Exception {
        // Simulate RateBeer result
        beer.setOverallRating(null);
        final Beer rateBeer = new Beer();
        rateBeer.setOverallRating(RATEBEER_OVERALL_RATING);
        when(rateBeerRepository.findOneById(anyLong())).thenReturn(Optional.of(rateBeer));

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
        assertThat(testBeer.getRateBeerId()).isEqualTo(DEFAULT_RATE_BEER_ID);
        assertThat(testBeer.getAbv()).isEqualTo(DEFAULT_ABV);
        assertThat(testBeer.getOverallRating()).isEqualTo(RATEBEER_OVERALL_RATING);
        assertThat(testBeer.getStyleRating()).isEqualTo(DEFAULT_STYLE_RATING);
        assertThat(testBeer.getStyle()).isEqualTo(DEFAULT_STYLE);
        assertThat(testBeer.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testBeer.getBrewery()).isEqualTo(DEFAULT_BREWERY);

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
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].rateBeerId").value(hasItem(DEFAULT_RATE_BEER_ID.intValue())))
                .andExpect(jsonPath("$.[*].abv").value(hasItem(DEFAULT_ABV.doubleValue())))
                .andExpect(jsonPath("$.[*].overallRating").value(hasItem(DEFAULT_OVERALL_RATING)))
                .andExpect(jsonPath("$.[*].styleRating").value(hasItem(DEFAULT_STYLE_RATING)))
                .andExpect(jsonPath("$.[*].style").value(hasItem(DEFAULT_STYLE.toString())))
                .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
                .andExpect(jsonPath("$.[*].brewery").value(hasItem(DEFAULT_BREWERY.toString())));
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
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.rateBeerId").value(DEFAULT_RATE_BEER_ID.intValue()))
            .andExpect(jsonPath("$.abv").value(DEFAULT_ABV.doubleValue()))
            .andExpect(jsonPath("$.overallRating").value(DEFAULT_OVERALL_RATING))
            .andExpect(jsonPath("$.styleRating").value(DEFAULT_STYLE_RATING))
            .andExpect(jsonPath("$.style").value(DEFAULT_STYLE.toString()))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()))
            .andExpect(jsonPath("$.brewery").value(DEFAULT_BREWERY.toString()));
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
        when(rateBeerRepository.findOneById(anyLong())).thenReturn(Optional.of(beer));
        beerService.save(beer);

        int databaseSizeBeforeUpdate = beerRepository.findAll().size();

        // Update the beer
        Beer updatedBeer = new Beer();
        updatedBeer.setId(beer.getId());
        updatedBeer.setName(UPDATED_NAME);
        updatedBeer.setRateBeerId(UPDATED_RATE_BEER_ID);
        updatedBeer.setAbv(UPDATED_ABV);
        updatedBeer.setOverallRating(UPDATED_OVERALL_RATING);
        updatedBeer.setStyleRating(UPDATED_STYLE_RATING);
        updatedBeer.setStyle(UPDATED_STYLE);
        updatedBeer.setCountry(UPDATED_COUNTRY);
        updatedBeer.setBrewery(UPDATED_BREWERY);

        restBeerMockMvc.perform(put("/api/beers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBeer)))
                .andExpect(status().isOk());

        // Validate the Beer in the database
        List<Beer> beers = beerRepository.findAll();
        assertThat(beers).hasSize(databaseSizeBeforeUpdate);
        Beer testBeer = beers.get(beers.size() - 1);
        assertThat(testBeer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBeer.getRateBeerId()).isEqualTo(UPDATED_RATE_BEER_ID);
        assertThat(testBeer.getAbv()).isEqualTo(UPDATED_ABV);
        assertThat(testBeer.getOverallRating()).isEqualTo(UPDATED_OVERALL_RATING);
        assertThat(testBeer.getStyleRating()).isEqualTo(UPDATED_STYLE_RATING);
        assertThat(testBeer.getStyle()).isEqualTo(UPDATED_STYLE);
        assertThat(testBeer.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testBeer.getBrewery()).isEqualTo(UPDATED_BREWERY);

        // Validate the Beer in ElasticSearch
        Beer beerEs = beerSearchRepository.findOne(testBeer.getId());
        assertThat(beerEs).isEqualToComparingFieldByField(testBeer);
    }

    @Test
    @Transactional
    public void deleteBeer() throws Exception {
        // Initialize the database
        when(rateBeerRepository.findOneById(anyLong())).thenReturn(Optional.of(beer));
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
        when(rateBeerRepository.findOneById(anyLong())).thenReturn(Optional.of(beer));
        beerService.save(beer);

        // Search the beer
        restBeerMockMvc.perform(get("/api/_search/beers?query=id:" + beer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(beer.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].rateBeerId").value(hasItem(DEFAULT_RATE_BEER_ID.intValue())))
            .andExpect(jsonPath("$.[*].abv").value(hasItem(DEFAULT_ABV.doubleValue())))
            .andExpect(jsonPath("$.[*].overallRating").value(hasItem(DEFAULT_OVERALL_RATING)))
            .andExpect(jsonPath("$.[*].styleRating").value(hasItem(DEFAULT_STYLE_RATING)))
            .andExpect(jsonPath("$.[*].style").value(hasItem(DEFAULT_STYLE.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].brewery").value(hasItem(DEFAULT_BREWERY.toString())));
    }
}
