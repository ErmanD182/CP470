package com.example.androidassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        chatList = (ListView)findViewById(R.id.listViewChat);
        sendChatButton = (Button)findViewById(R.id.sendButton);
        typeChat = (EditText)findViewById(R.id.typeChatEditText);
        chatArray = new ArrayList<>();
        messageAdapter =new ChatAdapter( this );
        chatList.setAdapter(messageAdapter);

        sendChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = typeChat.getText().toString();
                chatArray.add(msg);
                messageAdapter.notifyDataSetChanged();
                typeChat.setText("");
            }
        });
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
