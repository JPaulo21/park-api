package com.jp.parkapi.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageLocale {

    private final MessageSource messageSource;
    private final Locale locale;

    public String i18n(String key){
        return messageSource.getMessage(key, null, locale);
    }

    public String i18n(String key, String... args){
        return messageSource.getMessage(key, args, locale);
    }
}
