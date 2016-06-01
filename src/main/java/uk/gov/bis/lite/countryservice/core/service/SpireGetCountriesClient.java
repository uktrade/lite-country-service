package uk.gov.bis.lite.countryservice.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class SpireGetCountriesClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpireGetCountriesClient.class);

  private static final String SOAP_NAMESPACE = "http://www.fivium.co.uk/fox/webservices/ispire/SPIRE_COUNTRIES";
  private static final String SOAP_PREFIX = "spire";

  private final String soapUrl;
  private final String spireCredentials;

  public SpireGetCountriesClient(String soapUrl, String spireCredentials) {
    this.soapUrl = soapUrl;
    this.spireCredentials = spireCredentials;
  }

  public SOAPMessage executeRequest(String countrySetId) throws SOAPException, UnsupportedEncodingException {

    SOAPConnection soapConnection = null;

    try {
      SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
      soapConnection = soapConnectionFactory.createConnection();

      SOAPMessage request = createRequest(countrySetId);
      LOGGER.debug(messageAsString(request));

      SOAPMessage response = soapConnection.call(request, soapUrl);
      LOGGER.debug(messageAsString(response));

      return response;

    } finally {
      if (soapConnection != null) {
        soapConnection.close();
      }
    }
  }

  private SOAPMessage createRequest(String countrySetId) throws SOAPException, UnsupportedEncodingException {

    MessageFactory messageFactory = MessageFactory.newInstance();
    SOAPMessage soapMessage = messageFactory.createMessage();
    SOAPPart soapPart = soapMessage.getSOAPPart();

    SOAPEnvelope envelope = soapPart.getEnvelope();
    envelope.addNamespaceDeclaration(SOAP_PREFIX, SOAP_NAMESPACE);

    SOAPBody soapBody = envelope.getBody();
    SOAPElement soapBodyElem = soapBody.addChildElement("getCountries", SOAP_PREFIX);
    SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("countrySetId");
    soapBodyElem2.addTextNode(countrySetId);

    MimeHeaders headers = soapMessage.getMimeHeaders();
    headers.addHeader("SOAPAction", SOAP_NAMESPACE + "/getCountries");

    String authorization = Base64.getEncoder().encodeToString(spireCredentials.getBytes("utf-8"));
    headers.addHeader("Authorization", "Basic " + authorization);
    soapMessage.saveChanges();

    return soapMessage;
  }


  private static String messageAsString(SOAPMessage soapMessage) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      soapMessage.writeTo(outputStream);
      return outputStream.toString("UTF-8");
    } catch (IOException | SOAPException e) {
      return null;
    }
  }

}
