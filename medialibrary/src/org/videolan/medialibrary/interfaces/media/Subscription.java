package org.videolan.medialibrary.interfaces.media;

import android.os.Parcel;

import org.videolan.medialibrary.MLServiceLocator;
import org.videolan.medialibrary.interfaces.Medialibrary;
import org.videolan.medialibrary.media.MediaLibraryItem;

public abstract class Subscription extends MediaLibraryItem {

    public Subscription(long id, DiscoverService.Type type, String name, long parentId, int nbMedia, int nbUnplayedMedia, String artworkMRL) {
        this.mId = id;
        this.mType = type;
        this.mTitle = name;
        this.mParentId = parentId;
        this.mNbMedia = nbMedia;
        this.mNbUnplayedMedia = nbUnplayedMedia;
        this.mArtworkMRL = artworkMRL;
    }
    
    public Subscription(long id, int type, String name, long parentId, int nbMedia, int nbUnplayedMedia, String artworkMRL) {
        this.mId = id;
        this.mType = DiscoverService.Type.getValue(type);
        this.mTitle = name;
        this.mParentId = parentId;
        this.mNbMedia = nbMedia;
        this.mNbUnplayedMedia = nbUnplayedMedia;
        this.mArtworkMRL = artworkMRL;
    }

    public DiscoverService.Type mType;
    protected long mParentId;
    protected int mNbMedia;
    protected int mNbUnplayedMedia;
    protected String mArtworkMRL;


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

    public String getArtworkMrl() {
        return mArtworkMRL;
    }

    //fixme. These two methods are placeholders waiting for the author and summary to be implemented in the ML
    public String getAuthor() {
        return "Podcast Author";
    }

    public String getSummary() {
        return "Podcast Author is publishing "+getTitle()+"\n<b>Overwhelmed</b> by choice in the Peak TV era?<br/> <u>Join</u> hosts and IRL friends Tolani Shoneye (of 'The Receipts' podcast fame) and Gena-mour Barrett (who works for Netflix) as they discuss what they're watching, and what they think * you * should be watching too. Each week they'll be accompanied by a guest from the world of Netflix, who'll share a recommendation of their own. From brand new shows to overlooked gems, 10/10 Would Recommend is a freewheeling, unfiltered review show that will help you spend less time choosing, and more time actually watching. Produced by Broccoli Productions and @NetflixUK. Follow the hosts: @smilegena, @tolly_t" +
                "\nLink: https://feeds.simplecast.com/VJWbV9DP";
    }


    public abstract int getNewMediaNotification();
    public abstract boolean setNewMediaNotification(int value);
    public abstract long getCacheSize();
    public abstract long getMaxCacheSize();
    public abstract boolean setMaxCacheSize(long size);
    public abstract int getMaxCacheMedia();
    public abstract boolean setMaxCacheMedia(int size);
    public abstract MediaWrapper[] searchMedias(String query, int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites, int nbItems, int offset);
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
        dest.writeString(this.mArtworkMRL);
    }

    public void readFromParcel(Parcel source) {
        int tmpMType = source.readInt();
        this.mType = tmpMType == -1 ? null : DiscoverService.Type.values()[tmpMType];
        this.mParentId = source.readLong();
        this.mNbMedia = source.readInt();
        this.mArtworkMRL = source.readString();
    }

    protected Subscription(Parcel in) {
        super(in);
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : DiscoverService.Type.values()[tmpMType];
        this.mParentId = in.readLong();
        this.mNbMedia = in.readInt();
        this.mArtworkMRL = in.readString();
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
