package de.bethibande.k8s.web;

public interface FederatedFileRewrite {

    String getResourcePath();

    String rewrite(final String content, final String extensionId);

}
