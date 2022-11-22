package org.videolan.medialibrary.media;

import android.os.Parcel;

import org.videolan.medialibrary.interfaces.Medialibrary;
import org.videolan.medialibrary.interfaces.media.DiscoverService;
import org.videolan.medialibrary.interfaces.media.MediaWrapper;
import org.videolan.medialibrary.interfaces.media.Subscription;

public class DiscoverServiceImpl extends DiscoverService {
    public DiscoverServiceImpl(Type type) {super(type);}
    DiscoverServiceImpl(int type) {super(type);}

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
    public int getNbSubscriptions() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetNbSubscriptions(ml, this.mType.value) : -1;
    }

    @Override
    public int getNbUnplayedMedia() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetNbUnplayedMedia(ml, this.mType.value) : -1;
    }

    @Override
    public Subscription[] getSubscriptions(int sort, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetSubscriptions(ml, this.mType.value, sort, desc, includeMissing, onlyFavorites) : new Subscription[0];
    }

    @Override
    public int getNbMedia() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetNbMedia(ml, this.mType.value) : -1;
    }

    @Override
    public MediaWrapper[] getMedia(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetServiceMedia(ml, this.mType.value, sortingCriteria, desc, includeMissing, onlyFavorites) : Medialibrary.EMPTY_COLLECTION;
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
    private native int nativeGetNbSubscriptions(Medialibrary ml, int type);
    private native int nativeGetNbUnplayedMedia(Medialibrary ml, int type);
    private native Subscription[] nativeGetSubscriptions(Medialibrary ml, int type, int sort, boolean desc, boolean includeMissing, boolean onlyFavorites);
    private native int nativeGetNbMedia(Medialibrary ml, int type);
    private native MediaWrapper[] nativeGetServiceMedia(Medialibrary ml, int type, int sort, boolean desc, boolean includeMissing, boolean onlyFavorites);
    private native boolean nativeServiceRefresh(Medialibrary ml, int type);
}
