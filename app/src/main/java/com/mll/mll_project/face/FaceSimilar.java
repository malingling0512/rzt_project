package com.mll.mll_project.face;

public class FaceSimilar {

    private float xsd;
    private int sfxt; //是否为本人，0表示不是，1表示是

    public FaceSimilar(float xsd, int sfxt) {
        this.xsd = xsd;
        this.sfxt = sfxt;
    }

    public float getXsd() {
        return xsd;
    }

    public void setXsd(float xsd) {
        this.xsd = xsd;
    }

    public int getSfxt() {
        return sfxt;
    }

    public void setSfxt(int sfxt) {
        this.sfxt = sfxt;
    }

}
