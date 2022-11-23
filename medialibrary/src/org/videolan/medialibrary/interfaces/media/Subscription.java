package org.videolan.medialibrary.interfaces.media;

import android.os.Parcel;

import org.videolan.medialibrary.MLServiceLocator;
import org.videolan.medialibrary.interfaces.Medialibrary;
import org.videolan.medialibrary.media.MediaLibraryItem;

public abstract class Subscription extends MediaLibraryItem {

    public Subscription(long id, DiscoverService.Type type, String name, long parentId, int nbMedia, int nbUnplayedMedia) {
        this.mId = id;
        this.mType = type;
        this.mTitle = name;
        this.mParentId = parentId;
        this.mNbMedia = nbMedia;
        this.mNbUnplayedMedia = nbUnplayedMedia;
    }
    
    public Subscription(long id, int type, String name, long parentId, int nbMedia, int nbUnplayedMedia) {
        this.mId = id;
        this.mType = DiscoverService.Type.getValue(type);
        this.mTitle = name;
        this.mParentId = parentId;
        this.mNbMedia = nbMedia;
        this.mNbUnplayedMedia = nbUnplayedMedia;
    }

    public DiscoverService.Type mType;
    protected long mParentId;
    protected int mNbMedia;
    protected int mNbUnplayedMedia;


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
        return TYPE_SUBSCRIPTION;
    }

    public int getNbMedia() {
        return mNbMedia;
    }

    public int getNbUnplayedMedia() {
        return mNbUnplayedMedia;
    }


    public abstract int getNewMediaNotification();
    public abstract boolean setNewMediaNotification(int value);
    public abstract long getCachedSize();
    public abstract long getMaxCachedSize();
    public abstract boolean setMaxCachedSize(long size);
    public abstract Subscription[] getChildSubscriptions(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites);
    public abstract Subscription getParent();
    public abstract MediaWrapper[] getMedia(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites);
    public abstract boolean refresh();
    abstract public boolean delete();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
        dest.writeLong(this.mParentId);
        dest.writeInt(this.mNbMedia);
    }

    public void readFromParcel(Parcel source) {
        int tmpMType = source.readInt();
        this.mType = tmpMType == -1 ? null : DiscoverService.Type.values()[tmpMType];
        this.mParentId = source.readLong();
        this.mNbMedia = source.readInt();
    }

    protected Subscription(Parcel in) {
        super(in);
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : DiscoverService.Type.values()[tmpMType];
        this.mParentId = in.readLong();
        this.mNbMedia = in.readInt();
    }

    public static final Creator<Subscription> CREATOR = new Creator<Subscription>() {
        @Override
        public Subscription createFromParcel(Parcel source) {
            return MLServiceLocator.getAbstractSubscription(source);
        }

        @Override
        public Subscription[] newArray(int size) {
            return new Subscription[size];
        }
    };
}
