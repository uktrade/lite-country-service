package uk.gov.bis.lite.countryservice.spire;

import uk.gov.bis.lite.common.spire.client.SpireClient;
import uk.gov.bis.lite.common.spire.client.SpireClientConfig;
import uk.gov.bis.lite.common.spire.client.SpireRequestConfig;
import uk.gov.bis.lite.common.spire.client.parser.SpireParser;
import uk.gov.bis.lite.countryservice.spire.model.SpireCountry;

import javax.xml.bind.JAXBException;
import java.util.List;

public class SpireCountriesClient extends SpireClient<List<SpireCountry>> {

  public SpireCountriesClient(SpireParser<List<SpireCountry>> parser, SpireClientConfig clientConfig, SpireRequestConfig requestConfig) throws JAXBException {
    super(parser, clientConfig, requestConfig);
  }

}
