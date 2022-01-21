package models;

public abstract class Wagon {
    public int id;               // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon


    public Wagon(int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    public void setNextWagon(Wagon nextWagon) {
        this.nextWagon = nextWagon;
    }

    public void setPreviousWagon(Wagon previousWagon) {
        this.previousWagon = previousWagon;
    }

    /**
     * @return whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        Wagon currentWagon = this;
        return currentWagon.getNextWagon() != null;
    }

    /**
     * @return whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        Wagon currentWagon = this;
        return currentWagon.getPreviousWagon() != null;
    }

    /**
     * Returns the last wagon attached to it, if there are no wagons attached to it then this wagon is the last wagon.
     *
     * @return the wagon
     */
    public Wagon getLastWagonAttached() {
        Wagon currentWagon = this;

        while (currentWagon.hasNextWagon()) {
            currentWagon = currentWagon.getNextWagon();
        }
        return currentWagon;
    }

    /**
     * @return the length of the tail of wagons towards the end of the sequence
     * excluding this wagon itself.
     */
    public int getTailLength() {
        int tailLenght = 0;
        Wagon currentWagon = this;
        while (currentWagon.getNextWagon() != null) {
            currentWagon = currentWagon.getNextWagon();
            tailLenght++;
        }
        return tailLenght;
    }

    /**
     * Attaches the tail wagon behind this wagon, if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     *
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     */
    public void attachTail(Wagon tail) {
        Wagon currentWagon = this;

        if (currentWagon.hasNextWagon()) {
            throw new IllegalStateException("[Wagon-" + this.id + "] already has appended " +
                    "[Wagon-" + this.getNextWagon().id + "] to it!");
        }
        if (tail.hasPreviousWagon()) {
            throw new IllegalStateException("[Wagon-" + tail.id + "] is already attached to [Wagon-" +
                    tail.getPreviousWagon().id + "] in front of it!");
        }
        currentWagon.setNextWagon(tail);
        tail.setPreviousWagon(currentWagon);
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     *
     * @return the first wagon of the tail that has been detached
     * or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        Wagon currentWagon = this;
        Wagon nextWagon = this.getNextWagon();

        if (currentWagon.getNextWagon() == null) {
            return null;
        }

        currentWagon.getNextWagon().setPreviousWagon(null);
        currentWagon.setNextWagon(null);

        return nextWagon;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     *
     * @return the former previousWagon that has been detached from,
     * or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        Wagon currentWagon = this;
        Wagon previousWagon = currentWagon.getPreviousWagon();

        if (currentWagon.getPreviousWagon() == null) {
            return null;
        }

        currentWagon.getPreviousWagon().setNextWagon(null);
        currentWagon.setPreviousWagon(null);

        return previousWagon;
    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     *
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {

        Wagon currentWagon = this;

        currentWagon.getPreviousWagon().setNextWagon(null);
        currentWagon.setPreviousWagon(null);

        currentWagon.setPreviousWagon(front);
        front.setNextWagon(currentWagon);

    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if it exists.
     */
    public void removeFromSequence() {
        Wagon currentWagon = this;

        if (currentWagon.getPreviousWagon() != null) {
            currentWagon.getPreviousWagon().setNextWagon(currentWagon.getNextWagon());
        } else {
            currentWagon.getNextWagon().setPreviousWagon(null);
        }

        if (currentWagon.hasNextWagon()) {
            currentWagon.getNextWagon().setPreviousWagon(currentWagon.getPreviousWagon());
        } else {
            currentWagon.getPreviousWagon().setNextWagon(null);
        }

        currentWagon.setNextWagon(null);
        currentWagon.setPreviousWagon(null);
    }


    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     *
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        Wagon currentWagon = getPreviousWagon();
        setPreviousWagon(getNextWagon());
        setNextWagon(null);

        Wagon wagon = getPreviousWagon();
        //same as trainreverse basically
        while (wagon.hasNextWagon()) {
            Wagon temporaryWagon = wagon.getNextWagon();
            wagon.setNextWagon(wagon.getPreviousWagon());
            wagon.setPreviousWagon(temporaryWagon);
            wagon = wagon.getPreviousWagon();
        }

        wagon.setNextWagon(wagon.getPreviousWagon());
        wagon.setPreviousWagon(currentWagon);

        if (currentWagon != null) {
            currentWagon.setNextWagon(wagon);
        }

        return wagon;
    }

    @Override
    public String toString() {
        return "[Wagon-" +
                +id + "]";
    }
    // TODO
}
