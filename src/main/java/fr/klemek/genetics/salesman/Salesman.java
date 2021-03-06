package fr.klemek.genetics.salesman;

import fr.klemek.genetics.Subject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Salesman implements Subject {

    //variables

    private final byte[] path;

    private float distance;

    //constructors

    public Salesman() {
        this(false);
    }

    private Salesman(boolean empty) {
        if (empty) {
            this.path = new byte[Utils.dataSize];
            Arrays.fill(this.path, (byte) -1);
        } else {
            this.path = Utils.indexes((byte) Utils.dataSize);
            Utils.shuffle(this.path);
        }
        this.distance = 0f;
    }

    //accessors

    byte[] getPath() {
        return path;
    }

    //functions

    private void setCity(int place, byte city) {
        this.path[place] = city;
        this.distance = 0f;
    }

    private int getCityPlace(byte city) {
        return Utils.indexOf(this.path, city);
    }

    private byte getCity(int place) {
        return this.path[place];
    }

    private float distance() {
        if (distance != 0)
            return distance;
        distance = 0f;
        for (int i = 1; i < this.path.length; i++)
            distance += Utils.distanceBetweenCities(this.path[i - 1], this.path[i]);
        return distance;
    }

    private void swapCities(int place1, int place2) {
        byte tmp = this.path[place1];
        this.path[place1] = this.path[place2];
        this.path[place2] = tmp;
        this.distance = 0f;
    }

    //interface methods

    @Override
    public float score() {
        return this.distance();
    }

    @Override
    public void mutate(int level) {
        int place1;
        int place2;
        for (int i = 0; i < level; i++) {
            place1 = ThreadLocalRandom.current().nextInt(Utils.dataSize);
            place2 = Utils.randInt(0, Utils.dataSize, place1);
            this.swapCities(place1, place2);
        }
        this.distance = 0f;
    }

    @Override
    public Subject[] createChildren(Subject arg0) {
        if (!(arg0 instanceof Salesman))
            return new Salesman[0];

        Salesman other = (Salesman) arg0;

        Salesman[] children = new Salesman[]{new Salesman(true), new Salesman(true)};

        //generate cuts
        int cut1 = ThreadLocalRandom.current().nextInt(0, Utils.dataSize - 1);
        int cut2 = ThreadLocalRandom.current().nextInt(cut1 + 1, Utils.dataSize);

        //fill children from parents
        ArrayList<Byte> specChild0 = new ArrayList<>();
        ArrayList<Byte> specChild1 = new ArrayList<>();

        for (int i = 0; i < Utils.dataSize; i++) {
            if (i < cut1 || i >= cut2) {
                children[0].setCity(i, this.getCity(i));
                children[1].setCity(i, other.getCity(i));
            } else {
                children[0].setCity(i, other.getCity(i));
                specChild0.add(other.getCity(i));

                children[1].setCity(i, this.getCity(i));
                specChild1.add(this.getCity(i));
            }
        }

        //remove invalid data
        for (int i = 0; i < Utils.dataSize; i++) {
            if (i < cut1 || i >= cut2) {
                if (specChild0.contains(children[0].getCity(i)))
                    children[0].setCity(i, (byte) -1);
                if (specChild1.contains(children[1].getCity(i)))
                    children[1].setCity(i, (byte) -1);
            }
        }

        //fix missing data
        for (byte c = 0; c < Utils.dataSize; c++) {
            if (children[0].getCityPlace(c) == -1)
                for (int i = 0; i < Utils.dataSize; i++)
                    if (children[0].getCity(i) == -1) {
                        children[0].setCity(i, c);
                        break;
                    }
            if (children[1].getCityPlace(c) == -1)
                for (int i = 0; i < Utils.dataSize; i++)
                    if (children[1].getCity(i) == -1) {
                        children[1].setCity(i, c);
                        break;
                    }
        }

        return children;
    }
}
