-- IDRIS2
-- Idris2 is a dependently-typed language with Haskell-like syntax:
-- https://github.com/idris-lang/Idris2
-- You can use this unofficial online playground and REPL to test your code:
-- https://dunhamsteve.github.io/idris2-playground/

-- TASK
-- We want a format function that supports string formatting, behaving
-- like printf in C:
-- printf("I am %d years old.\n", 30);    -- C
-- format "I am %d years old.\n" 30       -- ours
--
-- We want it to be type-safe, meaning exact number and type of arguments:
-- format "%d foo %s" 30                  -- ERROR: expected 3 args, got 2
-- format "I am %d years old.\n" "hello"  -- ERROR: expected number, got string

-- SPECIFIERS
-- Let's start by defining the format specifiers we want to support,
-- like %d and %s
data FormatSpecifiers = IntSpecifier           -- %d
                      | StringSpecifier        -- %s
                      | LiteralSpecifier Char  -- anything else

-- We can define a function to map strings into the corresponding formatters
-- from a list of characters
parseFormatters : List Char -> List FormatSpecifiers
parseFormatters [] = []
parseFormatters ('%' :: 'd' :: rest) = IntSpecifier :: parseFormatters rest
parseFormatters ('%' :: 's' :: rest) = StringSpecifier :: parseFormatters rest
parseFormatters (c :: rest) = LiteralSpecifier c :: parseFormatters rest

-- TYPE-SAFETY
-- Let's translate a list of format specifiers into a list of
-- corresponding types for the arguments the user has to provide.
--
-- We define the type-function `formatType` to do the translation.
-- Note how the function returns a `Type`, which represent the type
-- of a type:
-- "hello" has type `String`
-- `String` has type `Type`
-- `Type` has type `Type 1`
-- `Type 1` has type `Type 2`
-- and so on... this is the hierarchy of so-called `type universes`.
--
-- We can define a function with variable arguments by chaining
-- function types (i.e., currying). Note how the base case is
-- called last, and should map to the return type of `format`.
-- `format` returns a formatted string, so that type is String.
formatType : List FormatSpecifiers -> Type
formatType [] = String 
formatType (IntSpecifier :: rest) = Int -> formatType rest        -- %d expects Int
formatType (StringSpecifier :: rest) = String -> formatType rest  -- %s expects String
formatType (LiteralSpecifier _ :: rest) = formatType rest         -- anything else is ignored                                       

-- FORMAT
-- `format` takes a string `fmt` containing format specifiers. Note how
-- its return type depends on `fmt`, and in particular is the chain
-- defines before terminating with the type `String`.
--
-- `pack` is used to convert a list of `Char`s into `String`, and
-- `unpack` viceversa.
format : (fmt: String) -> formatType (parseFormatters (unpack fmt))
format fmt = helper (parseFormatters (unpack fmt)) ""
  where
  helper : (fmtSpecs: List FormatSpecifiers) -> String -> formatType fmtSpecs
  helper [] result = result
  helper (IntSpecifier :: rest) result = \ n => helper rest (result ++ show n)
  helper (StringSpecifier :: rest) result = \ s => helper rest (result ++ s)
  helper (LiteralSpecifier c :: rest) result = helper rest (result ++ (pack [c]))

-- EXAMPLES
success1 : String
success1 = format "Hello, world!"

success2 : String
success2 = format "I am %d years old.\n" 10

success3 : String
success3 = format "I am %s, I am %d years old.\n" "Idris2" 18

-- wrong number of arguments:
-- expected `failure1 : String`
-- got      `failure1 : Int -> String`
failure1 : String 
failure1 = format "I am %s, I am %d years old.\n" "Idris2"

-- wrong type of arguments:
-- expected `Int` found `String`
failure2 : String
failure2 = format "I am %s, I am %d years old.\n" "Idris2" "18"