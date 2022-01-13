package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.UUID;

@Component
@WritingConverter
public class UuidToByteArrayConverter implements Converter<UUID, byte[]> {

    @Override
    public byte[] convert(final UUID uuid) {
        return uuid.toString().getBytes(Charset.defaultCharset());
    }
}
