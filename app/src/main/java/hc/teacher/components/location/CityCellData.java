package hc.teacher.components.location;

/**
 * Created by cry on 2015/10/22.
 */
public class CityCellData {
    public static final int TYPE_NAME = 0;
    public static final int TYPE_CUTLINE = 1;
    public static final int TYPE_BLANK = 2;

    public CityCellData(){
        this.title = "";
        this.type = 0;
    }

    public CityCellData(String title, int type){
        this.title = title;
        this.type = type;
    }

    public String title;
    public int type;
}
