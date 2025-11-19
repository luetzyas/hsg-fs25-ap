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

type FirstEmployeeAge = Vector<Employee>[0]["age"];
type EmployeeAge = Vector<Employee>[number]["age"];
type EmployeeSalary = [String, Vector<Employee>][1][number]["salary"];

/* KEYOF TYPE OPERATOR */
type EmployeeKey = keyof Employee;                               // "name" | "age" | "salary"
const employeeKey: EmployeeKey = "age";                          // OK

type CustomerKey = TODO;                                         // "name" | "age" | "balance"
const customerKey: CustomerKey = TODO;                           // OK
// const wrongEmployeeKey: CustomerKey = "salary";               // ERROR

/* UNION TYPES */
type Boolean = true | false;
type EmployeeOrCustomer = Employee | Customer;       
const charlie: EmployeeOrCustomer = { name: "Charlie", age: 50, balance: 500 };

type EmployeeOrCustomerKey = TODO;  // "name" | "age" | "salary" | "balance"

/* INTERSECTION TYPES */
type Never = true & false;
type EmployeeAndCustomer = Employee & Customer;      
const dave: EmployeeAndCustomer = { name: "Dave", age: 60, salary: 3000, balance: 1500 };

type EmployeeAndCustomerKey = TODO; // "name" | "age"

/* CONDITIONAL TYPES */
type SubType<A, B> = A extends B ? true : false;
type __check_subtype_1__ = SubType<Employee, { name: string }>;
type __check_subtype_2__ = SubType<Employee, { name: string, balance: number }>;

type HasKey<K, T> = TODO;

type __check_has_key_1__ = HasKey<"name", Employee>;
type __check_has_key_2__ = HasKey<"balance", Employee>;
type __check_has_key_3__ = HasKey<"name" | "balance", Employee>;

type TypeEqual<A, B> = TODO;

type __check_type_equal_1__ = TypeEqual<Employee, Employee>;
type __check_type_equal_2__ = TypeEqual<Employee, Customer>;

/* TYPE TESTS/GUARDS */
type Not<A extends boolean> = TODO;

type __check_not_1__ = Not<true>;
type __check_not_2__ = Not<false>;
// type __check_not_3__ = Not<"foo">; // ERROR

type Assert<T extends true[]> = T;
type __check_assert__ = Assert<[
    SubType<Employee, { name: string }>,
    Not<SubType<Employee, { name: string, balance: number }>>,
    TypeEqual<Employee, Employee>,
    Not<TypeEqual<Employee, Customer>>,
    HasKey<"name", Employee>,
    Not<HasKey<"balance", Employee>>,
]>;

/* MAPPED TYPES */
type IntersectionNumber<A, B> = TODO
type __check_intersection_number__ = IntersectionNumber<Employee, Customer>;

type Intersection<A, B> = TODO
type __check_intersection__ = Intersection<Employee, Customer>;