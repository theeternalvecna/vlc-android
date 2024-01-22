package org.videolan.medialibrary.interfaces.media;

import android.os.Parcel;
import android.os.Parcelable;

import org.videolan.medialibrary.MLServiceLocator;
import org.videolan.medialibrary.interfaces.Medialibrary;
import org.videolan.medialibrary.media.MediaLibraryItem;

public abstract class DiscoverService extends MediaLibraryItem implements Parcelable {

    public Type mType;
    protected int mNbMedia;
    protected int mNbUnplayedMedia;
    protected int mNbSubscriptions;

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

    protected DiscoverService(Type type, int nbUnplayedMedia, int nbMedia, int nbSubscriptions) {
        this.mType = type;
        this.mNbMedia = nbMedia;
        this.mNbUnplayedMedia = nbUnplayedMedia;
        this.mNbSubscriptions = nbSubscriptions;
    }
    protected DiscoverService(int type, int nbUnplayedMedia, int nbMedia, int nbSubscriptions) {
        this.mType = Type.getValue(type);
        this.mNbMedia = nbMedia;
        this.mNbUnplayedMedia = nbUnplayedMedia;
        this.mNbSubscriptions = nbSubscriptions;
    }

    private void init(int type) {
        mType = Type.getValue(type);
    }

    public MediaWrapper[] getTracks() {
        return getMedia(Medialibrary.SORT_DEFAULT, false, true, false);
    }

    @Override
    public int getTracksCount() {
        return getNbMedia();
    }

    public int getNbMedia() {
        return mNbMedia;
    }

    public int getNbSubscriptions() {
        return mNbSubscriptions;
    }

    public int getNbUnplayedMedia() {
        return mNbUnplayedMedia;
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
    public abstract Subscription[] getSubscriptions(int sort, boolean desc, boolean includeMissing, boolean onlyFavorites);
    public abstract MediaWrapper[] getMedia(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites);
    public abstract Subscription[] searchSubscriptions(String query, int sort, boolean desc, boolean includeMissing, boolean onlyFavorites, int nbItems, int offset);
    public abstract int searchSubscriptionsCount(String query,int sort, boolean desc, boolean includeMissing);
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
