package click.porito.commons.auth2authserver.common.infra.dev_notifier;

import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SimpleLogNotifier implements DeveloperNotifier{
    private static final Logger logger = Logger.getLogger(SimpleLogNotifier.class.getName());

    @Override
    public void notify(String message, Level logLevel) {
        logger.log(logLevel, message);
    }
}
