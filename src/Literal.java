import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Literal {
    public ArrayList<Predicates> predicates;
    public Literal(ArrayList<Predicates> predicates){
        this.predicates = predicates;
    }
    public Literal deepCopy(){
        ArrayList<Predicates> newSentence = new ArrayList<>(predicates);
        return new Literal(newSentence);
    }

    public Literal resolve(Predicates predicate){
        Literal newLiteral = deepCopy();
        HashMap<Parameter, Parameter> mapping = new HashMap<>();
        Predicates resolvable = null;
        ArrayList<Parameter> constantName = predicate.parameterList;
        //1. get variable
        for(Predicates current : newLiteral.predicates){
            if(current.predicateName.equals(predicate.predicateName)){
                boolean solve = true;
                ArrayList<Parameter> variableName = current.parameterList;
                for(int i = 0; i < variableName.size(); i ++){
                    if(variableName.get(i).constant && !variableName.get(i).equals(constantName.get(i))){
                        solve = false;
                        break;
                    }
                }
                if(solve){
                    for(int i = 0; i < variableName.size(); i ++){
                        mapping.put(variableName.get(i), constantName.get(i));
                    }
                }
            }
        }
        //2. assign constant to variable
        ArrayList<Predicates> result = new ArrayList<>();
        for(Predicates current : newLiteral.predicates){
            ArrayList<Parameter> currentParameter = current.parameterList;
            ArrayList<Parameter> newParameter = new ArrayList<>();
            for(Parameter c : currentParameter){
                if(c.variable){
                    Parameter constant = mapping.get(c);
                    if(constant != null){
                        newParameter.add(constant);
                    }else{
                        newParameter.add(c);
                    }
                }else{
                    newParameter.add(c);
                }
            }
            Predicates newPredicate = new Predicates(current.predicateSign, current.predicateName, newParameter);
            result.add(newPredicate);
        }
        //3. delete same negative
        for(Predicates current : result){
            if(current.predicateName.equals(predicate.predicateName) && current.getConstantList().equals(predicate.getConstantList())){
                result.remove(current);
                break;
            }
        }
        return new Literal(result);
    }
    public boolean validCombo(Predicates predicate){
        if(predicates == null || predicates.isEmpty()){
            return false;
        }
        for(int i = 0; i < predicates.size(); i ++){
            if(predicates.get(i).predicateName.equals(predicate.predicateName) && (predicates.get(i).predicateSign != predicate.predicateSign)){
                // get parameterList
                ArrayList<Parameter> parameter = predicate.parameterList;
                ArrayList<Parameter> variable = predicates.get(i).parameterList;
                for(int j = 0; j < parameter.size(); j++){
                    if(variable.get(j).constant){
                        if(!variable.get(j).equals(parameter.get(j))){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Literal literal = (Literal) o;
        return Objects.equals(predicates, literal.predicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicates);
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < predicates.size() - 1; i ++){
            sb.append(predicates.get(i).toString() + "|");
        }
        sb.append(predicates.get(predicates.size() - 1).toString() + ".");
        return sb.toString();
    }
    public boolean isEmpty(){
        return predicates.isEmpty();
    }

}
