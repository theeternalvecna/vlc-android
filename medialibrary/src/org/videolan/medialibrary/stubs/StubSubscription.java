package org.videolan.medialibrary.stubs;

import org.videolan.medialibrary.interfaces.media.DiscoverService;
import org.videolan.medialibrary.interfaces.media.MediaWrapper;
import org.videolan.medialibrary.interfaces.media.Subscription;

public class StubSubscription extends Subscription {

    StubSubscription(long id, DiscoverService.Type type, String name, long parentId, int nbMedia, int nbUpplayedMedia, String artworkMRL) {
        super(id, type, name, parentId, nbMedia, nbUpplayedMedia, artworkMRL);
    }

    StubSubscription(long id, int type, String name, long parentId, int nbMedia, int nbUpplayedMedia, String artworkMRL) {
        super(id, type, name, parentId, nbMedia, nbUpplayedMedia, artworkMRL);
    }

    @Override
    public int getNewMediaNotification() {
        return -1;
    }

    @Override
    public boolean setNewMediaNotification(int value) {
        return false;
    }

    @Override
    public long getCacheSize() {
        return 0;
    }

    @Override
    public long getMaxCacheSize() {
        return 0;
    }

    @Override
    public boolean setMaxCacheSize(long size) {
        return false;
    }

    @Override
    public int getMaxCacheMedia() {
        return 0;
    }

    @Override
    public boolean setMaxCacheMedia(int size) {
        return false;
    }

    @Override
    public int getNbUnplayedMedia() {
        return 0;
    }

    @Override
    public Subscription[] getChildSubscriptions(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        return null;
    }

    @Override
    public Subscription getParent() {
        return null;
    }

    @Override
    public MediaWrapper[] getMedia(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        return null;
    }

    @Override
    public MediaWrapper[] searchMedias(String query, int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites, int nbItems, int offset) {
        return new MediaWrapper[0];
    }

    @Override
    public boolean refresh() {
        return false;
    }

    @Override
    public int getNbMedia() {
        return 0;
    }

    @Override
    public boolean delete() {
        return false;
    }
}
