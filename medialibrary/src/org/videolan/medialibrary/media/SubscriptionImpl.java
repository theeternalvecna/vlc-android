package org.videolan.medialibrary.media;

import android.os.Parcel;

import androidx.annotation.Nullable;

import org.videolan.medialibrary.interfaces.Medialibrary;
import org.videolan.medialibrary.interfaces.media.DiscoverService;
import org.videolan.medialibrary.interfaces.media.MediaWrapper;
import org.videolan.medialibrary.interfaces.media.Subscription;

public class SubscriptionImpl extends Subscription {

    SubscriptionImpl(long id, DiscoverService.Type type, String name, long parentId, int nbMedia, int nbUpplayedMedia) {
        super(id, type, name, parentId, nbMedia, nbUpplayedMedia);
    }

    SubscriptionImpl(long id, int type, String name, long parentId, int nbMedia, int nbUpplayedMedia) {
        super(id, type, name, parentId, nbMedia, nbUpplayedMedia);
    }

    public SubscriptionImpl(Parcel source) {
        super(source);
    }


    @Override
    public int getNewMediaNotification() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeSubscriptionNewMediaNotification(ml, this.mId) : -1;
    }

    @Override
    public boolean setNewMediaNotification(int value) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSetSubscriptionNewMediaNotification(ml, this.mId, value);
    }

    @Override
    public long getCachedSize() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetSubscriptionCachedSize(ml, this.mId) : -2;
    }

    @Override
    public long getMaxCachedSize() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetSubscriptionMaxCachedSize(ml, this.mId) : -2;
    }

    @Override
    public boolean setMaxCachedSize(long size) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSetSubscriptionMaxCachedSize(ml, this.mId, size);
    }

    @Override
    public Subscription[] getChildSubscriptions(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetChildSubscriptions(ml, mId, sortingCriteria, desc, includeMissing, onlyFavorites) : new Subscription[0];
    }

    @Override
    @Nullable
    public Subscription getParent() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetParent(ml, mId) : null;
    }

    @Override
    public boolean refresh() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSubscriptionRefresh(ml, mId);
    }

    @Override
    public MediaWrapper[] getMedia(int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites) {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() ? nativeGetSubscriptionMedia(ml, mId, sortingCriteria, desc, includeMissing, onlyFavorites) : Medialibrary.EMPTY_COLLECTION;
    }

    @Override
    public boolean delete() {
        final Medialibrary ml = Medialibrary.getInstance();
        return ml.isInitiated() && nativeSubscriptionDelete(ml, mId);
    }

    private native int nativeSubscriptionNewMediaNotification(Medialibrary ml, long id);
    private native boolean nativeSetSubscriptionNewMediaNotification(Medialibrary ml, long id, int value);
    private native long nativeGetSubscriptionCachedSize(Medialibrary ml, long id);
    private native long nativeGetSubscriptionMaxCachedSize(Medialibrary ml, long id);
    private native boolean nativeSetSubscriptionMaxCachedSize(Medialibrary ml, long id, long size);
    private native Subscription[] nativeGetChildSubscriptions(Medialibrary ml, long id, int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites);
    private native Subscription nativeGetParent(Medialibrary ml, long id);
    private native boolean nativeSubscriptionRefresh(Medialibrary ml, long id);
    private native MediaWrapper[] nativeGetSubscriptionMedia(Medialibrary ml, long id, int sortingCriteria, boolean desc, boolean includeMissing, boolean onlyFavorites);
    private native boolean nativeSubscriptionDelete(Medialibrary ml, long id);
}
