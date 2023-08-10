package click.porito.commons.auth2authserver.infra.dev_notifier;

import java.util.logging.Level;

public interface DeveloperNotifier {
    void notify(String message, Level logLevel);
}
