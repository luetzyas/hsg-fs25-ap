/* UNSAFE FORMATTING */
// There is no relation between the format string and the arguments.
type UnsafeFormat = (...format: string[]) => (...args: any[]) => string;
const unsafeFormat: UnsafeFormat = (...format: string[]) => (...args: any[]) => {
    let i = 0;
    let result: string = "";
    for (let j=0; j<format.length; j++) {
        result = result.concat(format[j] == "%s" || format[j] == "%d" ? args[i++] : format[j]);
    }
    return result;
}
console.log(unsafeFormat("Hello ", "%s", ", your score is ", "%d")("Alice", 20));    // Ok!
console.log(unsafeFormat("Hello ", "%s", ", your score is ", "%d")("Bob", "20"));    // Oops, score should be a number!
console.log(unsafeFormat("Hello ", "%s", ", your score is ", "%d")("Charlie"));      // Oops, missing argument!

/* SAFE FORMATTING */
// There is a relation between the format string and the arguments.
type ExamineFormat<F> =
    F extends [ "%d", ...infer Tail ] ? [ number, ...ExamineFormat<Tail> ] :
    F extends [ "%s", ...infer Tail ] ? [ string, ...ExamineFormat<Tail> ] :
    F extends [ string, ...infer Tail ] ? ExamineFormat<Tail> :
    F extends [] ? [] :
    never;

type Format = <F extends string[]> (...format: F) => (...args: ExamineFormat<F>) => string;
const format: Format = <F extends string[]> (...format: F) => (...args: ExamineFormat<F>) => {
    let i = 0;
    let result: string = "";
    for (let j=0; j<format.length; j++) {
        result = result.concat(format[j] == "%s" || format[j] == "%d" ? args[i++] : format[j]);
    }
    return result;
}
console.log(format("Hello ", "%s", ", your score is ", "%d")("Alice", 20));     // Ok!
// console.log(format("Hello ", "%s", ", your score is ", "%d")("Bob", "20"));  // Compile-time Error: wrong argument type!
// console.log(format("Hello ", "%s", ", your score is ", "%d")("Charlie"));    // Compile-time Error: not enough arguments!