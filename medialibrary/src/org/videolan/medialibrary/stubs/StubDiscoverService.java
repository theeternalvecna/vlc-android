package org.videolan.medialibrary.stubs;

import android.os.Parcel;

import org.videolan.medialibrary.interfaces.media.DiscoverService;
import org.videolan.medialibrary.interfaces.media.MediaWrapper;
import org.videolan.medialibrary.interfaces.media.Subscription;

public class StubDiscoverService extends DiscoverService {
    public StubDiscoverService(Type type) {super(type);}
    StubDiscoverService(int type) {super(type);}

    public StubDiscoverService(Parcel source) {
        super(source);
    }

    @Override
    public boolean addSubscription(String mrl) {
        return false;
    }

    @Override
    public boolean isAutoDownloadEnabled() {
        return false;
    }

    @Override
    public boolean setAutoDownloadEnabled(boolean enabled) {
        return false;
    }

    @Override
    public boolean isNewMediaNotificationEnabled() {
        return false;
    }

    @Override
    public boolean setNewMediaNotificationEnabled(boolean enabled) {
        return false;
    }

    @Override
    public long getMaxCachedSize() {
        return 0;
    }

    @Override
    public boolean setMaxCachedSize(long size) {
        return false;
    }

    @Override
    public int getNbSubscriptions() {
        return 0;
    }

    @Override
    public int getNbUnplayedMedia() {
        return 0;
    }

    @Override
    public Subscription[] getSubscriptions(int sort, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        return new Subscription[0];
    }

    @Override
    public MediaWrapper[] getMedia(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        return new MediaWrapper[0];
    }

    @Override
    public int getNbMedia() {
        return 0;
    }

    @Override
    public boolean refresh() {
        return false;
    }
}
