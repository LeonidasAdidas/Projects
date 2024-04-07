import javax.swing.*;

public class TimeKeeper extends JLabel implements Runnable{
    private int sec;

    public TimeKeeper() {
        sec = 0;
    }
    public TimeKeeper(int x) {
        sec = x;
        if(sec == Integer.MAX_VALUE) {
            sec = 0;
        }
    }
    public int getTime() {
        return sec;
    }
    public String timeToString() {
        int tempmin = sec/60;
        String m;
        String s;
        if(tempmin < 10) {
            m = "0" + tempmin;
        }else {
            m = Integer.toString(tempmin);
        }
        int tempsec = sec-sec/60*60;
        if(tempsec < 10) {
            s = "0" + tempsec;
        }else {
            s = Integer.toString(tempsec);
        }
        return m + ":" + s;
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()) {
                setText(timeToString());
                repaint();
                sec++;
                Thread.sleep(1000);
            }
        }catch (InterruptedException e) {};

    }
}
