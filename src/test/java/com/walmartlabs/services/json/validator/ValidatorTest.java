package com.walmartlabs.services.json.validator;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ValidatorTest {

    private Validator testInstance;

    @Test
    public void validate_positive_test() throws IOException, URISyntaxException, ProcessingException {
        testInstance = new Validator.ValidatorBuilder().schemaFromPath("/EventPublishDTOSchemaV2.json").build();
        String json = getJsonFromPath("InputOrderForValidationTest.json");

        boolean result = testInstance.validate(json);

        Assert.assertTrue(result);
    }

    @Test
    public void validate_negative_negative() throws IOException, URISyntaxException, ProcessingException {
        testInstance = new Validator.ValidatorBuilder().schemaFromPath("/EventPublishDTOSchemaV2.json").build();
        String json = getJsonFromPath("InputOrderForValidationTestNegative.json");

        boolean result = testInstance.validate(json);

        Assert.assertFalse(result);
    }

    @Test
    public void report_message_test() throws IOException, URISyntaxException, ProcessingException {
        validate_negative_negative();

        String lastProcessedReportMessage = testInstance.getLastProcessedReportMessage();

        Assert.assertNotNull(lastProcessedReportMessage);
        System.out.println(lastProcessedReportMessage);

    }


    private String getJsonFromPath(String path) throws URISyntaxException, IOException {
        Path uri = Paths.get(getClass().getClassLoader().getResource(path).toURI());
        return Files.lines(uri).collect(Collectors.joining("\n"));
    }
}