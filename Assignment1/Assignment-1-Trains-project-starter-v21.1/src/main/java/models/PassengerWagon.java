package models;

public class PassengerWagon extends Wagon {

    private int numberofSeats;

    public PassengerWagon(int wagonId, int numberofSeats) {
        super(wagonId);
        this.numberofSeats = numberofSeats;
    }

    public int getNumberOfSeats() {

        return numberofSeats;
    }

    public void setNumberofSeats(int numberofSeats) {
        this.numberofSeats = numberofSeats;
    }
}
