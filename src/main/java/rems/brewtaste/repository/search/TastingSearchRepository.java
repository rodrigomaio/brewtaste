package rems.brewtaste.repository.search;

import rems.brewtaste.domain.Tasting;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Tasting entity.
 */
public interface TastingSearchRepository extends ElasticsearchRepository<Tasting, Long> {
}
