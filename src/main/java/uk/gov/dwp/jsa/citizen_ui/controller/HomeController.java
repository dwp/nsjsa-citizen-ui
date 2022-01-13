package uk.gov.dwp.jsa.citizen_ui.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.dwp.jsa.adaptors.enums.ClaimType;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.model.RestoreClaim;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimDBRepository;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.services.RestoreClaimService;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static uk.gov.dwp.jsa.citizen_ui.Constants.COOKIE_CLAIM_ID;
import static uk.gov.dwp.jsa.citizen_ui.Constants.SESSION_TIMEOUT;
import static uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController.addClaimIdCookie;
import static uk.gov.dwp.jsa.citizen_ui.controller.BaseFormController.addCookie;

/**
 * Home controller.
 */
@Controller
public class HomeController {

    private final ClaimRepository claimRepository;

    private final ClaimDBRepository claimDBRepository;

    private final RestoreClaimService restoreClaimService;

    private final int sessionTimeout;


    public HomeController(
            final ClaimRepository claimRepository,
            final ClaimDBRepository claimDBRepository,
            final RestoreClaimService restoreClaimService,
            @Value("${" + SESSION_TIMEOUT + "}") final int sessionTimeout

    ) {
        this.claimRepository = claimRepository;
        this.claimDBRepository = claimDBRepository;
        this.restoreClaimService = restoreClaimService;
        this.sessionTimeout = sessionTimeout;
    }


    /**
     * Shows the home page.
     *
     * @param model SpingBoot MVC model
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @param response HttpServletResponse
     * @return Name of view
     */
    @GetMapping(path = "/")
    public final String index(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final Model model,
            final HttpServletResponse response
    ) {
        final Claim claim = findClaimOrCreateNew(claimId);
        addClaimIdCookie(claim, response);
        return "index";
    }

    /**
     * Shows the home page.
     *
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @param response HttpServletResponse
     * @return Name of view
     */
    @GetMapping(path = "/eligible")
    public final String eligible(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final Model model,
            final HttpServletResponse response
    ) {
        final Claim claim = findClaimOrCreateNew(claimId);
        model.addAttribute("eligible", true);
        addCookie("eligibility-done", "true", response);
        addClaimIdCookie(claim, response);
        return "index";
    }

    @GetMapping(path = "/accessibility")
    public final String accessibility(final Model model) {
        model.addAttribute("backUrl", "javascript:history.back()");
        return "accessibility";
    }

    /**
     * Shows the Claimant start page.
     *
     * @param claimId Claim Id
     * @param model Spring MVC Model
     * @param response HttpServletResponse
     *
     * @return Name of view
     */
    @GetMapping(path = "/claimant")
    public final String claimant(
            @CookieValue(value = COOKIE_CLAIM_ID, required = false) final String claimId,
            final Model model,
            final HttpServletResponse response
    ) {
        final Claim claim = findClaimOrCreateNew(claimId);
        addClaimIdCookie(claim, response);
        return "claimant";
    }

    @GetMapping("/init-claim")
    public String initClaim(@RequestParam("claimType") final ClaimType claimType,
                            @RequestParam(value = "claimantId", required = false) final UUID claimantId,
                            final HttpServletResponse response) {


        if (ClaimType.EDIT_CLAIM.equals(claimType)) {
            Optional<ClaimDB> claimDB = claimDBRepository.findById(claimantId.toString());
            if (claimDB.isPresent()) {
                Claim claim = new Claim(ClaimType.EDIT_CLAIM);
                claimRepository.save(claim);
                claim.setClaimantId(claimantId.toString());
                restoreClaimService.restore(new RestoreClaim(claimDB.get(), claim));
                claimRepository.save(claim);
                addClaimIdCookie(claim, response);
            }
            return "redirect:/" + SummaryController.IDENTIFIER;
        } else {
            Claim claim = new Claim(ClaimType.NEW_CLAIM);
            addClaimIdCookie(claim, response);
            claimRepository.save(claim);
            return "redirect:/";
        }

    }

    private Claim findClaimOrCreateNew(final String claimId) {
        Claim claim;
        if (StringUtils.isEmpty(claimId)) {
            claim = new Claim();
            claim = claimRepository.save(claim);
        } else {
            claim = claimRepository.findById(claimId).orElse(new Claim());
            if (hasClaimantLastActivityExpired(claim)) {
                claim = new Claim();
                claim = claimRepository.save(claim);
            }
        }
        return claim;
    }

    private boolean hasClaimantLastActivityExpired(final Claim claim) {
        return claim.getClaimantLatestActivity().plusSeconds(sessionTimeout).isBefore(LocalDateTime.now());
    }
}
