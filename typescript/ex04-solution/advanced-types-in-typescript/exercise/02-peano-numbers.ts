import { Assert, Not, TypeEqual } from "./01-advanced-types";
export { Peano, Zero, Succ, ToNumber, ToPeano, Add, Fibonacci, LessThan };
type TODO = never;
const TODO: TODO = "not yet implemented" as never;
// DONT CHANGE THE ABOVE LINES

type Concat<A extends any[], B extends any[]> = [...A, ...B];

/* PEANO NUMBERS AS TYPES */
type Peano = TODO;  // A Peano number is the sum of a sequence of 1s.         
type Zero = TODO;
type Succ<N extends Peano> = TODO;

type One = Succ<Zero>; 
type Two = Succ<One>; 
type __check_peano__ = Assert<[
    TypeEqual<One, [1]>,
    TypeEqual<Two, Succ<Succ<Zero>>>,
]>;

/* NUMBER TO PEANO NUMBER */
type ToNumber<N extends Peano> = TODO;

type __check_to_number__ = Assert<[
    TypeEqual<ToNumber<Zero>, 0>,
    TypeEqual<ToNumber<One>, 1>,
    TypeEqual<ToNumber<Two>, 2>,
    Not<TypeEqual<ToNumber<Two>, 3>>,
]>;

/* PEANO NUMBER TO NUMBER */
type ToPeano<I extends number, Result extends Peano = Zero> = TODO;

type __check_to_peano__ = Assert<[
    TypeEqual<ToPeano<0>, Zero>,
    TypeEqual<ToPeano<1>, One>,
    TypeEqual<ToPeano<2>, Two>,
    TypeEqual<ToPeano<10>, [1,1,1,1,1,1,1,1,1,1]>,
    Not<TypeEqual<ToPeano<2>, One>>,
]>; 

/* ADDITION OF PEANO NUMBERS */
type Add<X extends Peano, Y extends Peano> = TODO;

type __check_add__ = Assert<[
    TypeEqual<ToNumber<Add<Zero, Zero>>, 0>,
    TypeEqual<ToNumber<Add<Zero, One>>, 1>,
    TypeEqual<ToNumber<Add<One, Zero>>, 1>,
    TypeEqual<ToNumber<Add<One, One>>, 2>,
    TypeEqual<ToNumber<Add<One, Two>>, 3>,
    TypeEqual<ToNumber<Add<Two, Two>>, 4>,
    Not<TypeEqual<ToNumber<Add<Two, Two>>, 5>>,
]>;

/* ORDERING OF PEANO NUMBERS */
type LessThan<X extends Peano, Y extends Peano> =
    X extends Zero
        ? Y extends Zero
            ? false // 0 < 0
            : true  // 0 < Y
        : X extends Succ<infer PX extends Peano>
            ? Y extends Zero
                ? false // X < 0
                : Y extends Succ<infer PY extends Peano>
                    ? LessThan<PX, PY> // X-1 < Y-1
                    : never
            : never;
            
type __check_less_than__ = Assert<[
    Not<LessThan<Zero, Zero>>,
    LessThan<Zero, One>,
    Not<LessThan<One, Zero>>,
    Not<LessThan<One, One>>,
    LessThan<One, Two>,
    Not<LessThan<Two, One>>,
    Not<LessThan<Two, Two>>,
]>;

/* FIBONACCI OF PEANO NUMBERS */
type Fibonacci<N extends Peano> = TODO;

type __check_fibonacci__ = Assert<[
    TypeEqual<ToNumber<Fibonacci<ToPeano<0>>>, 0>,
    TypeEqual<ToNumber<Fibonacci<ToPeano<1>>>, 1>,
    TypeEqual<ToNumber<Fibonacci<ToPeano<2>>>, 1>,
    TypeEqual<ToNumber<Fibonacci<ToPeano<3>>>, 2>,
    TypeEqual<ToNumber<Fibonacci<ToPeano<5>>>, 5>,
    TypeEqual<ToNumber<Fibonacci<ToPeano<8>>>, 21>,
]>;