export { SubType, TypeEqual, Not, Assert };
type TODO = never;
const TODO: TODO = "not yet implemented" as never;
// DONT CHANGE THE ABOVE LINES

/* GENERICS (TYPE LAMBDAS) */
type Vector<A> = A[]; // all elements have the same type A 
type NumberVector = Vector<number>;

type Pair <A,B> = [A, B]; // a tuple with two elements of type A and B
type Point2D = Pair<number, number>;

/* STRUCTURAL TYPES (PATH-DEPENDENT TYPES) */
type Employee = { name: string, age: number, salary: number };
type EmployeeName = Employee["name"];
const alice: Employee = { name: "Alice", age: 30, salary: 2000 };

type Customer = { name: string, age: number, balance: number };
type CustomerBalance = Customer["balance"]; // get the type of the "balance" property
const bob: Customer = { name: "Bob", age: 40, balance: 1000 }; // create customer object "bob"

// get the age of the first employee in a vector of employees
type FirstEmployeeAge = Vector<Employee>[0]["age"]; 
// get the age of any employee in a vector of employees
type EmployeeAge = Vector<Employee>[number]["age"]; 
const test: EmployeeAge = 45; // OK
// get the salary of any employee in a vector of employees contained in a tuple
type EmployeeSalary = [String, Vector<Employee>][1][number]["salary"]; 

/* KEYOF TYPE OPERATOR */
type EmployeeKey = keyof Employee;                               // "name" | "age" | "salary" 
type EmployeeKey2 = "name" | "age" | "salary";                   // "name" | "age" | "salary"      
const employeeKey: EmployeeKey = "age";                          // OK

type CustomerKey = keyof Customer;                               // "name" | "age" | "balance"
const customerKey: CustomerKey = "balance";                      // OK
// const wrongEmployeeKey: CustomerKey = "salary";               // ERROR

/* UNION TYPES */
type Boolean = true | false;
type EmployeeOrCustomer = Employee | Customer;       
const charlie: EmployeeOrCustomer = { name: "Charlie", age: 50, balance: 500 };

type EmployeeOrCustomerKey = keyof Employee | keyof Customer;  // "name" | "age" | "salary" | "balance"                        // OK
const charly: EmployeeOrCustomerKey = "salary";                      // OK
/* INTERSECTION TYPES */
type Never = true & false; // impossible type, has no values
//const neverValue: Never = true; // ERROR
type EmployeeAndCustomer = Employee & Customer;      
const dave: EmployeeAndCustomer = { name: "Dave", age: 60, salary: 3000, balance: 1500 };
function foo(arg: EmployeeAndCustomer): string {
    return `Name: ${arg.name}, Age: ${arg.age}, Salary: ${arg.salary}, Balance: ${arg.balance}`;
}
type EmployeeAndCustomerKey = keyof Employee & keyof Customer; // "name" | "age"

/* CONDITIONAL TYPES */ //IMPORTANT
type SubType<A, B> = A extends B ? true : false;
type __check_subtype_1__ = SubType<Employee, { name: string }>;
type __check_subtype_2__ = SubType<Employee, { name: string, balance: number }>; 

type HasKey<K, T> = SubType<K, keyof T>; // checks if K is a subtype of the keys of T

type __check_has_key_1__ = HasKey<"name", Employee>; //true
type __check_has_key_2__ = HasKey<"balance", Employee>; //false
type __check_has_key_3__ = HasKey<"name" | "balance", Employee>; //false

// checks if two types are equal 
type TypeEqual<A, B> = A extends B ? (B extends A ? true : false) : false;

type __check_type_equal_1__ = TypeEqual<Employee, Employee>; //tue
type __check_type_equal_2__ = TypeEqual<Employee, Customer>; //false

/* TYPE TESTS/GUARDS */
type Not<A extends boolean> = A extends true ? false : true;

type __check_not_1__ = Not<true>; // false
type __check_not_2__ = Not<false>; // true
// type __check_not_3__ = Not<"foo">; // ERROR

type Assert<T extends true[]> = T;
type __check_assert__ = Assert<[
    SubType<Employee, { name: string }>, // true: Employee has at least the "name" property
    Not<SubType<Employee, { name: string, balance: number }>>, // true: Employee does not have the "balance" property
    TypeEqual<Employee, Employee>, // true: Employee is equal to Employee
    Not<TypeEqual<Employee, Customer>>, // true: Employee is not equal to Customer
    HasKey<"name", Employee>, // true: name is a key of Employee
    Not<HasKey<"balance", Employee>>, // true: balance is not a key of Employee
]>;

/* MAPPED TYPES */
type IntersectionNumber<A, B> = {[K in keyof A & keyof B]: number};
type __check_intersection_number__ = IntersectionNumber<Employee, Customer>;

// A mapped type that constructs the intersection of two types A and B,
// keeping only the properties that have the same type in both A and B
type Intersection<A, B> = {[K in keyof A & keyof B as A[K] extends B[K] ? K : never]: A[K]};
type __check_intersection__ = Intersection<Employee, Customer>;