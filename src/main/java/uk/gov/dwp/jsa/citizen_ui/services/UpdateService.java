package uk.gov.dwp.jsa.citizen_ui.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.jsa.adaptors.BankDetailsServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.CircumstancesServiceAdaptor;
import uk.gov.dwp.jsa.adaptors.ClaimantServiceAdaptor;
import uk.gov.dwp.jsa.citizen_ui.model.Claim;
import uk.gov.dwp.jsa.citizen_ui.model.ClaimDB;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimDBRepository;
import uk.gov.dwp.jsa.citizen_ui.repository.ClaimRepository;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.BankDetailsService;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.CircumstancesService;
import uk.gov.dwp.jsa.citizen_ui.services.postclaim.ClaimantService;

import javax.management.InstanceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static uk.gov.dwp.jsa.citizen_ui.util.ObjectUtils.resolve;

@Service
public class UpdateService {

    private final ClaimantService claimantService;
    private final ClaimantServiceAdaptor claimantServiceAdaptor;
    private final BankDetailsService bankDetailsService;
    private final BankDetailsServiceAdaptor bankDetailsServiceAdaptor;
    private final CircumstancesService circumstancesService;
    private final CircumstancesServiceAdaptor circumstancesServiceAdaptor;
    private final ClaimDBRepository claimDBRepository;
    private final ClaimRepository claimRepository;

    public UpdateService(final ClaimantService claimantService,
                         final ClaimantServiceAdaptor claimantServiceAdaptor,
                         final BankDetailsService bankDetailsService,
                         final BankDetailsServiceAdaptor bankDetailsServiceAdaptor,
                         final CircumstancesService circumstancesService,
                         final CircumstancesServiceAdaptor circumstancesServiceAdaptor,
                         final ClaimDBRepository claimDBRepository,
                         final ClaimRepository claimRepository) {
        this.claimantService = claimantService;
        this.claimantServiceAdaptor = claimantServiceAdaptor;
        this.bankDetailsService = bankDetailsService;
        this.bankDetailsServiceAdaptor = bankDetailsServiceAdaptor;
        this.circumstancesService = circumstancesService;
        this.circumstancesServiceAdaptor = circumstancesServiceAdaptor;
        this.claimDBRepository = claimDBRepository;
        this.claimRepository = claimRepository;
    }

    public UUID updateClaim(final UUID claimId)
            throws InstanceNotFoundException {
        Claim claim = claimRepository.findById(claimId.toString()).orElseThrow(InstanceNotFoundException::new);
        final UUID claimantId = UUID.fromString(claim.getClaimantId());
        final ClaimDB claimDB = claimDBRepository.findById(claimantId.toString())
                .orElseThrow(InstanceNotFoundException::new);

        List<CompletableFuture> futures = new ArrayList<>();
        claimantService.getDataFromClaim(claimId)
                .ifPresent(c -> futures.add(claimantServiceAdaptor.updateClaimantData(c)));
        circumstancesService.getDataFromClaim(claimId).ifPresent(c ->
                futures.add(circumstancesServiceAdaptor.updateCircumstancesData(c, claimantId,
                        claimDB.getCircumstances().getId())));
        final Optional<UUID> id = resolve(() -> claimDB.getBankDetails().getId());
        futures.add(bankDetailsServiceAdaptor.updateBankDetailsData(bankDetailsService.getDataFromClaim(claimId),
                claimantId, id));
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

        claimRepository.deleteById(claimId.toString());
        return claimantId;
    }
}
