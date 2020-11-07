package com.test.tools.model;


public class Cms {
    private int fingerId;
    private String cmsName;
    private String path;
    private String matchPattern;
    private String options;
    private int hit;
    private int status;

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFingerId() {
        return this.fingerId;
    }

    public void setFingerId(int fingerId) {
        this.fingerId = fingerId;
    }

    public String getCmsName() {
        return this.cmsName;
    }

    public void setCmsName(String cmsName) {
        this.cmsName = cmsName;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMatchPattern() {
        return this.matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    public String getOptions() {
        return this.options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public int getHit() {
        return this.hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }


    public String toString() {
        return "Cms{fingerId=" + this.fingerId + ", cmsName='" + this.cmsName + '\'' + ", path='" + this.path + '\'' + ", matchPattern='" + this.matchPattern + '\'' + ", options='" + this.options + '\'' + ", hit=" + this.hit + ", status=" + this.status + '}';
    }
}
