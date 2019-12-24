package ru.hse.alyokhina;

public class Pair<T, E> {
    public T first;
    public E second;
    Pair(T first, E second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public E getSecond() {
        return second;
    }

    public void setSecond(E second) {
        this.second = second;
    }


    public static Pair<Integer, Integer> minus(Pair<Integer, Integer> pair1, Pair<Integer, Integer> pair2) {
        return new Pair<>(pair1.getFirst() - pair2.getFirst(), pair1.getSecond() - pair2.getSecond());
    }

    public static Pair<Integer, Integer> plus(Pair<Integer, Integer> pair1, Pair<Integer, Integer> pair2) {
        return new Pair<>(pair1.getFirst() + pair2.getFirst(), pair1.getSecond() + pair2.getSecond());
    }
}
