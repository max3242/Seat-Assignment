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
        seats[4] = new Student[12-(32-numStu)];
    }
    public String[][] sort() {
        int bestScore = Integer.MIN_VALUE;
        Student[][] bestLayout = null;
        for (int i = 0; i < 25; i++) {
            randomFill();
            optimize();
            enforceFrontRowRule();
            int score = totalScore();
            if (score > bestScore) {
                bestScore = score;
                bestLayout = copySeats();
            }
        }
        seats = bestLayout;
        return toGrid();
    }
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
            if (s1 == null || s2 == null) 
                continue;
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
    private void enforceFrontRowRule() {
        ArrayList<Student> displaced = new ArrayList<>();
        for (int c = 0; c < seats[0].length; c++) {
            Student s = seats[0][c];
            if (s != null && !s.isBack()) {
                displaced.add(s);
                seats[0][c] = null;
            }
        }
        for (int r = 1; r < seats.length && !displaced.isEmpty(); r++) {
            for (int c = 0; c < seats[r].length && !displaced.isEmpty(); c++) {
                if (seats[r][c] != null) 
                    continue;
                seats[r][c] = displaced.remove(0);
            }
        }
        for (Student s : displaced) {
            placeAnywhere(s);
        }
    }
    private void placeAnywhere(Student s) {
        for (int r = 1; r < seats.length; r++) {
            for (int c = 0; c < seats[r].length; c++) {
                if (seats[r][c] == null) {
                    seats[r][c] = s;
                    return;
                }
            }
        }
    }
    private int totalScore() {
        int score = 0;
        for (int r = 0; r < seats.length; r++) {
            for (int c = 0; c < seats[r].length; c++) {
                score += score(r, c);
            }
        }
        return score;
    }
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
    private boolean hasNeighbor(int r, int c, String name) {
        int[] dc = {-1, 1};
        for (int d : dc) {
            int nc = c + d;
            if (nc >= 0 && nc < seats[r].length) {
                Student s = seats[r][nc];
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
    private void finalSense(){
        int a=1;
        for (int r = 0; r < seats.length; r++) {
            for (int c = 0; c < seats[r].length; c++){
                System.out.println(a + " " + seats[r][c].getName());
                a++;
            }
        }
    }
    public static void main(String[] args) {
        Main m = new Main(31);

        //Add students here with m.list.add(new Student(name, back, p1, p2...)).
        //Name is a string with the person's full name (first last).
        //Back is a boolean. If they can't sit in the front row, back is false, if they can, it should be true.
        //P or preference is people they would like to sit next to, listed with the same name they have attributed
        //to their student object and any number of preferences can be listed but they aren't guaranteed
        //and the algorithm only takes into account the people immediately next to them.

        String[][] result = m.sort();
        for (String[] row : result){
            for (String s : row) System.out.print(s + "\t");
            System.out.println();
        }
        System.out.println();
        m.finalSense();
    }
}