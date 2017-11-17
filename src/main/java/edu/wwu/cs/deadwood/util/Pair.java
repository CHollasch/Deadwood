package edu.wwu.cs.deadwood.util;

/**
 * Immutable 2-tuple.
 *
 * @author Connor Hollasch
 * @since Oct 24, 7:33 PM
 */
public final class Pair<A, B>
{
    private final A first;
    private final B second;

    public Pair (final A first, final B second)
    {
        this.first = first;
        this.second = second;
    }

    public A getFirst ()
    {
        return this.first;
    }

    public B getSecond ()
    {
        return this.second;
    }
}
