-- **************************************************************************************************
-- * Finding All Possible Assignments                                                               *
-- **************************************************************************************************
{-
Task1: Cartesian Product                                                                       
Implement the function cartesian :: [a] -> [b] -> [(a, b)] that takes two lists and returns    
the one that contains all the pairs from their Cartesian product.  
input: cartesian [1,2] ['a','b','c']
output: [(1,'a'),(1,'b'),(1,'c'),(2,'a'),(2,'b'),(2,'c')]
-}
cartesian :: [a] -> [b] -> [(a, b)]
cartesian [] _ = [] -- empty list returns empty list
cartesian (x:xs) ys = [(x, y) | y <- ys] ++ cartesian xs ys

{-
Task2: List of Pairs to List of Lists
Implement the function lpl2ll :: [(a, [a])] -> [[a]] which, for every pair in the list, sets
the left-hand-side to the head of the right-hand-side.
input: lpl2ll [(1,[4]), (5,[6,7,8])]
output: [[1,4],[5,6,7,8]]
-}
lpl2ll :: [(a, [a])] -> [[a]]
lpl2ll [] = [] -- empty list returns empty list
lpl2ll ((x, ys):xys) = (x:ys) : lpl2ll xys

{-
Task3: Generalized Cartesian Product
Implement the function genCartesian :: [[a]] -> [[a]] using cartesian and lpl2ll.
input: genCartesian [[1],[2,3],[4,5,6]]
output: [[1,2,4],[1,2,5],[1,2,6],[1,3,4],[1,3,5],[1,3,6]]
-}
genCartesian :: [[a]] -> [[a]]
genCartesian [] = [[]] -- empty list returns list with empty list
genCartesian (xs:xss) = lpl2ll (cartesian xs (genCartesian xss))

{-
Task4: Find All Possible Assignments
Implement the allAssignments :: [String] -> [[(String,Bool)]] function, which takes a list
of variable names and returns all the possible assignments.
input: allAssignments ["x", "y"]
output: [[("x",True),("y",True)],[("x",True),("y",False)],[("x",False),("y",True)],[("x",False),("y",False)]]
-}
allAssignments :: [String] -> [[(String,Bool)]]
allAssignments [] = [[]] -- empty list returns list with empty list
allAssignments (v:vs) = lpl2ll (cartesian [(v, True), (v, False)] (allAssignments vs))

-- **************************************************************************************************
-- * Encoding a Formula                                                                             *
-- **************************************************************************************************
{-
Task5: Formula Data Type
In the course we have seen how to Algebraic Data Types (ADTs) can represent a grammar. Define the
data Formula ADT with the necessary constructors that match the grammar of <formula>.
ADT = algebraic Data Type
input: :info Formula
output:
type Formula :: *
data Formula
  = And Formula Formula
  | Or Formula Formula
  | Implies Formula Formula
  | Not Formula
  | Var String
        -- Defined at assignment.hs:58:1
instance Eq Formula -- Defined at assignment.hs:64:21
instance Show Formula -- Defined at assignment.hs:64:15
-}
data Formula
    = And       Formula Formula
    | Or        Formula Formula
    | Implies   Formula Formula
    | Not       Formula
    | Var       String
    deriving (Show, Eq)

{-
Task6: Formula Variables
Implement the function vars :: Formula -> [String] that collects all the variables that occur in the formula.
input: vars (Var "x")                       --> output: ["x"]
input: vars (And (Var "x") (Var "y"))       --> output: ["x","y"]
input: vars (Or (Var "a") (Not (Var "b")))  --> output: ["a","b"]
-}
vars :: Formula -> [String]
vars (Var v) = [v] 
vars (Not f) = vars f
vars (And f g) = vars f ++ vars g
vars (Or f g) = vars f ++ vars g
vars (Implies f g) = vars f ++ vars g

{-
Task7: Unique Elements in a List
Implement the unique :: Eq a => [a] -> [a] function that removes duplicates from a list.
input: contains 3 []                    --> output: False
input: unique [1,2,3,2,4,3,55,2,9,34]   --> output: [1,4,3,55,2,9,34]
input: unique ["x","y","x","z","y"]     --> output: ["z","x","y"]
-}
contains :: Eq a => a -> [a] -> Bool
contains _ [] = False -- if emtpy return False
contains x (y:ys) =
    if x == y
        then True
        else contains x ys
--        
unique :: Eq a => [a] -> [a]
unique [] = [] -- if emtpy return empty list
unique (x:xs) =
    if contains x xs
        then unique xs
        else x : unique xs

-- **************************************************************************************************
-- * Finding a Satisfying Assignment                                                                *
-- **************************************************************************************************
{-
Task8: Check Assignment
Implement the check :: Formula -> [(String,Bool)] -> Bool function that takes an assignment
and a formula and returns True if the assignment satisfies the formula and False otherwise. If a
variable in the formula does not occur in the list of assignments we may assume it is assigned to False.
input: valueOf "x" [("x",True),("y",False)]                         --> output: True
input: valueOf "y" [("x",True),("y",False)]                         --> output: False
input: check (Var "x") [("x",True)]                                 --> output: True
input: check (And (Var "x") (Var "y")) [("x",True),("y",False)]     --> output: False
-}
valueOf :: String -> [(String,Bool)] -> Bool
valueOf _ [] = False -- if emtpy return False
valueOf name ((v,val):rest) =
    if name == v
        then val
        else valueOf name rest
--
check :: Formula -> [(String,Bool)] -> Bool
check (Var x) asgn = valueOf x asgn
check (Not f) asgn = not (check f asgn)
check (And f g) asgn = check f asgn && check g asgn
check (Or f g) asgn = check f asgn || check g asgn
check (Implies f g) asgn = (not (check f asgn)) || check g asgn

{-
Task9: Solving a Formula
Implement the solve :: Formula -> Maybe [(String,Bool)] which takes a formula and returns,
if it exists, an assignment that makes it satisfiable.
input: solve (Or (Var "a") (Var "b"))           --> output: Just [("a",True),("b",True)]
input: solve (And (Not (Var "a")) (Var "a"))    --> output: Nothing
-}
solve :: Formula -> Maybe [(String,Bool)]
solve formula =
    let vs = unique (vars formula)
        aAsgn = allAssignments vs
        validAsgn = filter (\asg -> check formula asg) aAsgn
    in case validAsgn of
        [] -> Nothing
        (x:_) -> Just x 

{-
Task10: Use the Solver
Which of the following formulas is satisfiable? And what is an assignment that satisfies it? Encode
the following formulas and check them with your solver.
input: solve formulaTrue    --> output: Just [("a",True),("b",True),("c",True),("d",True),("e",True),("f",True)]
input: solve formulaNoth    --> output: Nothing
input: solve formulaOwn     --> output: Just [("a",True),("b",True),("c",True),("d",True)]
--
The two formulas are encoded with the Formula data type. 
The first one is satisfiable. 
The second one contains both a and not a, so solve returns Nothing.
-}
formulaTrue :: Formula
formulaTrue =
    Or (Var "a")
      (Or (Var "b")
        (Or (Var "c")
          (Or (Var "d")
            (Or (Var "e") (Var "f")))))

formulaNoth :: Formula
formulaNoth =
    And (Not (Var "a"))
      (And (Var "a")
        (And (Var "b")
          (And (Var "c")
            (And (Var "d")
              (And (Var "e") (Var "f"))))))

formulaOwn :: Formula
formulaOwn =
    Or (And (Var "a") (Var "b"))
       (And (Not (Var "c")) (Var "d"))