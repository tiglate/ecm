package ludo.mentis.aciem.ecm.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DevAppRunner implements ApplicationRunner {

    private final Environment environment;
    private final List<DataLoaderCommand> dataLoaders;
    private static final Logger log = LoggerFactory.getLogger(DevAppRunner.class);

    public DevAppRunner(List<DataLoaderCommand> dataLoaders, Environment environment) {
        this.dataLoaders = dataLoaders;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!canItRun()) {
            log.info("Initial data loading is disabled for non-development environments.");
            return;
        }

        log.info("Development environment detected. Starting data loading.");

        dataLoaders.sort(Comparator.comparingInt(DataLoaderCommand::getOrder));

        for (var cmd : dataLoaders) {
            if (!cmd.canItRun()) {
                log.info("DATA LOADER <{}> - SKIPPED", cmd.getName());
                continue;
            }
            log.info("DATA LOADER <{}> - START", cmd.getName());

            var startTime = System.nanoTime();
            var count = cmd.run();
            var endTime = System.nanoTime();

            var durationInNanos = endTime - startTime;
            var durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNanos);

            log.info("DATA LOADER <{}> - COMPLETED. AFFECTED ROWS: {}. It took {}ms to run.",
                    cmd.getName(), count, durationInMillis);
        }

        log.info("Initial data loading completed.");
    }

    protected boolean canItRun() {
        var activeProfiles = environment.getActiveProfiles();

        // If no profiles are active, default to true.
        if (activeProfiles == null || activeProfiles.length == 0) {
            return true;
        }

        // 2. Create a list of lower-case profiles to perform case-insensitive checks.
        var profiles = Arrays.stream(activeProfiles)
                .map(String::toLowerCase)
                .toList();

        // 3. Check that NO production-like profiles are present.
        var hasNoBlockingProfiles = profiles.stream()
                .noneMatch(p -> p.contains("prod") || p.contains("uat") || p.contains("qa"));

        // 4. Check that AT LEAST ONE development-like profile is present.
        var hasAllowedProfile = profiles.stream()
                .anyMatch(p -> p.contains("dev") || p.equals("default") || p.equals("local"));

        return hasNoBlockingProfiles && hasAllowedProfile;
    }
}
