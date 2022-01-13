package uk.gov.dwp.jsa.citizen_ui.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.dwp.jsa.citizen_ui.Constants;
import uk.gov.dwp.jsa.security.KeyLoader;

import java.security.PrivateKey;

@Component
public class PrivateKeyProvider {

    private PrivateKey privateKey;

    public PrivateKeyProvider(@Value("${" + Constants.AGENT_MODE + "}") final boolean pAgentMode,
                              @Value("${citizen-ui.app.key.private:}") final String privateKeyString,
                              final KeyLoader<String> keyLoader) {
        if (!pAgentMode && StringUtils.isNotEmpty(privateKeyString)) {
            this.privateKey = keyLoader.loadPrivateKey(privateKeyString);
        }
    }

    PrivateKey getPrivateKey() {
        return privateKey;
    }
}
