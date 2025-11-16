{- HLINT ignore -} -- Disables linting hints. After the lecture, try remove this line to enable hints and learn some Haskell best practices!

-- Functional Programming
primesBefore :: Integer -> [Integer]
-- Task1: The Sieve of Eratosthenes
primesBefore n = sieve [2..n] -- Zahlen von 2 bis n

--sieve :: [Integer] -> [Integer]
sieve [] = []
sieve (prime : ns) = prime : sieve (filter (not . multipleOf prime) ns)
multipleOf n m = m `mod` n == 0

-- Lazy Evaluation
nats :: [Integer]
-- Task2: Creatingh Infinite Lists
nats = startingAt 0 
  where
  startingAt n = n : startingAt (n + 1)

take' :: Int -> [a] -> [a] 
skip' :: Int -> [a] -> [a]
-- Task3: Querying Infinite Lists
take' 0 _ = [] -- take  the fist n elements from the list
take' n (x:xs) = x : take' (n-1) xs -- we can ignore the tail of the list
skip' 0 xs = xs -- skip the first n elements from the list
skip' n (_:xs) = skip' (n-1) xs -- we can ignore the head of the list

primes :: [Integer]
-- Task4: The Lazy Sieve of Eratosthenes
primes = sieve (skip' 2 nats) -- alle Primzahlen (ohne 0 und 1)

mersenne :: [Integer]
-- Task5: The Mersenne Primes
mersenne = filter isPower2Minus1 primes -- Mersenne-Primzahlen sind Primzahlen der Form 2^p - 1
isPower2Minus1 0 = False -- 2^0 - 1 = 0 ist keine Primzahl
isPower2Minus1 1 = True  -- 2^1 - 1 = 1 ist eine Primzahl
isPower2Minus1 2 = True  -- 2^2 - 1 = 3 ist eine Primzahl
isPower2 n = n `mod` 2 == 0 && isPower2 (n `div` 2) -- Prüfe ob n + 1 eine Zweierpotenz ist

first5Mersenne :: [Integer]
first5Mersenne = take 5 mersenne -- die ersten 5 Mersenne-Primzahlen

-- Higher-Order Functions

mapList :: (a -> b) -> [a] -> [b]
-- Task6: Map
mapList f [] = []
mapList f (x : xs) = f x : (mapList f xs) -- applies function f to each element of the list

map' :: (a -> b) -> [a] -> [b]
map' f xs = foldr (\ x acc -> f x : acc) [] xs -- implements map using foldr

filter' :: (a -> Bool) -> [a] -> [a]
-- Task7: Generality of Folding
filter' p xs = foldr (\ x acc -> if p x then x : acc else acc) [] xs -- implements filter using foldr

foldl' :: (b -> a -> b) -> b -> [a] -> b
foldl' f b xs = foldr (flip f) b $ reverse xs 
  where reverse xs = foldr (\ x acc -> acc ++ [x]) [] xs

findFirst :: (a -> Bool) -> [a] -> Integer
-- Task8: Functional List Manipulation (Advanced)
findFirst p xs = foldr (\ i a -> if a >= 0 then a else i) (-1) -- finds the index of the first element satisfying predicate p
                   (map (\ (i, _) -> i)
                     (filter (\ (_, x) -> p x)
                       (enumerate (reverse xs))))

enumerate :: [a] -> [(Integer, a)]
enumerate xs = fst (foldr (\ x (acc, index) -> ((index,x):acc, index+1)) ([], 0) xs) -- pairs each element with its index

-- Data Types and Type Classes

-- Model the following grammar with Haskell data types:
--   Program     ::= Instruction ";" Program
--                 | ""
--   Instruction ::= "push" Integer
--                 | "pop"
--                 | "dup"
--                 | "swap"
--                 | "apply" Op
--   Op          ::= "add" 
--                 | "sub" 
--                 | "mul" 
--                 | "div"

-- TODO complete by defining the data types for the grammar above
data Program = Cons Instruction Program
             | Nil
             deriving (Eq) -- Represents a program as a sequence of instructions or empty

data Instruction = Push Integer
                 | Pop
                 | Dup
                 | Swap
                 | Apply Op
                 deriving (Eq) -- Represents different types of instructions

data Op = Add 
        | Sub 
        | Mul 
        | Div
        deriving (Eq) -- Represents arithmetic operations
 
-- TODO complete by defining Show for all your data types
instance Show Program where
  show :: Program -> String
  show Nil = ""
  show (Cons i Nil) = show i ++ ";"
  show (Cons i p) = show i ++ "; " ++ show p

instance Show Instruction where
  show :: Instruction -> String
  show (Push n) = "push " ++ show n
  show Pop = "pop"
  show Dup = "dup"
  show Swap = "swap"
  show (Apply op) = "apply " ++ show op

instance Show Op where
  show :: Op -> String
  show Add = "add"
  show Sub = "sub"
  show Mul = "mul"
  show Div = "div"  

-- TODO define a type-class for "computable programs"

class Executable a where
  exec :: a -> [Integer] -> Maybe [Integer] -- Executes the program on a given stack, returning Maybe to handle errors
  execOnEmpty :: a -> Maybe [Integer] -- Executes the program on an empty stack
  execOnEmpty p = exec p [] -- Default implementation that executes on an empty stack

-- TODO implement that type-class for the proper data types. Test it with the
-- following programs:
program1 :: Program
program1 = Cons (Push 1) $ Cons (Push 2) $ Cons Dup $ Cons (Apply Mul) $ Cons Swap $ Cons (Apply Div) $ Cons (Push 4) $ Cons Pop Nil 
-- Result: Just [4]
program2 :: Program
program2 = Cons (Push 1) $ Cons (Push 2) $ Cons Dup $ Cons (Apply Mul) $ Cons Swap $ Cons (Apply Div) $ Cons Pop $ Cons Pop Nil
-- Result: Nothing (error)

instance Executable Program where
  exec Nil stack = Just stack
  exec (Cons instr rest) stack = case exec instr stack of
    Nothing -> Nothing
    Just newStack -> exec rest newStack

instance Executable Instruction where
  exec (Push n) stack = Just (n : stack) -- NOTE the stack grows from right to left, so the top of the stack is the head of the list
  exec Pop (x:xs) = Just xs
  exec Dup (x:xs) = Just (x:x:xs)
  exec Swap (y:x:xs) = Just (x:y:xs)
  exec (Apply op) (y:x:xs) =  -- NOTE we assume the operands are pushed in order (e.g., _ div _), so the top of the stack is the second operand
    let f = case op of
              Add -> (+)
              Sub -> (-)
              Mul -> (*)
              Div -> div
    in Just (f x y : xs)
  exec _ _ = Nothing

-- TODO implement the below function that maps a program into an optimized one.
-- Test it with the following program:
program3 :: Program
program3 = Cons (Push 1) $ Cons (Push 10) $ Cons Swap $ Cons Swap $ Cons (Push 20) $ Cons (Push 10) $ Cons Swap $ Cons Pop $ Nil -- [10,10,1]

optimize :: Program -> Program
optimize prog =
  let prog' = step prog in if prog' == prog then prog else optimize prog'
  where
  prog' = step prog
  step :: Program -> Program
  step Nil = Nil
  step (Cons (Push _) (Cons Pop p)) = p
  step (Cons Swap (Cons Swap p)) = p
  step (Cons (Push n) (Cons (Push m) (Cons Swap p))) = Cons (Push m) $ Cons (Push n) $ p
  step (Cons (Push n) (Cons (Push m) p)) =
    if n == m then Cons (Push n) $ Cons Dup p
    else Cons (Push n) $ step $ Cons (Push m) p
  step (Cons i p) = Cons i $ step p