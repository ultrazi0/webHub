/**
 * Return all paths of a given object
 * @example
 * ```TS
 * type TestType = {
 *     A: {
 *         B: {
 *             C: string;
 *         },
 *         D: number;
 *     },
 *     E: string;
 *     ARRAY: Array<{
 *         A: string;
 *         B: number;
 *     }>
 * }
 * // "A" | "A.B" | "A.B.C" | "A.E" | "D" | "ARRAY" | `ARRAY.${number}` | `ARRAY.${number}.A` | `ARRAY.${number}.B`
 * type TestResult = FieldPaths<TestType>;
 * ```
 */
export type FieldPaths<T> = T extends (infer U)[] ? (
    `${number}` | `${number}.${FieldPaths<U>}`
) : T extends object ? {
        [K in keyof T & (string | number)]: `${K}` | `${K}.${FieldPaths<T[K]>}`
}[keyof T & (string | number)] : never;

export const setError = <T,>(errors: T, path: FieldPaths<T>, message: string) => {
    const splitPath = path.split(".");

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    let pointer: any = errors;
    for (let i = 0; i < splitPath.length - 1; i++) {
        const key = splitPath[i];
        if (!pointer[key]) {
            pointer[key] = {};
        }
        pointer = pointer[key];
    }

    pointer[splitPath[splitPath.length - 1]] = message;

    return errors;

};
