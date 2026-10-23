package tv.projectivy.plugin.wallpaperprovider.api;
import tv.projectivy.plugin.wallpaperprovider.api.Wallpaper;
import tv.projectivy.plugin.wallpaperprovider.api.Event;

interface IWallpaperProviderService {
    List<Wallpaper> getWallpapers();
    void onEvent(in Event event);
}
