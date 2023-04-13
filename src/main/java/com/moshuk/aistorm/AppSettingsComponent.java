package com.moshuk.aistorm;

import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField apiKey = new JBTextField();
 //   private final JBCheckBox myIdeaUserStatus = new JBCheckBox("Do you use IntelliJ IDEA? ");

    public AppSettingsComponent() {
        HyperlinkLabel openaiKeyLink = new HyperlinkLabel("https://platform.openai.com/account/api-keys");

        openaiKeyLink.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://platform.openai.com/account/api-keys"));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    //    JLabel openaiKeyLink = new JLabel("<html><a href=\"http://www.example.com\">Click here for help</a></html>");
     //   JLabel label = new JLabel("<html><a href=\"http://www.example.com\">Click here for help</a></html>");
     //   label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
     //   label.setEnabled(true);
     //   openaiKeyLink.setUrl("https://platform.openai.com/account/api-keys");
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Enter ChatGPT API KEY: "), apiKey, 1, false)
        //        .addComponent(myIdeaUserStatus, 1)
          //      .addTooltip("https://platform.openai.com/account/api-keys")
                .addComponent(openaiKeyLink)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();


    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return apiKey;
    }

    @NotNull
    public String getApiKeyText() {
        return apiKey.getText();
    }

    public void setApiKeyText(@NotNull String newText) {
        apiKey.setText(newText);
    }



}