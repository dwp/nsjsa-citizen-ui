package uk.gov.dwp.jsa.citizen_ui.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SortCodeTest {

    @Test
    public void testConstructorSetsValues() {
        //Arrange
        final String expected = "112233";

        //Act
        final SortCode testSubject = new SortCode(expected);

        //Asset
        assertThat(testSubject.getCode()).isEqualTo(expected);
    }

    @Test
    public void testIsEmptyReturnsTrueWhenCodeNull() {
        //Arrange
        final SortCode testSubject = new SortCode();

        //Act and Assert
        assertThat(testSubject.isEmpty()).isTrue();
    }

    @Test
    public void testIsEmptyReturnsTrueWhenCodeEmpty() {
        //Arrange
        final SortCode testSubject = new SortCode();
        testSubject.setCode("");

        //Act and Assert
        assertThat(testSubject.isEmpty()).isTrue();
    }

    @Test
    public void testIsEmptyReturnsTrueWhenCodeWhiteSpace() {
        //Arrange
        final SortCode testSubject = new SortCode();
        testSubject.setCode("     ");

        //Act and Assert
        assertThat(testSubject.isEmpty()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorThrowsIllegalArgumentExceptionIfEmptySortCodeUsed() {
        new SortCode("");
    }

    @Test
    public void testSetCodeSetsValue() {
        //Arrange
        final SortCode testSubject = new SortCode();
        final String expected = "112233";

        //Act
        testSubject.setCode(expected);

        assertThat(testSubject.getCode()).isEqualTo(expected);
    }

    @Test
    public void testGetSanitisedCodeReturnsNullIfCodeBlank() {
        //Arrange
        final SortCode testSubject = new SortCode();

        //Act and assert
        assertThat(testSubject.getSanitisedCode()).isNull();
    }

    @Test
    public void testGetSanitisedCodeReturnsSameCodeIfCodeHasNoHyphens() {
        //Arrange
        final String expected = "112233";
        final SortCode testSubject = new SortCode(expected);

        //Act
        final String actual = testSubject.getSanitisedCode();

        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetSanitisedCodeReturnsCodeWithHyphens() {
        //Arrange
        final String expected = "11-22-33";
        final SortCode testSubject = new SortCode(expected);

        //Act
        final String actual = testSubject.getSanitisedCode();

        //Assert
        assertThat(actual).isEqualTo(expected.replace("-", ""));
    }

    @Test
    public void testGetFormattedCodeReturnsNullIfCodeIsEmpty() {
        //Arrange
        final SortCode testSubject = new SortCode();

        //Act
        final String actual = testSubject.getFormattedCode();

        //Assert
        assertThat(actual).isNull();
    }

    @Test
    public void testGetFormattedCodeReturnsSameCodeWhenAlreadyFormatted() {
        //Arrange
        final String expected = "11-22-33";
        final SortCode testSubject = new SortCode(expected);

        //Act
        final String actual = testSubject.getFormattedCode();

        //Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetFormattedCodeReturnsCorrectFormat() {
        //Arrange
        final String expected = "11-22-33";
        final SortCode testSubject = new SortCode(expected.replace("-", ""));

        //Act
        final String actual = testSubject.getFormattedCode();

        //Assert
        assertThat(actual).isEqualTo(expected);
    }
}
