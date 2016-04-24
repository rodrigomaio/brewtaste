package rems.brewtaste.repository.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import rems.brewtaste.domain.Beer;
import rems.brewtaste.repository.RateBeerRepository;

import java.io.IOException;
import java.util.Optional;

/**
 * Repository for the Beer entity from RateBeer using Jsoup.
 */
@Repository
public class RateBeerJsoupRepository implements RateBeerRepository {

	private static final Logger LOG = LoggerFactory.getLogger(RateBeerJsoupRepository.class);
	private static final String URL_PREFIX = "http://www.ratebeer.com/beer";

	private Document document;

	@Override
	public Optional<Beer> findOneById(long id) {
		Beer beer = null;
		try {
			connect(id);
		} catch (IOException e) {
			LOG.error("Failed to connect to RateBeer using id {}", id, e);
		}
		try {
			beer = new Beer();
			beer.setRateBeerId(id);
			beer.setAbv(fetchAbv());
			beer.setBrewery(fetchBrewery());
			beer.setStyle(fetchStyle());
			beer.setName(fetchName());
			beer.setOverallRating(fetchOverallRating());
		} catch (Exception e) {
			LOG.warn("Failed to fetch RateBeer info", e);
		}
		return Optional.ofNullable(beer);
	}

	private void connect(Long id) throws IOException {
		final String url = String.format("%s/%s/%s/", URL_PREFIX, Math.random(), id);
		document = Jsoup.connect(url).get();
	}

	private String getFirstItemTextFromCssQuery(String cssQuery) {
		final Elements elements = document.select(cssQuery);
		if (elements.size() < 1) {
			return null;
		}
		return elements.get(0).text();
	}

	private Double fetchAbv() {
		final String textFromCssQuery = getFirstItemTextFromCssQuery("abbr+ big strong");
		Double abv;
		try {
			abv = Double.valueOf(textFromCssQuery.split("%")[0]);
		} catch (Exception e) {
			return null;
		}
		return abv;
	}

	private String fetchBrewery() {
		String brewery = getFirstItemTextFromCssQuery("#_brand4 span");
		return brewery;
	}

	private String fetchStyle() {
		String style = getFirstItemTextFromCssQuery("#container div div br+ a");
		return style;
	}

	private String fetchName() {
		String name = getFirstItemTextFromCssQuery(".user-header span");
		return name;
	}

	private Integer fetchOverallRating() {
		String textFromCssQuery = getFirstItemTextFromCssQuery("#_aggregateRating6 br+ span");
		Integer overallRating;
		try {
			overallRating = Integer.valueOf(textFromCssQuery);
		} catch (NumberFormatException e) {
			return null;
		}
		return overallRating;
	}

}
