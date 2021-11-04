import java.util.Objects;

public class Parameter {
    String name;
    boolean constant;
    boolean variable;
    public Parameter(String name){
        this.name = name;
        if(name.charAt(0) >= 'a' && name.charAt(0) <= 'z'){
            constant = false;
            variable = true;
        }else{
            constant = true;
            variable = false;
        }
    }
    public String toString(){
        return name;
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, constant, variable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return Objects.equals(name, parameter.name);
    }
}
