export function capitalize(str: string) {
    return str.toLowerCase().replace(/^\w/, c => c.toUpperCase());
}