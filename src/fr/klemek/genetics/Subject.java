package fr.klemek.genetics;

public interface Subject {

    float score();

    void mutate(int level);

    Subject[] createChildren(Subject other);
}
