package no.nb.microservices.catalogsearchindex.core.model;

import org.elasticsearch.common.geo.GeoPoint;

public class GeoSearch {
    private GeoPoint topRight;
    private GeoPoint bottomLeft;
    private int precision = 5;

    public GeoPoint getTopRight() {
        return topRight;
    }

    public void setTopRight(GeoPoint topRight) {
        this.topRight = topRight;
    }

    public GeoPoint getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(GeoPoint bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
}
