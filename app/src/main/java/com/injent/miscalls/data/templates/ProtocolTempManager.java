package com.injent.miscalls.data.templates;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class ProtocolTempManager {

    private final String FILE_NAME = "protocol_temp.json";

    public List<ProtocolTemp> getProtocolList(String json){
        return new Gson().fromJson(json, new TypeToken<List<ProtocolTemp>>(){}.getType());
    }

    public void writeOfflineFile(Context context, List<ProtocolTemp> object) throws IOException {
        Log.d("JsonManager","Writing file");

        FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        OutputStreamWriter ow = new OutputStreamWriter(fos);

        ow.write(new Gson().toJson(object));

        ow.flush();
        ow.close();
        fos.close();
    }

    public String readOfflineFile(Context context) throws IOException {
        Log.d("JsonManager","Reading file");

        //Check if file exists
        File file = new File( context.getApplicationInfo()
                .dataDir + "/files/" + FILE_NAME);
        if (!file.exists()) {
            Log.d("JsonManager","File not found");
            return "";
        }

        try {
            InputStream stream = context.openFileInput(FILE_NAME);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);

            stream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
