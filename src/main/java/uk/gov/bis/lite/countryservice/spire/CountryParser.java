package uk.gov.bis.lite.countryservice.spire;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.bis.lite.common.spire.client.SpireResponse;
import uk.gov.bis.lite.common.spire.client.parser.SpireParser;
import uk.gov.bis.lite.countryservice.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.spire.model.CountryList;
import uk.gov.bis.lite.countryservice.spire.model.SpireCountry;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class CountryParser implements SpireParser<List<SpireCountry>> {

  private final JAXBContext jaxbContext;

  public CountryParser() throws JAXBException {
    this.jaxbContext = JAXBContext.newInstance(CountryList.class);
  }

  @Override
  public List<SpireCountry> parseResponse(SpireResponse spireResponse) {

    SOAPMessage soapMessage = spireResponse.getMessage();
    try {
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      NodeList getCountriesResponse = soapMessage.getSOAPBody().getElementsByTagName("*");
      if (getCountriesResponse.getLength() > 0) {
        Node node = ((Element) getCountriesResponse.item(0)).getElementsByTagName("COUNTRY_LIST").item(0);
        CountryList countryList = unmarshaller.unmarshal(node, CountryList.class).getValue();
        return countryList.getCountries();
      } else {
        return Collections.emptyList();
      }

    } catch (JAXBException | SOAPException e) {
      throw new CountryServiceException("Failed to parse Spire country service response.", e);
    }

  }
}
