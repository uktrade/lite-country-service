package uk.gov.bis.lite.countryservice.core.service;

import java.util.Arrays;
import java.util.Optional;

public enum CountrySet {

    EXPORT_CONTROL("export-control", "EXPORT_CONTROL"),
    DENIALS("denials", "DENIALS");

    private final String name;
    private final String spireCountrySetId;

    CountrySet(String name, String spireCountrySetId) {
        this.name = name;
        this.spireCountrySetId = spireCountrySetId;
    }

    public String getName() {
        return name;
    }

    public String getSpireCountrySetId() {
        return spireCountrySetId;
    }

    public static Optional<CountrySet> getByName(String name) {
        return Arrays.stream(CountrySet.values()).filter(c -> c.getName().equals(name)).findFirst();
    }

}
