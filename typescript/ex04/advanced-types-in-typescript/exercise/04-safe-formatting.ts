type TODO = never;
const TODO: TODO = "not yet implemented" as never;
// DONT CHANGE THE ABOVE LINES

/* UNSAFE FORMATTING */
// There is no relation between the format string and the arguments.
type UnsafeFormat = TODO;
const unsafeFormat: UnsafeFormat = TODO;
console.log(unsafeFormat("Hello ", "%s", ", your score is ", "%d")("Alice", 20));    // Ok!
console.log(unsafeFormat("Hello ", "%s", ", your score is ", "%d")("Bob", "20"));    // Oops, score should be a number!
console.log(unsafeFormat("Hello ", "%s", ", your score is ", "%d")("Charlie"));      // Oops, missing argument!

/* SAFE FORMATTING */
// There is a relation between the format string and the arguments.
type ExamineFormat<F> = TODO;

type Format = TODO;
const format: Format = TODO;
console.log(format("Hello ", "%s", ", your score is ", "%d")("Alice", 20));     // Ok!
// console.log(format("Hello ", "%s", ", your score is ", "%d")("Bob", "20"));  // Compile-time Error: wrong argument type!
// console.log(format("Hello ", "%s", ", your score is ", "%d")("Charlie"));    // Compile-time Error: not enough arguments!