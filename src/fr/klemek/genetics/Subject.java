package fr.klemek.genetics;

public interface Subject {

    float score();

    void mutate(int level);

    boolean valid();

    Subject[] createChildren(Subject other);
}
