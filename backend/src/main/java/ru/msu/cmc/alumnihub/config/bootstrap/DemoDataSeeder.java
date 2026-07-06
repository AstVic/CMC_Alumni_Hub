package ru.msu.cmc.alumnihub.config.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import ru.msu.cmc.alumnihub.profile.repository.AlumniProfileRepository;

import javax.sql.DataSource;

/**
 * Loads the demo dataset (alumni, cards, invites, questions) on startup — only
 * when {@code app.seed-demo=true} and the database has no profiles yet. Runs
 * after {@link AdminBootstrapRunner} so demo invitations can reference the admin.
 *
 * <p>Never active in production (the property defaults to false there).
 */
@Component
@Order(2)
@ConditionalOnProperty(name = "app.seed-demo", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final DataSource dataSource;
    private final AlumniProfileRepository profileRepository;

    public DemoDataSeeder(DataSource dataSource, AlumniProfileRepository profileRepository) {
        this.dataSource = dataSource;
        this.profileRepository = profileRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (profileRepository.count() > 0) {
            log.info("Demo seed skipped: data already present.");
            return;
        }
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/demo/demo_data.sql"));
        populator.setSeparator(";");
        populator.execute(dataSource);
        log.info("Demo dataset loaded (app.seed-demo=true).");
    }
}
