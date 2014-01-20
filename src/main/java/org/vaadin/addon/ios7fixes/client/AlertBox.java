package org.vaadin.addon.ios7fixes.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AlertBox extends DialogBox {
    
    interface Stylebundle  extends ClientBundle {
        @Source("alertstyle.css")
        @CssResource.NotStrict
        CssResource css();
    }
    static Stylebundle s = GWT.create(Stylebundle.class);
    
    protected AlertBox(String type, String msg) {
        s.css().ensureInjected();
        
        setText(type);
        setGlassEnabled(true);
        setModal(true);
        FlowPanel panel = new FlowPanel();
        Label label = new Label(msg);
        Button ok = new Button("OK");
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                AlertBox.this.hide();
            }
        });
        panel.add(label);
        panel.add(ok);
        setWidget(panel);
    }

    public static void alert(String msg) {
        new AlertBox("Alert", msg).center();
    }

    public static void confirm(String msg) {
        msg = "Sorry, due to a sad iOS7 bug, we couldn't "
                + "handle following request. Cancel or "
                + "empty answer was given for you to this request: \"" + msg + "\"";
        new AlertBox("Confirm or Prompt Request Failed", msg).show();
    }
}