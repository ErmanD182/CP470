package com.example.androidassignments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MessageDetails extends AppCompatActivity {
private Button deleteButton;
private long id;
private String msg;
private TextView message, msg_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        deleteButton = (Button)(findViewById(R.id.deleteMessageButton));
        Bundle args = this.getIntent().getExtras();

        msg = args.getString("message");
        id = args.getLong("id");
        message = (TextView)findViewById(R.id.messageHereTextView);
        msg_id = (TextView)findViewById(R.id.IDHereTextView);

        message.setText(msg);
        msg_id.setText(Long.toString(id));

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent(  );
                resultIntent.putExtra("Response", id);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

    }
}
