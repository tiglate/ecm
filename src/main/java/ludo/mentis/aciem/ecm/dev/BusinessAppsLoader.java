package ludo.mentis.aciem.ecm.dev;

import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessAppsLoader implements DataLoaderCommand {

    private final BusinessAppRepository repository;

    public BusinessAppsLoader(BusinessAppRepository repository) {
        this.repository = repository;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return "Applications";
    }

    @Override
    public boolean canItRun() {
        return repository.count() == 0;
    }

    @Override
    public int run() {
        var apps = List.of(
            new BusinessApp("CRE01", "Core Banking System"),
            new BusinessApp("CRM02", "Customer Relationship Manager"),
            new BusinessApp("LND03", "Loan Origination Platform"),
            new BusinessApp("TRD04", "Treasury & Trading System"),
            new BusinessApp("FXM05", "Foreign Exchange Management"),
            new BusinessApp("RPT06", "Regulatory Reporting Suite"),
            new BusinessApp("MOB07", "Mobile Banking App"),
            new BusinessApp("IBK08", "Internet Banking Portal"),
            new BusinessApp("KYC09", "Know Your Customer Platform"),
            new BusinessApp("RISK1", "Risk Management & Compliance Tool")
        );
        repository.saveAll(apps);
        return apps.size();
    }
}
