package tv.projectivy.plugin.wallpaperprovider.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    public static final int TYPE_DISPLAYED = 0;
    public static final int TYPE_SKIPPED = 1;
    public static final int TYPE_STARTED = 2;
    public static final int TYPE_STOPPED = 3;

    public int type;
    public String uri;

    public Event() {}

    public Event(int type, String uri) {
        this.type = type;
        this.uri = uri;
    }

    protected Event(Parcel in) {
        type = in.readInt();
        uri = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(uri);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) { return new Event(in); }
        @Override
        public Event[] newArray(int size) { return new Event[size]; }
    };
}
