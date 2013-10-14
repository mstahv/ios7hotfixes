package org.vaadin.addon.ios7fixes.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;

public class Ios7HackLoader implements EntryPoint {

    private int bootWidth;
    private int bootHeight;

    public void onModuleLoad() {

        /*
         * Most critical issues happen in "Home Screen Web Apps". That is when
         * apps use <meta name="apple-mobile-web-app-capable" content="yes">
         * meta tag and have been added to home screen. There e.g. viewport
         * settings cause major head ache and alert/prompt/confirm don't work at
         * all.
         * 
         * At least currently this tool only fixes stuff for thos apps.
         */
        if (isiOS7HomeScreenApp()) {

            if (isLandscape()) {
                bootWidth = getWindowInnerHeight();
                bootHeight = getWindowInnerWidth();
            } else {
                bootHeight = getWindowInnerHeight();
                bootWidth = getWindowInnerWidth();
            }

            /* "Somewhat" working viewport settings */
            addHeightToViewPort();

            // ... and set it each time size changes (most often orientation
            // change)
            Window.addResizeHandler(new ResizeHandler() {
                @Override
                public void onResize(ResizeEvent event) {
                    // Defer to get correct orientation, 800 ms seems to be just
                    // enough (tested on ipad mini)
                    new Timer() {
                        @Override
                        public void run() {
                            if (!isVirtualKeyboardOn()) {
                                fixHtmlHeightToWindowInnerHeight();
                            }
                        }
                    }.schedule(800);
                }
            });

            hookCustomAlerts();
        }

    }

    private boolean isLandscape() {
        switch (getOrientation()) {
        case 0:
        case 180:
            return false;
        case 90:
        case -90:
        default:
            return true;
        }
    }

    /**
     * @return does the magic described by method name based on UA inspection
     */
    private boolean isiOS7HomeScreenApp() {
        String ua = Navigator.getUserAgent();
        return ua != null && ua.contains("OS 7_0") && ua.contains("iP")
                && !ua.contains("Safari");
    }

    private boolean isVirtualKeyboardOn() {
        int referenceHeight = isLandscape() ? bootWidth : bootHeight;
        int differeceToStart = Math.abs(referenceHeight
                - getWindowInnerHeight());
        // Allow small changes (~ status bar & e.g. hotspot notification)
        if (differeceToStart > 100) {
            return true;
        }
        return false;
    }

    private static final native void log(String s)
    /*-{
        $wnd.console.log(s);
    }-*/;

    private static final native void d()
    /*-{
        debugger;
    }-*/;

    /**
     * Modifies viewport tag to include both width=device-width AND
     * height=device-height. The latter is not generally know or used, but seems
     * to prevent ios from changen screen size when virtual keyboard pops on.
     * This is how it works in Safari, Android, mobile IE and in previous
     * version of iOS home screen web apps.
     * <p>
     * Instead of "device-height" we use pixel height reported by
     * window.innerHeight. On orientation changes that must be updated.
     */
    private void addHeightToViewPort() {
        MetaElement item = getViewportTag();
        if (item != null) {
            String attribute = item.getContent();
            if (!attribute.contains("width")) {
                attribute += ",width=device-width";
            }
            int viewPortHeight = getWindowInnerHeight();
            if (!attribute.contains("height")) {
                attribute += ",height=" + viewPortHeight;
            } else {
                attribute = updateViewPortHeight(attribute, viewPortHeight);
            }
            item.setContent(attribute);
            ;
        }
    }

    private MetaElement getViewportTag() {
        NodeList<Element> metas = Document.get().getElementsByTagName("meta");
        for (int i = 0; i < metas.getLength(); i++) {
            MetaElement item = metas.getItem(i).cast();
            if ("viewport".equals(item.getAttribute("name"))) {
                return item;
            }
        }
        return null;
    }

    private native final void hookCustomAlerts()
    /*-{
        console.log("fixing broken alert in ios7 alert");
        $wnd.alert = function(msg) {
            @org.vaadin.addon.ios7fixes.client.AlertBox::alert(Ljava/lang/String;)(msg);
        };
        $wnd.confirm  = function(msg) {
            @org.vaadin.addon.ios7fixes.client.AlertBox::confirm(Ljava/lang/String;)(msg);
        };
        $wnd.prompt = function(msg) {
            @org.vaadin.addon.ios7fixes.client.AlertBox::confirm(Ljava/lang/String;)(msg);
        };
        console.log("alert,confirm and prompt now 'polyfilled'");
    }-*/;

    private void fixHtmlHeightToWindowInnerHeight() {
        final MetaElement tag = getViewportTag();
        int viewPortHeight = getWindowInnerHeight();
        String c = updateViewPortHeight(tag.getContent(), viewPortHeight);
        tag.setContent(c);
    }

    private static final native String updateViewPortHeight(String s, int h)
    /*-{
        return s.replace(/,height=\w+/,",height=" + h);
    }-*/;;

    private static final native int getOrientation()
    /*-{
        return $wnd.orientation;
    }-*/;

    private static native int getWindowInnerHeight()
    /*-{
        return $wnd.innerHeight;
    }-*/;

    private static native int getWindowInnerWidth()
    /*-{
        return $wnd.innerWidth;
    }-*/;

}
