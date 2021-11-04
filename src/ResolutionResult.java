import java.util.HashSet;
import java.util.Iterator;

public class ResolutionResult {
    public Literal result;
    public HashSet<Literal> unsolved;
    public ResolutionResult(Literal result, HashSet<Literal> unsolved){
        this.result = result;
        this.unsolved = unsolved;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(result.toString() + "\n");
        Iterator<Literal> iterator = unsolved.iterator();
        while(iterator.hasNext()){
            sb.append(iterator.next().toString());
        }
        return sb.toString();
    }

    public boolean unsolvedPartEmpty(){
        return unsolved.isEmpty();
    }
}
