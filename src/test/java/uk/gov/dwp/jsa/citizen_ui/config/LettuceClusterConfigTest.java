package uk.gov.dwp.jsa.citizen_ui.config;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class LettuceClusterConfigTest {

    private static final String PASSWORD = "PASSWORD";
    private static final String HOST = "HOST_AND_PORT";
    private static final int PORT = 1234;
    private static final String HOST_AND_PORT = HOST + ":" + PORT;

    private LettuceClusterConfig config;
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Before
    public void beforeEachTest() {
        initMocks(this);
    }


    @Test
    public void setsPassword() {
        givenAConfig();
        whenISetThePassword();
        thenThePasswordIsSet();

    }

    @Test
    public void setsHost() {
        givenAConfig();
        whenISetTheHost();
        thenTheHostIsSet();

    }

    @Test
    public void createsLettuceConnectionFactory() {
        givenAConfig();
        whenISetThePassword();
        whenISetTheHost();
        whenIGetTheLettuceConnectionFactory();
        thenTheLettuceConnectionFactoryIsCreated();
    }

    private void givenAConfig() {
        config = new LettuceClusterConfig();
    }

    private void whenISetThePassword() {
        config.setPassword(PASSWORD);
    }

    private void whenISetTheHost() {
        config.setHost(HOST_AND_PORT);
    }

    private void whenIGetTheLettuceConnectionFactory() {
        lettuceConnectionFactory = config.lettuceConnectionFactory();
    }


    private void thenTheLettuceConnectionFactoryIsCreated() {

        final RedisClusterConfiguration clusterConfiguration = lettuceConnectionFactory.getClusterConfiguration();
        assertThat(clusterConfiguration.getPassword(), is(RedisPassword.of(PASSWORD)));

        final Set<RedisNode> clusterNodes = (Set<RedisNode>) ReflectionTestUtils.getField(clusterConfiguration, "clusterNodes");
        assertTrue(clusterNodes.stream().anyMatch(clusterNode -> clusterNode.getHost().equals(HOST)));
        assertTrue(clusterNodes.stream().anyMatch(clusterNode -> clusterNode.getPort().equals(PORT)));

        LettuceClientConfiguration clientConfig = lettuceConnectionFactory.getClientConfiguration();
        assertThat(clientConfig.isUseSsl(), is(true));
        assertThat(clientConfig.getShutdownTimeout(), is(Duration.ZERO));
    }

    private void thenTheHostIsSet() {
        assertThat(config.getHost(), is(HOST_AND_PORT));
    }

    private void thenThePasswordIsSet() {
        assertThat(config.getPassword(), is(PASSWORD));
    }
}
