import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class Change {
    public static void main(String[] args) throws FileNotFoundException {
        // read from File + negate Query
        File InputFile = new File("input.txt");
        Scanner inputReader = new Scanner(InputFile);
        int numQueries = inputReader.nextInt();
        inputReader.nextLine();
        String[] negatedQueryList = new String[numQueries];
        for (int i = 0; i < numQueries; i++) {
            negatedQueryList[i] = negateQuery(inputReader.nextLine());
        }
        int numSentences = inputReader.nextInt();
        inputReader.nextLine();
        String[] sentenceArray = new String[numSentences];
        for (int i = 0; i < numSentences; i++) {
            sentenceArray[i] = inputReader.nextLine();
        }
        // KB without => &
        HashSet<String> KB = unifyKB(sentenceArray);
        // KB into predicate and Literal
        HashSet<Literal> standKB = standardizeKB(KB);
        HashMap<Predicates, ArrayList<Literal>> indexKB = generateIndexKB(standKB);
        HashMap<PredicateIndex, ArrayList<Predicates>> preProcessKB = generatepreProcessKB(standKB);
        output(preProcessKB, indexKB, negatedQueryList);
    }

    // negate Query
    public static String negateQuery(String query) {
        String negatedQuery = "";
        if (query.charAt(0) == '~') {
            negatedQuery = query.substring(1);
        } else {
            negatedQuery = '~' + query;
        }
        return negatedQuery;
    }

    // first step change KB sentence into String negative & and no more => and &
    public static HashSet<String> unifyKB(String[] sentenceArray) {
        HashSet<String> KB = new HashSet<>();
        for (String sentence : sentenceArray) {
            String[] currentSentence = sentence.split(" ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currentSentence.length - 1; i++) {
                // predicate
                if (currentSentence[i].charAt(0) == '~' || (currentSentence[i].charAt(0) >= 'A' && currentSentence[i].charAt(0) <= 'Z')) {
                    sb.append(negateQuery(currentSentence[i]));
                } else if (currentSentence[i].equals("=>")) {
                    sb.append("|");
                } else if (currentSentence[i].equals("&")) {
                    sb.append("|");
                }
            }
            sb.append(currentSentence[currentSentence.length - 1]);
            KB.add(sb.toString());
        }
        return KB;
    }

    public static HashSet<Literal> standardizeKB(HashSet<String> KB) {
        HashSet<Literal> standKB = new HashSet<>();
        Iterator iterator = KB.iterator();
        while (iterator.hasNext()) {
            String current = (String) iterator.next();
            ArrayList<Predicates> predicates = new ArrayList<>();
            String[] currentLiteral = current.split("\\|");
            for (int i = 0; i < currentLiteral.length; i++) {
                Predicates currentPredicate = standardize(currentLiteral[i]);
                predicates.add(currentPredicate);
            }
            standKB.add(new Literal(predicates));
        }
        return standKB;
    }

    public static Predicates standardize(String sentence) {
        boolean predicateSign;
        String predicateName;
        ArrayList<Parameter> parameterList = new ArrayList<>();
        String[] query = sentence.split("\\(");
        if (query[0].charAt(0) == '~') {
            predicateSign = false;
            predicateName = query[0].substring(1);
        } else {
            predicateSign = true;
            predicateName = query[0];
        }
        String[] currentParameter = query[1].split("[,\\)]+");
        for (String parameter : currentParameter) {
            parameterList.add(new Parameter(parameter));
        }
        Predicates currentPredicate = new Predicates(predicateSign, predicateName, parameterList);
        return currentPredicate;
    }

    public static HashMap<Predicates, ArrayList<Literal>> generateIndexKB(HashSet<Literal> standKB) {
        HashMap<Predicates, ArrayList<Literal>> indexKB = new HashMap<>();
        Iterator<Literal> iterator = standKB.iterator();
        while (iterator.hasNext()) {
            Literal currentLiteral = iterator.next();
            for (Predicates predicate : currentLiteral.predicates) {
                if (!indexKB.containsKey(predicate)) {
                    indexKB.put(predicate, new ArrayList<Literal>(Arrays.asList(currentLiteral)));
                } else {
                    ArrayList<Literal> currentResult = indexKB.get(predicate);
                    currentResult.add(currentLiteral);
                    indexKB.put(predicate, currentResult);
                }
            }
        }
        return indexKB;
    }

    public static HashMap<PredicateIndex, ArrayList<Predicates>> generatepreProcessKB(HashSet<Literal> standKB) {
        HashMap<PredicateIndex, ArrayList<Predicates>> preProcess = new HashMap<>();
        Iterator<Literal> iterator = standKB.iterator();
        while (iterator.hasNext()) {
            Literal currentLiteral = iterator.next();
            for (Predicates predicate : currentLiteral.predicates) {
                PredicateIndex indexPredicate = new PredicateIndex(predicate.predicateSign, predicate.predicateName);
                if (!preProcess.containsKey(indexPredicate)) {
                    preProcess.put(indexPredicate, new ArrayList<>(Arrays.asList(predicate)));
                } else {
                    ArrayList<Predicates> predicates = preProcess.get(indexPredicate);
                    predicates.add(predicate);
                    preProcess.put(indexPredicate, predicates);
                }
            }
        }
        return preProcess;
    }

    public static ArrayList<Predicates> getIndexPredicate(HashMap<PredicateIndex, ArrayList<Predicates>> preProcessKB, Predicates currentPredicate) {
        ArrayList<Predicates> result = new ArrayList<>();
        PredicateIndex index = currentPredicate.getIndex();
        ArrayList<Predicates> list = preProcessKB.get(index);
        if (list == null || list.size() == 0) {
            return null;
        }
        for (Predicates p : list) {
            if (currentPredicate.validCombo(p)) {
                result.add(p);
            }
        }
        return result;
    }

    public static void output(HashMap<PredicateIndex, ArrayList<Predicates>> preProcessKB, HashMap<Predicates, ArrayList<Literal>> indexKB, String[] negatedQueryList) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream("output.txt");
        PrintWriter pw = new PrintWriter(fos);
        for(String cQuery : negatedQueryList){
            HashSet<Literal> unsolved = new HashSet<>();
            Predicates predicate = standardize(cQuery);
            Literal currentQuery = new Literal(new ArrayList<>(Arrays.asList(predicate)));
            ArrayList<ResolutionResult> currentResolution = new ArrayList<>(Arrays.asList(new ResolutionResult(currentQuery, unsolved)));
            boolean result = resolution(preProcessKB, indexKB, currentResolution);
            if(result){
                pw.println("TRUE");
            }else{
                pw.println("FALSE");
            }
        }
        pw.close();
    }

    public static boolean resolution(HashMap<PredicateIndex, ArrayList<Predicates>> preProcessKB, HashMap<Predicates, ArrayList<Literal>> indexKB, ArrayList<ResolutionResult> currentQuery){
        ArrayList<Boolean> answerList = new ArrayList<>();
        HashSet<Literal> resolved = new HashSet<>();
        Resolution(resolved, answerList, preProcessKB, currentQuery,indexKB);
        Boolean result = false;
        for(Boolean answer :  answerList){
            if(answer){
                result = true;
                return result;
            }
        }
        return result;
    }

    public static void Resolution(HashSet<Literal> resolved, ArrayList<Boolean> answer, HashMap<PredicateIndex, ArrayList<Predicates>> preProcessKB, ArrayList<ResolutionResult> currentQuery, HashMap<Predicates, ArrayList<Literal>> indexKB){
        // base case
        // case 1 : one Literal is empty and unsolved is empty then return true
        for(ResolutionResult currentResult : currentQuery){
            if(currentResult.result.isEmpty() && currentResult.unsolvedPartEmpty()){
                answer.add(true);
                return;
            }
        }
        // case 2 : all unsolved part is not empty, return false
        ArrayList<Literal> nextTurn = new ArrayList<>();
        for(ResolutionResult currentResult : currentQuery){
            if(!currentResult.result.isEmpty()&& currentResult.unsolvedPartEmpty()){
                nextTurn.add(currentResult.result);
            }
        }
        if(nextTurn.isEmpty()){
            answer.add(false);
            return;
        }
//        ArrayList<Boolean> answerList = new ArrayList<>();
        for(Literal currentLiteral : nextTurn){
            ArrayList<ResolutionResult> result = new ArrayList<>();
            ArrayList<Literal> current = new ArrayList<>();
            HashSet<Literal> unsolved = new HashSet<>();
            if(resolved.contains(currentLiteral)){
                answer.add(false);
                return;
            }else{
                resolved.add(currentLiteral);
            }
            singleResolution(result, current, 0, preProcessKB, unsolved, currentLiteral, indexKB);
            Resolution(resolved,answer, preProcessKB, result, indexKB);
            resolved.remove(currentLiteral);
        }
    }

    public static void singleResolution(ArrayList<ResolutionResult> result, ArrayList<Literal> current, int index, HashMap<PredicateIndex, ArrayList<Predicates>> preProcessKB, HashSet<Literal> unsolved, Literal currentQuery, HashMap<Predicates, ArrayList<Literal>> indexKB) {
        // 最后一层
        if (index == currentQuery.predicates.size()) {
            Literal currentLiteral = buildLiteral(current);
            HashSet<Literal> currentUnsolved = new HashSet<>(unsolved);
            ResolutionResult currentResult = new ResolutionResult(currentLiteral, currentUnsolved);
            result.add(currentResult);
            return;
        }
        //当前层
        Predicates currentPredicate = currentQuery.predicates.get(index);
        currentPredicate = currentPredicate.negativePredicate();
        ArrayList<Predicates> getIndex = getIndexPredicate(preProcessKB, currentPredicate);
        // case1
        if (getIndex == null || getIndex.size() == 0) {
            Literal unsolvable = new Literal(new ArrayList<>(Arrays.asList(currentPredicate.negativePredicate())));
            unsolved.add(unsolvable);
            singleResolution(result, current, index + 1, preProcessKB, unsolved, currentQuery, indexKB);
            unsolved.remove(unsolvable);
        } else {
            for (Predicates cpredicate : getIndex) {
                ArrayList<Literal> KBLiteral = indexKB.get(cpredicate);
                if (KBLiteral == null || KBLiteral.size() == 0) {
                    Literal unsolvable = new Literal(new ArrayList<>(Arrays.asList(currentPredicate.negativePredicate())));
                    unsolved.add(unsolvable);
                    singleResolution(result, current, index + 1, preProcessKB, unsolved, currentQuery, indexKB);
                    unsolved.remove(unsolvable);
                } else {
                    for (Literal l : KBLiteral) {
                        if (l.validCombo(currentPredicate)) {
                            Literal afterResolution = l.resolve(currentPredicate);
                            current.add(afterResolution);
                            singleResolution(result, current, index + 1, preProcessKB, unsolved, currentQuery, indexKB);
                            current.remove(afterResolution);
                        } else {
                            Literal unsolvable = new Literal(new ArrayList<>(Arrays.asList(currentPredicate.negativePredicate())));
                            unsolved.add(unsolvable);
                            singleResolution(result, current, index + 1, preProcessKB, unsolved, currentQuery, indexKB);
                            unsolved.remove(unsolvable);
                        }
                    }
                }
            }
        }
    }

    public static Literal buildLiteral(ArrayList<Literal> literals){
        ArrayList<Predicates> list = new ArrayList<>();
        for(Literal current: literals){
            for(Predicates currentPredicates : current.predicates){
                if(!currentPredicates.isEmpty()){
                    list.add(currentPredicates);
                }
            }
        }
        Literal result = new Literal(list);
        return result;
    }
}
