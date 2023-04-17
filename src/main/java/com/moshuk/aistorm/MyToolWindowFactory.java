package com.moshuk.aistorm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class MyToolWindowFactory implements ToolWindowFactory, ProjectManagerListener {

    private static MyToolWindowFactory instance;
    private JTextArea inputTextArea;
    private JTextPane outputTextPane;
    private JBScrollPane outputScrollPane;

    private JSONArray messages;

    private ChatGPTClient chatClient;

    public ToolWindow AItoolWindow;


  //  private MyToolWindowFactory() {}

    public static MyToolWindowFactory getInstance() {
        if (instance == null) {
            instance = new MyToolWindowFactory();
        }
        return instance;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, ToolWindow toolWindow) {
        // Create your tool window content here
        JPanel content = new JPanel(new BorderLayout());
        this.messages = new JSONArray();
        // Create a text area for user input
        inputTextArea = new JTextArea();
        inputTextArea.setEditable(true);

        // Create a text pane for output
        outputTextPane = new JTextPane();
        outputTextPane.setEditable(false);

        // Wrap the output text pane in a JBScrollPane
        outputScrollPane = new JBScrollPane(outputTextPane);

        this.chatClient = new ChatGPTClient(null,project);

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


     //   String text = "Hello, world!";
      //  JComponent panel = new JPanel(new BorderLayout());
     //   panel.add(new JLabel(text), BorderLayout.CENTER);
        Content myContent = toolWindow.getContentManager().getFactory().createContent(content, "", false);
        toolWindow.getContentManager().addContent(myContent);

/*
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content myContent = contentFactory.createContent(content, "", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(myContent);
*/
        // Set the content for your tool window
    //    toolWindow.getContentManager().addContent(
  //              ContentFactory.SERVICE.getInstance().createContent(content, "", false));
        AItoolWindow = toolWindow;
        instance = this;
    }
/*
    @Override
    public void projectOpened(Project project) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

        if (AItoolWindow == null) {

            AItoolWindow = toolWindowManager.registerToolWindow("AiStorm", true, ToolWindowAnchor.BOTTOM);

         //   ToolWindow myToolWindow = toolWindowManager.getToolWindow("AiStorm");
            this.createToolWindowContent(project, AItoolWindow);


            }
        this.showToolWindow();

    }
*/
    public void showToolWindow() {
        if (AItoolWindow != null) {
            AItoolWindow.show(null);
        }
    }

    public void processInput(String input) {
        // Process the user input here
        // You can do anything you want with the input
        // For example, you can print it to the console
   //         String endpointUrl = "https://api.openai.com/v1/chat/completions";
        // https://platform.openai.com/account/api-keys


        //ChatGPTClient client = new ChatGPTClient(endpointUrl);

        int maxTokens = 50;

        //     HashMap<String, String> params = new HashMap<String, String>();

 //       String instruction = "Finish this";
    //    params.put("prompt", input);
      //  params.put("input", input);

        /*
        messages=[
        {"role": "system", "content": "You are a helpful assistant."},
        {"role": "user", "content": "Who won the world series in 2020?"},
        {"role": "assistant", "content": "The Los Angeles Dodgers won the World Series in 2020."},
        {"role": "user", "content": "Where was it played?"}
    ]
         */
        JSONObject  message = new JSONObject();

        JSONObject request = new JSONObject();

        message.put("role", "user");
        message.put("content", input);

        this.messages.put(message);  //TODO

      //  request.put("top_p", 1);
        request.put("messages", this.messages);
     //   params.put("messages", messages);

        String response = this.chatClient.generateText(request, maxTokens);

        appendToOutput("AI RESPONSE: " + response, Color.BLACK);
    }

    public void appendToOutput(String text, Color color) {
        // Append text to the output area
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

        if(this.outputTextPane == null)
        {
            return;
        }
        int length = outputTextPane.getDocument().getLength();
        this.outputTextPane.setCaretPosition(length);
        this.outputTextPane.setCharacterAttributes(attributeSet, false);
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