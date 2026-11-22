package br.uniube.academia;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AcademiaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademiaApiApplication.class, args);
    }

    /**
     * Relogio da aplicacao.
     *
     * <p>Em producao e o relogio do sistema; no teste, um Clock fixo torna os cenarios de
     * fronteira de idade deterministicos.
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
