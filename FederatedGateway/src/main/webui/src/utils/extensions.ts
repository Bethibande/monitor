export function fetchAvailableModulePaths(): Promise<string[]> {
    return fetch("/api/v1/extensions/web-modules").then(res => res.json());
}