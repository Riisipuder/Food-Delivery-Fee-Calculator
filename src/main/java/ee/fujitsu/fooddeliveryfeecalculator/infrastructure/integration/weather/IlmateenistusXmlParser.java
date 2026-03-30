package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.integration.weather;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedWeatherStation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherFeedObservation;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class IlmateenistusXmlParser {

    public List<WeatherFeedObservation> parse(String xmlPayload) {
        try {
            Document document = buildDocument(xmlPayload);
            Element rootElement = document.getDocumentElement();
            Instant observedAt = parseObservedAt(rootElement);

            Map<SupportedWeatherStation, WeatherFeedObservation> observationsByStation =
                new EnumMap<>(SupportedWeatherStation.class);

            NodeList stationNodes = rootElement.getElementsByTagName("station");
            for (int index = 0; index < stationNodes.getLength(); index++) {
                Node stationNode = stationNodes.item(index);
                if (stationNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element stationElement = (Element) stationNode;
                String stationName = requiredText(stationElement, "name");

                SupportedWeatherStation.fromStationName(stationName).ifPresent(supportedStation -> {
                    if (observationsByStation.containsKey(supportedStation)) {
                        throw new WeatherFeedParsingException(
                            "Duplicate supported station in XML feed: " + supportedStation.stationName()
                        );
                    }

                    String wmoCode = requiredText(stationElement, "wmocode");
                    BigDecimal airTemperature = requiredDecimal(stationElement, "airtemperature");
                    BigDecimal windSpeed = requiredDecimal(stationElement, "windspeed");
                    String weatherPhenomenon = optionalText(stationElement, "phenomenon");

                    observationsByStation.put(
                        supportedStation,
                        new WeatherFeedObservation(
                            supportedStation.city(),
                            supportedStation.stationName(),
                            wmoCode,
                            airTemperature,
                            windSpeed,
                            weatherPhenomenon,
                            observedAt
                        )
                    );
                });
            }

            ensureAllSupportedStationsArePresent(observationsByStation);
            return orderedObservations(observationsByStation);
        } catch (WeatherFeedParsingException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new WeatherFeedParsingException("Failed to parse the Ilmateenistus XML feed", exception);
        }
    }

    private Document buildDocument(String xmlPayload) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setExpandEntityReferences(false);
        factory.setNamespaceAware(false);

        var documentBuilder = factory.newDocumentBuilder();
        documentBuilder.setErrorHandler(new DefaultHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });

        return documentBuilder.parse(new InputSource(new StringReader(xmlPayload)));
    }

    private Instant parseObservedAt(Element rootElement) {
        String timestamp = rootElement.getAttribute("timestamp");
        if (timestamp == null || timestamp.isBlank()) {
            throw new WeatherFeedParsingException("Missing observations timestamp attribute");
        }

        try {
            return Instant.ofEpochSecond(Long.parseLong(timestamp.trim()));
        } catch (NumberFormatException exception) {
            throw new WeatherFeedParsingException("Invalid observations timestamp: " + timestamp, exception);
        }
    }

    private String requiredText(Element parentElement, String tagName) {
        String value = optionalText(parentElement, tagName);
        if (value.isBlank()) {
            throw new WeatherFeedParsingException("Missing required tag value: " + tagName);
        }
        return value;
    }

    private String optionalText(Element parentElement, String tagName) {
        NodeList childNodes = parentElement.getElementsByTagName(tagName);
        if (childNodes.getLength() == 0) {
            throw new WeatherFeedParsingException("Missing required tag: " + tagName);
        }

        String textContent = childNodes.item(0).getTextContent();
        return textContent == null ? "" : textContent.trim();
    }

    private BigDecimal requiredDecimal(Element parentElement, String tagName) {
        String value = requiredText(parentElement, tagName);

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            throw new WeatherFeedParsingException(
                "Invalid decimal value for tag '" + tagName + "': " + value,
                exception
            );
        }
    }

    private void ensureAllSupportedStationsArePresent(
        Map<SupportedWeatherStation, WeatherFeedObservation> observationsByStation
    ) {
        for (SupportedWeatherStation supportedStation : SupportedWeatherStation.values()) {
            if (!observationsByStation.containsKey(supportedStation)) {
                throw new WeatherFeedParsingException(
                    "Missing required supported station in XML feed: " + supportedStation.stationName()
                );
            }
        }
    }

    private List<WeatherFeedObservation> orderedObservations(
        Map<SupportedWeatherStation, WeatherFeedObservation> observationsByStation
    ) {
        List<WeatherFeedObservation> observations = new ArrayList<>();
        for (SupportedWeatherStation supportedStation : SupportedWeatherStation.values()) {
            observations.add(observationsByStation.get(supportedStation));
        }
        return observations;
    }
}
