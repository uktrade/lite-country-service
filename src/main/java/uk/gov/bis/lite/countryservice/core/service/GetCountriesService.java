package uk.gov.bis.lite.countryservice.core.service;

import com.google.inject.Inject;
import uk.gov.bis.lite.countryservice.api.Country;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.core.exception.CountryServiceException;

import javax.xml.bind.JAXBException;
import java.util.List;

public class GetCountriesService {

    private final CountryListCache countryListCache;

    @Inject
    public GetCountriesService(CountryListCache countryListCache) throws JAXBException {
        this.countryListCache = countryListCache;
    }

    public List<Country> getCountryList(String countrySetName) throws CountryServiceException {
        return countryListCache.get(countrySetName);
    }

}
