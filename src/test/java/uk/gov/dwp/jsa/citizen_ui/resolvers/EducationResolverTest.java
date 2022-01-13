package uk.gov.dwp.jsa.citizen_ui.resolvers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.jsa.adaptors.dto.claim.circumstances.Circumstances;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationConfirmationController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseDurationController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseHoursController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationCourseNameController;
import uk.gov.dwp.jsa.citizen_ui.controller.education.EducationPlaceController;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.form.common.*;
import uk.gov.dwp.jsa.citizen_ui.model.form.education.EducationCourseHoursQuestion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EducationResolverTest {

    private static final String EXPECTED_COURSE_NAME = "ANY_COURSE";
    public static final StringQuestion ANSWER_COURSE_NAME = new StringQuestion(EXPECTED_COURSE_NAME);

    private static final String EXPECTED_PLACE = "ANY_PLACE";
    public static final StringQuestion ANSWER_PLACE = new StringQuestion(EXPECTED_PLACE);

    private static final int STARTDATE_DAY = 1;
    private static final Month STARTDATE_MONTH = Month.JANUARY;
    private static final int STARTDATE_YEAR = 2018;
    private static final LocalDate EXPECTED_STARTDATE = LocalDate.of(STARTDATE_YEAR, STARTDATE_MONTH, STARTDATE_DAY);
    private static final DateQuestion ANSWER_STARTDATE = new DateQuestion(STARTDATE_DAY, STARTDATE_MONTH.getValue(), STARTDATE_YEAR);

    private static final int ENDDATE_DAY = 3;
    private static final Month ENDDATE_MONTH = Month.APRIL;
    private static final int ENDDATE_YEAR = 2018;
    private static final LocalDate EXPECTED_ENDDATE = LocalDate.of(ENDDATE_YEAR, ENDDATE_MONTH, ENDDATE_DAY);
    private static final DateQuestion ANSWER_ENDDATE = new DateQuestion(ENDDATE_DAY, ENDDATE_MONTH.getValue(), ENDDATE_YEAR);

    public static final DateRangeQuestion ANSWER_DATERANGE = new EducationDurationQuestion(ANSWER_STARTDATE, ANSWER_ENDDATE);

    private static final Double EXPECTED_HOURS = 10.0;
    public static final EducationCourseHoursQuestion ANSWER_HOURS = new EducationCourseHoursQuestion(BigDecimal.valueOf(EXPECTED_HOURS));

    @Mock
    private Claim mockClaim = new Claim();

    private EducationResolver testSubject;

    @Before
    public void setUp() {
        testSubject = new EducationResolver();
        when(mockClaim.get(EducationPlaceController.IDENTIFIER)).thenReturn(Optional.of(ANSWER_PLACE));
        when(mockClaim.get(EducationCourseDurationController.IDENTIFIER)).thenReturn(Optional.of(ANSWER_DATERANGE));
        when(mockClaim.get(EducationCourseNameController.IDENTIFIER)).thenReturn(Optional.of(ANSWER_COURSE_NAME));
        when(mockClaim.get(EducationCourseHoursController.IDENTIFIER)).thenReturn(Optional.of(ANSWER_HOURS));
    }

    @Test
    public void educationDetailsAreResolvedFromClaimSuccessfully() {
        when(mockClaim.get(EducationConfirmationController.IDENTIFIER)).thenReturn(Optional.of(new BooleanQuestion(true)));

        Circumstances result = new Circumstances();
        testSubject.resolve(mockClaim, result);

        assertEquals(EXPECTED_COURSE_NAME, result.getEducation().getCourseName());
        assertEquals(EXPECTED_PLACE, result.getEducation().getInstitutionName());
        assertEquals(EXPECTED_STARTDATE, result.getEducation().getStartDate());
        assertEquals(EXPECTED_ENDDATE, result.getEducation().getEndDate());
        assertEquals(EXPECTED_HOURS, result.getEducation().getHoursPerWeek());
    }

    @Test
    public void educationDetailsAreNotResolvedIfEducationConfirmationAnswerIsNo() {
        when(mockClaim.get(EducationConfirmationController.IDENTIFIER)).thenReturn(Optional.of(new BooleanQuestion(false)));

        Circumstances result = new Circumstances();
        testSubject.resolve(mockClaim, result);

        assertNull(result.getEducation());
    }

}
