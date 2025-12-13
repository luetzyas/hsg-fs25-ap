// -----------------------------------------------------------------------------
// Prelude
// -----------------------------------------------------------------------------

/// A possibly infinite source of elements of type [A].
pub trait Stream<A> {
    /// Return a [Some] containing the next element of the stream, or [None] if
    /// the stream is exhausted.
    fn next(&mut self) -> Option<A>;
}

/// Skip the first n elements of a [Stream].
pub fn skip<A, S: Stream<A>>(mut source: S, n: usize) -> S {
    for _ in 0..n {
        source.next();
    }
    source
}

/// Transform the input [Stream] into a [Vec] by consuming all its elements.
pub fn vec<A, S: Stream<A>>(mut source: S) -> Vec<A> {
    let mut result = Vec::new();
    while let Some(x) = source.next() {
        result.push(x);
    }
    result
}

pub mod assignment;
pub use assignment::*;