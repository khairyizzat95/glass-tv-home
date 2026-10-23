package tv.projectivy.plugin.wallpaperprovider.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Wallpaper implements Parcelable {
    public static final int MEDIA_TYPE_VIDEO = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;

    public String uri;
    public String title;
    public String description;
    public int mediaType;

    public Wallpaper() {}

    protected Wallpaper(Parcel in) {
        uri = in.readString();
        title = in.readString();
        description = in.readString();
        mediaType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(mediaType);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Wallpaper> CREATOR = new Creator<Wallpaper>() {
        @Override
        public Wallpaper createFromParcel(Parcel in) { return new Wallpaper(in); }
        @Override
        public Wallpaper[] newArray(int size) { return new Wallpaper[size]; }
    };
}
