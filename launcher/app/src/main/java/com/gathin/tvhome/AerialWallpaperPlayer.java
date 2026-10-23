package com.gathin.tvhome;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import tv.projectivy.plugin.wallpaperprovider.api.Event;
import tv.projectivy.plugin.wallpaperprovider.api.IWallpaperProviderService;
import tv.projectivy.plugin.wallpaperprovider.api.Wallpaper;

import java.util.List;

final class AerialWallpaperPlayer implements TextureView.SurfaceTextureListener {
    private static final String TAG = "TvHome.Aerial";
    static final String AERIAL_PACKAGE = "com.neilturner.aerialviews";
    static final String AERIAL_SERVICE_CLASS =
            "com.neilturner.aerialviews.services.projectivy.WallpaperProviderService";
    static final String BIND_ACTION = "tv.projectivy.plugin.WALLPAPER_PROVIDER";

    private final Context ctx;
    private final ViewGroup container;
    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private IWallpaperProviderService providerService;
    private List<Wallpaper> wallpapers;
    private int wallpaperIndex = 0;
    private boolean surfaceReady = false;
    private boolean bound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.i(TAG, "AerialViews service connected");
            providerService = IWallpaperProviderService.Stub.asInterface(binder);
            try {
                wallpapers = providerService.getWallpapers();
                Log.d(TAG, "got " + (wallpapers != null ? wallpapers.size() : 0) + " wallpapers");
                if (wallpapers != null && !wallpapers.isEmpty() && surfaceReady) {
                    playNext();
                }
            } catch (RemoteException e) {
                Log.w(TAG, "getWallpapers failed: " + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "AerialViews service disconnected");
            providerService = null;
        }
    };

    AerialWallpaperPlayer(Context ctx, ViewGroup container) {
        this.ctx = ctx.getApplicationContext();
        this.container = container;
    }

    void start() {
        textureView = new TextureView(ctx);
        textureView.setSurfaceTextureListener(this);
        container.addView(textureView, 0, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Intent intent = new Intent(BIND_ACTION);
        intent.setComponent(new ComponentName(AERIAL_PACKAGE, AERIAL_SERVICE_CLASS));
        try {
            bound = ctx.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            if (!bound) Log.w(TAG, "bindService failed — AerialViews not installed or not enabled?");
        } catch (Exception e) {
            Log.w(TAG, "bindService threw: " + e.getMessage());
        }
    }

    void stop() {
        releasePlayer();
        if (bound) {
            try { ctx.unbindService(connection); } catch (Exception ignored) {}
            bound = false;
        }
        if (textureView != null && textureView.getParent() != null) {
            ((ViewGroup) textureView.getParent()).removeView(textureView);
        }
        textureView = null;
        providerService = null;
    }

    private void playNext() {
        if (wallpapers == null || wallpapers.isEmpty() || !surfaceReady) return;
        Wallpaper w = wallpapers.get(wallpaperIndex % wallpapers.size());
        wallpaperIndex++;
        if (w.uri == null || w.uri.isEmpty() || w.mediaType != Wallpaper.MEDIA_TYPE_VIDEO) {
            if (wallpaperIndex < wallpapers.size() * 2) playNext(); // skip non-video, avoid infinite loop
            return;
        }
        notifyEvent(Event.TYPE_DISPLAYED, w.uri);
        playUri(w.uri);
    }

    private void playUri(String uri) {
        releasePlayer();
        if (textureView == null || !surfaceReady) return;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(uri);
            mediaPlayer.setSurface(new Surface(textureView.getSurfaceTexture()));
            mediaPlayer.setLooping(false);
            mediaPlayer.setVolume(0f, 0f);
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(mp -> playNext());
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.w(TAG, "MediaPlayer error what=" + what + " extra=" + extra);
                playNext();
                return true;
            });
            mediaPlayer.prepareAsync();
            Log.d(TAG, "playing: " + uri);
        } catch (Exception e) {
            Log.w(TAG, "playUri failed: " + e.getMessage());
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            try { mediaPlayer.stop(); } catch (Exception ignored) {}
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void notifyEvent(int type, String uri) {
        if (providerService == null) return;
        try { providerService.onEvent(new Event(type, uri)); } catch (Exception ignored) {}
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) {
        surfaceReady = true;
        if (wallpapers != null && !wallpapers.isEmpty()) playNext();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
        surfaceReady = false;
        releasePlayer();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture st) {}
}
