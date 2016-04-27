package rems.brewtaste.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rems.brewtaste.domain.Beer;
import rems.brewtaste.repository.RateBeerRepository;
import rems.brewtaste.service.RateBeerService;

import javax.inject.Inject;
import java.util.Optional;

import static rems.brewtaste.service.util.NullAwareBeanUtils.copyNonNullProperties;

/**
 * Service Implementation for managing RateBeer external info.
 */
@Service
public class RateBeerServiceImpl implements RateBeerService {

    private static final Logger LOG = LoggerFactory.getLogger(RateBeerServiceImpl.class);

    @Inject
    private RateBeerRepository rateBeerRepository;

    @Override
    public Beer fetch(Beer beer) {
        LOG.debug("Request to fetch from RateBeer: {}", beer);
        final Optional<Beer> beerOptional = rateBeerRepository.findOneById(beer.getRateBeerId());
        if (beerOptional.isPresent()) {
            final Beer fetched = beerOptional.get();
            copyNonNullProperties(beer, fetched);
            return fetched;
        }
        return beer;
    }

}
