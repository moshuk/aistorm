package com.moshuk.aistorm;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ChatGPTClient {

    private static final Logger LOG = Logger.getInstance(ChatGPTClient.class);

    private String endpointUrl;
    private String apiKey;

    public Project project;

    public ChatGPTClient(String endpointUrl, Project project) {
        if (endpointUrl != null) {
                this.endpointUrl = endpointUrl;
            }
         else{
       //         this.endpointUrl = "https://api.openai.com/v1/edits";
                this.endpointUrl = "https://api.openai.com/v1/chat/completions";
            }


        AppSettingsState settings = ServiceManager.getService(AppSettingsState.class);
        String apiKey = settings.apiKey;


        this.apiKey = apiKey;
    }

    public String generateText(JSONObject params, int maxTokens) {
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(60, TimeUnit.SECONDS).build();
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject requestBody = new JSONObject();

       // requestBody.put("prompt", prompt);

        for (String key : params.keySet()) {
            requestBody.put(key, params.get(key));
        }
        requestBody.put("top_p", 1);
  //      requestBody.put("max_tokens", maxTokens);
        requestBody.put("model", "gpt-3.5-turbo");
    //    requestBody.put("model", "code-davinci-edit-0011");


        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
                .url(endpointUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                LOG.info("REQUEST: " + response.code() + "url: "+endpointUrl+" requestBody.toString() "+requestBody.toString());

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                JSONArray choices = json.getJSONArray("choices");
                JSONObject firstChoice = choices.getJSONObject(0);
                LOG.info("RESPONSE: " + responseBody );
                JSONObject message = firstChoice.getJSONObject("message");
                return message.getString("content");
             //   return firstChoice.toString();
             //   return responseBody;
            } else {
                LOG.error("Failed to generate text: " + response.code() + " " + response.message() + "url: "+endpointUrl+" requestBody.toString() "+requestBody.toString());
if(response.code() == 401)
{
    Messages.showErrorDialog("Please check you Open AI api key.", "Error");
    ShowSettingsUtil.getInstance().showSettingsDialog(this.project, AppSettingsConfigurable.class);
}

            }
        } catch (IOException e) {
            Messages.showErrorDialog("An error has occurred.", "Error");
            LOG.error("Failed to generate text exceotion: " + e.getMessage());
        }

        return null;
    }
}