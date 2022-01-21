package models;

public class Train {
    public String origin;
    public String destination;
    private Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    public void setEngine(Locomotive engine) {
        this.engine = engine;
    }

    /* three helper methods that are useful in other methods */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    public boolean isPassengerTrain() {
        return this.firstWagon instanceof PassengerWagon;
    }

    public boolean isFreightTrain() {
        return this.firstWagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     */
    public void setFirstWagon(Wagon wagon) {
        firstWagon = wagon;
    }

    /**
     * @return the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        int numberOfWagons = 0;
        Wagon currentWagon = firstWagon;

        if (firstWagon == null) {
            return 0;
        }
// very simple loop that keeps going through the currentwagons next (first being firstwagon) to check if there's a next, which is counted every iteration
        while (currentWagon != null) {
            numberOfWagons++;
            currentWagon = currentWagon.getNextWagon();
        }
        return numberOfWagons;
    }

    /**
     * @return the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        Wagon currentWagon = firstWagon;

        if (firstWagon == null) {
            return null;
        }

        while (currentWagon.hasNextWagon()) {
            currentWagon = currentWagon.getNextWagon();
        }

        return currentWagon;
    }


    /**
     * @return the total number of seats on a passenger train
     * (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        int totalNumberOfSeats = 0;

        if (this.firstWagon instanceof FreightWagon) {
            return 0;
        }
//cast currentwagon as passengerwagon so we can access numberofseats
        PassengerWagon currentWagon = (PassengerWagon) firstWagon;

        if (currentWagon == null) {
            return 0;
        }

        while (currentWagon != null) {
            totalNumberOfSeats += currentWagon.getNumberOfSeats();
            currentWagon = (PassengerWagon) currentWagon.getNextWagon();
        }
        return totalNumberOfSeats;
    }

    /**
     * calculates the total maximum weight of a freight train
     *
     * @return the total maximum weight of a freight train
     * (return 0 for a passenger train)
     */
    public int getTotalMaxWeight() {
        int totalMaxWeight = 0;

        if (this.firstWagon instanceof PassengerWagon) {
            return 0;
        }
//cast freight to access maxweight
        FreightWagon currentWagon = (FreightWagon) firstWagon;

        if (currentWagon == null) {
            return 0;
        }
        while (currentWagon != null) {
            totalMaxWeight += currentWagon.getMaxWeight();
            currentWagon = (FreightWagon) currentWagon.getNextWagon();
        }

        return totalMaxWeight;
    }

    /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     *
     * @param position
     * @return the wagon found at the given position
     * (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        int wagonPosition = 1;
        Wagon currentWagon = null;

        if (!hasWagons()) {
            return currentWagon;
        }

        currentWagon = firstWagon;

        if (position < getNumberOfWagons() || position >= 1) {

            while (currentWagon.hasNextWagon()) {

                if (position == wagonPosition) {
                    return currentWagon;
                } else if (position > getNumberOfWagons() || position < 1) {
                    return null;
                }
                wagonPosition++;
                currentWagon = currentWagon.getNextWagon();
            }
        }
        return currentWagon;
    }

    /**
     * Finds the wagon with a given wagonId
     *
     * @param wagonId
     * @return the wagon found
     * (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon currentWagon = firstWagon;

        if (firstWagon == null) {
            return null;
        }
        for (int i = 0; i < getNumberOfWagons(); i++) {
            if (currentWagon.id == wagonId) {
                return currentWagon;
            } else {
                currentWagon = currentWagon.getNextWagon();
            }
        }
        return null;
    }

    /**
     * Determines if the given sequence of wagons can be attached to the train
     * Verfies of the type of wagons match the type of train (Passenger or Freight)
     * Verfies that the capacity of the engine is sufficient to pull the additional wagons
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return
     */
    public boolean canAttach(Wagon wagon) {

        if (wagon instanceof FreightWagon && getEngine().getMaxWagons() > getNumberOfWagons()) {
            if (hasWagons()) {
                return getFirstWagon() instanceof FreightWagon && ((FreightWagon) wagon).getMaxWeight() < getTotalMaxWeight();
            } else return true;
        }

        if (wagon instanceof PassengerWagon && getEngine().getMaxWagons() > getNumberOfWagons()) {

            if (hasWagons()) {
                return getFirstWagon() instanceof PassengerWagon;
            } else return true;

        }
        return false;
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {

        if (!canAttach(wagon)) {
            return false;
        }

        if (firstWagon == null) {
            wagon.setPreviousWagon(null);
            firstWagon = wagon;
            return true;
        }

        wagon.detachFront();
        Wagon lastWagon = firstWagon;

        while (lastWagon.getNextWagon() != null) {
            lastWagon = lastWagon.getNextWagon();
        }

        lastWagon.setNextWagon(wagon);
        wagon.setPreviousWagon(lastWagon);

        return true;
    }


    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {

        if (!canAttach(wagon)) {
            return false;
        }

        if (firstWagon == null) {
            firstWagon = wagon;
            firstWagon.setPreviousWagon(null);
            return true;
        }
        //check if wagon is already on train and loop through wagons
        Wagon currentWagon = firstWagon;
        while (currentWagon.hasNextWagon()) {
            if (currentWagon.id == wagon.id) {
                return false;
            }
            currentWagon = currentWagon.getNextWagon();
        }

        Wagon tempWagon = wagon;

        while (wagon.hasNextWagon()) {
            wagon = wagon.getNextWagon();
        }

        firstWagon.setPreviousWagon(wagon);
        wagon.setNextWagon(firstWagon);
        firstWagon = tempWagon;
        return true;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given wagon position in the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible of the engine has insufficient capacity
     * or the given position is not valid in this train)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        Wagon currentWagon = firstWagon;

        //check if position isn't out of bound of list and isn't 1
        if (getNumberOfWagons() <= position && position != 1) {
            return false;
        }
// very basic that's almost in every method > is firstwagon null? then given wagon is firstwagon.
        if (firstWagon == null) {
            firstWagon = wagon;
            return true;
        }

        for (int i = 1; i < position; i++) {
            currentWagon = currentWagon.getNextWagon();
        }

        wagon.setPreviousWagon(currentWagon);
        wagon.setNextWagon(currentWagon.getNextWagon());
        currentWagon.getNextWagon().setPreviousWagon(wagon);
        currentWagon.setNextWagon(wagon);
        return true;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param wagonId
     * @param toTrain
     * @return whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        Wagon currentWagon = findWagonById(wagonId);

//check if can attach
        if (!canAttach(currentWagon)) {
            return false;
        }

//check if train before and after
        if (currentWagon.hasNextWagon() && currentWagon.hasPreviousWagon()) {
            Wagon previousWagon = currentWagon.getPreviousWagon();
            Wagon nextWagon = currentWagon.getNextWagon();

            previousWagon.setNextWagon(nextWagon);
            nextWagon.setPreviousWagon(previousWagon);

            currentWagon.setPreviousWagon(null);
            currentWagon.setNextWagon(null);

            //check before but not after
        } else if (currentWagon.hasPreviousWagon() && !currentWagon.hasNextWagon()) {
            Wagon previousWagon = currentWagon.getPreviousWagon();
            currentWagon.setPreviousWagon(null);
            previousWagon.setNextWagon(null);

            //check after but not before
        } else if (currentWagon.hasNextWagon() && !currentWagon.hasPreviousWagon()) {
            Wagon nextWagon = currentWagon.getNextWagon();
            setFirstWagon(nextWagon);
            nextWagon.setPreviousWagon(null);
            currentWagon.setNextWagon(null);
        }

        if (toTrain.getFirstWagon() == null) {
            toTrain.setFirstWagon(currentWagon);
        } else {
            Wagon lastWagon = toTrain.getFirstWagon().getLastWagonAttached();
            lastWagon.setNextWagon(currentWagon);
            currentWagon.setPreviousWagon(lastWagon);
        }
        return true;
    }

    /**
     * Tries to split this train before the given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param position
     * @param toTrain
     * @return whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        Wagon currentWagon = findWagonAtPosition(position);

        if (!toTrain.canAttach(currentWagon) || currentWagon == null) {
            return false;
        }

        if (toTrain.getFirstWagon() != null) {
            return false;

        } else if (currentWagon.getPreviousWagon() == null) {
            setFirstWagon(null);

        } else {
            currentWagon.getPreviousWagon().setNextWagon(null);
        }

        currentWagon.setPreviousWagon(null);
        toTrain.attachToRear(currentWagon);

        return true;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     * the previous wagon of the last wagon becomes the second wagon
     * etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        //Swapping all next and previous nodes.

        if (firstWagon == null) {
            return;
        }

        Wagon currentWagon = firstWagon;
        Wagon temporaryWagon = null;
        currentWagon.setPreviousWagon(null);

        //go through linked nodes till end of list is reached
        //use temp wagon to be able to switch previous and next around current.
        while (currentWagon != null) {
            temporaryWagon = currentWagon.getPreviousWagon();
            currentWagon.setPreviousWagon(currentWagon.getNextWagon());
            currentWagon.setNextWagon(temporaryWagon);
            currentWagon = currentWagon.getPreviousWagon();
        }
        //check in case list empty
        if (temporaryWagon != null) {
            firstWagon = temporaryWagon.getPreviousWagon();
        }
    }

//    public boolean attachToRear(PassengerWagon wagon) {
//
//        if (this.firstWagon instanceof FreightWagon) {
//
//            return false;
//        }
//
//        PassengerWagon lastWagon = (PassengerWagon) firstWagon;
//        wagon.setNextWagon(null);
//
//        if (lastWagon == null) {
//            wagon.setPreviousWagon(null);
//            firstWagon = wagon;
//            return true;
//        }
//
//        while (lastWagon.getNextWagon() != null) {
//            lastWagon = (PassengerWagon) lastWagon.getNextWagon();
//        }
//
//        lastWagon.setNextWagon(wagon);
//        wagon.setPreviousWagon(lastWagon);
//        return true;
//    }
//
//    public boolean attachToRear(FreightWagon wagon) {
//
//        System.out.println(wagon.id);
//        if (this.firstWagon instanceof PassengerWagon) {
//            return false;
//        }
//
//        FreightWagon lastWagon = (FreightWagon) firstWagon;
//        wagon.setNextWagon(null);
//
//        if (lastWagon == null) {
//            wagon.setPreviousWagon(null);
//            firstWagon = wagon;
//            return true;
//        }
//
//        while (lastWagon.getNextWagon() != null) {
//            lastWagon = (FreightWagon) lastWagon.getNextWagon();
//        }
//
//        lastWagon.setNextWagon(wagon);
//        wagon.setPreviousWagon(lastWagon);
//        return true;
//    }
//
//
//    public boolean insertAtFront(FreightWagon wagon) {
//
//        if (isPassengerTrain()) {
//            return false;
//        }
//
//        if (firstWagon != null) {
//            firstWagon.setPreviousWagon(wagon);
//            wagon.setNextWagon(firstWagon);
//        }
//        firstWagon = wagon;
//        return true;
//    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(engine.toString());
        Wagon currentWagon = getFirstWagon();
        while (currentWagon != null) {
            result.append(currentWagon.toString());
            currentWagon = currentWagon.getNextWagon();
        }
        result.append(String.format(" with %d wagons and %d seats from %s to %s", getNumberOfWagons(),
                getTotalNumberOfSeats(), getOrigin(), getDestination()));
        return result.toString();
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }
}

