{- HLINT ignore -} -- Disables linting hints. After the lecture, try remove this line to enable hints and learn some Haskell best practices!

-- Functional Programming
primesBefore :: Integer -> [Integer]
primesBefore n = sieve [2..n]

sieve [] = []
sieve (prime : ns) = prime : sieve (filter (not . multipleOf prime) ns)

multipleOf n m = m `mod` n == 0

-- Lazy Evaluation
nats :: [Integer]
nats = startingAt 0
  where
  startingAt n = n : startingAt (n + 1)

take' :: Integer -> [a] -> [a]
take' 0 _ = []
take' n (x:xs) = x : take' (n-1) xs

skip' :: Integer -> [a] -> [a]
skip' 0 xs = xs
skip' n (x:xs) = skip' (n-1) xs

primes :: [Integer]
primes = sieve (skip' 2 nats)

mersenne :: [Integer]
mersenne = filter (isPower2Minus1) primes

isPower2 0 = False
isPower2 1 = True
isPower2 2 = True
isPower2 n = n `mod` 2 == 0 && isPower2 (n `div` 2)

isPower2Minus1 :: Integral t => t -> Bool
isPower2Minus1 n = isPower2 (n + 1)

first5Mersenne :: [Integer]
first5Mersenne = take 5 mersenne

-- Higher-Order Functions

mapList :: (a -> b) -> [a] -> [b]
mapList f [] = []
mapList f (x : xs) = f x : (mapList f xs)

map' :: (a -> b) -> [a] -> [b]
map' f xs = foldr (\ x acc -> f x : acc) [] xs

filter' :: (a -> Bool) -> [a] -> [a]
filter' p xs = foldr (\ x acc -> if p x then x : acc else acc) [] xs

foldl' :: (b -> a -> b) -> b -> [a] -> b
foldl' f b xs = foldr (flip f) b $ reverse xs 
  where reverse xs = foldr (\ x acc -> acc ++ [x]) [] xs

findFirst :: (a -> Bool) -> [a] -> Integer
findFirst p xs = foldr (\ i a -> if a >= 0 then a else i) (-1)
                   (map (\ (i, _) -> i)
                     (filter (\ (_, x) -> p x)
                       (enumerate (reverse xs))))

enumerate :: [a] -> [(Integer, a)]
enumerate xs = fst (foldr (\ x (acc, index) -> ((index,x):acc, index+1)) ([], 0) xs)

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
             deriving (Eq)

data Instruction = Push Integer
                 | Pop
                 | Dup
                 | Swap
                 | Apply Op
                 deriving (Eq)

data Op = Add 
        | Sub 
        | Mul 
        | Div 
        deriving (Eq)
 
-- TODO complete by defining Show for all your data types
instance Show Program where
  show :: Program -> String
  show Nil = ""
  show (Cons i Nil) = show i ++ ";"
  show (Cons i p) = show i ++ "; " ++ show p

instance Show Instruction where
  show (Push n) = "push " ++ show n
  show Pop = "pop"
  show Dup = "dup"
  show Swap = "swap"
  show (Apply op) = "apply " ++ show op

instance Show Op where
  show Add = "add"
  show Sub = "sub"
  show Mul = "mul"
  show Div = "div"

-- TODO define a type-class for "computable programs"

class Executable a where
  exec :: a -> [Integer] -> Maybe [Integer]

  execOnEmpty :: a -> Maybe [Integer]
  execOnEmpty a = exec a []  -- default implementation (implementation for free)

-- TODO implement that type-class for the proper data types. Test it with the
-- following programs:
program1 :: Program
program1 = Cons (Push 1) $ Cons (Push 2) $ Cons Dup $ Cons (Apply Mul) $ Cons Swap $ Cons (Apply Div) $ Cons (Push 4) $ Cons Pop Nil -- [4]
program2 :: Program
program2 = Cons (Push 1) $ Cons (Push 2) $ Cons Dup $ Cons (Apply Mul) $ Cons Swap $ Cons (Apply Div) $ Cons Pop $ Cons Pop Nil -- Nothing (error)

instance Executable Program where
  exec Nil stack = Just stack
  exec (Cons i p) stack = case exec i stack of
    Nothing -> Nothing
    Just stack -> exec p stack

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