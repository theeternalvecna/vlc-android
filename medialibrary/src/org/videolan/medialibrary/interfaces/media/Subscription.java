package org.videolan.medialibrary.interfaces.media;

import org.videolan.medialibrary.interfaces.Medialibrary;
import org.videolan.medialibrary.media.MediaLibraryItem;

public abstract class Subscription extends MediaLibraryItem {

    public Subscription(long id, DiscoverService.Type type, String name, long parentId) {
        this.mId = id;
        this.mType = type;
        this.mTitle = name;
        this.mParentId = parentId;
    }
    
    public Subscription(long id, int type, String name, long parentId) {
        this.mId = id;
        this.mType = DiscoverService.Type.getValue(type);
        this.mTitle = name;
        this.mParentId = parentId;
    }

    public DiscoverService.Type mType;
    protected long mParentId;


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

    public abstract int getNewMediaNotification();
    public abstract boolean setNewMediaNotification(int value);
    public abstract long getCachedSize();
    public abstract long getMaxCachedSize();
    public abstract boolean setMaxCachedSize(long size);
    public abstract int getNbUnplayedMedia();
    public abstract Subscription[] getChildSubscriptions(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites);
    public abstract Subscription getParent();
    public abstract MediaWrapper[] getMedia(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites);
    public abstract boolean refresh();
    public abstract int getNbMedia();
}
