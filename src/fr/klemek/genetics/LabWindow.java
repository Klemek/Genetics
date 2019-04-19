package fr.klemek.genetics;

import java.awt.*;

public interface LabWindow<T extends Subject> {

    String getBestDescription(T best);

    void paintCustom(Graphics2D g2, int w, int h, T best);
}
