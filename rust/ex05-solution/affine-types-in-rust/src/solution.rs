// -----------------------------------------------------------
// TASK 1
// Implement the necessary structures for the trie.
// -----------------------------------------------------------

// To represent the trie we will need a hashmap.
// One in offered in the standard library which we import with this line:
use std::collections::HashMap;

// Next we define the structure of the nodes of the trie:
struct Node {
    // The `end` flag indicates whether the word encoded by the path from the root to
    // this node exists in the trie or not.
    end: bool,
    // `edges` records all the node children of the current child and labels each edge
    // with a character.
    edges: HashMap<char, Node>,
}

// Next we define the structure of a trie
struct Trie {
    // In this assignment, a trie is just the root node
    root: Node,
}

// -----------------------------------------------------------
// TASK 2
// Implement methods that creates an empty node and trie.
// -----------------------------------------------------------

// Methods of a structure are defined in `impl` blocks
impl Node {
    // A method is just a function. Unlike traditional object-oriented languages
    // there's no `this` keyword. Instead, like Python, the first argument `self`
    // of a method stands for `this`. If this argument is missing then the method
    // is similar to a static method.
    // Also, unlike other object-oriented languages, there is no `new` keyword that
    // calls a constructor. In fact in rust there are no constructors; you just call
    // a static method that returns an instance of the structure, exactly as we do here:
    fn new() -> Node {
        // Like javascript objects, Rust objects are declared between curly braces,
        // but with the name of the structure preceding it.
        // The order of the fields does not matter.
        Node {
            end: false,            // by default, the empty node isn't an end node
            edges: HashMap::new(), // by default, a newly created node has no edges so we use the empty hashmap
        }
    }
}

// Now that we know how to make an empty Node, we can make an empty constructor
// for the Trie as follows:
impl Trie {
    fn new() -> Trie {
        Trie { root: Node::new() }
    }
}

// -----------------------------------------------------------
// TASK 3
// Implement a method that adds a word to a trie.
// -----------------------------------------------------------

// There can be more than one `impl` block associated to a structure.
// This allows us to define related methods in a modular way if we wish.
impl Node {
    // To add a word to a trie we will need a method that, given a node, will
    // return the next node at the edge labeled with `c`. It would also be
    // helpful if the method adds an edge and an empty node if the edge does not exist.
    // The following method is exactly such a method.
    //
    // Note the following:
    // 1. This method is not static because its first argument is `self`
    // 2. This method will probably mutate the object on which it's called.
    //    The `&mut` specifier on the `self` gives this away; the method requires
    //    a mutable (the `mut` part) reference (the `&` part) to the object.
    // 3. The other argument is a char. Since this is NOT a reference to a char,
    //    the `&` is not present, then the method is taking ownership of the character.
    //    So, at the calling site, the character is no longer available to be used
    //    after calling this method.
    // 4. The method returns a mutable reference to a Node.
    //    a. The returned node will be mutated (see `add_word` later): we may need
    //       to add children to this node or set its `end` flag.
    //    b. It must be a reference, otherwise the following happens:
    //       i.  Since the object owns the node, it must rescind its ownership over that
    //           Node to the caller of this method. But we will need that node later! So
    //           we must keep it! The best we can do is to borrow it to the caller, and
    //           that's what references do.
    //       ii. We could alternatively clone it and return the clone to the caller.
    //           But then any modification the caller does on the node will not be
    //           reflected inside the data structure but only on their clone.
    fn get_or_insert_target_mut(&mut self, c: char) -> &mut Node {
        self.edges
            // Using the `entry` method we can get metadata about the hashmap's
            // entry at the given key. Including whether it exists or not.
            // https://doc.rust-lang.org/std/collections/struct.HashMap.html#method.entry
            .entry(c)
            // Given an entry, we can get its content (as a mutable
            // reference) or create an entry for it when it's missing.
            // The default entry in our case will be the empty node.
            // https://doc.rust-lang.org/std/collections/hash_map/enum.Entry.html#method.or_insert
            .or_insert(Node::new())
    }
}

impl Trie {
    // In this method we add a word to the trie.
    // Much like the `get_or_insert_target_mut` method discussed earlier, this method will need
    // to modify the object on which it's called. Hence the `&mut self` argument is needed.
    // Moreover, the second argument is just a `String`. So the method will take ownership of
    // the string being added. This generally makes sense as the semantics of the method imply
    // that the String being added will **move** from the caller to the insides of the datastructure.
    // In other words, the datastructure is now responsible for the chunk of memory that the
    // word takes.
    fn add_word(&mut self, word: String) {
        // In Rust, by default every variable you create with `let` is declared immutable unless
        // you use `let mut`. As we will need to **reassign** a new value to `current` as we
        // are traversing the tree we need to declare it as mutable.
        // NOTE: a mutable variable does not mean that its content are mutable!!! A mutable
        // variable just means that you can change the value to which the variable points to.
        let mut current: &mut Node = &mut self.root;
        for c in word.bytes() {
            // In Rust you don't need parentheses in the `if`, `for`, and `while`
            current = current.get_or_insert_target_mut(c as char);
        }
        current.end = true;
    }
}

// -----------------------------------------------------------
// TASK 4
// Implement a method that checks if there are strings that
// match a given prefix in the trie.
// -----------------------------------------------------------

