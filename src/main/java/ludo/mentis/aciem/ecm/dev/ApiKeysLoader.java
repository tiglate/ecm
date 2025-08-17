package ludo.mentis.aciem.ecm.dev;

import ludo.mentis.aciem.ecm.domain.ApiKey;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.ApiKeyRepository;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.service.PasswordService;
import ludo.mentis.aciem.ecm.util.ApiKeyUtils;
import ludo.mentis.aciem.ecm.util.RandomUtils;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;


@Component
public class ApiKeysLoader implements DataLoaderCommand {

    private final ApiKeyRepository apiKeyRepository;
    private final BusinessAppRepository applicationRepository;
    private final RandomUtils randomUtils;
    private final PasswordService passwordService;
    private final ApiKeyUtils apiKeyUtils;

    protected static final String[] SCI_FI_SERVER_NAMES = {
            "DEV_VULCAN_CORE",
            "QA_DEATH_STAR",
            "UAT_TARDIS_NODE",
            "PROD_MILLENNIUM_FALCON",
            "BUAT_BORG_CUBE",
            "QA_SERENITY_ENGINE",
            "UAT_ENTERPRISE_DOCK",
            "PROD_MARS_COLONY",
            "DEV_SKYNET_HUB",
            "QA_RAZORCREST_CACHE",
            "UAT_JUPITER_STATION",
            "PROD_HYPERDRIVE_ARRAY",
            "DEV_XENON_GATEWAY",
            "QA_ANDROMEDA_LOOP",
            "UAT_OBLIVION_CLUSTER",
            "PROD_REPLICANT_GRID",
            "BUAT_NEXUS_PROTOCOL",
            "QA_CYLON_NODE",
            "UAT_TRANTOR_ARCHIVE",
            "PROD_ZORG_SERVER",
            "DEV_QUANTUM_REALM",
            "QA_KLINGON_FIREWALL",
            "UAT_VOYAGER_STACK",
            "PROD_GALACTICA_CORE",
            "DEV_PLASMA_ROUTER",
            "QA_NEBULA_CACHE",
            "BUAT_SPOCK_LOGIC",
            "PROD_ASTEROID_DB",
            "DEV_WARP_DRIVE",
            "QA_ALDERAAN_BACKUP"
    };

    public ApiKeysLoader(final ApiKeyRepository apiKeyRepository,
                         final BusinessAppRepository applicationRepository,
                         final RandomUtils randomUtils,
                         final PasswordService passwordService,
                         final ApiKeyUtils apiKeyUtils) {
        this.apiKeyRepository = apiKeyRepository;
        this.applicationRepository = applicationRepository;
        this.randomUtils = randomUtils;
        this.passwordService = passwordService;
        this.apiKeyUtils = apiKeyUtils;
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public String getName() {
        return "API Keys";
    }

    @Override
    public boolean canItRun() {
        return apiKeyRepository.count() == 0;
    }

    @Override
    public int run() {
        var created = 0;
        var faker = new Faker();

        var apps = this.applicationRepository.findAll();
        if (apps == null || apps.isEmpty()) {
            return 0;
        }

        // Iterate deterministically over all app/environment pairs to avoid duplicates
        for (var app : apps) {
            if (created >= 30) {
                break;
            }
            for (var environment : Environment.values()) {
                if (created >= 30) {
                    break;
                }
                // Skip if a record already exists for this (application, environment)
                if (apiKeyRepository.existsByApplicationIdAndEnvironmentId(app.getId(), environment.getId())) {
                    continue;
                }

                var apiKey = new ApiKey();
                apiKey.setApplication(app);

                var secret = apiKeyUtils.generateApiKey();
                if (secret == null) {
                    // In tests, ApiKeyUtils may be a mock without stubbing; avoid passing null to password service
                    secret = "";
                }
                var envelop = passwordService.encryptPasswordToEntity(secret);
                apiKey.setCipherEnvelope(envelop);

                apiKey.setClientId(faker.internet().uuid());
                apiKey.setEnvironment(environment);
                apiKey.setServer(getRandomServerName(environment));

                apiKey.setUpdatedBy(faker.name().firstName());

                apiKeyRepository.save(apiKey);
                created++;
            }
        }

        return created;
    }

    protected String getRandomServerName(Environment environment) {
        if (environment == null) {
            return null;
        }
        var matchingServers = new java.util.ArrayList<String>();
        var prefix = environment.name() + "_";
        for (var server : SCI_FI_SERVER_NAMES) {
            if (server.startsWith(prefix)) {
                matchingServers.add(server);
            }
        }
        if (matchingServers.isEmpty()) {
            return null;
        }
        return randomUtils.pickRandomBoolean()
                ? matchingServers.get(randomUtils.getRandomNumberInRange(0, matchingServers.size() - 1))
                : null;
    }
}
