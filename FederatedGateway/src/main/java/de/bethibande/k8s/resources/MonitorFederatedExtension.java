package de.bethibande.k8s.resources;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;
import io.quarkus.kubernetes.client.KubernetesResources;

@ShortNames("mfe")
@Version("v1alpha1")
@Group("de.bethibande")
@Kind("MonitorFederatedExtension")
@KubernetesResources
public class MonitorFederatedExtension extends CustomResource<MonitorFederatedExtensionSpec, MonitorFederatedExtensionStatus> {



}
