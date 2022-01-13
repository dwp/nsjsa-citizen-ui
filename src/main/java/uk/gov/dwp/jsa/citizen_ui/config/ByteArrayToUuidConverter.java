package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.UUID;

@Component
@ReadingConverter
public class ByteArrayToUuidConverter implements Converter<byte[], UUID> {

    @Override
    public UUID convert(final byte[] bytes) {
        return UUID.fromString(new String(bytes, Charset.defaultCharset()));
    }
}
