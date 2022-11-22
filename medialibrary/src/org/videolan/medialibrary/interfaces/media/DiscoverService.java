package org.videolan.medialibrary.interfaces.media;

import android.os.Parcel;
import android.os.Parcelable;

import org.videolan.medialibrary.MLServiceLocator;
import org.videolan.medialibrary.interfaces.Medialibrary;
import org.videolan.medialibrary.media.MediaLibraryItem;

public abstract class DiscoverService extends MediaLibraryItem implements Parcelable {

    public enum Type {
        PODCAST(1);

        public final int value;

        Type(int val) {
            this.value = val;
        }

        public static Type getValue(int val) {
            for (Type type : Type.values()) {
                if (type.value == val) {
                    return type;
                }
            }
            return null;
        }
    }

    protected DiscoverService(Type type) {
        this.mType = type;
    }
    protected DiscoverService(int type) {this.mType = Type.getValue(type);}

    private void init(int type) {
        mType = Type.getValue(type);
    }

    public Type mType;

    public MediaWrapper[] getTracks() {
        return getMedia(Medialibrary.SORT_DEFAULT, false, true, false);
    }

    @Override
    public int getTracksCount() {
        return getNbMedia();
    }

    @Override
    public boolean setFavorite(boolean favorite) {
        return false;
    }

    @Override
    public int getItemType() {
        return TYPE_SERVICE;
    }

    public abstract boolean addSubscription(String mrl);
    public abstract boolean isAutoDownloadEnabled();
    public abstract boolean setAutoDownloadEnabled(boolean enabled);
    public abstract boolean isNewMediaNotificationEnabled();
    public abstract boolean setNewMediaNotificationEnabled(boolean enabled);
    public abstract long getMaxCachedSize();
    public abstract boolean setMaxCachedSize(long size);
    public abstract int getNbSubscriptions();
    public abstract int getNbUnplayedMedia();
    public abstract Subscription[] getSubscriptions(int sort, boolean desc, boolean includeMissing, boolean onlyFavorites);
    public abstract int getNbMedia();
    public abstract MediaWrapper[] getMedia(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites);
    public abstract boolean refresh();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
    }

    public void readFromParcel(Parcel source) {
        int tmpType = source.readInt();
        this.mType = tmpType == -1 ? null : Type.values()[tmpType];
    }

    protected DiscoverService(Parcel in) {
        super(in);
        int tmpType = in.readInt();
        init(tmpType);
    }

    public static final Creator<DiscoverService> CREATOR = new Creator<DiscoverService>() {
        @Override
        public DiscoverService createFromParcel(Parcel source) {
            return MLServiceLocator.getAbstractService(source);
        }

        @Override
        public DiscoverService[] newArray(int size) {
            return new DiscoverService[size];
        }
    };
}
