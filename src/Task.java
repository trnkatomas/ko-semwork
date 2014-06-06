

public class Task implements Comparable<Task> {

    private double start;
    private double end;
    private double duration;
    private String name;
    private String description;
    private int dayInWeek;
    private TaskType taskType;
    private int week;
    private double deadlineTime;
    private int deadlineDay;
    private boolean assigned = false;
    private double origDuration;
    private int divided = 0;

    public static final int STRICT_NOT_ASSIGNED_PENALTY = 5000;
    public static final int MIDTERM_NOT_ASSIGNED_PENALTY = 1000;
    public static final int STRETCH_NOT_ASSIGNED_PENALTY = 500;
    public static final int VOLUNTARY_NOT_ASSIGNED_PENALTY = 50;
    public static final int STRETCH_SHORTEN_PENALTY = 50;
    public static final int MIDTERM_DIVISION_PENALTY = 50;

    public Task(double start, int duration, String name, int dayInWeek,TaskType tt) {
        this.start = start;
        this.duration = duration;
        this.end = start+duration;
        this.name = name;
        this.dayInWeek = dayInWeek;
        if (tt!=TaskType.midtermTask){
            this.deadlineDay = dayInWeek;
            this.deadlineTime = this.end;
        }
        this.taskType = tt;
        this.origDuration = this.duration;
    }

    public Task(int deadlineDay, double deadlineTime, int duration, String name, TaskType tt) {
        this.deadlineTime = deadlineTime;
        this.deadlineDay = deadlineDay;
        this.duration = duration;
        this.name = name;
        this.taskType = tt;
        this.start = 0;
        this.origDuration = this.duration;
    }

    public Task(int deadlineDay, double deadlineTime, double duration, String name, TaskType tt) {
        this.deadlineTime = deadlineTime;
        this.deadlineDay = deadlineDay;
        this.duration = duration;
        this.name = name;
        this.taskType = tt;
        this.start = 0;
        this.origDuration = this.duration;
    }

    public Task(double start, double end,String name, int dayInWeek, TaskType tt) {
        this.start = start;
        this.end = end;
        this.duration = end-start;
        this.name = name;
        this.dayInWeek = dayInWeek;
        this.taskType = tt;
        if (tt!=TaskType.midtermTask){
            this.deadlineDay = dayInWeek;
            this.deadlineTime = this.end;
        }
        this.origDuration = this.duration;
    }

    public Task(Task t){
        this.start = t.getStart();
        this.duration = t.getDuration();
        this.end = t.getEnd();
        this.name = t.getName();
        this.dayInWeek = t.getDayInWeek();
        this.deadlineDay = t.getDeadlineDay();
        this.deadlineTime = t.getDeadlineTime();
        this.taskType = t.getTaskType();
        this.origDuration = this.duration;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public double getEndDP() {
        return (dayInWeek-1)*24+end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDayInWeek() {
        return dayInWeek;
    }

    public void setDayInWeek(int dayInWeek) {
        this.dayInWeek = dayInWeek;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public double getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(double deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public int getDeadlineDay() {
        return deadlineDay;
    }

    public void setDeadlineDay(int deadlineDay) {
        this.deadlineDay = deadlineDay;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDivided() {
        return divided;
    }

    public void setDivided(int divided) {
        this.divided = divided;
    }

    public double getOrigDuration() {
        return origDuration;
    }

    public void setOrigDuration(double origDuration) {
        this.origDuration = origDuration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "start=" + start +
                ", end=" + end +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Comparator of task, user for heuristic sorting
     * @param o
     * @return
     */
    @Override
    public int compareTo(Task o) {
        if (this.getDeadlineDay()<o.getDeadlineDay()){
            return -1;
        }
        else{
            if(this.getDeadlineDay()>o.getDeadlineDay()){
                return 1;
            }
            else{
                if(this.getDeadlineTime()<o.getDeadlineTime()){
                    return -1;
                }
                else{
                    return 1;
                }
            }
        }
    }
}
