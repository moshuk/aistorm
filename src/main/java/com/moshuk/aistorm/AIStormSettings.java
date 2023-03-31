package com.moshuk.aistorm;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AIStormSettings implements Configurable {
    private JTextField apiKeyTextField;
    private JPanel settingsPanel;

    private String apiKey;

    public AIStormSettings() {
        // initialize settings panel
        apiKeyTextField = new JTextField(apiKey);
        settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.add(new JLabel("API Key:"), BorderLayout.WEST);
        settingsPanel.add(apiKeyTextField, BorderLayout.CENTER);
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "My Plugin Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return settingsPanel;
    }

    @Override
    public boolean isModified() {
        return !apiKey.equals(apiKeyTextField.getText());
    }

    @Override
    public void apply() throws ConfigurationException {
        apiKey = apiKeyTextField.getText();
    }

    @Override
    public void reset() {
        apiKeyTextField.setText(apiKey);
    }
}