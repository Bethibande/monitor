package de.bethibande.k8s.translations;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Path("/federated/translations")
public class TranslationsEndpoint {

    private final TranslationsService translationsService;

    @Inject
    public TranslationsEndpoint(final TranslationsService translationsService) {
        this.translationsService = translationsService;
    }

    @GET
    public Map<String, Object> get(final @QueryParam("locale") String locale) {
        final Map<String, Object> bundle = translationsService.getTranslations(Locale.forLanguageTag(locale));
        if (bundle == null) throw new NotFoundException("No translations found for locale " + locale);

        return bundle;
    }

}
