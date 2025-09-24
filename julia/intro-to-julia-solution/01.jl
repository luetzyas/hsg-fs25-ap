################################################################################
# 1. Ask the AI to showcase and explain the basic language features of Julia
################################################################################

# Basic Language Features of Julia

# 1. Variables and Data Types
x = 10          # Integer
y = 3.14        # Float
name = "Julia"  # String
is_valid = true # Boolean

# 2. Basic Arithmetic
sum = x + y
difference = x - y
product = x * y
quotient = x / y

# 3. Control Flow
if x > y
    println("x is greater than y")
elseif x < y
    println("x is less than y")
else
    println("x is equal to y")
end

# 4. Loops
# For loop
for i in 1:5
    println("Iteration $i")
end

# While loop
count = 1
while count <= 5
    println("Count is $count")
    global count += 1
end

# 5. Functions
function greet(name)
    return "Hello, $(name)!"
end

println(greet("Julia"))

# 6. Arrays
arr = [1, 2, 3, 4, 5]
println(arr[1])  # Accessing elements
push!(arr, 6)    # Adding elements
println(arr)

# 7. Dictionaries
dict = Dict("name" => "Julia", "age" => 10)
println(dict["name"])  # Accessing values
dict["age"] = 11       # Modifying values
println(dict)

################################################################################
# 2. Ask the AI to showcase and explain the advanced language features of Julia
################################################################################

# Advanced Language Features of Julia

# 1. Multiple Dispatch

struct Circle
    radius::Float64
end

struct Rectangle
    width::Float64
    height::Float64
end

function area(shape::Circle)
    return π * shape.radius^2
end

function area(shape::Rectangle)
    return shape.width * shape.height
end

c = Circle(5.0)
r = Rectangle(4.0, 6.0)
println("Area of circle: ", area(c))
println("Area of rectangle: ", area(r))

# 2. Metaprogramming
macro sayhello(name)
    return :(println("Hello, ", $name, "!"))
end

@sayhello "Julia"

# 3. Type System and Parametric Types
struct Point{T}
    x::T
    y::T
end

p1 = Point{Int}(2, 3)
p2 = Point{Float64}(2.5, 3.5)
println("Point 1: ", p1)
println("Point 2: ", p2)

# 4. Parallel and Distributed Computing
using Distributed
addprocs(2)  # Add 2 worker processes

@everywhere function parallel_sum(arr)
    return sum(arr)
end

arr = [1, 2, 3, 4, 5]
result = @distributed (+) for i in arr
    i
end

println("Parallel sum: ", result)

# 5. Macros
macro timeit(expr)
    quote
        start = time()
        result = $expr
        println("Time: ", time() - start, " seconds")
        result
    end
end

@timeit begin
    local sum = 0
    for i in 1:1000000
        sum += i
    end
    sum
end