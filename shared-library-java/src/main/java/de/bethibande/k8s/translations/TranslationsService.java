package de.bethibande.k8s.translations;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TranslationsService {

    private final String fallbackLocale;
    private final Map<Locale, Map<String, Object>> translations = new ConcurrentHashMap<>();

    public TranslationsService(final @ConfigProperty(name = "i18n.fallbackLocale", defaultValue = "en_US") String fallbackLocale) {
        this.fallbackLocale = fallbackLocale;
    }

    private Map<String, Object> toMap(final ResourceBundle bundle) {
        final Map<String, Object> map = new HashMap<>();
        for (final String key : bundle.keySet()) {
            map.put(key, bundle.getObject(key));
        }
        return map;
    }

    private Optional<Map<String, Object>> load(final Locale locale) {
        final ResourceBundle bundle = ResourceBundle.getBundle("translations.translations", locale, Thread.currentThread().getContextClassLoader());
        if (bundle == null) return Optional.empty();

        return Optional.of(toMap(bundle));
    }

    public void injectBundle(final Locale locale, final Map<String, Object> bundle) {
        translations.put(locale, bundle);
    }

    public void injectBundle(final Locale locale, final ResourceBundle bundle) {
        injectBundle(locale, toMap(bundle));
    }

    public Map<String, Object> getTranslations(final Locale locale) {
        final Map<String, Object> bundle = translations.get(locale);
        if (bundle != null) return bundle;

        final Optional<Map<String, Object>> optBundle = this.load(locale);
        if (optBundle.isPresent()) {
            translations.put(locale, optBundle.get());
            return optBundle.get();
        }

        return getTranslations(Locale.forLanguageTag(fallbackLocale));
    }

    public Map<Locale, Map<String, Object>> getTranslations() {
        return translations;
    }
}
