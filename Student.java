import java.util.ArrayList;
public class Student {
    boolean back;
    String name;
    ArrayList<String> nextTo = new ArrayList<>();
    public Student(String n, boolean b, String... firstLast) {
        name = n;
        back = b;
        for (String a : firstLast) {
            nextTo.add(a);
        }
    }
    public boolean isBack() {
        return back;
    }
    public String getName() {
        return name;
    }
    public ArrayList<String> getNextTo() {
        return nextTo;
    }
}
