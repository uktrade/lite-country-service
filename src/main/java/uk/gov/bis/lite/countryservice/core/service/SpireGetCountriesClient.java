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

    private final String soapUrl;
    private final String soapNamespace;
    private final String soapAction;
    private final String spireCredentials;

    public SpireGetCountriesClient(String soapUrl, String soapNamespace, String soapAction, String spireCredentials) {
        this.soapUrl = soapUrl;
        this.soapNamespace = soapNamespace;
        this.soapAction = soapAction;
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
        envelope.addNamespaceDeclaration("spire", soapNamespace);

        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("getCountries", "spire");
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("countrySetId");
        soapBodyElem2.addTextNode(countrySetId);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction + "getCompanies");

        String authorization = Base64.getEncoder().encodeToString(spireCredentials.getBytes("utf-8"));
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
