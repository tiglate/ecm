package ludo.mentis.aciem.ecm.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevAppRunnerTest {

    @Mock
    private Environment environment;

    @Mock
    private ApplicationArguments args;

    @Mock
    private DataLoaderCommand cmd1;

    @Mock
    private DataLoaderCommand cmd2;

    private List<DataLoaderCommand> loaders;

    @BeforeEach
    void setUp() {
        loaders = new ArrayList<>();
    }

    @Test
    @DisplayName("canItRun() returns true when no profiles are active")
    void canItRun_noProfiles_true() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[]{});
        var runner = new DevAppRunner(List.of(), environment);
        assertTrue(runner.canItRun());
    }

    @Test
    @DisplayName("canItRun() returns true for dev/local/default and no blocking profiles")
    void canItRun_devLocalDefault_true() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        assertTrue(new DevAppRunner(List.of(), environment).canItRun());

        when(environment.getActiveProfiles()).thenReturn(new String[]{"LOCAL"});
        assertTrue(new DevAppRunner(List.of(), environment).canItRun());

        when(environment.getActiveProfiles()).thenReturn(new String[]{"default"});
        assertTrue(new DevAppRunner(List.of(), environment).canItRun());
    }

    @Test
    @DisplayName("canItRun() returns false if any blocking profile (prod/uat/qa) is present")
    void canItRun_blockingProfiles_false() {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        assertFalse(new DevAppRunner(List.of(), environment).canItRun());

        when(environment.getActiveProfiles()).thenReturn(new String[]{"Qa"});
        assertFalse(new DevAppRunner(List.of(), environment).canItRun());

        when(environment.getActiveProfiles()).thenReturn(new String[]{"uat", "dev"});
        assertFalse(new DevAppRunner(List.of(), environment).canItRun());
    }

    @Test
    @DisplayName("run() sorts loaders by order, skips when canItRun=false, and invokes run() only on allowed ones")
    void run_sortsAndExecutesAllowedOnly() throws Exception {
        // Arrange environment as dev so runner executes
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        // Provide two loaders out of order to verify sorting
        when(cmd1.getOrder()).thenReturn(1);
        when(cmd1.getName()).thenReturn("CMD1");
        when(cmd1.canItRun()).thenReturn(true);
        when(cmd1.run()).thenReturn(5);

        when(cmd2.getOrder()).thenReturn(2);
        when(cmd2.getName()).thenReturn("CMD2");
        when(cmd2.canItRun()).thenReturn(false);

        loaders.add(cmd2);
        loaders.add(cmd1);

        var runner = new DevAppRunner(loaders, environment);

        // Act
        runner.run(args);

        // Assert: executed in order of getOrder, run only for cmd1
        InOrder inOrder = inOrder(cmd1, cmd2);
        inOrder.verify(cmd1).canItRun();
        inOrder.verify(cmd1).run();
        inOrder.verify(cmd2).canItRun();
        verify(cmd2, never()).run();
    }

    @Test
    @DisplayName("run() returns immediately and does not touch loaders when not allowed to run")
    void run_notAllowed_returnsEarly() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});

        loaders.add(cmd1);
        loaders.add(cmd2);

        var runner = new DevAppRunner(loaders, environment);
        runner.run(args);

        // No interactions with commands
        verifyNoInteractions(cmd1, cmd2);
    }
}
