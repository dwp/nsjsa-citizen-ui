package uk.gov.dwp.jsa.citizen_ui.controller.citizen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CookieDetailControllerTest {
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Model model;

    private CookieDetailController sut;

    @Before
    public void setup() {
        sut = new CookieDetailController();
    }

    @Test
    public void getCookiePolicy() {
        String path = sut.getCookiePolicy(request, model);
        assertThat(path, is("citizen/cookies-details"));
    }
}
