// -----------------------------------------------------------
// TASK 1
// Implement the necessary structures for the trie.
// -----------------------------------------------------------

// To represent the trie we will need a hashmap.
// One in offered in the standard library which we import with this line:
use std::collections::HashMap;

// Next we define the structure of the nodes of the trie:
struct Node {
    // TODO
}

// Next we define the structure of a trie
struct Trie {
    // TODO
}

// -----------------------------------------------------------
// TASK 2
// Implement methods that creates an empty node and trie.
// -----------------------------------------------------------

// Methods of a structure are defined in `impl` blocks
impl Node {
    // TODO
    fn new() {}
}

// Now that we know how to make an empty Node, we can make an empty constructor
// for the Trie as follows:
impl Trie {
    // TODO
    fn new() {}
}

// -----------------------------------------------------------
// TASK 3
// Implement a method that adds a word to a trie.
// -----------------------------------------------------------

impl Trie {
    // TODO
    fn add_word() {}
}

// -----------------------------------------------------------
// TASK 4
// Implement a method that checks if there are strings that
// match a given prefix in the trie.
// -----------------------------------------------------------

impl Trie {
    // TODO
    fn has_prefix() {}
}

// -----------------------------------------------------------
// TASK 5
// Implement a method that returns all the words that match a
// given prefix.
// -----------------------------------------------------------

impl Trie {
    // TODO
    fn get_all_words() {}
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
