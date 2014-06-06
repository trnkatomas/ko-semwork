import java.lang.reflect.Array;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tomas
 * Date: 4/9/14
 * Time: 10:43 PM
 */
public class TimeTableScheduler implements Runnable {

    private TimeTable timeTable;
    private ArrayList<Task> tasks;
    private boolean DEBUG = false;// true;
    private ArrayList<TimeTable> solutions = new ArrayList<TimeTable>();
    private TimeTable init_res;

    /**
     * Initialization of the actual schedule to show functionality.
     */
    public void init(){

        // sample school schedule and other tasks
        tasks = new ArrayList<Task>();
        tasks.add(new Task(8.25,9.75,"PAH-lecture", 1, TaskType.strictTask));
        tasks.add(new Task(10.00,11.50,"BIA-lecture", 1, TaskType.strictTask));
        tasks.add(new Task(12.75,14.25,"MPV-lecture", 1, TaskType.strictTask));
        tasks.add(new Task(14.50,16.25,"PAH-exercise", 1, TaskType.strictTask));
        tasks.add(new Task(20.00,22.00,"basket", 1, TaskType.voluntaryTask));
        tasks.add(new Task(8.25,10.50,"TAL-lecture", 2, TaskType.strictTask));
        tasks.add(new Task(10.75,13.25,"KO-lecture", 2, TaskType.strictTask));
        tasks.add(new Task(18.00,20.00,"Eva", 2, TaskType.voluntaryTask));
        tasks.add(new Task(20.50,23.00,"patecnici", 2, TaskType.strictTask));
        tasks.add(new Task(11.00,12.30,"TAL-exercise", 3, TaskType.strictTask));
        tasks.add(new Task(14.50,16.00,"MPV-exercise", 3, TaskType.strictTask));
        tasks.add(new Task(9.25,10.75,"KO-exercise", 4, TaskType.strictTask));
        tasks.add(new Task(12.75,14.25,"BIA-exercise", 4, TaskType.strictTask));
        tasks.add(new Task(20.00,24.00,"Filmovy-klub", 4, TaskType.strictTask));
        tasks.add(new Task(8.00,10.00,"Bytova-snidane", 5, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 1, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 2, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 3, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 4, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 5, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 6, TaskType.stretchTask));
        tasks.add(new Task(0.00 ,8, "spanek", 7, TaskType.stretchTask));
        tasks.add(new Task(8.25,9.75,"PAH-lecture", 8, TaskType.strictTask));
        tasks.add(new Task(10.00,11.50,"BIA-lecture", 8, TaskType.strictTask));
        tasks.add(new Task(12.75,14.25,"MPV-lecture", 8, TaskType.strictTask));
        tasks.add(new Task(14.50,16.25,"PAH-exercise", 8, TaskType.strictTask));
        tasks.add(new Task(20.00,22.00,"basket", 8, TaskType.strictTask));
        tasks.add(new Task(8.25,10.50,"TAL-lecture", 9, TaskType.strictTask));
        tasks.add(new Task(10.75,13.25,"KO-lecture", 9, TaskType.strictTask));
        tasks.add(new Task(18.00,20.00,"Eva", 9, TaskType.strictTask));
        tasks.add(new Task(20.50,23.00,"patecnici", 9, TaskType.strictTask));
        tasks.add(new Task(11.00,12.30,"TAL-exercise", 10, TaskType.strictTask));
        tasks.add(new Task(14.50,16.00,"MPV-exercise", 10, TaskType.strictTask));
        tasks.add(new Task(9.25,10.75,"KO-exercise", 11, TaskType.strictTask));
        tasks.add(new Task(12.75,14.25,"BIA-exercise", 11, TaskType.strictTask));
        tasks.add(new Task(20.00,24.00,"Filmovy-klub", 11, TaskType.strictTask));
        tasks.add(new Task(8.00,10.00,"Bytova-snidane", 12, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 8, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 9, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 10, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 11, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 12, TaskType.stretchTask));
        tasks.add(new Task(0.00, 8, "spanek", 13, TaskType.stretchTask));
        tasks.add(new Task(0.00 ,8, "spanek", 14, TaskType.stretchTask));
        tasks.add(new Task(2,24.00,5,"MPV-ass",TaskType.midtermTask));
        tasks.add(new Task(7,24.00,6,"PAH-ass",TaskType.midtermTask));
        tasks.add(new Task(14,24.00,10,"KO-ass",TaskType.midtermTask));
        tasks.add(new Task(3,11.00,3,"TAL-ukol",TaskType.midtermTask));
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public TimeTable getTimeTable(){
        return timeTable;
    }

    public ArrayList<TimeTable> getSolutions(){
        return solutions;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Implemented interface method for the thread
     */
    @Override
    public void run() {
        long s = System.nanoTime();
        plan();
        // Dummy example from the original paper
        // Stack<Integer> partialSchdule = chengDPApproach(null);
        System.out.printf("Executing time for %d task is : %.2f s\n",tasks.size(),(System.nanoTime()-s)/(double)10e8);
    }

    /**
     * The main method for planing, the initial solution for only strict task
     * is computed, whether the minimal subset of task is feasible. If is then
     * the solution for all the task is being searched.
     * If the solutions is still not found, then comes to the word dividing
     * and shrinking of the tasks in the fist tries and then removing voluntary
     * tasks.
     */
    public void plan(){
        init();
        TimeTable tt = new TimeTable(tasks);
        long start = System.nanoTime();
        // optimal sequence order for one week
        ArrayList<Task> week = tt.getWeek(1, true);
        System.out.println("First week size: "+ week.size());
        Stack<Integer> orderOfTasks = null;
        if (week.size()<=22) {
            orderOfTasks = chengDPApproach(week);
        }
        TimeTable tt_init = new TimeTable(tasks);
        TimeTable tt_s = new TimeTable(week);
        if (orderOfTasks!=null) {
            tt_s.applyOrdering(orderOfTasks);
        }
        tt_init.init_solution();
        tt_init.sortIndexes();
        TimeTable vysledek = bratleyAlg(tt_init, 0, Double.MAX_VALUE);
        if (vysledek==null){
            System.out.println("Feasible solution not found");
            return;
        }
        init_res = new TimeTable(vysledek);
        init_res.final_solution();
        solutions.add(vysledek);
        vysledek.final_solution();
        double penalty = vysledek.computePenalty();
        System.out.println("Penalty for init solution is: "+penalty);
        System.out.printf("Running time: %f\n", (System.nanoTime() - start) / 1000000000.0);
        tt_init.final_solution();
        start = System.nanoTime();
        TimeTable vysledek2 = bratleyAlg(vysledek, 0, penalty);
        if (vysledek2!=null) {
            double penalty2 = vysledek2.computePenalty();
            this.timeTable = vysledek2;
        }
        System.out.printf("Running time: %f\n",(System.nanoTime()-start)/1000000000.0);

        if (vysledek2==null){
            for(int i=0;i<tasks.size();i++){
                Task t = tasks.get(i);
                if (t.getTaskType()==TaskType.midtermTask){
                    ArrayList<Task> temp_tasks = new ArrayList<Task>();
                    temp_tasks.addAll(tasks);
                    temp_tasks.remove(i);
                    tt = new TimeTable(init_res);
                    Task t2 = new Task(t);
                    t2.setDuration(t2.getDuration()/2);
                    t2.setName(t2.getName()+"-1");
                    Task t3 = new Task(t);
                    t3.setDuration(t3.getDuration()/2);
                    t3.setName(t3.getName()+"-2");
                    t2.setDivided(1);
                    t3.setDivided(1);
                    temp_tasks.add(i,t2);
                    temp_tasks.add(t3);
                    tt = new TimeTable(temp_tasks);
                    start = System.nanoTime();
                    tt.setAssigned(init_res.getAssigned());
                    tt.setOrder(init_res.getOrder());
                    vysledek = bratleyAlg(tt, 0, Double.MAX_VALUE);
                    System.out.printf("Running time: %f\n",(System.nanoTime()-start)/1000000000.0);
                    if (vysledek!=null) {
                        this.timeTable = vysledek;
                        solutions.add(vysledek);
                        System.out.println("Penalty for feasible solution: "+ vysledek.computePenalty());
                    }
                }
                if (t.getTaskType()==TaskType.voluntaryTask){
                    tt = new TimeTable(init_res);
                    start = System.nanoTime();
                    boolean[] assigned = tt.getAssigned();
                    assigned[i] = true;
                    tt.setAssigned(assigned);
                    vysledek = bratleyAlg(tt, 0, penalty);
                    System.out.printf("Running time: %f\n",(System.nanoTime()-start)/1000000000.0);
                    if (vysledek!=null) {
                        this.timeTable = vysledek;
                        assigned = vysledek.getAssigned();
                        assigned[i] = false;
                        vysledek.setAssigned(assigned);
                        this.timeTable = vysledek;
                        solutions.add(vysledek);
                        System.out.println("Penalty for feasible solution: "+ vysledek.computePenalty());
                    }
                }
            }
        }
    }

    /**
     * Since the task is strictly NP-hard to obtain the solution we have to enumerate
     * all the possibilities. To improve the search space (i.e. shirnk it) the Braley's
     * algorithm with bounds is implemented.
     *
     * @param timeTable
     * @param start
     * @param upperBound
     * @return TimeTable
     */
    public TimeTable bratleyAlg(TimeTable timeTable,int start, double upperBound ){
        TimeTable newTT = new TimeTable(timeTable);
        double penalty = newTT.computePenalty();
        if (penalty> upperBound){
            return null;
        }
        for(int i=start;i<newTT.getOrder().length;i++){
             boolean[] assigned = newTT.getAssigned();
             int[] order = newTT.getOrder();
             for(int j=0;j<assigned.length;j++){
                 if (assigned[j]){
                     continue;
                 }else{
                     boolean bound = false;
                     int feasible = newTT.assign(i,j);
                     switch (feasible){
                         case 1:
                            assigned[j] = true;
                            newTT.setAssigned(assigned);
                            boolean complete = newTT.isComplete();
                            if (complete){
                                newTT.printSchedule();
                                if (this.timeTable==null){
                                    this.timeTable = newTT;
                                }
                                return newTT;
                            }
                            if (newTT.getAssigned()==timeTable.getAssigned()){
                                return null;
                            }
                            TimeTable res = bratleyAlg(newTT,i+(int)(newTT.getTasks().get(j).getDuration()*4), upperBound);
                            if (res==null){
                                assigned[j] = false;
                                newTT.setAssigned(assigned);
                                newTT.setOrder(order);
                            }else{
                                /** Can be done this way - if solution is found for the relaxed problem, then everything
                                 * is fine; if solution for midterm tasks and other is found, then the solution will have
                                 * it's suboptimal value - there is no need, to shrink the task again, or move divided
                                 * task to another place
                                 */
                                return res;
                            }
                            break;
                         case 0:
                             break;
                         case -1:
                             return null;
                     }
                     if (bound){
                         break;
                     }
                 }
             }
        }
        return null;
    }

    /**
     * Method changing in every call input array with combination
     * of desired size in lexicographical order.
     *
     * @param vybrano
     * @param size
     * @return
     */
    private static boolean combinations(int[] vybrano, int size) {
        int size2 = vybrano.length;
        for (int i = size2 - 1; i > -1; i--) {
            if (vybrano[i] < size - (size2 - i)) {
                vybrano[i]++;
                return true;
            } else {
                if ((i - 1) < 0) {
                    return false;
                }
                int k = vybrano[i - 1] + 2;
                for (int j = i; j < size2; j++) {
                    vybrano[j] = k++;
                }
            }
        }
        return false;
    }

    /**
     * Auxiliary method computing binomial number
     *
     * @param a
     * @param b
     * @return
     */
    private int combNumber(int a, int b){
        long temp = 1;
        b = (b>(a/2)) ? (a-b) : b;
        for(int i=0;i<b;i++){
            temp *= a--;
        }
        long temp2 = 1;
        while(b>0){
            temp2 *= b--;
        }
        return (int)(temp/temp2);
    }

    private  int[] choice;

    /**
     * Algorithm based on dynamic programing implemented
     * according to http://dx.doi.org/10.1016/0898-1221(90)90001-Z.
     * Output of this algorithm is an optimal sequence for selected tasks.
     * Although the algorithm is DP, it scaled with the combinatorial numbers
     * of selected tasks - which allow quick computation only to number of
     * tasks around 20. The output sequence is optimal according to the
     * DP principal of optimality.
     * The algorithm was slightly modified - in the computation of current
     * reward the deadline is subtracted - my task have not only release dates
     * but also deadlines.
     */
    public Stack<Integer> chengDPApproach(ArrayList<Task> tasksToSeq){

        if (tasksToSeq==null) {
            tasksToSeq = new ArrayList<Task>();
            // syntax of adding task
            //public Task(int deadlineDay, double deadlineTime, int duration, String name, TaskType tt) {
            tasksToSeq.add(new Task(0, 74, 15, "t1", TaskType.midtermTask));
            tasksToSeq.add(new Task(0, 45, 27, "t1", TaskType.midtermTask));
            tasksToSeq.add(new Task(0, 11, 63, "t1", TaskType.midtermTask));
            tasksToSeq.add(new Task(0, 2, 71, "t1", TaskType.midtermTask));
            tasksToSeq.add(new Task(0, 15, 86, "t1", TaskType.midtermTask));
            tasksToSeq.add(new Task(0, 14, 99, "t1", TaskType.midtermTask));
        }

        int stages = tasksToSeq.size();
        int tasksS = tasksToSeq.size();

        ArrayList<HashMap<SubsetId,Double>> fTable = new ArrayList<HashMap<SubsetId,Double>>(stages);
        ArrayList<HashMap<SubsetId,SubsetId>> policy = new ArrayList<HashMap<SubsetId,SubsetId>>();
        ArrayList<HashMap<SubsetId,Integer>> policyA = new ArrayList<HashMap<SubsetId,Integer>>();

        //DEBUG = false;
        for(int s=1;s<=stages;s++){
            choice = new int[s];
            for(int c=0;c<s;c++){
                choice[c] = c;
            }
            fTable.add(new HashMap<SubsetId,Double>());
            do{
                //DEBUG output
                if(DEBUG){
                    System.out.print("[ ");
                    for(int i=0;i<s;i++){
                        System.out.printf("%d ",choice[i]);
                    }
                    System.out.print("]\n");
                }
                double optimalValue =  Double.MAX_VALUE;
                for(int c=0;c<choice.length;c++){
                    //DEBUG output
                    if (DEBUG)
                        System.out.printf("\t%d\t",choice[c]);

                    //adding action after all jobs in J (ie. after all int the set)
                    Task t = tasksToSeq.get(choice[c]);
                    // computing return value
                    double Cj = 0;
                    boolean[] ch = new boolean[tasksS];
                    for(int i=0;i<choice.length;i++){
                        Task t2 = tasksToSeq.get(choice[i]);
                        Cj -= t2.getEndDP();
                        Cj += t2.getDuration();
                        ch[choice[i]] = true;
                    }
                    double r = Math.abs(Cj - t.getDeadlineTime());

                    // store computed value in table
                    SubsetId si = new SubsetId();
                    si.jobs = ch;
                    fTable.get(s-1).put(si,r);

                    //DEBUG output
                    if (DEBUG)
                        System.out.printf("r: %3.1f",r);

                    // computing optimal value function
                    boolean[] newChoice = new boolean[tasksS];
                    int skip = 0;
                    int addJob = -1;
                    for(int j=0;choice.length>1 && j+skip<choice.length;j++){
                        if (j==c){
                            skip = 1;
                            addJob = choice[j];
                        }
                        if (j+skip>=choice.length){
                            break;
                        }
                        newChoice[choice[j+skip]] = true;
                    }
                    SubsetId oldId = new SubsetId(newChoice);
                    double f = computeCostFunction(s-1,fTable,oldId);
                    double rf = r+f;
                    if(rf<optimalValue){
                        optimalValue = rf;
                        addJob = (addJob>=0) ? addJob : choice[c];//choice[c];
                        if(policy.size()<s) {
                            policy.add(new HashMap<SubsetId, SubsetId>());
                            policyA.add(new HashMap<SubsetId, Integer>());
                        }
                        policy.get(s-1).put(si,oldId);
                        policyA.get(s-1).put(si,addJob);
                    }
                    fTable.get(s-1).put(si,optimalValue);
                    //DEBUG output
                    // r + cost function
                    if (DEBUG) {
                        System.out.printf("\tr+f: %3.1f\t", (r + f));
                        //System.out.print(policy.get(s - 1).get(si) + " " + si.addJob);
                        System.out.println();
                    }
                    if (s==stages && c==choice.length-1){
                        System.out.printf("optimalSolutionValue: %.2f\n",optimalValue);
                    }
                }
            }while(combinations(choice,tasksS));
        }
        boolean neco = true;
        int stage = stages-1;
        SubsetId previous = null;
        System.out.println("Optimal job sequence:");
        Stack<Integer> stack = new Stack<Integer>();
        while(neco){
            if (stage==stages-1){
                for(SubsetId si : policy.get(stage).keySet()){
                    previous = si;
                    break;
                }
            }
            stack.push(policyA.get(stage).get(previous));
            previous = policy.get(stage--).get(previous);
            if(stage<0){
                neco = false;
            }
        }
        if (DEBUG==true) {
            StringBuilder sb = new StringBuilder();
            Stack<Integer> stack2 = new Stack<Integer>();
            stack2.addAll(stack);
            while (!stack2.isEmpty()) {
                sb.append(tasksToSeq.get(stack2.peek()));
                sb.append("\n");
                System.out.print(stack2.pop() + " ");
            }
            System.out.println();
            System.out.println(sb);
        }
        return stack;
    }

    /**
     * Computation of the cost function for the Cheng DP algorithm
     *
     * @param stage - desired stage of planning
     * @param fTable - table with stored values
     * @param vyber - subset of task that we are examining
     * @return
     */
    public double computeCostFunction(int stage,ArrayList<HashMap<SubsetId,Double>> fTable,SubsetId vyber){
        if (stage==0) {
            return 0;
        }else{
            return fTable.get(stage-1).get(vyber);
        }
    }

    /**
     * Auxiliary class allowing to store and compare information about selected tasks.
     * Basically it is just wrapper for array of booleans with bitwise comparison.
     */
    private class SubsetId{
        public boolean[] jobs;
        public int addJob;

        public SubsetId(){}

        public SubsetId(int length){
            this.jobs = new boolean[length];
        }

        public SubsetId(boolean[] jobs){
            this.jobs = jobs;
        }

        public SubsetId(SubsetId id){
            this.jobs = id.jobs;
        }

        @Override
        public String toString(){
            String s = "";
            s+= "[ ";
            for (int i=0;i<jobs.length;i++){
                if(jobs[i]){
                  s+= i+" ";
                }
            }
            s+=" ]";
            return s;
        }


        @Override
        public int hashCode() {
            return jobs != null ? Arrays.hashCode(jobs) : 0;
        }

        @Override
        public boolean equals(Object o){
            SubsetId o2;
            if (SubsetId.class.isInstance(o)) {
                o2 = (SubsetId) o;
            }else{
                return false;
            }
            if(o2.jobs.length!=jobs.length){
                return false;
            }
            for(int i=0;i<jobs.length;i++){
               if (jobs[i]!=o2.jobs[i]){
                   return false;
               }
            }
            return true;
        }
    }
}


