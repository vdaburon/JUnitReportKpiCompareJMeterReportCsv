package io.github.vdaburon.jmeter.utils.comparekpi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class UtilsJsonFile {
    public static void saveJsonFile(GlobalResult globalResult, String jsonFileOut) throws IOException, TemplateException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(new File(jsonFileOut), globalResult);
    }
}
