package uk.gov.dwp.jsa.citizen_ui.routing;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class RoutingSteps {

    private static final String FORWARD_SLASH = "/";
    private static final String COUNT = "/%s/";
    private static final String EMPTY = "";

    private final Map<String, Step> steps = new HashMap<>();
    private static final Map<String, String> PAGE_TITLES = new HashMap<>();

    public void register(final Step step) {
        steps.put(step.getIdentifier(), step);
    }

    public void deregister(final Step step) {
        steps.remove(step.getIdentifier());
    }

    public Optional<Step> getStep(final String identifier) {
        Step step = steps.get(identifier);
        return Optional.ofNullable(step);
    }

    public Optional<Step> getNextStep(final String identifier) {
        Optional<Step> step = getStep(identifier);
        if (step.isPresent()) {
            String nextStepIdentifier = getSanitisedNextStepIdentifier(step.get());
            return getStep(nextStepIdentifier);
        }
        return Optional.empty();
    }

    public String getSanitisedNextStepIdentifier(final Step step) {
        return step.getNextStepIdentifier()
                                .replaceFirst(FORWARD_SLASH, EMPTY)
                                .replace(COUNT, FORWARD_SLASH);
    }

    public Map<String, String> getKeyValuePairForPageTitles() {
        return PAGE_TITLES;
    }

    static {
    PAGE_TITLES.put("form/eligibility/residence", "eligibility.residence.form.question");
    PAGE_TITLES.put("form/eligibility/working", "eligibility.working.form.question");
    PAGE_TITLES.put("form/eligibility/working-over", "eligibility.hoursworkingperweek.form.question");
    PAGE_TITLES.put("form/eligibility/contributions", "eligibility.contributions.form.question");
    PAGE_TITLES.put("form/eligibility/eligible", "eligibility.eligible.heading");
    PAGE_TITLES.put("form/default-claim-start", "claimstart.default.label");
    PAGE_TITLES.put("form/claim-start", "claimstart.heading");
    PAGE_TITLES.put("form/backdating/why-not-apply-sooner", "backdating.whynow.details.title");
    PAGE_TITLES.put("form/backdating/have-you-been-in-paid-work-since", "backdating.been.in.work.since.question");
    PAGE_TITLES.put("form/backdating/have-you-been-in-full-time-education", "backdating.been.in.full.time.education.title");
    PAGE_TITLES.put("form/backdating/have-you-asked-for-advice", "backdating.asked.for.advice.question");
    PAGE_TITLES.put("form/backdating/have-you-been-unable-to-work-due-to-illness", "backdating.unable.to.work.due.to.illness.tab.question");

    PAGE_TITLES.put("form/nino", "nino.form.question");
    PAGE_TITLES.put("form/personal-details", "personaldetails.heading");
    PAGE_TITLES.put("form/date-of-birth", "dateofbirth.question");
    PAGE_TITLES.put("form/personal-details/address", "about.address.heading");
    PAGE_TITLES.put("form/personal-details/language-preference", "personaldetails.languagepreference.heading");
    PAGE_TITLES.put("form/personal-details/address-is-it-postal", "personaldetails.postal.question");
    PAGE_TITLES.put("form/personal-details/postal-address", "about.address.postal.heading");
    PAGE_TITLES.put("form/personal-details/contact/telephone", "contactpreferences.phone.question");
    PAGE_TITLES.put("form/personal-details/contact/email-confirmation",
                    "contactpreferences.emailconfirmation.question");
    PAGE_TITLES.put("form/personal-details/contact/email", "contactpreferences.email.question");
    PAGE_TITLES.put("form/bank-account", "bankaccount.form.heading");
    PAGE_TITLES.put("form/other-benefits/have-you-applied", "otherbenefits.question");
    PAGE_TITLES.put("form/other-benefits/details", "other.benefits.details.title");
    PAGE_TITLES.put("form/claim-start/jury-service/have-you-been", "juryservice.haveyoubeen.form.question");
    PAGE_TITLES.put("form/claim-start/jury-service/start-date", "juryservice.date.question");
    PAGE_TITLES.put("form/current-work/are-you-working", "current.work.is.working.question");
        PAGE_TITLES.put("form/current-work/details/is-work-paid", "current.work.is.paid.question");
            // if is paid
            PAGE_TITLES.put("form/current-work/details/how-often-paid", "current.work.how.often.paid.heading");
            PAGE_TITLES.put("form/current-work/details/name", "current.work.employers.name.question");
            PAGE_TITLES.put("form/current-work/details/address", "current.work.address.question");
            PAGE_TITLES.put("form/current-work/details/phone", "currentwork.employer.phone.question");
            PAGE_TITLES.put("form/current-work/details/hours", "currentwork.hours.question");
            PAGE_TITLES.put("form/current-work/details/self-employed-confirmation",
                            "currentwork.selfemployedconfirmation.question");
            PAGE_TITLES.put("form/current-work/has-another-job", "currentwork.has.another.job.question");
            // if is voluntary
                // Can you choose if you’re paid for this voluntary work? = YES or NO (both display same view)
                // the two below routes are in addition to the above current-work questions, not instead of
                PAGE_TITLES.put("form/current-work/details/choose-payment", "currentwork.choosepayment.question");
                PAGE_TITLES.put("form/current-work/details/get-paid", "currentwork.voluntarypaid.question");
    PAGE_TITLES.put("form/previous-employment/has-previous-work", "previousemployment.hasPreviousWork.question");
        PAGE_TITLES.put("form/previous-employment/employer-details/dates", "previousemployment.date.question");
        PAGE_TITLES.put("form/previous-employment/employer-details/why-end",
                        "previousemployment.employerdetails.whyended.question");
        PAGE_TITLES.put("form/previous-employment/employer-details/name",
                        "previousemployment.employerdetails.name.question");
        PAGE_TITLES.put("form/previous-employment/employer-details/address",
                        "previousemployment.address.heading");
        PAGE_TITLES.put("form/previous-employment/employer-details/phone",
                        "previousemployment.employerdetails.phone.question");
        PAGE_TITLES.put("form/previous-employment/employer-details/expect-payment",
                        "previousemployment.expectpayment.form.title");
        PAGE_TITLES.put("form/previous-employment/employer-details/status",
                        "previousemployment.employmentstatus.question");
        PAGE_TITLES.put("form/previous-employment/add-work", "previousWork.hasAnotherWork.question");
    PAGE_TITLES.put("form/outside-work/has-outside-work", "work.outside.uk.question");
    PAGE_TITLES.put("form/pensions/current/has-pension", "pensions.current.has.pension.question");
        PAGE_TITLES.put("form/pensions/current/details/provider-name", "pensions.current.providers.name.question");
        PAGE_TITLES.put("form/pensions/current/details/provider-address",
                        "pensions.current.providers.address.question");
        PAGE_TITLES.put("form/pensions/current/details/payment-frequency",
                        "pensions.current.paymentfrequency.question");
        PAGE_TITLES.put("form/pensions/current/details/pension-increase", "pensions.current.pensionincrease.question");
        PAGE_TITLES.put("form/pensions/current/details/increase-date", "pensions.current.month.question");
        PAGE_TITLES.put("form/pensions/current/has-another-pension", "pensions.current.has.another.question");
        PAGE_TITLES.put("form/education/have-you-been", "education.haveyoubeen.form.question");
    PAGE_TITLES.put("form/education/course-name", "education.coursename.form.question");
    PAGE_TITLES.put("form/education/place", "education.place.form.question");
    PAGE_TITLES.put("form/education/course-hours", "education.coursehours.form.heading");
    PAGE_TITLES.put("form/education/course-duration", "education.courseduration.date.question");
    PAGE_TITLES.put("form/availability/available-for-interview", "availability.guard.form.question");
    PAGE_TITLES.put("form/availability/availability", "availability.question");
    PAGE_TITLES.put("form/summary", "summary.header");
    PAGE_TITLES.put("form/declaration", "declaration.form.question.heading");
    PAGE_TITLES.put("claimant-confirmation", "confirmation.complete");
    }
}
