public class Main {


    public static void main(String[] args) {

        // init the scheduler
        TimeTableScheduler tts = new TimeTableScheduler();
        Thread t = new Thread(tts);

        // init of the graphics
        TimeTableWindow js = new TimeTableWindow(null);
        Thread t2 = new Thread(js);

        // run the threads
        t2.run();
        t.run();

        // waiting loop for the thread
        while(t.isAlive()){
            try{
                Thread.sleep(100);
                if(tts.getTimeTable()!=null){
                    js.setTT(tts.getTimeTable());
                    js.revalidate();
                    js.repaint();
                }
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }

        System.out.println("Computation has been executed");
        // set computed timetable and repaint it
        js.setTTS(tts);
        js.revalidate();
        js.repaint();
    }
}
