// -----------------------------
// 1. Quantities
// -----------------------------
//
/* Task 1: Values, Distances and Times */
type Value<U> = {
    value: number;
    unit: U;
};
type DistanceMeters = Value<'meter'>; //distance is in measured in meters
type TimeSeconds = Value<'second'>; //time is measured in seconds

const d1: DistanceMeters = { value: 10, unit: "meter" };
const t1: TimeSeconds = { value: 20, unit: "second" };

/* Task 2: More on Units */
type DistanceUnits = 'meter'| 'feet' | 'yard';
type TimeUnits = 'minute' | 'second';

type Distance = Value<DistanceUnits>;
type Time = Value<TimeUnits>;

//examples
const dMeters: Distance = { value: 10, unit: "meter" };
const tMin: Time = { value: 2, unit: "minute" };
const tSec: Time = { value: 30, unit: "second" };
// -----------------------------
// 2. Addition of Quantities
// -----------------------------
//
/* Task 3: First Try */
// The type "U" is generic, and ts does not check, if both values have the same concrete unit literal type.
function addT3<U>(
    q0: Value<U>, 
    q1: Value<U>
): Value<U> {
    return { value: q0.value + q1.value, unit: q0.unit };
} 
//examples
const badDistance: Value<"meter"> = { value: 10, unit: "meter" };
const badTime: Value<"second"> = { value: 5, unit: "second" };
const wrong = addT3(badDistance, badTime); // this should be a type error, but is not

/* Task 4: Second Try */
// The problem with addT4 is that U2 is constrained to be a subtype of U1.
function addT4<U1, U2 extends U1>(
    q0: Value<U1>, 
    q1: Value<U2>
) : Value<U1> {
    return { value: q0.value + q1.value, unit: q0.unit };
}
//examples
const dist: Value<"meter"> = { value: 10, unit: "meter" };
const time: Value<"second"> = { value: 5, unit: "second" };
//const wrong2 = addT4(dist, time); // this is now a type error

/* Task 5: Type-Level Equality */
type Same<T1, T2> =
    T1 extends T2 // check if T1 is assignable to T2
        ? T2 extends T1 // check if T2 is assignable to T1
            ? T1 // if both checks passed, T1 and T2 are the same type
            : never
        : never;

/* Task 6: Third time’s the charm */
function add<U1, U2>(
    q0: Value<U1>,
    q1: Value<U2> & Value<Same<U1, U2>> // ensure U2 is exactly the same type as U1
): Value<U1> {
    return { value: q0.value + q1.value, unit: q0.unit };
}

// -----------------------------
// 3. Unit conversion
// -----------------------------
// 
/* Task 7: Convert Units*/
function convertDistance<
    From extends DistanceUnits,
    To extends DistanceUnits
>(
    d: Value<From>,
    targetUnit: To
): Value<To> {

    const toMeters: Record<DistanceUnits, number> = {
        meter: 1,
        feet: 0.3048,
        yard: 0.9144
    };

    const valueInMeters = d.value * toMeters[d.unit];
    const targetValue = valueInMeters / toMeters[targetUnit];

    return { value: targetValue, unit: targetUnit };
}


function convertTime<To extends TimeUnits>(
    t: Time,
    targetUnit: To
): Value<To> {
    const toSeconds: Record<TimeUnits, number> = {
        minute: 60,
        second: 1
    };

    const valueInSeconds = t.value * toSeconds[t.unit];
    const targetValue = valueInSeconds / toSeconds[targetUnit];

    return { value: targetValue, unit: targetUnit };
}

//DEMO Task 7
// Input distances
const tenMeters: Value<"meter"> = { value: 10, unit: "meter" };
const twentyYards: Value<"meter"> = { value: 20, unit: "meter" };

const twentyYardsInMeters: Value<"meter"> = convertDistance(twentyYards, "meter");
const sumInMeters = add(tenMeters, twentyYardsInMeters);
const sumInFeet = convertDistance(sumInMeters, "feet");

// -----------------------------
// 4. Product of Quantities
// -----------------------------
//
/* Task 8: Product of Units */
type Prod<U1, U2> = U1 extends string
    ? U2 extends string
        ? `${U1}*${U2}`
        : never
    : never;

/* Task 9: Product of Quantities */
function mult<U1, U2>(
    q1: Value<U1>,
    q2: Value<U2>
): Value<Prod<U1, U2>> {
    return {
        value: q1.value * q2.value,
        unit: `${q1.unit}*${q2.unit}` as Prod<U1, U2>
    };
}
//example
const speed = mult(
    { value: 10, unit: "meter" },
    { value: 3, unit: "second" }
);

// -----------------------------
// 5. Adapting to New Requirements
// -----------------------------
//
/* Task 10: Restricting to metric */
// List of units NASA still allows
type MetricAllowed = "millimeter" | "centimeter" | "meter" | "kilometer";
type NewDistanceUnits = DistanceUnits & MetricAllowed;
