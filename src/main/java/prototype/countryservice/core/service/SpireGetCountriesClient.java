package prototype.countryservice.core.service;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototype.countryservice.CountryServiceConfiguration;

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

    private final String soapUrl;
    private final String soapNamespace;
    private final String soapAction;

    public SpireGetCountriesClient(String soapUrl, String soapNamespace, String soapAction) {
        this.soapUrl = soapUrl;
        this.soapNamespace = soapNamespace;
        this.soapAction = soapAction;
    }

    @Inject
    public SpireGetCountriesClient(CountryServiceConfiguration configuration) {
        this.soapUrl = configuration.getSoapUrl();
        this.soapNamespace = configuration.getSoapNamespace();
        this.soapAction = configuration.getSoapAction();
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

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("spire", soapNamespace);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("getCountries", "spire");
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("countrySetId");
        soapBodyElem2.addTextNode(countrySetId);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction + "getCompanies");

        String authorization = Base64.getEncoder().encodeToString("lu@gov.uk:dev".getBytes("utf-8"));
        headers.addHeader("Authorization", "Basic " + authorization);
        soapMessage.saveChanges();

        return soapMessage;
    }


    private static String messageAsString(SOAPMessage soapMessage) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);
            return outputStream.toString();
        } catch(IOException | SOAPException e) {
            return null;
        }
    }

}
