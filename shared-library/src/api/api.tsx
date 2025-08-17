import {ExtensionConfig} from "../index";

interface ApiClientConfig {
    baseUrl?: string;
    extension?: ExtensionConfig;
}

interface ApiClientOptions {
    method?: string;
    headers?: Record<string, string>;
    body?: any;
}

export function buildClient(): ApiClientBuilder {
    return new ApiClientBuilder();
}

export class ApiClientBuilder {
    private config: ApiClientConfig = {};

    withBaseUrl(url: string): ApiClientBuilder {
        this.config.baseUrl = url;
        return this;
    }

    forExtension(extension: ExtensionConfig): ApiClientBuilder {
        this.config.extension = extension;
        return this;
    }

    build(): ApiClient {
        return new ApiClient(this.config);
    }
}

export class ApiClient {
    private readonly config: ApiClientConfig;

    constructor(config: ApiClientConfig) {
        this.config = config;
    }

    private resolveUrl(path: string): string {
        if (this.config.extension) {
            return `/fed/${this.config.extension.name}/api${path}`;
        }
        return `${this.config.baseUrl || ""}${path}`;
    }

    async fetch<T>(path: string, options: ApiClientOptions = {}): Promise<T> {
        const response = await fetch(this.resolveUrl(path), {
            method: options.method || "GET",
            headers: {
                "Content-Type": "application/json",
                ...options.headers
            },
            body: options.body ? JSON.stringify(options.body) : undefined,
            credentials: "include" // This ensures cookies are sent with requests
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return response.json();
    }
}
