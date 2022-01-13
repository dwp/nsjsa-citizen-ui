package uk.gov.dwp.jsa.citizen_ui.services;

import org.junit.Test;

import static org.junit.Assert.*;

public class WelshPostcodeServiceTest {

    @Test
    public void isWelshPostCode_ForWelshPostCode() {
        boolean result = WelshPostcodeService.isWelshPostCode("CF7 5UH");
        assertTrue(result);
    }

    @Test
    public void isWelshPostCode_ForWelshPostCodeWithMixedCase() {
        boolean result = WelshPostcodeService.isWelshPostCode("cF7 5Uh");
        assertTrue(result);
    }

    @Test
    public void isWelshPostCode_ForEnglishPostCode() {
        boolean result = WelshPostcodeService.isWelshPostCode("NE9 5UH");
        assertFalse(result);
    }

    @Test
    public void isWelshPostCode_ForCF1() {
        boolean result = WelshPostcodeService.isWelshPostCode("CF1 5UH");
        assertTrue(result);
    }

    @Test
    public void isWelshPostCode_ForCF16() {
        boolean result = WelshPostcodeService.isWelshPostCode("CF16 5UH");
        assertFalse(result);
    }
}
