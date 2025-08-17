package de.bethibande.k8s.web;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FederatedWebBundleRewrite implements FederatedFileRewrite {

    @Override
    public String getResourcePath() {
        return "web/assets/remoteEntry.js";
    }

    @Override
    public String rewrite(final String contents, final String extensionId) {
        final String path = "/fed/%s/web/assets".formatted(extensionId);

        return contents.replaceAll("__federation_import\\('(/[^/.]+)+\\.js'\\)", "__federation_import('" + path + "$1.js')")
                .replaceAll("const base = '([^']+)';", "const base = '';");
    }

}
