import java.util.ArrayList;
import java.util.Objects;

public class Predicates {
    public boolean predicateSign;
    public String predicateName;
    public ArrayList<Parameter> parameterList;
    public Predicates(boolean predicateSign, String predicateName, ArrayList<Parameter> parameterList){
        this.predicateSign = predicateSign;
        this.predicateName = predicateName;
        this.parameterList = parameterList;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(predicateSign + " ");
        sb.append(predicateName + " ");
        if(parameterList == null || parameterList.isEmpty()){
            sb.append(" ");
            return sb.toString();
        }
        for(int i = 0; i < parameterList.size() - 1; i++){
            sb.append(parameterList.get(i).toString() + ",");
        }
        sb.append(parameterList.get(parameterList.size() - 1).toString() + " ");
        return sb.toString();
    }
    public Predicates negativePredicate(){
        boolean newSign;
        if(predicateSign == true){
            newSign = false;
        }else{
            newSign = true;
        }
        return new Predicates(newSign, predicateName, parameterList);
    }
    public ArrayList<Parameter> getConstantList(){
        ArrayList<Parameter> constant = new ArrayList<>();
        for(Parameter c : parameterList){
            if(c.constant){
                constant.add(c);
            }
        }
        return constant;
    }
    public boolean validCombo(Predicates predicate){
        if(predicate.predicateSign != this.predicateSign || !predicate.predicateName.equals(this.predicateName)){
           return false;
        }
        ArrayList<Parameter> parameter = this.parameterList;
        ArrayList<Parameter> predicateParameter = predicate.parameterList;
        for(int j = 0; j < parameter.size(); j++){
            if(predicateParameter.get(j).constant){
                if(!parameter.get(j).equals(predicateParameter.get(j))){
                    return false;
                }
            }
        }
        return true;
    }
    public boolean validComboConstant(Predicates predicate){
        if(predicate.predicateSign != this.predicateSign || !predicate.predicateName.equals(this.predicateName)){
            return false;
        }
        ArrayList<Parameter> predicateConstant = predicate.getConstantList();
        ArrayList<Parameter> thisConstant = this.getConstantList();
        if(predicateConstant.size() == thisConstant.size()){
            for(int i = 0; i < predicateConstant.size(); i ++){
                if(!predicateConstant.get(i).equals(thisConstant.get(i))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public PredicateIndex getIndex(){
        return new PredicateIndex(predicateSign, predicateName);
    }
    public boolean isEmpty(){
        return predicateName == null;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Predicates that = (Predicates) o;
        return predicateSign == that.predicateSign &&
                Objects.equals(predicateName, that.predicateName) &&
                Objects.equals(parameterList, that.parameterList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicateSign, predicateName, parameterList);
    }
}
