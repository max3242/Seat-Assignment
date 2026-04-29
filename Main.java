import java.util.ArrayList;
import java.util.Collections;

class Student {
    private boolean back;
    private String name;
    private ArrayList<String> nextTo = new ArrayList<>();

    public Student(String name, boolean back, String... prefs) {
        this.name = name;
        this.back = back;
        for (String p : prefs) nextTo.add(p);
    }

    public boolean isBack() { return back; }
    public String getName() { return name; }
    public ArrayList<String> getNextTo() { return nextTo; }
}

public class Main {

    ArrayList<Student> list = new ArrayList<>();
    Student[][] seats;

    public Main(int numStu) {
        seats = new Student[5][];
        seats[0] = new Student[8];
        seats[1] = new Student[4];
        seats[2] = new Student[4];
        seats[3] = new Student[4];
        seats[4] = new Student[36 - numStu];
    }

    // =========================
    // MAIN ENTRY
    // =========================
    public String[][] sort() {

        fillAnyValid();
        optimize();

        return toGrid();
    }

    // =========================
    // STEP 1: SAFE FILL (NO DEADLOCK POSSIBLE)
    // =========================
    private void fillAnyValid() {

        ArrayList<Student> remaining = new ArrayList<>(list);

        for (int r = 0; r < seats.length; r++) {
            for (int c = 0; c < seats[r].length; c++) {

                if (remaining.isEmpty()) return;

                Student best = remaining.remove(0);

                // ensure back constraint
                if (!best.isBack() && r == 0) {
                    remaining.add(best);
                    continue;
                }

                seats[r][c] = best;
            }
        }
    }

    // =========================
    // STEP 2: GLOBAL OPTIMIZATION
    // =========================
    private void optimize() {

        int iterations = 0;
        int MAX = 8000;

        while (iterations++ < MAX) {

            boolean improved = false;

            for (int r1 = 0; r1 < seats.length; r1++) {
                for (int c1 = 0; c1 < seats[r1].length; c1++) {
                    for (int r2 = 0; r2 < seats.length; r2++) {
                        for (int c2 = 0; c2 < seats[r2].length; c2++) {

                            if (seats[r1][c1] == null || seats[r2][c2] == null)
                                continue;

                            int before = score(r1, c1) + score(r2, c2);

                            swap(r1, c1, r2, c2);

                            int after = score(r1, c1) + score(r2, c2);

                            if (after < before) {
                                swap(r1, c1, r2, c2);
                            } else {
                                improved = true;
                            }
                        }
                    }
                }
            }

            if (!improved) break;
        }
    }

    // =========================
    // SCORING
    // =========================
    private int score(int r, int c) {

        Student s = seats[r][c];
        if (s == null) return 0;

        int score = 0;

        for (String p : s.getNextTo()) {
            if (hasNeighbor(r, c, p)) {
                score += 50;
            }
        }

        return score;
    }

    // =========================
    // HELPERS
    // =========================
    private boolean hasNeighbor(int r, int c, String name) {

        int[][] dirs = {{0,-1},{0,1}};

        for (int[] d : dirs) {
            int nc = c + d[1];

            if (nc >= 0 && nc < seats[r].length) {
                Student s = seats[r][nc];
                if (s != null && s.getName().equals(name)) return true;
            }
        }

        return false;
    }

    private void swap(int r1, int c1, int r2, int c2) {
        Student t = seats[r1][c1];
        seats[r1][c1] = seats[r2][c2];
        seats[r2][c2] = t;
    }

    private String[][] toGrid() {

        String[][] out = new String[seats.length][];

        for (int i = 0; i < seats.length; i++) {
            out[i] = new String[seats[i].length];
            for (int j = 0; j < seats[i].length; j++) {
                out[i][j] = seats[i][j] == null ? "EMPTY" : seats[i][j].getName();
            }
        }

        return out;
    }

    // =========================
    // MAIN METHOD
    // =========================
    public static void main(String[] args) {

        Main m = new Main(32);

        m.list.add(new Student("Liam Carter", true, "Noah Bennett", "Emma Collins"));
        m.list.add(new Student("Noah Bennett", false, "Liam Carter"));
        m.list.add(new Student("Emma Collins", true, "Olivia Brooks"));
        m.list.add(new Student("Olivia Brooks", false, "Emma Collins"));
        m.list.add(new Student("Ava Richardson", true));

        m.list.add(new Student("Sophia Hayes", true));
        m.list.add(new Student("Isabella Foster", false));
        m.list.add(new Student("Mia Simmons", true));
        m.list.add(new Student("Charlotte Price", false));
        m.list.add(new Student("Amelia Ward", true));

        m.list.add(new Student("Harper Cox", false));
        m.list.add(new Student("Evelyn Diaz", true));
        m.list.add(new Student("Abigail Ramirez", false));
        m.list.add(new Student("Emily Peterson", true));
        m.list.add(new Student("Ella Gray", false));

        m.list.add(new Student("James Watson", true));
        m.list.add(new Student("Benjamin Sanders", false));
        m.list.add(new Student("Lucas Powell", true));
        m.list.add(new Student("Henry Long", false));
        m.list.add(new Student("Alexander Patterson", true));

        m.list.add(new Student("Michael Hughes", true));
        m.list.add(new Student("Daniel Flores", false));
        m.list.add(new Student("Matthew Butler", true));
        m.list.add(new Student("Joseph Simmons", false));
        m.list.add(new Student("Samuel Bryant", true));

        m.list.add(new Student("David Griffin", false));
        m.list.add(new Student("Andrew Russell", true));
        m.list.add(new Student("Carter Hayes", true));
        m.list.add(new Student("Wyatt Jenkins", false));
        m.list.add(new Student("John Coleman", true));

        m.list.add(new Student("Luke Henderson", false));

        String[][] result = m.sort();

        for (String[] row : result) {
            for (String s : row) System.out.print(s + "\t");
            System.out.println();
        }
    }
}