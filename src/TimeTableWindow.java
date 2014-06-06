
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;

public class TimeTableWindow extends JFrame implements Runnable{

    private JComboBox<String> schedules;
    private ArrayList<TimeTable> solutions = new ArrayList<TimeTable>();

    @Override
    public void run() {
        initUI();
        setVisible(true);
    }

    private TimeTableScheduler tts;
    private TimeTable tt;
    private TimeTablePainter panel;
    private int days = 14;

    public TimeTableWindow(TimeTableScheduler tts) {
        setTTS(tts);
        initUI();
    }

    /**
     * initialization of the TimeTableWindow
     */
    private void initUI() {
        setTitle("TimeTableWindow");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel =  new TimeTablePainter();
        if (tt!=null){
            panel.setTimeTable(tt);
            days = tt.getNumdays();
        }
        panel.setPreferredSize(new Dimension((days+1)*80+50, 24*4*10+40));
        JScrollPane jsp = new JScrollPane(panel);
        jsp.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        setLayout(new BorderLayout());
        Container c = new Container();
        c.setLayout(new BorderLayout());
        schedules = new JComboBox<String>();
        schedules.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                tt = tts.getSolutions().get(schedules.getSelectedIndex());
                panel.setTimeTable(tt);
                revalidate();
                repaint();
            }
        });
        //schedules.addA
        c.add(schedules, BorderLayout.NORTH);
        add(c,BorderLayout.EAST);
        add(jsp, BorderLayout.CENTER);
        setSize(1000, 1000);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    /**
     * setting of the computed timetable
     * @param tts
     */
    public void setTTS(TimeTableScheduler tts){
        this.tts = tts;
        if (tts!=null && !this.tts.getSolutions().isEmpty()){
            this.solutions = tts.getSolutions();
            Collections.reverse(this.solutions);
            int i = 0;
            for(TimeTable t: solutions){
                schedules.addItem("Schedule "+(i++)+" - penalty: "+t.computePenalty());
            }
            this.setTT(this.tts.getTimeTable());
        }

    }

    public void setTT(TimeTable tt){
        this.tt = tt;
        panel.setTimeTable(tt);
        days = tt.getNumdays();
        panel.setPreferredSize(new Dimension((days+1)*80+50, 24*4*10+40));
    }

}
