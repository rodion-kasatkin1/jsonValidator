package com.walmartlabs.services.json.validator;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import java.io.IOException;

public interface JsonValidator {
    boolean validate(String json) throws IOException, ProcessingException;
}
