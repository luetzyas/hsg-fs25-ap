export { SubType, TypeEqual, Not, Assert };

/* GENERICS (TYPE LAMBDAS) */
type Vector<A> = A[];
type NumberVector = Vector<number>;

type Pair<A, B> = [A, B];
type Point2D = Pair<number, number>;

/* STRUCTURAL TYPES (PATH-DEPENDENT TYPES) */
type Employee = { name: string, age: number, salary: number };
type EmployeeName = Employee["name"];
const alice: Employee = { name: "Alice", age: 30, salary: 2000 };

type Customer = { name: string, age: number, balance: number };
type CustomerBalance = Customer["balance"];
const bob: Customer = { name: "Bob", age: 40, balance: 1000 };

type FirstEmployeeAge = Vector<Employee>[0]["age"];
type EmployeeAge = Vector<Employee>[number]["age"];
type EmployeeSalary = [String, Vector<Employee>][1][number]["salary"];

/* KEYOF TYPE OPERATOR */
type EmployeeKey = keyof Employee;                               // "name" | "age" | "salary"
type CustomerKeys = keyof Customer;                              // "name" | "age" | "balance"
const employeeKey: EmployeeKey = "age";                          // OK
const customerKey: CustomerKeys = "balance";                     // OK
// const wrongEmployeeKey: CustomerKeys = "salary";              // ERROR

/* UNION TYPES */
type Boolean = true | false;
type EmployeeOrCustomer = Employee | Customer;       
const charlie: EmployeeOrCustomer = { name: "Charlie", age: 50, balance: 500 };           // OK
type EmployeeOrCustomerKey = keyof Employee | keyof Customer;  // "name" | "age" | "salary" | "balance"

/* INTERSECTION TYPES */
type Never = true & false;
type EmployeeAndCustomer = Employee & Customer;      
const dave: EmployeeAndCustomer = { name: "Dave", age: 60, salary: 3000, balance: 1500 }; // OK
type EmployeeAndCustomerKey = keyof Employee & keyof Customer; // "name" | "age"

/* CONDITIONAL TYPES */
type SubType<A, B> = A extends B ? true : false;
type __check_subtype_1__ = SubType<Employee, { name: string }>;
type __check_subtype_2__ = SubType<Employee, { name: string, balance: number }>;

type HasKey<K, T> = SubType<K, keyof T>;  // or K extends keyof T ? true : false;
type __check_has_key_1__ = HasKey<"name", Employee>;
type __check_has_key_2__ = HasKey<"balance", Employee>;
type __check_has_key_3__ = HasKey<"name" | "balance", Employee>;

type TypeEqual<A, B> = 
    B extends A 
        ? A extends B 
            ? true 
            : false
        : false;
type __check_type_equal_1__ = TypeEqual<Employee, Employee>;
type __check_type_equal_2__ = TypeEqual<Employee, Customer>;

/* TYPE TESTS */
type Not<A extends boolean> = A extends true ? false : true;
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
type IntersectionNumber<A, B> = {
    [K in (keyof A & keyof B)]: number  // we lost the field types!
}
type __check_intersection_number__ = IntersectionNumber<Employee, Customer>;

type Intersection<A, B> = {
    [K in (keyof A & keyof B) as A[K] extends B[K] ? K : never]: A[K];
}
type __check_intersection__ = Intersection<Employee, Customer>;