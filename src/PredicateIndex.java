import java.util.Objects;

public class PredicateIndex {
    public boolean predicateSign;
    public String predicateName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredicateIndex that = (PredicateIndex) o;
        return predicateSign == that.predicateSign &&
                Objects.equals(predicateName, that.predicateName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicateSign, predicateName);
    }

    public PredicateIndex(boolean predicateSign, String predicateName){
        this.predicateSign = predicateSign;
        this.predicateName = predicateName;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if(predicateSign){
            sb.append("True" + " ");
        }else{
            sb.append("False" + " ");
        }
        sb.append(predicateName);
        return sb.toString();
    }
}
