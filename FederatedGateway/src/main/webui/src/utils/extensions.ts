import {ApiClientBuilder} from "@monitor/shared-library";

const client = new ApiClientBuilder()
    .withBaseUrl("/api/v1/extensions")
    .build()

export function fetchAvailableModulePaths(): Promise<string[]> {
    return client.fetch("/web-modules");
}