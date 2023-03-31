package com.moshuk.aistorm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class MyToolWindowFactory implements ToolWindowFactory {

    private static MyToolWindowFactory instance;
    private JTextArea inputTextArea;
    private JTextPane outputTextPane;
    private JBScrollPane outputScrollPane;

  //  private MyToolWindowFactory() {}

    public static MyToolWindowFactory getInstance() {
        if (instance == null) {
            instance = new MyToolWindowFactory();
        }
        return instance;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Create your tool window content here
        JPanel content = new JPanel(new BorderLayout());

        // Create a text area for user input
        inputTextArea = new JTextArea();
        inputTextArea.setEditable(true);

        // Create a text pane for output
        outputTextPane = new JTextPane();
        outputTextPane.setEditable(false);

        // Wrap the output text pane in a JBScrollPane
        outputScrollPane = new JBScrollPane(outputTextPane);

        // Add a listener for user input
        inputTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Get the user input
                    String input = inputTextArea.getText().trim();

                    // Clear the input area
                    inputTextArea.setText("");

                    // Show the user input in the output area
                    appendToOutput(">>> " + input, Color.BLUE);

                    // Do something with the user input
                    processInput(input);
                }
            }
        });

        // Create a panel to hold the input text area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputTextArea, BorderLayout.CENTER);

        // Add the input and output panels to the content panel
        content.add(outputScrollPane, BorderLayout.CENTER);
        content.add(inputPanel, BorderLayout.SOUTH);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content myContent = contentFactory.createContent(content, "", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(myContent);

        // Set the content for your tool window
    //    toolWindow.getContentManager().addContent(
  //              ContentFactory.SERVICE.getInstance().createContent(content, "", false));

        instance = this;
    }

    public void processInput(String input) {
        // Process the user input here
        // You can do anything you want with the input
        // For example, you can print it to the console
            String endpointUrl = "https://api.openai.com/v1/engines/text-davinci-003/completions";
        // https://platform.openai.com/account/api-keys
        String apiKey = "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";

        ChatGPTClient client = new ChatGPTClient(endpointUrl);

        int maxTokens = 50;

        HashMap<String, String> params = new HashMap<String, String>();

 //       String instruction = "Finish this";
        params.put("prompt", input);
      //  params.put("input", input);

        String response = client.generateText(params, maxTokens);

        appendToOutput("AI RESPONSE: " + response, Color.BLACK);
    }

    public void appendToOutput(String text, Color color) {
        // Append text to the output area
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

        int length = outputTextPane.getDocument().getLength();
        outputTextPane.setCaretPosition(length);
        outputTextPane.setCharacterAttributes(attributeSet, false);
  //      outputTextPane.replaceSelection(text + "\n");

        StyledDocument doc = outputTextPane.getStyledDocument();


        try
        {
            doc.insertString(doc.getLength(), "\n"+text+ "\n", null );
        }
        catch(Exception e) { System.out.println(e); }



        // Scroll to the bottom of the output area
        JScrollBar verticalScrollBar = outputScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }
}