package uk.gov.dwp.jsa.citizen_ui.config;


import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ByteArrayToRedisCustomConverterConfigTest {

    private static final UUID MY_UUID = UUID.fromString("5a3fe9c7-f7d8-464c-9973-e5b6580d27ed");

    private ByteArrayToUuidConverter byteArrayToUuidConverter;

    @Before
    public void before() {
        this.byteArrayToUuidConverter = new ByteArrayToUuidConverter();
    }

    @Test
    public void byteArrayIsConvertedToUuid() {

        final byte[] uuid_bytes = new byte[] {
                53, 97, 51, 102, 101, 57, 99, 55, 45, 102, 55, 100, 56, 45,
                52, 54, 52, 99, 45, 57, 57, 55, 51, 45, 101, 53, 98, 54, 53,
                56, 48, 100, 50, 55, 101, 100};

        final UUID actual = byteArrayToUuidConverter.convert(uuid_bytes);

        assertThat(actual, is(equalTo(MY_UUID)));
    }

}
