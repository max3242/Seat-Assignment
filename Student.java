import java.util.ArrayList;
public class Student{
  boolean back;
  String name;
  Arraylist<String> next = new Arraylist<String>;
  public Student(String n, boolean b, String...firstLast){
    name=n;
    back=b;
    for(String a: firstLast){
      next.add(firstLast);
    }
  }
  public boolean back(){
    return this.back;
  }
  public String name(){
    return this.name;
  }
  public Arraylist<String> next(){
    return this.next;
  }
}
