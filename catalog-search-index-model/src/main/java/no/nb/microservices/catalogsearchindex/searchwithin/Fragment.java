package no.nb.microservices.catalogsearchindex.searchwithin;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Fragment {
    private String text;
    private String before;
    private String after;
    private String pageId;
    private Position position;

    public Fragment() {

    }

    public Fragment(String text, String before, String after, String pageId, Position position) {
        this.text = text;
        this.before = before;
        this.after = after;
        this.pageId = pageId;
        this.position = position;
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

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
