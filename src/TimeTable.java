import java.util.*;

public class TimeTable {

    private ArrayList<Task> tasks;
    private int numdays = 0;
    private int[] order;
    private boolean[] assigned;
    private boolean[] relaxation;
    private int[] indexes;
    private Integer[] indexes2;

    /**
     * Create initial TimeTable from tasks
     * @param tasks
     */
    public TimeTable(ArrayList<Task> tasks){
        this.tasks = tasks;
        //Collections.sort(this.tasks);
        for (int i=0;i<tasks.size();i++){
            numdays = (numdays < tasks.get(i).getDayInWeek()) ? tasks.get(i).getDayInWeek() : numdays;
            numdays = (numdays < tasks.get(i).getDeadlineDay()) ? tasks.get(i).getDayInWeek() : numdays;
        }
        this.assigned = new boolean[tasks.size()];
        this.indexes = new int[tasks.size()];
        this.indexes2 = new Integer[tasks.size()];
        this.relaxation = new boolean[tasks.size()];
        this.order = new int[numdays*24*4];
        Arrays.fill(order,-1);
        sortIndexes();
    }

    /**
     * Simply copy constructor
     * @param timeTable
     */
    public TimeTable(TimeTable timeTable){
        this.tasks = timeTable.getTasks();
        this.numdays = timeTable.getNumdays();
        this.order = new int[numdays*24*4];
        this.assigned = new boolean[tasks.size()];
        for(int i=0;i<order.length;i++){
            this.order[i] = timeTable.getOrder()[i];
        }
        for(int i=0;i<assigned.length;i++){
            if (timeTable.getAssigned().length>i)
                this.assigned[i] = timeTable.getAssigned()[i];
            else{
                this.assigned[i] = false;
            }
        }
        this.relaxation = Arrays.copyOf(timeTable.getRelaxation(),timeTable.getRelaxation().length);
        this.indexes = Arrays.copyOf(timeTable.getIndexes(),timeTable.getIndexes().length);
        this.indexes2 = new Integer[tasks.size()];
        sortIndexes();
    }

    /**
     * Sorts task according to their due dates
     */
    public void sortTask(){
        // it works but it changes the indexes that are used in array assigned, relaxed and order
        Collections.sort(this.tasks);
    }

