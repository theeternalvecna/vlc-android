package org.videolan.medialibrary.media;

import android.os.Parcel;

import org.videolan.medialibrary.interfaces.Medialibrary;
import org.videolan.medialibrary.interfaces.media.DiscoverService;
import org.videolan.medialibrary.interfaces.media.MediaWrapper;
import org.videolan.medialibrary.interfaces.media.Subscription;

public class DiscoverServiceImpl extends DiscoverService {
    public DiscoverServiceImpl(Type type, int nbUnplayedMedia, int nbMedia, int nbSubscriptions) {super(type, nbUnplayedMedia, nbMedia, nbSubscriptions);}
    DiscoverServiceImpl(int type, int nbUnplayedMedia, int nbMedia, int nbSubscriptions) {super(type, nbUnplayedMedia, nbMedia, nbSubscriptions);}

    public DiscoverServiceImpl(Parcel source) {
        super(source);
    }

    public boolean addSubscription(String mrl) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeAddSubscription(ml, this.mType.value, mrl);
    }

    @Override
    public boolean isAutoDownloadEnabled() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeIsAutoDownloadEnabled(ml, this.mType.value);
    }

    @Override
    public boolean setAutoDownloadEnabled(boolean enabled) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSetAutoDownloadEnabled(ml, this.mType.value, enabled);
    }

    @Override
    public boolean isNewMediaNotificationEnabled() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeIsNewMediaNotificationEnabled(ml, this.mType.value);
    }

    @Override
    public boolean setNewMediaNotificationEnabled(boolean enabled) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSetNewMediaNotificationEnabled(ml, this.mType.value, enabled);
    }

    @Override
    public long getMaxCachedSize() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetMaxCachedSize(ml, this.mType.value) : -2L;
    }

    @Override
    public boolean setMaxCachedSize(long size) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSetMaxCachedSize(ml, this.mType.value, size);
    }

    @Override
    public Subscription[] getSubscriptions(int sort, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetSubscriptions(ml, this.mType.value, sort, desc, includeMissing, onlyFavorites) : new Subscription[0];
    }


    @Override
    public MediaWrapper[] getMedia(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetServiceMedia(ml, this.mType.value, sortingCriteria, desc, includeMissing, onlyFavorites) : Medialibrary.EMPTY_COLLECTION;
    }

    @Override
    public Subscription[] searchSubscriptions(String query, int sort, boolean desc, boolean includeMissing, boolean onlyFavorites, int nbItems, int offset) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeSearchSubscriptions(ml, mType.value, query, sort, desc, includeMissing, onlyFavorites, nbItems, offset) : new Subscription[0];
    }

    @Override
    public int searchSubscriptionsCount(String query, int sort, boolean desc, boolean includeMissing) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeSearchSubscriptionsCount(ml, mType.value, query) : 0;
    }

    @Override
    public boolean refresh() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeServiceRefresh(ml, this.mType.value);
    }

    private native boolean nativeAddSubscription(Medialibrary ml, int type, String mrl);
    private native boolean nativeIsAutoDownloadEnabled(Medialibrary ml, int type);
    private native boolean nativeSetAutoDownloadEnabled(Medialibrary ml, int type, boolean enabled);
    private native boolean nativeIsNewMediaNotificationEnabled(Medialibrary ml, int type);
    private native boolean nativeSetNewMediaNotificationEnabled(Medialibrary ml, int type, boolean enabled);
    private native long nativeGetMaxCachedSize(Medialibrary ml, int type);
    private native boolean nativeSetMaxCachedSize(Medialibrary ml, int type, long size);
    private native Subscription[] nativeGetSubscriptions(Medialibrary ml, int type, int sort, boolean desc, boolean includeMissing, boolean onlyFavorites);
    private native MediaWrapper[] nativeGetServiceMedia(Medialibrary ml, int type, int sort, boolean desc, boolean includeMissing, boolean onlyFavorites);
    private native Subscription[] nativeSearchSubscriptions(Medialibrary ml, int type, String query, int sort, boolean desc, boolean includeMissing, boolean onlyFavorites, int nbItems, int offset);
    private native int nativeSearchSubscriptionsCount(Medialibrary ml, int type, String query);
    private native boolean nativeServiceRefresh(Medialibrary ml, int type);
}
