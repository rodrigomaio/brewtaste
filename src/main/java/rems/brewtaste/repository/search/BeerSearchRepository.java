package rems.brewtaste.repository.search;

import rems.brewtaste.domain.Beer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Beer entity.
 */
public interface BeerSearchRepository extends ElasticsearchRepository<Beer, Long> {
}
