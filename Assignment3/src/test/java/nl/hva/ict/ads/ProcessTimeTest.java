package nl.hva.ict.ads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.System.nanoTime;

public class ProcessTimeTest {
    //20 seconds / 20000ms
    private final double ALLOWED_TIME = 20000;
    // Any amount of  competitors sufficient to test sorting time
    private final int TEST_SUBJECTS = 5000000;
    protected ChampionSelector championSelector;
    protected Sorter<Archer> sorter = new ArcherSorter();
    protected List<Archer> testArchers;
    protected Comparator<Archer> scoringScheme = Archer::compareByHighestTotalScoreWithLeastMissesAndLowestId;

    @BeforeEach
    void setup() {
        championSelector = new ChampionSelector(1L);
        testArchers = new ArrayList<>(championSelector.enrollArchers(TEST_SUBJECTS));
    }

    @Test
    void AllSortsTest() {
        List<Archer> testArchersCopy;
        double totalTestTime;
        int totalCompetitors = 100;
//        do {
//            testArchersCopy = testArchers.subList(0, totalCompetitors);
//            totalTestTime = quickSortTime(totalCompetitors, testArchersCopy);
//            totalCompetitors = totalCompetitors * 2;
//        } while (totalTestTime < ALLOWED_TIME && totalCompetitors <= TEST_SUBJECTS);
//        totalCompetitors = 100;
//        do {
//            testArchersCopy = testArchers.subList(0, totalCompetitors);
//            totalTestTime = listSortTime(totalCompetitors, testArchersCopy);
//            totalCompetitors = totalCompetitors * 2;
//        } while (totalTestTime < ALLOWED_TIME && totalCompetitors <= TEST_SUBJECTS);
//        totalCompetitors = 100;
        do {
            testArchersCopy = testArchers.subList(0, totalCompetitors);
            totalTestTime = topsHeapSortTime(totalCompetitors, testArchersCopy);
            totalCompetitors = totalCompetitors * 2;
        } while (totalTestTime < ALLOWED_TIME && totalCompetitors <= TEST_SUBJECTS);
//        totalCompetitors = 100;
//        do {
//            testArchersCopy = testArchers.subList(0, totalCompetitors);
//            totalTestTime = selInsSortTime(totalCompetitors, testArchersCopy);
//            totalCompetitors = totalCompetitors * 2;
//        } while (totalTestTime < ALLOWED_TIME && totalCompetitors <= TEST_SUBJECTS);
    }

    //This test looks at the sorting time of Quick Sort
    //Some of the results don't actually register a time because of the fact that java can't really register nanoseconds and I also have a fast computer (I think?)
    //I hope I didn't miss anything but I couldn't really find any ways of registering sort time on really low listsizes

    public double quickSortTime(int totalCompetitors, List<Archer> testArchersCopy) {

        double totalTestTime = 0;

        //Bit of cleaning before the sort
        System.gc();
        //store start time before sort
        double startTestTimer = nanoTime();
        // sort list
        sorter.quickSort(testArchersCopy, scoringScheme);
        //store time after sorting
        double endTestTimer = nanoTime();

        //subtract start time from end time to see how much time it took to sort (yes this could be done in the endTestTimer variables but I wanted to separate it
        totalTestTime = ((endTestTimer - startTestTimer) * 1E-6);
        System.out.println("With a total of: " + totalCompetitors + " competitors, it took the quick sorting algorithm this long: " + totalTestTime);
        return totalTestTime;
    }


    public double selInsSortTime(int totalCompetitors, List<Archer> testArchersCopy) {
        double totalTestTime = 0;

//Bit of cleaning before the sort
        System.gc();
        //store start time before sort
        double startTestTimer = nanoTime();
        // sort list
        sorter.selInsSort(testArchersCopy, scoringScheme);
        //store time after sorting
        double endTestTimer = nanoTime();

        //subtract start time from end time to see how much time it took to sort (yes this could be done in the endTestTimer variables but I wanted to separate it
        totalTestTime = ((endTestTimer - startTestTimer) * 1E-6);
        System.out.println("With a total of: " + totalCompetitors + " competitors, it took the selIns sorting algorithm this long: " + totalTestTime + "ms");
        return totalTestTime;
    }


    public double topsHeapSortTime(int totalCompetitors, List<Archer> testArchersCopy) {
        double totalTestTime = 0;

//Bit of cleaning before the sort
        System.gc();
        //store start time before sort
        double startTestTimer = nanoTime();
        // sort list
        sorter.topsHeapSort(totalCompetitors, testArchersCopy, scoringScheme);
        //store time after sorting
        double endTestTimer = nanoTime();

        //subtract start time from end time to see how much time it took to sort (yes this could be done in the endTestTimer variables but I wanted to separate it
        totalTestTime = ((endTestTimer - startTestTimer) * 1E-6);
        System.out.println("With a total of: " + totalCompetitors + " competitors, it took the top heap sorting algorithm this long: " + totalTestTime + "ms");
        return totalTestTime;
    }


    public double listSortTime(int totalCompetitors, List<Archer> testArchersCopy) {
        double totalTestTime = 0;

//Bit of cleaning before the sort
        System.gc();
        //store start time before sort
        double startTestTimer = nanoTime();
        // sort list
        testArchersCopy.sort(scoringScheme);
        //store time after sorting
        double endTestTimer = nanoTime();

        //subtract start time from end time to see how much time it took to sort (yes this could be done in the endTestTimer variables but I wanted to separate it
        totalTestTime = ((endTestTimer - startTestTimer) * 1E-6);
        System.out.println("With a total of: " + totalCompetitors + " competitors, it took the list sorting algorithm this long: " + totalTestTime + "ms");
        return totalTestTime;
    }

}
