package ludo.mentis.aciem.ecm.dev;

import ludo.mentis.aciem.ecm.domain.Credential;
import ludo.mentis.aciem.ecm.model.CredentialType;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import ludo.mentis.aciem.ecm.service.PasswordService;
import ludo.mentis.aciem.ecm.util.RandomUtils;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Component
public class CredentialsLoader implements DataLoaderCommand {

    private final CredentialRepository credentialRepository;
    private final BusinessAppRepository applicationRepository;
    private final RandomUtils randomUtils;
    private final PasswordService passwordService;

    public CredentialsLoader(final CredentialRepository credentialRepository,
                             final BusinessAppRepository applicationRepository,
                             final RandomUtils randomUtils, PasswordService passwordService) {
        this.credentialRepository = credentialRepository;
        this.applicationRepository = applicationRepository;
        this.randomUtils = randomUtils;
        this.passwordService = passwordService;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public String getName() {
        return "Credentials";
    }

    @Override
    public boolean canItRun() {
        return credentialRepository.count() == 0;
    }

    @Override
    public int run() {
        var count = 0;
        var faker = new Faker();

        var apps = this.applicationRepository.findAll();

        for (;count < 30; count++) {
            var credential = new Credential();

            var appPosition = faker.random().nextInt(0, apps.size() - 1);
            credential.setApplication(apps.get(appPosition));

            var password = faker.internet().password();
            var envelop = passwordService.encryptPasswordToEntity(password);
            credential.setCipherEnvelope(envelop);

            credential.setEnvironment(randomUtils.pickRandomEnumValue(Environment.class));
            credential.setCredentialType(randomUtils.pickRandomEnumValue(CredentialType.class));
            credential.setEnabled(randomUtils.pickRandomBoolean());
            credential.setUsername(faker.internet().username());
            credential.setUrl(faker.internet().url());
            credential.setNotes(faker.lorem().paragraph());
            credential.setCreatedBy(faker.name().firstName());
            credential.setVersion(1);

            credentialRepository.save(credential);
        }

        return count;
    }
}
