# FederatedGateway
This project implements the external gateway.
It's responsible for
- Exposing internal services to the outside world.
- Access control to exposed services.
- Providing the core of the web application.

#### Conents
- [Routing](#routing)
- [Web Federation](#web-federation)
- [Translations API](#translations-api)

### Routing
By default the gateway serves the cor web app under the route `/`. Other internal services are proxied under `/fed/<extension-name>/*`.
The gateway automatically configures its router based in its built-in CRD. To expose a service as an extension you can simply use the following resource:
```yaml
apiVersion: de.bethibande/v1alpha1
kind: MonitorFederatedExtension
metadata:
  name: metrics
spec:
  endpoints:
    - endpoint: http://backend.metrics.svc.cluster.local/federated/api
      capability: API
      filters:
        - path: "/v1/metrics"
          permission: ext.metrics.read
        - path: "/v1/admin"
          permission: system.admin
    - endpoint: http://backend.metrics.svc.cluster.local/federated/web/assets/siteEntry.js
      capability: WebModule
    - endpoint: http://backend.metrics.svc.cluster.local/federated/translations
      capability: Translations
```
This configuration would generate the following external routes:
- `/fed/metrics/api/*`
  - `/fed/metrics/api/v1/metrics` Will require the permission `ext.metrics.read` to access
  - `/fed/metrics/api/v1/admin` Will require the permission `system.admin` to access
- `/fed/metrics/web/module.js` Will serve the federated web module, loaded by the frontend on runtime. See [here](#web-federation) for more information.
- `/fed/metrics/translations` Will serve an implementation of the built-in translation API. See [here](#translations-api) for more information.

> [!CAUTION]
> Please note that user permissions are not yet implemented.

### Web Federation
The frontend implements an extendable infrastructure using the [vite module federation plugin](https://github.com/originjs/vite-plugin-federation).
The gateways internal API will expose a list of available web modules, this list is read by the frontend on runtime and then loaded.
Each extension must export a default config specifying its id and all of the features it provides and configuration it requires.
That way extensions can configure custom routes in the frontend and add new entries to the nav-bar.
Extensions may also provide components used by other views such as widgets and more.

> [!WARNING]
> This feature is not yet implemented.
> More information on the implementation details will follow later.

### Translations API
The gateway provides a built-in translation API. This API is used by the frontend to load translations.

> [!WARNING]
> This feature is not yet implemented.
> More information on the implementation details will follow later.