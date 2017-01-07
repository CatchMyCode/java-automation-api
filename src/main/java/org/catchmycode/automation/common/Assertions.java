package org.catchmycode.automation.common;


import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.List;

public interface Assertions {

    Logger LOGGER = LoggerFactory.getLogger(Assertions.class);

    /**
     * Since TestNG genericizes JsonNodes as Iterables, we need to override this functionality
     *
     * @param actual the actual value
     * @param expected the expected value
     */
    default void assertNodeEquals(JsonNode actual, JsonNode expected) {

        if ((actual == null && expected == null)
                || (actual.isNull() && expected.isNull())) {
            return;
        }

        Assert.assertEquals((Object) actual, (Object) expected);
    }

    /**
     * Since TestNG genericizes JsonNodes as Iterables, we need to override this functionality
     *
     * @param actual the JsonNode to test
     */
    default void assertNodeNotNull(JsonNode actual) {

        if (actual == null || actual.isNull()) {
            throw new AssertionError("Node should not have contained a null value");
        }
    }

    /**
     * Checks a nodes first level "map" of fields for null and asserts all are NOT null
     *
     * @param actual the JsonNode to test
     */
    default void assertNodeChildrenNotNull(JsonNode actual) {

        if (actual == null || actual.isNull()) {
            throw new AssertionError("Node should not have contained a null value");
        }

        for (JsonNode field : actual) {
            if (field.isNull()) {
                throw new AssertionError("Expected " + field.asText() + " field not to be null");
            }
        }
    }

    /**
     * Asserts that a node is a text type and optionally nullable
     * <p>
     * Also checks the length
     *
     * @param node      the JSON node
     * @param name      the node name to get
     * @param nullable  if the field can be nullable
     * @param maxLength the max length supported/allowed
     */
    default void assertTextNode(JsonNode node, String name, boolean nullable, int maxLength) {

        if (node == null || name == null || name.isEmpty()) {
            throw new AssertionError("Node and name are required fields");
        }
        if (isNullOkay(node, name, nullable)) {
            return;
        }
        Assert.assertTrue(node.get(name).isTextual(), "The field \"" + name + "\" " +
                "is expected to have a type of text/string ");
        if (maxLength > -1) {
            Assert.assertTrue(node.get(name).asText().length() <= maxLength, "The field \"" + name
                    + "\" exceeds the max length of " + maxLength);
        }
    }

    /**
     * Asserts that a node is a text type and optionally nullable
     *
     * @param node     the JSON node
     * @param name     the node name to get
     * @param nullable if the field can be nullable
     */
    default void assertTextNode(JsonNode node, String name, boolean nullable) {
        assertTextNode(node, name, nullable, -1);
    }

    /**
     * Asserts that a node matches a provided regex string/pattern
     *
     * @param node     the JSON node
     * @param name     the node name to get
     * @param pattern  the pattern
     * @param nullable if the field can be nullable
     */
    default void assertNodeMatches(JsonNode node, String name, String pattern, boolean nullable) {

        if (node == null || name == null || name.isEmpty() || pattern == null) {
            throw new AssertionError("Node, name and pattern are required fields");
        }

        if (isNullOkay(node, name, nullable)) {
            return;
        }
        Assert.assertTrue(node.get(name).asText().matches(pattern), "The field \"" + name + "\" " +
                "does not match pattern of \"" + pattern + "\"");
    }

    /**
     * Asserts a node is a JSON container type
     *
     * @param node     the JSON node
     * @param name     the node name to get
     * @param nullable if the field can be nullable
     */
    default void assertContainerNode(JsonNode node, String name, boolean nullable) {

        if (node == null || name == null || name.isEmpty()) {
            throw new AssertionError("Node and name are required fields");
        }

        if (isNullOkay(node, name, nullable)) {
            return;
        }
        Assert.assertTrue(node.get(name).isContainerNode(), "The field \"" + name + "\" is expected to be a container node");
    }

    /**
     * Asserts a node is one of the following: float, decimal or double
     *
     * @param node     the JSON node
     * @param name     the node name to get
     * @param nullable if the field can be nullable
     */
    default void assertDecimalNode(JsonNode node, String name, boolean nullable) {

        if (node == null || name == null || name.isEmpty()) {
            throw new AssertionError("Node and name are required fields");
        }

        if (isNullOkay(node, name, nullable)) {
            return;
        }
        Assert.assertTrue(node.get(name).isBigDecimal() ||
                        node.get(name).isFloat() ||
                        node.get(name).isDouble()
                , "The field \"" + name + "\" is expected to have a type of decimal/float/double ");
    }

    /**
     * Asserts a node is of an integer type
     *
     * @param node     the JSON node
     * @param name     the node name to get
     * @param nullable if the field can be nullable
     */
    default void assertIntegerNode(JsonNode node, String name, boolean nullable) {

        if (node == null || name == null || name.isEmpty()) {
            throw new AssertionError("Node and name are required fields");
        }

        if (isNullOkay(node, name, nullable)) {
            return;
        }

        Assert.assertTrue(node.get(name).isInt(), "The field \"" + name + "\" is expected to have a type of int ");
    }

    /**
     * Internal method to check null for certain asserts
     *
     * @param node     the JSON node
     * @param name     the node name to get
     * @param nullable if the field can be nullable
     * @return true if name is allowed to be null
     */
    default boolean isNullOkay(JsonNode node, String name, boolean nullable) {

        try {
            assertNodeNotNull(node.get(name));
        } catch (AssertionError ex) {
            if (nullable) {
                return true;
            }
            throw (ex);
        }
        return false;
    }

    /**
     * Assert that the provided node is either a decimal node or that it has a string value that
     * can be converted to a double.
     *
     * @param node the node
     * @param name the name
     * @param nullable if null is allowed
     */
    default void assertDecimalValue(JsonNode node, String name, boolean nullable) {
        try {
            assertDecimalNode(node, name, nullable);
        } catch (AssertionError ex) {
            try {
                //Try to convert the value to a double.
                new Double(node.get(name).asText());
            } catch (NumberFormatException numException) {
                //The value cannot be converted, throw the assertion failure.
                throw (ex);
            }
        }
    }

    /**
     * Assert that a node's value exists in the the provided array.
     *
     * @param node          the JSON node
     * @param name          the node name to get
     * @param allowedValues An array containing allowed values.
     */
    default void assertInRange(JsonNode node, String name, List<String> allowedValues) {
        Assert.assertTrue(allowedValues.contains(node.get(name).asText()), "The field \"" + name + "\" with value \""
                + node.get(name).asText() + "\" is not valid.");
    }

    /**
     * Assert that a node has an integer value in the provided range.
     *
     * @param node         the JSON node
     * @param name         the node name to get
     * @param minInclusive the minimum allowed value
     * @param maxInclusive the maximum allowed value
     */
    default void assertInRange(JsonNode node, String name, int minInclusive, int maxInclusive) {
        int value = node.get(name).asInt();
        Assert.assertTrue(value >= minInclusive && value <= maxInclusive, "The field \"" + name + "\" is out of range ["
                + minInclusive + " to " + maxInclusive + "].");
    }
}
