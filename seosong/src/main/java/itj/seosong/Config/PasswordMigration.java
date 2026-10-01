package itj.seosong.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import itj.seosong.Services.UserService;

/**
 * Na inicialização, converte para BCrypt as senhas que ainda estão em texto puro no banco.
 * Pode rodar várias vezes sem problema: senhas já com hash são ignoradas.
 */
@Component
public class PasswordMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigration.class);

    private final UserService userService;

    public PasswordMigration(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        int count = userService.hashLegacyPasswords();
        if (count > 0) {
            log.info("{} senha(s) em texto puro convertida(s) para BCrypt.", count);
        }
    }
}