impl Node {
    // This method will return the child of a node labeled with character `c` (if it exists).
    //
    // Some things to note:
    // 1. This method will not modify the object it's called on. We can be confident about this
    //    because the first argument is `&self`, just a reference (by default immutable) to the
    //    object.
    // 2. The character being queried for is also passed as an immutable reference. This makes
    //    sense because the data structure will not keep the query inside it, it will only use
    //    it (without modifying it) to do some checks internally and rescind its access to it
    //    afterwards. That being said, there's nothing wrong about changing `c:&char` to `c:char`
    //    but that will be very annoying to the callers of this method and violates the principle
    //    of least privilege.
    // 3. The method returns an `Option` to indicate that a labeled child may not exist. That's
    //    the same Option you've seen in Scala (aka Maybe in Haskell).
    // 4. The optional value is to a reference! `Option<&Node>` is NOT the same as `&Option<Node>` !!
    fn get_target(&self, c: &char) -> Option<&Node> {
        self.edges.get(c)
    }
}

impl Trie {
    // This is the method that will check if a given prefix exists in the trie.
    // Note that just like `get_target` neither the object nor the prefix are mutable reference.
    // To respect the principle of least privilege we make both immutable references.
    fn has_prefix(&self, prefix: &String) -> bool {
        // This variable declaration should clear up the difference between a mutable declaration
        // (`let mut`) and a mutable reference (`&mut`).
        // As we traverse the trie we will need to change the node to which current points to, i.e.
        // we will be writing `current = ...`. To enable this we need to declare `current` mutable
        // with the `let mut`. However, this does not mean that we can modify the node that `current`
        // points to! So we cannot write `current.end = true` for example because `current` points
        // to an immutable reference.
        let mut current: &Node = &self.root;
        for c in prefix.bytes() {
            // Rust has pattern matching, exactly like Haskell and Scala.
            match current.get_target(&(c as char)) {
                Some(next) => {
                    current = next;
                }
                None => {
                    return false;
                }
            }
        }
        true
    }
}

// -----------------------------------------------------------
// TASK 5
// Implement a method that returns all the words that match a
// given prefix.
// -----------------------------------------------------------

impl Node {
    // To get all the words that start with a certain prefix we start by implementing
    // the following utility function that returns all the words made by the sub-trie
    // rooted at the object node. The resulting words will all be prefixed with some
    // given string.
    //
    // Note: a dynamic list in Rust has type `Vec<T>` and it's called a vector
    // (a terminology from C++)
    fn get_all_words(&self, prefix: &String) -> Vec<String> {
        let mut result = vec![];

        if self.end {
            // when the `end` flag is set, it means that the word up to the node is in
            // the trie. So we must added to the result. Unfortunately we cannot just
            // add it to the list as we need a `String` but the prefix is a borrowed
            // String: we cannot just take what we borrow and pass it on as if it's owned!
            // By returning `Vec<String>` we are passing the ownership of the vector
            // AND ITS CONTENT to the caller.
            // The only way to then convert the `&String` to a `String` is to clone it
            // and pass the ownership of the clone to the vector.
            result.push(prefix.clone());
        }

        // There is a caveat here. We cannot loop over `self.edges` but rather only over
        // `&self.edges`! That's because the loop is not allowed to consume `self.edges`,
        // i.e. it cannot take its ownership because the method does not own it to begin
        // with! `self` was borrowed to the method through the reference and thus
        // transitively, `self.edges` was also borrowed to the method, so the loop must
        // borrow it. We indicate this by looping over the reference.
        for (c, next) in &self.edges {
            // Now that we want to recurse to get all the subwords of the children we must
            // construct a new prefix. To do so we must clone the prefix to append the label to.
            // Do you see how Rust makes us aware of how many clones traditional languages
            // do for us under the hood?
            let mut new_prefix = prefix.clone();
            new_prefix.push(c.clone());
            result.append(&mut next.get_all_words(&new_prefix));
        }

        result
    }
}

impl Trie {
    // This is the method on Trie that gets all words that start with some prefix
    fn get_all_words(&self, prefix: &String) -> Vec<String> {
        let mut current: &Node = &self.root;
        for c in prefix.bytes() {
            match current.get_target(&(c as char)) {
                Some(next) => {
                    current = next;
                }
                None => {
                    return vec![];
                }
            }
        }
        current.get_all_words(prefix)
    }
}

// -----------------------------------------------------------
// MAIN: The Trie in Action
// -----------------------------------------------------------

// The output of the `main` function is this:
// > trie has prefix 'an': true
// > trie has prefix 'andy': false
// > trie has prefix 'e': false
// > all the words in the trie that start with `d` are: ["dad", "do"]
// > all the words in the trie are: ["arts", "ant", "ants", "and", "dad", "do"]

fn main() {
    let mut trie = Trie::new();

    trie.add_word(String::from("and"));
    trie.add_word(String::from("ant"));
    trie.add_word(String::from("ants"));
    trie.add_word(String::from("arts"));
    trie.add_word(String::from("dad"));
    trie.add_word(String::from("do"));

    println!(
        "trie has prefix 'an': {}",
        trie.has_prefix(&String::from("an"))
    );
    println!(
        "trie has prefix 'andy': {}",
        trie.has_prefix(&String::from("andy"))
    );
    println!(
        "trie has prefix 'e': {}",
        trie.has_prefix(&String::from("e"))
    );

    println!(
        "all the words in the trie that start with `d` are: {:?}",
        trie.get_all_words(&String::from("d"))
    );
    println!(
        "all the words in the trie are: {:?}",
        trie.get_all_words(&String::from(""))
    );
}