    /**
     * Sorts indexes of the tasks according to their due dates
     */
    public void sortIndexes(){
        for(int i=0;i<indexes2.length;i++){
            indexes2[i] = i;
        }
        Arrays.sort(indexes2, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return tasks.get(o1.intValue()).compareTo(tasks.get(o2.intValue()));
            }
        });
    }

    /**
     * generates relaxed problem with only strict tasks
     */
    public void init_solution() {
        for(int i=0;i<tasks.size();i++){
             if(tasks.get(i).getTaskType()!=TaskType.strictTask){
                 relaxation[i] = true;
             }
        }
        assigned = relaxation;
    }

    public ArrayList<Task> getWeek(int numberOfWeek, boolean forChengDP){
        if (numberOfWeek>(numdays/7)){
            return null;
        }
        ArrayList<Task> week = new ArrayList<Task>();
        for(Task t: tasks){
            if (t.getDayInWeek()<=7*numberOfWeek &&
                    (t.getTaskType()==TaskType.stretchTask || t.getTaskType()==TaskType.strictTask)) {
                week.add(t);
            }
        }
        return week;
    }


    /**
     * Set the initial values to array assigned to allow assigning all the tasks
     */
    public void final_solution(){
        for (int i=0;i<tasks.size();i++){
            assigned[i] = (relaxation[i]) ? false : assigned[i];
            relaxation[i] = false;
        }
    }

    /**
     * The main method during the assignment - it computes the feasibility of scheduling selected task on
     * selected place int the timetable. The output is depend on what type of Task is being assigned and
     * what possible rule is possible broken
     *
     * @param startTime
     * @param taskId
     * @return
     */
    public int assign(int startTime, int taskId){
        //taskId = indexes2[taskId];
        Task t = tasks.get(taskId);
        int feasible = 1;
        int[] newOrder = Arrays.copyOf(order,order.length);

        if (t.getTaskType()==TaskType.midtermTask){
            t.setStart((double)startTime/4);
            t.setDayInWeek((startTime/(24*4))+1);
        }
        int trueStartTime = (int) (24*4*(t.getDayInWeek()-1)+4*t.getStart());
        if (t.getTaskType()==TaskType.midtermTask){
            trueStartTime = startTime;
        }

        // check release time
        if (trueStartTime<=startTime){
            feasible = 1;
            trueStartTime = startTime;
        }else{
            feasible = 0;
        }
        if (feasible<1){
            return feasible;
        }
        // check deadline time
        if (trueStartTime+t.getDuration()*4 <= startTime){
            return -1;
        }
        if (trueStartTime+t.getDuration()*4 <= (t.getDeadlineDay()-1)*24*4 + t.getDeadlineTime()*4){
            feasible = 1;
        }else{
           feasible = -1;
        }
        for(int i=trueStartTime;feasible>0 && i<trueStartTime+t.getDuration()*4;i++){
            // check length of schedule and whether the array is not occupied // probably useless
            feasible += (i>=order.length || order[i]>0) ? -1 : 0;
            if (feasible <= 0){
                break;
            }else{
                newOrder[i] = taskId;
            }
        }
        if (feasible>0){
            order = newOrder;
        }
        //assigned[taskId] = (feasible>0);
        return feasible;
    }

    /**
     * Check whether all the task are assigned
     * @return boolean
     */
    public boolean isComplete(){
        boolean complete = true;
        for(int i=0;complete && i<assigned.length;i++){
           complete = complete && assigned[i];
        }
        return complete;
    }

    /**
     * Print schedule as table to stdout
     */
    public void printSchedule(){
        for (int i=0;i<order.length/(24*4);i++){
            for( int j=0;j<24*4;j++){
                System.out.printf("%d ",order[i*24*4+j]);
            }
            System.out.println();
        }
    }

    /**
     * Compares computed optimal sequence with EDF heuristics
     * @param orderOfTasks
     */
    public void applyOrdering(Stack<Integer> orderOfTasks) {
        int counter = 0;
        this.sortIndexes();
        boolean ordersCorrect = true;
        while(!orderOfTasks.isEmpty()){
            if (orderOfTasks.pop()!=indexes2[counter++]){
                System.out.println("ordering not applicable");
                ordersCorrect = false;
            }
        }
        if (ordersCorrect){
            sortTask();
        }
    }

    /**
     * Computing penalty for schedule, more detailed description is in the paper
     * @return double - sum of partial s penalty
     */
    public double computePenalty(){
        double penalty = 0;
        boolean missedPenalty = false;
        boolean shortenPenalty = false;
        boolean dividePenalty = false;
        for (int i=0;i<tasks.size();i++){
            missedPenalty = (assigned[i] && !relaxation[i]) ? false : true;
            shortenPenalty = (Math.abs(tasks.get(i).getDuration()-tasks.get(i).getOrigDuration())>0) ? true : false;
            dividePenalty = (tasks.get(i).getDivided()>0) ? true : false;
            switch (tasks.get(i).getTaskType()){
                case voluntaryTask:
                    if (missedPenalty){
                        penalty += Task.VOLUNTARY_NOT_ASSIGNED_PENALTY;
                    }
                    break;
                case stretchTask:
                    if (missedPenalty){
                        penalty += Task.STRETCH_NOT_ASSIGNED_PENALTY;
                    }
                    if (shortenPenalty){
                        penalty += Math.abs(tasks.get(i).getDuration()-tasks.get(i).getOrigDuration())*Task.STRETCH_SHORTEN_PENALTY;
                    }
                    break;
                case strictTask:
                    if (missedPenalty){
                        penalty += Task.STRICT_NOT_ASSIGNED_PENALTY;
                    }
                    break;
                case midtermTask:
                    if (missedPenalty){
                        penalty += Task.MIDTERM_NOT_ASSIGNED_PENALTY;
                    }
                    if (dividePenalty){
                        penalty += tasks.get(i).getDivided()*Task.MIDTERM_DIVISION_PENALTY;
                    }
                    break;
            }
        }
        return penalty;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public int[] getOrder() {
        return order;
    }

    public void setOrder(int[] order,boolean copy)
    {
        if (copy){
            this.order = Arrays.copyOf(order,order.length);
        }else {
            this.order = order;
        }
    }

    public void setOrder(int[] order) {
        this.order = order;
    }

    public int getNumdays() {
        return numdays;
    }

    public void setNumdays(int numdays) {
        this.numdays = numdays;
    }

    public boolean[] getAssigned() {
        return assigned;
    }

    public void setAssigned(boolean[] assigned,boolean copy){
        if (copy){
            this.assigned = Arrays.copyOf(assigned,assigned.length);
        }
        else{
            this.assigned = assigned;
        }
    }

    public void setAssigned(boolean[] assigned) {
        this.assigned = assigned;
    }

    public boolean[] getRelaxation(){
        return relaxation;
    }

    public int[] getIndexes() {
        return indexes;
    }

}
