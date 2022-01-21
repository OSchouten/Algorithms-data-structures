package nl.hva.ict.ads;

public class Archer {
    public static int MAX_ARROWS = 3;
    public static int MAX_ROUNDS = 10;

    private static int tracker = 135788;
    //ID is final so it can't be changed but because I haven't declared it I can assign it in the constructor and this will ensure it can't be changed later.
    private final int id;
    private String firstName;
    private String lastName;
    //using a 2d array to store all the information per round per arrow.
    int[][] score3DArray;


    /**
     * Constructs a new instance of Archer and assigns a unique id to the instance.
     * Each new instance should be assigned a number that is 1 higher than the last one assigned.
     * The first instance created should have ID 135788;
     *
     * @param firstName the archers first name.
     * @param lastName  the archers surname.
     */
    public Archer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = getId() + tracker++;
        score3DArray = new int[11][3];
    }


    /**
     * Registers the points for each of the three arrows that have been shot during a round.
     *
     * @param round  the round for which to register the points. First round has number 1.
     * @param points the points shot during the round, one for each arrow.
     */
    public void registerScoreForRound(int round, int[] points) {
        System.arraycopy(points, 0, score3DArray[round], 0, 3);
    }


    /**
     * Calculates/retrieves the total score of all arrows across all rounds
     *
     * @return totalScore of competitor
     */
    public int getTotalScore() {
        int totalScore = 0;
        for (int[] ints : score3DArray) {
            for (int j = 0; j < score3DArray[1].length; j++) {
                totalScore += ints[j];
            }
        }
        return totalScore;
    }

    /**
     * compares the scores/id of this archer with the scores/id of the other archer according to
     * the scoring scheme: highest total points -> least misses -> earliest registration
     * The archer with the lowest id has registered first
     *
     * @param other the other archer to compare against
     * @return negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestTotalScoreWithLeastMissesAndLowestId(Archer other) {
        //check whose score is highest
        if (this.getTotalScore() > other.getTotalScore())
            return -1;
        else if (this.getTotalScore() < other.getTotalScore())
            return 1;

        //check who has the most misses
        if (calculateMisses(this.score3DArray) > calculateMisses(other.score3DArray))
            return 1;
        else if (calculateMisses(this.score3DArray) < calculateMisses(other.score3DArray))
            return -1;

        //if all else fails compare id
        return Integer.compare(this.getId(), other.getId());
    }

    /**
     * loop through 2dimensional array to find all 0 scores return count of misses
     * trackvariable + j should always equal points.lenght.
     * @return total misses of competitor
     */
    public int calculateMisses(int[][] points) {
        int totalMisses = 0;

        //2D array double loop
        int trackVariable = points.length;

        for (int[] ints : points) {
            for (int j = 0; j < points[1].length; j++) {
                if (ints[j] == 0)
                    totalMisses++;
                trackVariable--;
            }
        }
        return totalMisses;
    }


    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // TODO provide a toSting implementation to format archers nicely

    @Override
    public String toString() {
        return
                id + " (" + getTotalScore() + ") " +
                        firstName + ' ' +
                        lastName;
    }
}
