package com.moshuk.aistorm;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashMap;

public class AiAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        String actionId = event.getActionManager().getId(this);

        String instruction  = "";

        switch(actionId) {
            case "com.moshuk.aistorm.AIGenerateAction":
                instruction = "Finish following code. Provide only code with code inline comments";
                break;
            case "com.moshuk.aistorm.AIFixitAction":
                instruction = "Fix this code. Provide only code with code inline comments";
                break;
            case "com.moshuk.aistorm.AIOptimizeAction":
                instruction =  "Optimize following code. Provide only code with code comments";
                break;
            case "com.moshuk.aistorm.AICustomAction":
                String customInstruction = Messages.showInputDialog("What do you wana do:", "What do you wana do?", null);
                if (customInstruction != null) {
                    instruction = customInstruction;
                    //System.out.println("Введенный текстовый параметр: " + instructionCustom);
                } else {
                    instruction ="";
                }
                break;
            default:
                instruction ="";
        }

        Project project = event.getProject();
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        Document document = editor.getDocument();
        // Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();

        String selectedText = editor.getSelectionModel().getSelectedText();
        //send to ChatGPT

        ChatGPTClient client = new ChatGPTClient(null,project);
        String input = selectedText;

       // String instruction = "Fix this";

        JSONObject request = new JSONObject();
        JSONArray messages = new JSONArray();

        JSONObject message = new JSONObject();
        message.put("role", "system");
        message.put("content", instruction);
        messages.put(message);

        message = new JSONObject();
        message.put("role", "user");
        message.put("content", input);
        messages.put(message);


   //     params.put("instruction", instructionCustom);
   //     params.put("input", input);

        int maxTokens = 50;
        request.put("messages", messages);
        String response = client.generateText(request, maxTokens);
    //    LOG.info(response);


        MyToolWindowFactory myToolWindowFactory = MyToolWindowFactory.getInstance();

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