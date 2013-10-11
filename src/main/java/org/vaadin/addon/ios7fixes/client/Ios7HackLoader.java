package org.vaadin.addon.ios7fixes.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;

public class Ios7HackLoader implements EntryPoint {

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

            /* "Somewhat" working viewport settings */
            addHeightToViewPort();

            // The "viewport" size correct, set explicitly set height
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    fixHtmlHeightToWindowInnerHeight();
                }
            });

            // ... and set it each time size changes (most often orientation
            // change)
            Window.addResizeHandler(new ResizeHandler() {
                @Override
                public void onResize(ResizeEvent event) {
                    fixHtmlHeightToWindowInnerHeight();
                }
            });

            /*
             * With the above the body will get scrolled to bit odd positions
             * 
             * Note, that this cannot be added using scroll handler because
             * scrolling due to VKB don't always fire scroll. Thus we'll just
             * have to run the check periodically.
             */
            Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

                @Override
                public boolean execute() {
                    int scrollTop = Document.get().getBody().getScrollTop();
                    if (scrollTop != 0 && !isKeyboardOn()) {
                        // Most likely virtual keyboard popped up finally off,
                        // lazily scroll back when keyboard is off
                        Document.get().getBody().setScrollTop(0);
                    }
                    return true;
                }
            }, 300);

            hookCustomAlerts();
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

    /**
     * Modifies viewport tag to include both width=device-width AND
     * height=device-height. The latter is not generally know or used, but seems
     * to prevent ios from changen screen size when virtual keyboard pops on.
     * This is how it works in Safari, Android, mobile IE and in previous
     * version of iOS home screen web apps.
     * <p>
     * Without this viewport on home screen web shrinks when e.g. virtual
     * keyboard pops on, causing all kinds of weird stuff. Setting
     * "device-width" & "device-height" somewhat fixes the issue, but size is
     * still bit wrong in vertical orientation, more in horizontal mode. Note
     * that setting the "right pixel value" don't work either, I have tried
     * that. Thus in addition to this we also define the actually correct size
     * from window.innerHeigt to document element, which again causes some
     * scroll issues when keyboard gets on and off, but we'll fix them too with
     * timers :-).
     */
    private void addHeightToViewPort() {
        NodeList<Element> metas = Document.get().getElementsByTagName("meta");
        for (int i = 0; i < metas.getLength(); i++) {
            MetaElement item = metas.getItem(i).cast();
            if ("viewport".equals(item.getAttribute("name"))) {
                String attribute = item.getAttribute("content");
                if (!attribute.contains("width")) {
                    attribute += ",width=device-width";
                }
                if (!attribute.contains("height")) {
                    attribute += ",height=device-height";
                }
                item.setAttribute("content", attribute);
            }
        }

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

    private boolean isKeyboardOn() {
        return Math.abs(Document.get().getBody().getOffsetHeight()
                - getWindowInnerHeight()) > 45;
    }

    private void fixHtmlHeightToWindowInnerHeight() {
        /*
         * window.innerHeight/Width seems to be the only thing that one can
         * count in iOS7. We'll fix that to the problematic height
         */
        Document.get().getDocumentElement().getStyle()
                .setHeight(getWindowInnerHeight(), Unit.PX);
    }

    private static native int getWindowInnerHeight()
    /*-{
        return $wnd.innerHeight;
    }-*/;

}
