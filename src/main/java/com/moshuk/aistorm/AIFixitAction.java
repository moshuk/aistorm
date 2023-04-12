package com.moshuk.aistorm;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;

public class AIFixitAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        Document document = editor.getDocument();
        // Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();

        String selectedText = editor.getSelectionModel().getSelectedText();
        //send to ChatGPT


        //   String endpointUrl = "https://api.openai.com/v1/completions";
        // https://platform.openai.com/account/api-keys
  //      String apiKey = "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";

        ChatGPTClient client = new ChatGPTClient(null);
        String input = selectedText;
        HashMap<String, String> params = new HashMap<String, String>();

        String instruction = "Fix this";
        params.put("instruction", instruction);
        params.put("input", input);

        int maxTokens = 50;
        String response = client.generateText(null, maxTokens);
    //    LOG.info(response);


        MyToolWindowFactory myToolWindowFactory = MyToolWindowFactory.getInstance();


     //   ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
 //       ToolWindow toolWindow = toolWindowManager.getToolWindow("AiStorm");
 //       MyToolWindowFactory myToolWindowFactory = new MyToolWindowFactory();
//        myToolWindowFactory.init(toolWindow);
        myToolWindowFactory.appendToOutput(">>> " + instruction, Color.BLUE);
        myToolWindowFactory.appendToOutput(">>> " + input, Color.BLUE);
        myToolWindowFactory.appendToOutput("Processed input: " + response, Color.BLACK);

        // Replace the selection with a fixed string.
        // Must do this document change in a write action context.
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, response)
        );

        // De-select the text range that was just replaced
        primaryCaret.removeSelection();
    }
}