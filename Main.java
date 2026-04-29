import java.util.*;

class Student {
    private boolean back;
    private String name;
    private ArrayList<String> nextTo = new ArrayList<>();

    public Student(String name, boolean back, String... prefs) {
        this.name = name;
        this.back = back;
        Collections.addAll(nextTo, prefs);
    }

    public boolean isBack() { return back; }
    public String getName() { return name; }
    public ArrayList<String> getNextTo() { return nextTo; }
}

public class Main {

    ArrayList<Student> list = new ArrayList<>();
    Student[][] seats;
    Random rand = new Random();

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

        int bestScore = Integer.MIN_VALUE;
        Student[][] bestLayout = null;

        // 🔁 MULTIPLE RANDOM RESTARTS (important)
        for (int i = 0; i < 25; i++) {

            randomFill();
            optimize();

            int score = totalScore();

            if (score > bestScore) {
                bestScore = score;
                bestLayout = copySeats();
            }
        }

        seats = bestLayout;
        return toGrid();
    }

    // =========================
    // INITIAL FILL (RESPECT HARD CONSTRAINT)
    // =========================
    private void randomFill() {

        ArrayList<Student> frontAllowed = new ArrayList<>();
        ArrayList<Student> backOnly = new ArrayList<>();

        for (Student s : list) {
            if (s.isBack()) frontAllowed.add(s);
            else backOnly.add(s);
        }

        Collections.shuffle(frontAllowed);
        Collections.shuffle(backOnly);

        int f = 0, b = 0;

        for (int r = 0; r < seats.length; r++) {
            for (int c = 0; c < seats[r].length; c++) {

                if (r == 0) {
                    if (f < frontAllowed.size()) {
                        seats[r][c] = frontAllowed.get(f++);
                    }
                } else {
                    if (f < frontAllowed.size()) {
                        seats[r][c] = frontAllowed.get(f++);
                    } else if (b < backOnly.size()) {
                        seats[r][c] = backOnly.get(b++);
                    }
                }
            }
        }
    }

    // =========================
    // SIMULATED ANNEALING
    // =========================
    private void optimize() {

        double temp = 1000;
        double cooling = 0.997;

        int currentScore = totalScore();

        while (temp > 0.1) {

            int r1 = rand.nextInt(seats.length);
            int c1 = rand.nextInt(seats[r1].length);

            int r2 = rand.nextInt(seats.length);
            int c2 = rand.nextInt(seats[r2].length);

            Student s1 = seats[r1][c1];
            Student s2 = seats[r2][c2];

            if (s1 == null || s2 == null) continue;

            // 🚫 HARD CONSTRAINT
            if ((!s1.isBack() && r2 == 0) || (!s2.isBack() && r1 == 0)) {
                continue;
            }

            swap(r1, c1, r2, c2);

            int newScore = totalScore();
            int delta = newScore - currentScore;

            if (delta > 0 || Math.exp(delta / temp) > rand.nextDouble()) {
                currentScore = newScore;
            } else {
                swap(r1, c1, r2, c2);
            }

            temp *= cooling;
        }
    }

    // =========================
    // TOTAL SCORE
    // =========================
    private int totalScore() {

        int score = 0;

        for (int r = 0; r < seats.length; r++) {
            for (int c = 0; c < seats[r].length; c++) {
                score += score(r, c);
            }
        }

        return score;
    }

    // =========================
    // SCORING (PREFERENCE HEAVY)
    // =========================
    private int score(int r, int c) {

        Student s = seats[r][c];
        if (s == null) return 0;

        int score = 0;

        for (String p : s.getNextTo()) {
            if (hasNeighbor(r, c, p)) {
                score += 100;
            }
        }

        return score;
    }

    // =========================
    // CHECK NEIGHBORS (4-DIRECTIONAL = MUCH BETTER)
    // =========================
    private boolean hasNeighbor(int r, int c, String name) {

        int[][] dirs = {
            {0,-1},{0,1},{-1,0},{1,0}
        };

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];

            if (nr >= 0 && nr < seats.length &&
                nc >= 0 && nc < seats[nr].length) {

                Student s = seats[nr][nc];
                if (s != null && s.getName().equals(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void swap(int r1, int c1, int r2, int c2) {
        Student t = seats[r1][c1];
        seats[r1][c1] = seats[r2][c2];
        seats[r2][c2] = t;
    }

    private Student[][] copySeats() {
        Student[][] copy = new Student[seats.length][];
        for (int i = 0; i < seats.length; i++) {
            copy[i] = Arrays.copyOf(seats[i], seats[i].length);
        }
        return copy;
    }

    // =========================
    // OUTPUT
    // =========================
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

        m.list.add(new Student("Ethan Brooks", true, "Lucas Reed", "Mason Turner"));
        m.list.add(new Student("Lucas Reed", false, "Ethan Brooks"));
        m.list.add(new Student("Mason Turner", true, "Ethan Brooks"));

        m.list.add(new Student("Aiden Scott", true, "Logan Price"));
        m.list.add(new Student("Logan Price", false, "Aiden Scott", "Noah Hayes"));
        m.list.add(new Student("Noah Hayes", true, "Logan Price"));

        m.list.add(new Student("Elijah Ward", true, "James Fox"));
        m.list.add(new Student("James Fox", false, "Elijah Ward"));

        m.list.add(new Student("Benjamin Cole", true, "Henry Bell"));
        m.list.add(new Student("Henry Bell", false, "Benjamin Cole", "Jack Murphy"));
        m.list.add(new Student("Jack Murphy", true, "Henry Bell"));

        m.list.add(new Student("Owen Bailey", true, "Samuel Cox"));
        m.list.add(new Student("Samuel Cox", false, "Owen Bailey"));

        m.list.add(new Student("Wyatt Rivera", true, "Daniel Morris"));
        m.list.add(new Student("Daniel Morris", false, "Wyatt Rivera"));

        m.list.add(new Student("Gabriel Rogers", true, "Carter Cook", "Julian Morgan"));
        m.list.add(new Student("Carter Cook", false, "Gabriel Rogers"));
        m.list.add(new Student("Julian Morgan", true, "Gabriel Rogers"));

        m.list.add(new Student("Isaac Peterson", true));
        m.list.add(new Student("Levi Gray", false));
        m.list.add(new Student("Anthony Russell", true));
        m.list.add(new Student("Dylan Griffin", false));

        m.list.add(new Student("Christopher Diaz", true, "Andrew Hayes"));
        m.list.add(new Student("Andrew Hayes", false, "Christopher Diaz"));

        m.list.add(new Student("Joshua Bennett", true, "Nathan Sanders"));
        m.list.add(new Student("Nathan Sanders", false, "Joshua Bennett"));

        m.list.add(new Student("Ryan Powell", true));
        m.list.add(new Student("Adrian Long", false));

        m.list.add(new Student("Easton Patterson", true, "Charles Hughes"));
        m.list.add(new Student("Charles Hughes", false, "Easton Patterson"));

        m.list.add(new Student("Aaron Flores", true));
        m.list.add(new Student("Thomas Butler", false));
        
        String[][] result = m.sort();

        for (String[] row : result) {
            for (String s : row) System.out.print(s + "\t");
            System.out.println();
        }
    }
}