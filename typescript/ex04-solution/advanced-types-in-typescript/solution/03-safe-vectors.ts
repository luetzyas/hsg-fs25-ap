import { Zero, Succ, Peano, ToPeano, LessThan } from "./02-peano-numbers";
import { TypeEqual } from "./01-advanced-types";
export { Phantom, SafeVectorAlternative as SafeVector };

/* NAIVE SAFE VECTORS */
// Let's first try a naive implementation. This does not work because 
// UnsafeVector<T,X> and UnsafeVector<T,Y> both resolve to the same type 
// { readonly content: T[]; }, for all X and Y.
type UnsafeVector<T, N extends Peano> = { 
    readonly content: T[]; 
};

type __check_unsafe_vector__ = TypeEqual<  // Broken: this must not be true!!!
    UnsafeVector<number, Zero>, 
    UnsafeVector<number, Succ<Zero>>
>; 

/* PHATHOM TYPES */
// We need to keep track of the different X and Y. To do that, we can add a new
// field, which is only needed at compile-tipe to keep track of N. So, we need a
// type that 
// (a) depends on N (to track it)
// (b) is inaccessible at runtime
//
// The type `(never => N)` is perfect for that: it depends on N, but at runtime
// it is just a function that can never be called, and trying to call it will
// fail type-checking (at compile-time). We call this a "phantom type", as 
// normally they are erased at runtime.
type Phantom<X> = (_: never) => X;
function phantom<X>(): Phantom<X> { return _ => { throw new Error("Attempted access to a Phantom Type."); } }

const phantom_test = phantom<Number>(); // How can we call this function?
// phantom_test(123);                   // This fails type-checking!
// phantom_test(undefined);             // Even this fails at type-checking!
// phantom_test(123 as never);          // Oops, you can still do some black magic...

/* SAFE VECTORS */
type SafeVector<T, N extends Peano> = { 
    readonly content: T[]; 
    readonly size: Phantom<N>;
};

type __check_safe_vector__ = TypeEqual<  // Works: they are different!
    SafeVector<number, Zero>, 
    SafeVector<number, Succ<Zero>>
>;

/* SAFE VECTOR OPERATIONS */
function empty<T>(): SafeVector<T, Zero> {
    return { 
        content: [], 
        size: phantom<Zero>() 
    } 
};
function push<T, N extends Peano>(vec: SafeVector<T, N>, top: T): SafeVector<T, Succ<N>> {
    return { 
        content: vec.content.concat([top]), 
        size: phantom<Succ<N>>() 
    };
}
function pop<T, N extends Peano>(vec: SafeVector<T, Succ<N>>): [T, SafeVector<T, N>] {
    const top: T = vec.content[vec.content.length - 1];
    const rest: SafeVector<T, N> = { 
        content: vec.content.slice(0, vec.content.length - 1),
        size: phantom<N>()
    };
    return [top, rest];
}
function get<T, N extends Peano, I extends number>(
    vec: SafeVector<T, N>, 
    index: LessThan<ToPeano<I>, N> extends true ? I : never,
): T {
    return vec.content[vec.content.length - 1 - index];
}

const s0 = empty<number>();
const s1 = push(s0, 10);
const s2 = push(s1, 20);
const [top1, s3] = pop(s2);
const [top2, s4] = pop(s3);
// const cantPop = pop(s4);               // Compile-time error: pop cannot be applied to an empty vector. Hurray!
const _20 = get(s2, 1);
// const cantGetOutOfBound = get(s2, 2);  // Compile-time error: index 2 is out of bounds. Hurray!

/* SAFE VECTORS AS A CLASS */
// Alternatively, we can use a class and information hiding with visibility 
// modifiers.
class SafeVectorAlternative<T, N extends Peano> {
    private readonly content: T[]; 
    private readonly phantom?: N = undefined;
    private constructor(content: T[]) { this.content = content; }

    static empty<T>(): SafeVectorAlternative<T, Zero> {
        return new SafeVectorAlternative<T, Zero>([]);
    }
    push(top: T): SafeVectorAlternative<T, Succ<N>> {
        return new SafeVectorAlternative<T, Succ<N>>(this.content.concat([top]));
    }
    pop<P extends Peano>(this: SafeVectorAlternative<T, Succ<P>>): [T, SafeVectorAlternative<T, P>] {
        const content: T[] = this.content;
        const top: T = content[content.length - 1];
        const rest: SafeVectorAlternative<T, P> = new SafeVectorAlternative<T, P>(content.slice(0, content.length - 1));
        return [top, rest];   
    }
    get<I extends number>(index: LessThan<ToPeano<I>, N> extends true ? I : never): T {
        return this.content[this.content.length - 1 - index];
    }
}

type __check_safe_vector_alternative__ = TypeEqual<  // Works: they are different!
    SafeVectorAlternative<number, Zero>, 
    SafeVectorAlternative<number, Succ<Zero>>
>;

const vec = SafeVectorAlternative.empty<number>().push(10).push(20);
const emptyVec = vec.pop().pop();
// const cantPop = emptyStack.pop();      // Compile-time error: pop cannot be applied to an empty vector. Hurray!
const _10 = vec.get(0);
// const cantGetOutOfBound = vec.get(2);  // Compile-time error: index 2 is out of bounds. Hurray!
