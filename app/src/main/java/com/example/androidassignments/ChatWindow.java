package com.example.androidassignments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {
    private boolean frameLayoutCheck;
    private FrameLayout frameLo;
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
    private Cursor cursor;
    private boolean tabletCheck = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        dbH = new ChatDatabaseHelper(this);
        database = dbH.getWritableDatabase();
        cursor = database.query(dbH.TABLE_NAME,allItems, null, null, null, null, null);
        chatList = (ListView)findViewById(R.id.listViewChat);
        sendChatButton = (Button)findViewById(R.id.sendButton);
        typeChat = (EditText)findViewById(R.id.typeChatEditText);
        chatArray = new ArrayList<>();
        messageAdapter =new ChatAdapter( this );
        chatList.setAdapter(messageAdapter);
        frameLo = (FrameLayout)findViewById(R.id.frameLayout);

        //Check if frame layout is loaded and if tablet layout is being used
        if (frameLo == null){
            tabletCheck = false;
        }
        else{
            tabletCheck = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        if(tabletCheck == false){
            Log.i(ACTIVITY_NAME,"FrameLayout not loaded, using phone layout.");
        }
        else{
            Log.i(ACTIVITY_NAME,"FrameLayout loaded, using tablet layout.");
        }
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

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String msg = messageAdapter.getItem(i);
                long id = messageAdapter.getItemId(i);
                MessageFragment mf = new MessageFragment(messageAdapter);
                Bundle args = new Bundle();
                args.putString("message",msg);
                args.putLong("id",id);

                //using tablet and landscape orientation
                if (tabletCheck == true && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    mf.setArguments(args);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.frameLayout,mf);
                    ft.commit();



                }
                //using phone or tablet portrait orientation
                else{
                    Intent intent = new Intent(ChatWindow.this,MessageDetails.class);
                    intent.putExtras(args);
                    startActivityForResult(intent,10);
                }
            }
        });

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10){
            if (resultCode == RESULT_OK) {
                long id2 = 0;
                long id =  data.getLongExtra("Response",id2);

                database.delete(ChatDatabaseHelper.TABLE_NAME,ChatDatabaseHelper.KEY_ID + "=" + id,null);
                finish();
                startActivity(getIntent());
            }
        }

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
         public long getItemId(int position){
             Cursor temp = database.query(dbH.TABLE_NAME,allItems, null, null, null, null, null);
             temp.moveToPosition(position);
             return temp.getLong(temp.getColumnIndex(ChatDatabaseHelper.KEY_ID));
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

    public static class MessageFragment extends Fragment{
        private ChatAdapter ca;
        private Button button;
        private TextView message, id;
        public MessageFragment(ChatAdapter ca){
            this.ca = ca;
        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_message_details,
                    container, false);
            button = (Button)view.findViewById(R.id.deleteMessageButton);
            message = (TextView)view.findViewById(R.id.messageHereTextView);
            id = (TextView)view.findViewById(R.id.IDHereTextView);

            Bundle args = this.getArguments();
            message.setText(args.getString("message"));
            id.setText(Long.toString(args.getLong("id")));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                }
            });
            return view;
        }
    }
}
