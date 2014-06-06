

/**
 * Created with IntelliJ IDEA.
 * User: tomas
 * Date: 4/24/14
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


class TimeTablePainter extends JPanel {

    TimeTable timeTable;
    int[] order;
    private Color[] colors;
    private int[] colorUsed;
    private Random r = new Random();
    private ArrayList<Rectangle> tasks;
    int numday = 0;

    /**
     * Method doing the drawing, draws the grid for each day and each hour
     * and afterwords it draw all events with different colors
     * @param g
     */
    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        BasicStroke bs2 = new BasicStroke(2, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_MITER);
        g2d.setStroke(bs2);
        int width = 80;
        int height = 10;
        int dayWidth = 80;
        int dayHeader = 35;
        String[] days = new String[]{"pondělí","úterý","středa","čtvrtek","pátek","sobota","neděle"};
        for (int i=0;i<numday;i++){
            g.setColor(Color.BLACK);
            g2d.drawString(days[i%days.length],15+(i+1)*dayWidth, dayHeader-10);
            // draw hours
            for (int j=0;j<24*4;j++){
                if (i==0 && j%4==0){
                    g.setColor(Color.BLACK);
                    g2d.drawString(String.format("%02d:%02d",j/4,0),30, (j+4)*height);
                }
            }
            if (order != null){
                for (int j=0;j<24*4;j++){

                    // draw hour and day grid
                    if (j%4==0){
                        g.setColor(Color.BLACK);
                        g2d.drawRect((i+1)*dayWidth, dayHeader+j*height, width, 4*height);
                    }
                    if (order[i*24*4+j]<0){
                        g.setColor(Color.WHITE);
                    }else{
                        g.setColor(colors[order[i*24*4+j]]);
                        colorUsed[order[i*24*4+j]]++;
                    }
                    //if (order[i*24*4+j]>=0 && tasks[order[i*24*4+j]]==null){
                    //    tasks[order[i*24*4+j]] = new Rectangle(order[i*24*4+j],(i + 1) * dayWidth, dayHeader + j * height, width, timeTable.getTasks().get(order[i*24*4+j]).getDuration()*4*height);
                    //}
                    if (order[i*24*4+j]>=0) {
                        tasks.add(new Rectangle(order[i * 24 * 4 + j], (i + 1) * dayWidth, dayHeader + j * height, width, height));
                    }
                    g2d.fillRect((i + 1) * dayWidth, dayHeader + j * height, width, height);
                    if (order[i*24*4+j]>=0 && colorUsed[order[i*24*4+j]]==0) {
                        g.setColor(Color.BLACK);
                        g2d.drawString(String.format("%s",timeTable.getTasks().get(order[i*24*4+j]).getName()),(i + 1) * dayWidth, dayHeader + (j+1) * height);
                        //g2d.drawS
                    }

                }
            }
        }
        addMouseMotionListener(new MouseMotionListener() {
            /**
             * Auxiliary class with method allowing to show name of task
             * known bug: with multiple monitors it's not working properly
             * @param e
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                Point a = MouseInfo.getPointerInfo().getLocation();
                Point g = getMousePosition();
                a = e.getLocationOnScreen();
                try {
                    for (int t = 0; t < tasks.size(); t++) {
                        if (tasks.get(t).contains(a.getX(), a.getY())) {
                            setToolTipText(timeTable.getTasks().get(tasks.get(t).getID()) + "");
                            break;
                        }
                    }
                }catch(Exception ex){
                    //neco se deje, ale nic zasaniho
                }
                ToolTipManager.sharedInstance().mouseMoved(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * Overrided method of the component for painting
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    /**
     * Setting of timetable, initialization of colors and information about tasks
     * @param timeTable
     */
    public void setTimeTable(TimeTable timeTable){
        this.timeTable = timeTable;
        if (timeTable!=null){
            this.order = timeTable.getOrder();
            numday = timeTable.getNumdays();
            colors = new Color[timeTable.getTasks().size()];
            colorUsed = new int[timeTable.getTasks().size()];
            tasks = new ArrayList<Rectangle>();//new Rectangle[timeTable.getTasks().size()];
            for(int c=0;c<colors.length;c++){
               colors[c] = new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255));
               colorUsed[c] = -1;
               //tasks[c] = null;
            }
        }
    }

    /**
     * Auxiliary class for information about tasks
     */
    public class Rectangle{
        double x,y,width,height;
        int ID;

        public Rectangle(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Rectangle(int ID, double x, double y, double width, double height) {
            this(x,y,width,height);
            this.ID = ID;
        }

        public boolean contains(double x, double y){
            return x>this.x && x<this.x+this.width && y>this.y && y<this.y+this.height;
        }

        public int getID(){
            return ID;
        }

    }
}


