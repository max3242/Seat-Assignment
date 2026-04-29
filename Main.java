import java.util.ArrayList;

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

    public String[][] sort() {
        if (backtrack(0)) {
            String[][] result = new String[seats.length][];
            for (int i = 0; i < seats.length; i++) {
                result[i] = new String[seats[i].length];
                for (int j = 0; j < seats[i].length; j++) {
                    result[i][j] = (seats[i][j] == null) ? "EMPTY" : seats[i][j].getName();
                }
            }
            return result;
        }
        return null;
    }

    private boolean backtrack(int index) {
        if (index == list.size()) return true;
        Student s = list.get(index);
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                if (seats[i][j] == null && canPlace(s, i, j)) {
                    seats[i][j] = s;
                    if (backtrack(index + 1)) return true;
                    seats[i][j] = null;
                }
            }
        }
        return false;
    }

    private boolean canPlace(Student s, int row, int col) {
        if (!s.isBack() && row == 0) return false;
        for (String neighborName : s.getNextTo()) {
            if (hasNeighbor(row, col, neighborName)) {
                return true;
            }
        }
        return !hasPlacedPreferredNeighbor(s);
    }

    private boolean hasNeighbor(int row, int col, String name) {
        int[][] dirs = {{0, -1}, {0, 1}}; // left, right only
        for (int[] d : dirs) {
            int r = row;
            int c = col + d[1];
            if (c >= 0 && c < seats[row].length) {
                if (seats[r][c] != null && seats[r][c].getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasPlacedPreferredNeighbor(Student s) {
        for (Student placed : list) {
            if (s.getNextTo().contains(placed.getName())) {
                for (Student[] row : seats) {
                    for (Student seat : row) {
                        if (seat == placed) return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Main m = new Main(10);
        m.list.add(new Student("Alice", true, "Bob"));
        m.list.add(new Student("Bob", true, "Alice"));
        m.list.add(new Student("Charlie", false));
        m.list.add(new Student("David", true));
        String[][] result = m.sort();
        if (result != null) {
            for (String[] row : result) {
                for (String s : row) {
                    System.out.print(s + "\t");
                }
                System.out.println();
            }
        } else {
            System.out.println("No valid seating found.");
        }
    }
}
