package uk.gov.dwp.jsa.citizen_ui.security;

import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MutableHttpServletRequestTest {

    private static final String CUSTOM_HEADER_KEY = "customHeaderKey";

    private static final String CUSTOM_HEADER_VALUE = "customHeaderValue";

    private static final String ROOT_HEADER_KEY = "rootHeaderKey";

    private static final String ROOT_HEADER_VALUE = "rootHeaderValue";

    private static final List<String> EXPECTED_HEADER_NAMES = Arrays.asList(ROOT_HEADER_KEY, CUSTOM_HEADER_KEY);

    private static final Map.Entry<String, String> CUSTOM_HEADER =
            new DefaultMapEntry<>(CUSTOM_HEADER_KEY, CUSTOM_HEADER_VALUE);

    private static final Map<String, String> ROOT_HEADER = Collections.singletonMap(ROOT_HEADER_KEY, ROOT_HEADER_VALUE);

    @Mock
    private HttpServletRequest rootRequest;

    private MutableHttpServletRequest testSubject;


    @Before public void setUp() {
        testSubject = new MutableHttpServletRequest(rootRequest);
        testSubject.putHeader(CUSTOM_HEADER.getKey(), CUSTOM_HEADER.getValue());
        when(rootRequest.getHeaderNames()).thenReturn(Collections.enumeration(ROOT_HEADER.keySet()));
        when(rootRequest.getHeader(ROOT_HEADER_KEY)).thenReturn(ROOT_HEADER_VALUE);
    }

    @Test
    public void test_getHeaderNames_ShouldReturnListWithBoth() {
        assertThat(Collections.list(testSubject.getHeaderNames()),
                IsIterableContainingInAnyOrder.containsInAnyOrder(EXPECTED_HEADER_NAMES.toArray()));
    }

    @Test
    public void test_getHeader_ShouldReturnRootValueWhenRootKey() {
        assertEquals(ROOT_HEADER_VALUE, testSubject.getHeader(ROOT_HEADER_KEY));
    }

    @Test
    public void test_getHeader_ShouldReturnCustomValueWhenCustomKey() {
        assertEquals(CUSTOM_HEADER_VALUE, testSubject.getHeader(CUSTOM_HEADER_KEY));
    }

}
