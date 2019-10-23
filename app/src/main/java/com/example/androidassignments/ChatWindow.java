package com.example.androidassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {
    private ListView chatList;
    private Button sendChatButton;
    private EditText typeChat;
    private ArrayList<String> chatArray;
    private ChatAdapter messageAdapter;
    private ChatDatabaseHelper dbH;
    private SQLiteDatabase database;
    private String[] allItems = { ChatDatabaseHelper.KEY_ID,
            ChatDatabaseHelper.KEY_MESSAGE };
    private final String ACTIVITY_NAME = "ChatWindow";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        dbH = new ChatDatabaseHelper(this);
        database = dbH.getWritableDatabase();
        Cursor cursor = database.query(dbH.TABLE_NAME,allItems, null, null, null, null, null);
        chatList = (ListView)findViewById(R.id.listViewChat);
        sendChatButton = (Button)findViewById(R.id.sendButton);
        typeChat = (EditText)findViewById(R.id.typeChatEditText);
        chatArray = new ArrayList<>();
        messageAdapter =new ChatAdapter( this );
        chatList.setAdapter(messageAdapter);

        cursor.moveToFirst();
        while(!cursor.isAfterLast() ) {
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            Log.i(ACTIVITY_NAME, "Cursor’s column count =" + cursor.getColumnCount() );
            chatArray.add(cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            cursor.moveToNext();
        }
        cursor.moveToFirst();
        for(int i = 0; i <  cursor.getColumnCount(); i++){
            Log.i(ACTIVITY_NAME,"COLUMN: " + cursor.getColumnName(i));
            cursor.moveToNext();
        }
        cursor.close();

        sendChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                String msg = typeChat.getText().toString();
                values.put(ChatDatabaseHelper.KEY_MESSAGE,msg);
                long insertId = database.insert(ChatDatabaseHelper.TABLE_NAME, null,
                        values);
                Cursor cursor = database.query(ChatDatabaseHelper.TABLE_NAME,
                        allItems, ChatDatabaseHelper.KEY_ID + " = " + insertId, null,
                        null, null, null);
                cursor.moveToFirst();
                cursor.close();
                chatArray.add(msg);
                messageAdapter.notifyDataSetChanged();
                typeChat.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbH.close();
    }

     private class ChatAdapter extends ArrayAdapter<String>{
        public ChatAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount(){
            return chatArray.size();
        }

        public String getItem(int position){
            return chatArray.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null ;
            if(position%2 == 0) {
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
            }
            else {
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            }

            TextView message = (TextView)result.findViewById(R.id.message_text);
            message.setText(getItem(position));
            return result;

        }

    }
}
