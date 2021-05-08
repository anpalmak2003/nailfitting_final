package ru.anpalmak.nailfiffing;

import java.net.URL;

public class NailDesign {
    String designName = "";
    String username = "";
    URL nailPhotoDesign;
    public NailDesign(String designName, String username, URL nailPhotoDesign)
    {
        this.designName=designName;
        this.username=username;
        this.nailPhotoDesign=nailPhotoDesign;
    }
    public String getDesignName()
    {
        return designName;
    }
    public String getUsername()
    {
        return username;
    }
    public URL getNailPhotoDesign()
    {
        return nailPhotoDesign;
    }
}
