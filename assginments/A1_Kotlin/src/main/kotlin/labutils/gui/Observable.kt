package labutils.gui

/** A stream of events [E]. */
class Observable<E> {
    private var callbacks: MutableList<(E) -> Unit> = mutableListOf()
    /** Add a callback that is executed after every event by the event source. */
    fun observe(callback: (E) -> Unit) = this.callbacks.add(callback)
    /** Push a new event as the event source. */
    fun push(event: E) = this.callbacks.forEach { it(event) }
}