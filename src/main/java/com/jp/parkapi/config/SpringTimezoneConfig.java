package com.jp.parkapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class SpringTimezoneConfig {

    @PostConstruct() // Após a classe ser inicializada pelo Spring, (como a classe está usando a annotation do Spring será instanciada (new Constructor) automáticamente)
                     // , o método abaixo será chamado pelo construtor da classe
    public void timezoneConfig(){
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }
}
