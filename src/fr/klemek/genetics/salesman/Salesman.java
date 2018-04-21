package fr.klemek.genetics.salesman;

import fr.klemek.genetics.Subject;
import fr.klemek.genetics.Utils;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Salesman implements Subject {

    //variables

    private final byte[] path;

    private float distance;
    private boolean valid;

    //constructors

    public Salesman() {
        this(false);
    }

    public Salesman(boolean empty) {
        if (empty) {
            this.path = new byte[Data.DATA_SIZE];
            Utils.fill(this.path, (byte) -1);
            this.valid = false;
        } else {
            this.path = Utils.indexes((byte) Data.DATA_SIZE);
            Utils.shuffle(this.path);
            this.valid = true;
        }
        this.distance = 0f;
    }

    public Salesman(byte[] path) {
        this.path = path;
        this.valid = false;
        this.distance = 0f;
    }

    //accessors

    public byte[] getPath() {
        return path;
    }

    //functions

    private void setCity(int place, byte city) {
        this.path[place] = city;
        this.distance = 0f;
        this.valid = false;
    }

    private int getCityPlace(byte city) {
        return Utils.indexOf(this.path, city);
    }

    private byte getCity(int place) {
        return this.path[place];
    }

    private float distance() {
        if (!valid())
            return 0f;
        if (distance != 0)
            return distance;
        distance = 0f;
        for (int i = 1; i < this.path.length; i++)
            distance += Data.distanceBetweenCities(this.path[i - 1], this.path[i]);
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
    public boolean valid() {
        if (valid)
            return true;
        valid = true;
        ArrayList<Byte> past = new ArrayList<>();
        for (byte city : this.path) {
            if (city < 0 || city >= Data.DATA_SIZE) {
                valid = false;
                break;
            }
            if (past.contains(city)) {
                valid = false;
                break;
            }
            past.add(city);
        }
        return valid;
    }

    @Override
    public float score() {
        return this.distance();
    }

    @Override
    public void mutate(int level) {
        if (!this.valid())
            return;
        int place1;
        int place2;
        for (int i = 0; i < level; i++) {
            place1 = ThreadLocalRandom.current().nextInt(Data.DATA_SIZE);
            place2 = Utils.randInt(0, Data.DATA_SIZE, place1);
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
        int cut1 = ThreadLocalRandom.current().nextInt(0, Data.DATA_SIZE - 1);
        int cut2 = ThreadLocalRandom.current().nextInt(cut1 + 1, Data.DATA_SIZE);

        //fill children from parents
        ArrayList<Byte> specChild0 = new ArrayList<>();
        ArrayList<Byte> specChild1 = new ArrayList<>();

        for (int i = 0; i < Data.DATA_SIZE; i++) {
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
        for (int i = 0; i < Data.DATA_SIZE; i++) {
            if (i < cut1 || i >= cut2) {
                if (specChild0.contains(children[0].getCity(i)))
                    children[0].setCity(i, (byte) -1);
                if (specChild1.contains(children[1].getCity(i)))
                    children[1].setCity(i, (byte) -1);
            }
        }

        //fix missing data
        for (byte c = 0; c < Data.DATA_SIZE; c++) {
            if (children[0].getCityPlace(c) == -1)
                for (int i = 0; i < Data.DATA_SIZE; i++)
                    if (children[0].getCity(i) == -1) {
                        children[0].setCity(i, c);
                        break;
                    }
            if (children[1].getCityPlace(c) == -1)
                for (int i = 0; i < Data.DATA_SIZE; i++)
                    if (children[1].getCity(i) == -1) {
                        children[1].setCity(i, c);
                        break;
                    }
        }

        if (children[0] != null && children[0].valid() && children[1] != null && children[1].valid())
            return children;
        return new Salesman[0];
    }
}
