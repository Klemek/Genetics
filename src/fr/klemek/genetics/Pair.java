package fr.klemek.genetics;

import java.util.Objects;

public class Pair<T> {
    private final T first;
    private final T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?> pair = (Pair<?>) o;
        return (Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second) || Objects.equals(first, pair.second) &&
                Objects.equals(second, pair.first));
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}