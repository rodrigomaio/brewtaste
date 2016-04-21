package rems.brewtaste.web.rest;

import rems.brewtaste.BrewtasteApp;
import rems.brewtaste.domain.Tasting;
import rems.brewtaste.repository.TastingRepository;
import rems.brewtaste.service.TastingService;
import rems.brewtaste.repository.search.TastingSearchRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TastingResource REST controller.
 *
 * @see TastingResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BrewtasteApp.class)
@WebAppConfiguration
@IntegrationTest
public class TastingResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_STR = dateTimeFormatter.format(DEFAULT_DATE);
    private static final String DEFAULT_APPEARANCE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_APPEARANCE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_AROMA = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_AROMA = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_FLAVOR = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_FLAVOR = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_MOUTHFEEL = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_MOUTHFEEL = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_FINISH = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_FINISH = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_GENERAL_IMPRESSION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_GENERAL_IMPRESSION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private TastingRepository tastingRepository;

    @Inject
    private TastingService tastingService;

    @Inject
    private TastingSearchRepository tastingSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTastingMockMvc;

    private Tasting tasting;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TastingResource tastingResource = new TastingResource();
        ReflectionTestUtils.setField(tastingResource, "tastingService", tastingService);
        this.restTastingMockMvc = MockMvcBuilders.standaloneSetup(tastingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        tastingSearchRepository.deleteAll();
        tasting = new Tasting();
        tasting.setDate(DEFAULT_DATE);
        tasting.setAppearance(DEFAULT_APPEARANCE);
        tasting.setAroma(DEFAULT_AROMA);
        tasting.setFlavor(DEFAULT_FLAVOR);
        tasting.setMouthfeel(DEFAULT_MOUTHFEEL);
        tasting.setFinish(DEFAULT_FINISH);
        tasting.setGeneralImpression(DEFAULT_GENERAL_IMPRESSION);
    }

    @Test
    @Transactional
    public void createTasting() throws Exception {
        int databaseSizeBeforeCreate = tastingRepository.findAll().size();

        // Create the Tasting

        restTastingMockMvc.perform(post("/api/tastings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tasting)))
                .andExpect(status().isCreated());

        // Validate the Tasting in the database
        List<Tasting> tastings = tastingRepository.findAll();
        assertThat(tastings).hasSize(databaseSizeBeforeCreate + 1);
        Tasting testTasting = tastings.get(tastings.size() - 1);
        assertThat(testTasting.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTasting.getAppearance()).isEqualTo(DEFAULT_APPEARANCE);
        assertThat(testTasting.getAroma()).isEqualTo(DEFAULT_AROMA);
        assertThat(testTasting.getFlavor()).isEqualTo(DEFAULT_FLAVOR);
        assertThat(testTasting.getMouthfeel()).isEqualTo(DEFAULT_MOUTHFEEL);
        assertThat(testTasting.getFinish()).isEqualTo(DEFAULT_FINISH);
        assertThat(testTasting.getGeneralImpression()).isEqualTo(DEFAULT_GENERAL_IMPRESSION);

        // Validate the Tasting in ElasticSearch
        Tasting tastingEs = tastingSearchRepository.findOne(testTasting.getId());
        assertThat(tastingEs).isEqualToComparingFieldByField(testTasting);
    }

    @Test
    @Transactional
    public void getAllTastings() throws Exception {
        // Initialize the database
        tastingRepository.saveAndFlush(tasting);

        // Get all the tastings
        restTastingMockMvc.perform(get("/api/tastings?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tasting.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)))
                .andExpect(jsonPath("$.[*].appearance").value(hasItem(DEFAULT_APPEARANCE.toString())))
                .andExpect(jsonPath("$.[*].aroma").value(hasItem(DEFAULT_AROMA.toString())))
                .andExpect(jsonPath("$.[*].flavor").value(hasItem(DEFAULT_FLAVOR.toString())))
                .andExpect(jsonPath("$.[*].mouthfeel").value(hasItem(DEFAULT_MOUTHFEEL.toString())))
                .andExpect(jsonPath("$.[*].finish").value(hasItem(DEFAULT_FINISH.toString())))
                .andExpect(jsonPath("$.[*].generalImpression").value(hasItem(DEFAULT_GENERAL_IMPRESSION.toString())));
    }

    @Test
    @Transactional
    public void getTasting() throws Exception {
        // Initialize the database
        tastingRepository.saveAndFlush(tasting);

        // Get the tasting
        restTastingMockMvc.perform(get("/api/tastings/{id}", tasting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(tasting.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE_STR))
            .andExpect(jsonPath("$.appearance").value(DEFAULT_APPEARANCE.toString()))
            .andExpect(jsonPath("$.aroma").value(DEFAULT_AROMA.toString()))
            .andExpect(jsonPath("$.flavor").value(DEFAULT_FLAVOR.toString()))
            .andExpect(jsonPath("$.mouthfeel").value(DEFAULT_MOUTHFEEL.toString()))
            .andExpect(jsonPath("$.finish").value(DEFAULT_FINISH.toString()))
            .andExpect(jsonPath("$.generalImpression").value(DEFAULT_GENERAL_IMPRESSION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTasting() throws Exception {
        // Get the tasting
        restTastingMockMvc.perform(get("/api/tastings/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTasting() throws Exception {
        // Initialize the database
        tastingService.save(tasting);

        int databaseSizeBeforeUpdate = tastingRepository.findAll().size();

        // Update the tasting
        Tasting updatedTasting = new Tasting();
        updatedTasting.setId(tasting.getId());
        updatedTasting.setDate(UPDATED_DATE);
        updatedTasting.setAppearance(UPDATED_APPEARANCE);
        updatedTasting.setAroma(UPDATED_AROMA);
        updatedTasting.setFlavor(UPDATED_FLAVOR);
        updatedTasting.setMouthfeel(UPDATED_MOUTHFEEL);
        updatedTasting.setFinish(UPDATED_FINISH);
        updatedTasting.setGeneralImpression(UPDATED_GENERAL_IMPRESSION);

        restTastingMockMvc.perform(put("/api/tastings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTasting)))
                .andExpect(status().isOk());

        // Validate the Tasting in the database
        List<Tasting> tastings = tastingRepository.findAll();
        assertThat(tastings).hasSize(databaseSizeBeforeUpdate);
        Tasting testTasting = tastings.get(tastings.size() - 1);
        assertThat(testTasting.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTasting.getAppearance()).isEqualTo(UPDATED_APPEARANCE);
        assertThat(testTasting.getAroma()).isEqualTo(UPDATED_AROMA);
        assertThat(testTasting.getFlavor()).isEqualTo(UPDATED_FLAVOR);
        assertThat(testTasting.getMouthfeel()).isEqualTo(UPDATED_MOUTHFEEL);
        assertThat(testTasting.getFinish()).isEqualTo(UPDATED_FINISH);
        assertThat(testTasting.getGeneralImpression()).isEqualTo(UPDATED_GENERAL_IMPRESSION);

        // Validate the Tasting in ElasticSearch
        Tasting tastingEs = tastingSearchRepository.findOne(testTasting.getId());
        assertThat(tastingEs).isEqualToComparingFieldByField(testTasting);
    }

    @Test
    @Transactional
    public void deleteTasting() throws Exception {
        // Initialize the database
        tastingService.save(tasting);

        int databaseSizeBeforeDelete = tastingRepository.findAll().size();

        // Get the tasting
        restTastingMockMvc.perform(delete("/api/tastings/{id}", tasting.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean tastingExistsInEs = tastingSearchRepository.exists(tasting.getId());
        assertThat(tastingExistsInEs).isFalse();

        // Validate the database is empty
        List<Tasting> tastings = tastingRepository.findAll();
        assertThat(tastings).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTasting() throws Exception {
        // Initialize the database
        tastingService.save(tasting);

        // Search the tasting
        restTastingMockMvc.perform(get("/api/_search/tastings?query=id:" + tasting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tasting.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)))
            .andExpect(jsonPath("$.[*].appearance").value(hasItem(DEFAULT_APPEARANCE.toString())))
            .andExpect(jsonPath("$.[*].aroma").value(hasItem(DEFAULT_AROMA.toString())))
            .andExpect(jsonPath("$.[*].flavor").value(hasItem(DEFAULT_FLAVOR.toString())))
            .andExpect(jsonPath("$.[*].mouthfeel").value(hasItem(DEFAULT_MOUTHFEEL.toString())))
            .andExpect(jsonPath("$.[*].finish").value(hasItem(DEFAULT_FINISH.toString())))
            .andExpect(jsonPath("$.[*].generalImpression").value(hasItem(DEFAULT_GENERAL_IMPRESSION.toString())));
    }
}
