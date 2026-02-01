package dev.rotator.util;

public class Pair<F, S> {
    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair<?, ?> other)) return false;
        return (first == null ? other.first == null : first.equals(other.first))
                && (second == null ? other.second == null : second.equals(other.second));
    }

    @Override
    public int hashCode() {
        int result = (first != null) ? first.hashCode() : 0;
        result = 31 * result + ((second != null) ? second.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
