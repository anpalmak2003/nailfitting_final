package ru.anpalmak.nailfiffing.Draw;

import com.google.firebase.database.IgnoreExtraProperties;
/**Информация об изображении*/
@IgnoreExtraProperties
public class ImageNailInfo {
   public String url;
    public String username;
   public String imageName;
   public String access;
    public ImageNailInfo(){};
    public ImageNailInfo(String url, String username, String imageName, String access)
    {
        this.url=url;
        this.username=username;
        this.imageName=imageName;
        this.access=access;
    }
}
