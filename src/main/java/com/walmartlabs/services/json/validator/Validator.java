package com.walmartlabs.services.json.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Validator implements JsonValidator {
    private static Logger LOG = LoggerFactory.getLogger(Validator.class);

    private JsonNode schemaValidation;
    private ProcessingReport lastProcessedReport;

    public JsonNode getSchemaValidation() {
        return schemaValidation;
    }

    public void setSchemaValidation(JsonNode schemaValidation) {
        this.schemaValidation = schemaValidation;
    }

    public boolean validate(String json) throws IOException, ProcessingException {
        LOG.info("Started json validation: " + json);
        final JsonNode inputJson = JsonLoader.fromString(json);

        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(schemaValidation);
        ProcessingReport report = schema.validate(inputJson, true);
        this.lastProcessedReport = report;

        LOG.info("Finished json validation with status: " + (report.isSuccess() ? "ok" : "fail"));
        if (!report.isSuccess()) {
            List<ProcessingMessage> errorMessages = getErrorMessages(report);
            LOG.error("Some error in json object: " + errorMessages.toString());
        }

        return report.isSuccess();
    }


    private List<ProcessingMessage> getErrorMessages(ProcessingReport report) {
        Stream<ProcessingMessage> messages = StreamSupport.stream(report.spliterator(), false);
        List<ProcessingMessage> collect = messages.filter(me -> me.getLogLevel().equals(LogLevel.ERROR)).collect(Collectors.toList());
        return collect;
    }

    public String getLastProcessedReportMessage() {
        return lastProcessedReport.toString();
    }

    public static final class ValidatorBuilder {
        private JsonNode schemaValidation;

        public ValidatorBuilder() {
        }

        public ValidatorBuilder schemaFromJsonNode(JsonNode schema) {
            this.schemaValidation = schema;
            return this;
        }

        public ValidatorBuilder schemaFromPath(String pathToSchema) throws IOException {
            this.schemaValidation = JsonLoader.fromResource(pathToSchema);
            return this;
        }

        public ValidatorBuilder schemaFromURL(URL urlToSchema) throws IOException {
            this.schemaValidation = JsonLoader.fromURL(urlToSchema);
            return this;
        }

        public Validator build() {
            Validator validator = new Validator();
            validator.setSchemaValidation(this.schemaValidation);
            return validator;
        }

    }
}
