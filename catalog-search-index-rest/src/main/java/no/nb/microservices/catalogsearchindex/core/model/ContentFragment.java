package no.nb.microservices.catalogsearchindex.core.model;

public class ContentFragment {
    private int x;
    private int y;
    private int w;
    private int h;
    private String pageid;
    private String text;
    private String before;
    private String after;


    public ContentFragment() {

    }

    public ContentFragment(int x, int y, int w, int h, String pageid, String text, String before, String after) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.pageid = pageid;
        this.text = text;
        this.before = before;
        this.after = after;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getPageid() {
        return pageid;
    }

    public void setPageid(String pageid) {
        this.pageid = pageid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}
