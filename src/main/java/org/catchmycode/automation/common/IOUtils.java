package org.catchmycode.automation.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.catchmycode.automation.environment.PropertyLoader;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.collections.ListMultiMap;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.*;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonStructureEquals;
import static org.testng.Assert.fail;

public interface IOUtils {

    String DEFAULT_DATA_DELIMITER = "\\|\\|";
    ObjectMapper mapper = new ObjectMapper();

    Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    /**
     * Loads a single text file content to a list
     *
     * @param dataFileLocation the resource path
     * @return the object array of data
     */
    default List<String> getTestDataFromFileAsList(String dataFileLocation) {
        ArrayList<String> data = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(IOUtils.class.getClassLoader()
                .getResourceAsStream(dataFileLocation)))) {

            String line = null;
            while ((line = fileReader.readLine()) != null) {

                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    data.add(line);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot open " + dataFileLocation);
            fail();
        }
        return data;
    }

    /**
     * Loads a name/value formatted CSV file into a map
     *
     * NOTE: this will only pick up the first two fields in the file and ignores the rest
     *
     * @param dataFileLocation the resource path
     * @return the map of data
     */
    default ListMultiMap<String, String> getTestDataFromFileAsMap(String dataFileLocation) {
        ListMultiMap<String, String> data = new ListMultiMap<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(IOUtils.class.getClassLoader()
                .getResourceAsStream(dataFileLocation)))) {

            String line = null;
            while ((line = fileReader.readLine()) != null) {

                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    String[] lineArray = line.split(",");
                    data.put(lineArray[0], lineArray[1]);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot open " + dataFileLocation);
            fail();
        }
        return data;
    }

    /**
     * Loads a single text file content to a single string
     *
     * @param dataFileLocation the resource path
     * @return the object array of data
     */
    default String getAllTestDataFromFile(String dataFileLocation) {

        StringBuilder data = new StringBuilder();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(IOUtils.class.getClassLoader()
                .getResourceAsStream(dataFileLocation)))) {

            String line = null;
            while ((line = fileReader.readLine()) != null) {
                data.append(line + "\r\n");
            }
        } catch (Exception e) {
            LOGGER.error("Cannot open " + dataFileLocation);
            fail();
        }
        return data.toString();
    }

    /**
     * Loads a single text file content to an Iterator
     *
     * @param dataFileLocation the resource path
     * @return the object array of data
     */
    default Iterator<Object[]> getTestDataFromCsvFile(String dataFileLocation) {
        return getTestDataFromCsvFile(dataFileLocation, false);
    }

    /**
     * Loads a single text file content to an Iterator
     *
     * @param dataFileLocation the resource path
     * @param disableShuffle   turns off shuffling
     * @return the object array of data
     */
    default Iterator<Object[]> getTestDataFromCsvFile(String dataFileLocation, boolean disableShuffle) {
        List<Object[]> data = new ArrayList<>();

        int counter = 0;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(IOUtils.class
                .getClassLoader().getResourceAsStream(dataFileLocation)))) {

            String line = null;
            while ((line = fileReader.readLine()) != null) {

                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    //data.add((line.split(",")));
                    data.add(new Object[]{new TestRecord(line.split(","))});
                    counter++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot open " + dataFileLocation);
            fail();
        }

        //randomize the order of the data set
        if (!disableShuffle) {
            Collections.shuffle(data);
        }

        //limit the size if needed
        int maxCsvRecords = PropertyLoader.getInstance().getPropertyAsInteger("max.csv.records");
        if (maxCsvRecords > data.size()) {
            maxCsvRecords = data.size();
        }

        //return the list segment
        return data.subList(0, maxCsvRecords).iterator();

    }

    /**
     * Loads a number of text file content to a data provider dataset for TestNG
     *
     * @param dataFileLocation the resource path
     * @return the object array of data
     */
    default Iterator<Object[]> getTestDataFromPath(String dataFileLocation) {
        ArrayList<Object[]> data = new ArrayList<>();
        try (BufferedReader directoryReader = new BufferedReader(new InputStreamReader(IOUtils.class.getClassLoader()
                .getResourceAsStream(dataFileLocation)))) {

            String fileName = null;
            while ((fileName = directoryReader.readLine()) != null) {

                try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(IOUtils.class.getClassLoader()
                        .getResourceAsStream(dataFileLocation + "/" + fileName)))) {

                    String line = null;
                    while ((line = fileReader.readLine()) != null) {

                        if (!line.startsWith("#") && !line.trim().isEmpty()) {
                            data.add(new String[]{line});
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot open " + dataFileLocation);
            fail();
        }
        return data.iterator();
    }

    /**
     * Global static object mapper for all implementations
     *
     * @return the static object mapper
     */
    default ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Parses a String into a {@link org.w3c.dom.Document}
     *
     * @param json String
     * @return the json node
     */
    default JsonNode parseJson(String json) {

        if (json == null || json.isEmpty()) {
            fail("Requested JSON string cannot be parsed as it is empty");
        }

        try {
            return mapper.readTree(json);
        } catch (Exception ex) {
            fail("Requested JSON string cannot be parsed as it is invalid");
            return null;
        }
    }

    /**
     * Parses a String into a {@link org.w3c.dom.Document}
     *
     * @param xml String
     * @return the document
     */
    default JsonNode parseXmlAsJson(String xml) {

        if (xml == null || xml.isEmpty()) {
            fail("Requested XML string cannot be parsed as it is empty");
        }

        try {
            //note the XML mapper in Jackson does not properly support arrays, so we org.json instead
            String converted = XML.toJSONObject(xml).toString();
            return mapper.readTree(converted);
        } catch (Exception ex) {
            fail("Requested XML string cannot be parsed as it is invalid");
            return null;
        }
    }

    /**
     * Parses a String into a {@link org.w3c.dom.Document}
     *
     * @param xml String
     * @return the document
     */
    default Document parseXml(String xml) {

        if (xml == null || xml.isEmpty()) {
            fail("Requested XML string cannot be parsed as it is empty");
        }

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(false);
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();

            return documentBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

        } catch (Exception ex) {
            fail("Requested JSON string cannot be parsed as it is invalid");
            return null;
        }
    }


    /**
     * Verifies that a JSON document matches another in terms of structure
     *
     * @param actual   the actual JSON payload
     * @param expected the expected JSON payload to match
     * @throws AssertionError the error
     */
    default void verifyJsonTemplateStructure(String actual, String expected) throws AssertionError {

        JsonNode actualNode = parseJson(actual);
        JsonNode expectedNode = parseJson(expected);
        assertJsonStructureEquals(expectedNode, actualNode);

    }

    /**
     * Verifies that an XML document matches another in terms of structure
     *
     * @param actual   the actual XML payload
     * @param expected the expected XML payload to match
     * @throws AssertionError the error
     */
    default void verifyXmlTemplateStructure(String actual, String expected) throws AssertionError {

        JsonNode actualNode = parseXmlAsJson(actual);
        JsonNode expectedNode = parseXmlAsJson(expected);
        assertJsonStructureEquals(actualNode, expectedNode);
    }

    /**
     * Replaces multiple strings in a single string via a supplied map
     *
     * @param template the template to replace strings in
     * @param values   a map of tokens and values
     * @return replaces the map keys with the values within the template
     */
    default String replaceAll(final String template, Map<String, String> values) {

        if (template == null) {
            return null;
        }

        if (values == null) {
            return template;
        }

        String value = template;
        for (Map.Entry entry : values.entrySet()) {
            value = value.replace(entry.getKey().toString(), entry.getValue().toString());
        }
        return value;
    }

    /**
     * Loads a number of text file content to a data provider dataset for TestNG
     *
     * @param dataFileLocation the resource path
     * @return the object array of data
     */
    default Iterator<Object[]> getTestDataFromPathRaw(String dataFileLocation) {
        ArrayList<Object[]> data = new ArrayList<>();
        try (BufferedReader directoryReader = new BufferedReader(new InputStreamReader(IOUtils.class.getClassLoader()
                .getResourceAsStream(dataFileLocation)))) {

            String fileName = null;
            while ((fileName = directoryReader.readLine()) != null) {

                try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(IOUtils.class.getClassLoader()
                        .getResourceAsStream(dataFileLocation + "/" + fileName)))) {

                    String line = null;
                    StringBuilder content = new StringBuilder();
                    while ((line = fileReader.readLine()) != null) {
                        content.append(line + "\r\n");
                    }
                    data.add(new Object[]{content.toString()});
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot open " + dataFileLocation);
            fail();
        }
        return data.iterator();
    }
}
